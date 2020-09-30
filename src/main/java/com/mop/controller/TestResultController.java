package com.mop.controller;

import com.alibaba.fastjson.JSONObject;
import com.mop.async.AsyncTask;
import com.mop.entity.*;
import com.mop.service.IConfigService;
import com.mop.service.IRulesService;
import com.mop.service.ITestresultService;
import com.mop.utils.FileUtil;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;

@RestController
public class TestResultController {

    private final static Logger log = Logger.getLogger(TestResultController.class);

    @Resource
    private ITestresultService testResultService;

    @Resource
    private IRulesService rulesService;

    @Resource
    private IConfigService configService;

    @Autowired
    private AsyncTask asyncTask;

    @ResponseBody
    @RequestMapping(value = "/upload", produces = "application/json; charset=UTF-8")
    public Json upload(@RequestParam(name = "file") MultipartFile file,@RequestParam(name = "logTable") String logTable)
    {
        Json j = new Json();
        try{
            if (!file.isEmpty())
            {
                String filename = file.getOriginalFilename();
                log.info("原文件名：" + filename);
                String suffixName = filename.substring(filename.lastIndexOf("."));
                log.info("文件后缀：" + suffixName);
                if (!suffixName.equals(".csv"))
                {
                    j.setCode("1002");
                    j.setData(false);
                    j.setMsg("文件格式不正确");
                    return j;
                }
                String filePath = "e:\\";
                filePath = "/data/ruleFile/";
                String realPath = filePath + logTable + ".csv";
                log.info("完整路径：" + realPath);
                File file1 = new File(realPath);
                if (!file1.getParentFile().exists())
                {
                    file1.getParentFile().mkdirs();
                }
                //保存文件
                file.transferTo(file1);
                j.setCode("1001");
                j.setData(true);
                j.setMsg("上传成功");
            }
            else
            {
                j.setCode("1002");
                j.setData(false);
                j.setMsg("文件不能为空");
                return j;
            }
            return j;
        }catch (IOException e){
            j.setCode("1002");
            j.setData(false);
            j.setMsg("上传失败");
            return j;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/importExcel", produces = "application/json; charset=UTF-8")
    public Json importExcelToJson(@RequestParam(name = "file") MultipartFile file,@RequestParam(name = "logTable") String logTable) {
        Json j = new Json();
        JSONObject object = new JSONObject(new LinkedHashMap<>());
        FileInputStream fis = null;
        try{
            String filename = file.getOriginalFilename();
            log.info("原文件名：" + filename);
            String suffixName = filename.substring(filename.lastIndexOf("."));
            log.info("文件后缀：" + suffixName);
            String filePath = "/data/ruleFile/" + filename;
            File file1 = new File(filePath);
            if (!file1.getParentFile().exists())
            {
                file1.getParentFile().mkdirs();
            }
            //上传文件
            file.transferTo(file1);
            //用流的方式获得文件
            fis = new FileInputStream(file1);
            XSSFWorkbook workbook = new XSSFWorkbook(fis);
            XSSFSheet sheet = workbook.getSheetAt(0);
            ArrayList<String> array = null;
            boolean onOff = true;
            int i;
            for (Row row : sheet)
            {
                array = new ArrayList<>();
                i = 0;
                for (Cell cell : row)
                {
                    if (i > 0 || onOff)
                    {
                        array.add(cell.getStringCellValue());
                    }
                    i++;
                }
                if (onOff)
                {
                    object.put("columns",array);
                    onOff = false;
                }
                else
                {
                    try {
                        object.put(row.getCell(0).getStringCellValue(),array);
                    }catch (NullPointerException e){
                        log.info("多读了一行");
                        break;
                    }
                }
            }
            Date now = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            String nowTime = dateFormat.format(now);
            Rule rule = new Rule();
            rule.setTableName(logTable);
            rule.setRuleJson(object.toJSONString());
            if (rulesService.getRule(logTable) == null)
            {
                rule.setCreateTime(nowTime);
                rulesService.addRule(rule);
                j.setCode("1001");
                j.setData(true);
                j.setMsg("上传规则成功");
            }
            else
            {
                rule.setRid(null);
                rule.setCreateTime(null);
                rule.setUpdateTime(nowTime);
                rulesService.updateRule(rule);
                j.setCode("1001");
                j.setData(true);
                j.setMsg("替换规则成功");
            }
            fis.close();
        }catch (Exception e){
            e.printStackTrace();
            j.setCode("1002");
            j.setData(false);
            j.setMsg("上传失败");
        }
        return j;
    }

    @ResponseBody
    @GetMapping(value = "/getRule", produces = "application/json; charset=UTF-8")
    public Json getRule(@RequestParam("tablename")String tableName){
        Json j = new Json();
        try{
            Rule rule = rulesService.getRule(tableName);
            if (rule != null)
            {
                j.setCode("1001");
                j.setData(true);
                j.setObj(rule);
            }
            else
            {
                j.setCode("1002");
                j.setData(false);
                j.setMsg("无数据");
                j.setObj("null");
            }
        }catch (Exception e){
            j.setCode("1002");
            j.setData(false);
            j.setMsg("查询失败");
            j.setObj("null");
        }
        return j;
    }

    @ResponseBody
    @GetMapping(value = "/download", produces = "application/json; charset=UTF-8")
    public Json download(HttpServletRequest request, HttpServletResponse response, @RequestParam(name = "filename") String filename)
    {
        Json j = new Json();
        if(FileUtil.download(request,response,filename))
        {
            j.setCode("1001");
            j.setData("success");
            j.setMsg("下载成功");
        }
        else
        {
            j.setCode("1002");
            j.setData("error");
            j.setMsg("下载失败");
        }
        return j;
    }

    @ResponseBody
    @RequestMapping(value = "/addcase" ,method = RequestMethod.POST ,produces = "application/json; charset=UTF-8")
    public Json addcase(@RequestBody Map<String,Object> mapValue) throws InterruptedException, ExecutionException {
        Json j = new Json();
        String logname = (String) mapValue.get("logname");
        String appname = (String) mapValue.get("appname");
        String os = (String) mapValue.get("os");
        String appversion = (String) mapValue.get("appversion");
        String dateline = (String) mapValue.get("dateline");
        String testnumValue = (String) mapValue.get("testnum");
        int testnum = Integer.parseInt(testnumValue);
        String owner = (String) mapValue.get("owner");
        String logname_cn = (String) mapValue.get("logname_cn");
        asyncTask.asyncAddCase(logname,appname,os,appversion,dateline,testnum,owner,logname_cn);
        j.setCode("1001");
        j.setData(true);
        j.setMsg("测试结果会陆续生成，测试完成后会将测试结果详情发送至邮箱，请注意查收。");
        return j;
    }

    @ResponseBody
    @RequestMapping("/testresults")
    public List<Testresult> getTestresults() {
        return testResultService.getTestresults();
    }

    @ResponseBody
    @RequestMapping("/testresults/{tid}")
    public Testresult getTestresult(@PathVariable(name = "tid") int tid) {
        return testResultService.getTestresult(tid);
    }

    @ResponseBody
    @RequestMapping(value = "/testConfig",produces = "application/json")
    public Map<String,Object> testConfig(){
        return configService.getIsNullConfig("log_app_install");
    }

}

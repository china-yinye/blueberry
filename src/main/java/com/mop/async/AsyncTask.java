package com.mop.async;

import com.mop.entity.*;
import com.mop.service.IConfigService;
import com.mop.service.IRollbackTaskService;
import com.mop.service.ITestresultService;
import com.mop.test.Execute;
import com.mop.utils.FileUtil;
import com.mop.utils.HttpUtil;
import com.mop.utils.MailUtil;
import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class AsyncTask {

    private final static Logger log = Logger.getLogger(AsyncTask.class);

    @Resource
    private ITestresultService testResultService;

    @Resource
    private IRollbackTaskService IRollbackTaskService;

    @Resource
    private IConfigService configService;

    @Async("doTestExecutor")
    @SuppressWarnings("unchecked")
    public void asyncAddCase(String logname, String appname, String os, String appversion, String dateline, int testnum, String owner, String logname_cn) {
        Testresult testresult = new Testresult();
        RollbackTask rollbackTask = new RollbackTask();
        RollbackTask task;
        Execute exe;
        MailNotice mailNotice = new MailNotice();
        ArrayList<String> errorCode = new ArrayList<>();
        ArrayList<String> errorLogName = new ArrayList<>();
        ArrayList<String> reason = new ArrayList<>();
        ArrayList<String> passLog = new ArrayList<>();
        ArrayList<String> failLog = new ArrayList<>();
        if (logname != null && appname != null && os != null && appversion != null && dateline != null && testnum > 0)
        {
            String msg;
            int passSum = 0,failSum = 0,cn_index = 0;
            String[] logName_cn = logname_cn.split(",");
            //批量执行
            for (String database_tableName : logname.split(","))
            {
                String dataBase = database_tableName.split("\\.")[0];
                String tableName = database_tableName.split("\\.")[1];
                testresult.setOwner(owner);
                testresult.setLogname(dataBase + "." + tableName);
                testresult.setAppname(appname);
                testresult.setOs(os);
                testresult.setDateline(dateline);
                if (appversion.equals(""))
                {
                    testresult.setAppversion("");
                }
                else
                {
                    testresult.setAppversion(appversion);
                }
                String startTime = dateline.split(",")[0];
                String endTime = dateline.split(",")[1];
                String data = HttpUtil.getData(dataBase, tableName, testnum, startTime, endTime, os, appname, appversion);
                if (data.length() == 3)
                {
                    msg = exceptionReason(data);
                    errorCode.add(data);
                    errorLogName.add(logName_cn[cn_index]);
                    reason.add(msg);
                    cn_index++;
                    continue;
                }
                exe = new Execute(configService,tableName);
                Map<String, Object> map = exe.execute(data, tableName);
                //生成失败附件
                List<Testcase2> failList = (List<Testcase2>) map.get("failList");
                String filePath = FileUtil.failedCase_attachment(failList,tableName);
                FieldCount fieldCount = (FieldCount) map.get("fieldcount");
                TestReport testReport = (TestReport) map.get("TestReport");
                //详细字段测试结果
                Map<String, Integer> networkCount = (Map<String, Integer>) map.get("networkCount");
                Map<String, Integer> isReturnCount = (Map<String, Integer>) map.get("isReturnCount");
                StringBuffer body = MailUtil.html_Subject(fieldCount, appname, testReport, isReturnCount, networkCount);
                testresult.setPasspercent(testReport.getSuccessPercent());
                testresult.setTestnum(testReport.getTestcaseNum());
                testresult.setTestdetail(body.toString());
                double pass_perDouble = Double.parseDouble(testResultService.getPassPercent(database_tableName).split("%")[0]);
                if (testReport.getSuccessPercent() >= pass_perDouble)
                {
                    testresult.setIspass(1);
                    passLog.add(logName_cn[cn_index]);
                    passSum++;
                }
                else
                {
                    testresult.setIspass(0);
                    failLog.add(logName_cn[cn_index]);
                    failSum++;
                }
                testresult.setIsdelete(0);
                testresult.setFilepath(filePath);
                Date now = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String nowTime = dateFormat.format(now);
                testresult.setCreatetime(nowTime);
                testResultService.addcase(testresult);
                //########################添加回归任务#########################
                //将测试表名经过关联后得到正式库表名
                Map<String,Object> hashmap = new HashMap<>();
                if (IRollbackTaskService.getIsAppTypeId(database_tableName) == 1)
                {
                    hashmap.put("appTypeId",appname);
                }
                else
                {
                    hashmap.put("appTypeId",null);
                }
                hashmap.put("tableName",database_tableName);
                String onlineTableName = IRollbackTaskService.getTableName(hashmap);
                rollbackTask.setTableName(onlineTableName);
                rollbackTask.setAppTypeId(appname);
                rollbackTask.setOs(os);
                rollbackTask.setAppVersion(appversion);
                //查询是否存在任务
                if ((task = IRollbackTaskService.checkTaskIsExists(rollbackTask)) == null)
                {
                    log.info("添加新的回归任务");
                    rollbackTask.setAppTypeId(appname);
                    rollbackTask.setTableName(onlineTableName);
                    rollbackTask.setOs(os);
                    rollbackTask.setAppVersion(appversion);
                    rollbackTask.setIsRun(0);
                    rollbackTask.setTestNum(10000);
                    rollbackTask.setCreateTime(nowTime);
                    IRollbackTaskService.addRollbackTask(rollbackTask);
                }
                else
                {
                    //修改了回归任务的执行天数，有重复的任务就置零
                    log.info("回归任务已存在，不添加");
                    Date updateTaskNow = new Date();
                    String updateTaskTime = dateFormat.format(updateTaskNow);
                    task.setUpdateTime(updateTaskTime);
                    IRollbackTaskService.updateTask(task);
                }
                cn_index++;
            }
            //所有测试执行完发送邮件通知
            mailNotice.setAppTypeId(appname);
            mailNotice.setLogSum(logname.split(",").length);
            mailNotice.setPassSum(passSum);
            mailNotice.setFailSum(failSum);
            mailNotice.setPassLog(passLog);
            mailNotice.setFailLog(failLog);
            mailNotice.setErrorLogName(errorLogName);
            mailNotice.setReason(reason);
            StringBuffer content = MailUtil.notice(mailNotice);
            String cc = "wangminghui@021.com,yinye@021.com",to,title;
            if (errorCode.contains(MailNotice.TIMEOUT) || errorCode.contains(MailNotice.NO_RESPONSE))
            {
                cc += ",wanghongwei@021.com";
            }
            if ((to = configService.getEmailByOwner(owner)) != null) {
                title = "日志测试结果";
            } else {
                to = "yinye@021.com";
                title = "日志测试结果（无法发送给测试人员，无" + owner + "对应的邮箱地址）";
            }
            MailUtil.sendMail(to,title,content.toString(),cc,null);
        }
    }

    //定义的异常类型
    public String exceptionReason(String status){
        String msg;
        switch (status) {
            case MailNotice.NO_DATA:
                msg = "数据接口无数据，请联系对应开发核对是否有上报数据";
                break;
            case MailNotice.NO_RESPONSE:
                msg = "数据接口无响应，请钉钉联系王明辉";
                break;
            case MailNotice.TIMEOUT:
                msg = "数据接口请求超时，请钉钉联系王明辉";
                break;
            default:
                msg = "请稍后重试";
                break;
        }
        return msg;
    }

}
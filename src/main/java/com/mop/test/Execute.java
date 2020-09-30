package com.mop.test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.mop.service.IConfigService;
import com.mop.entity.FieldCount;
import com.mop.entity.TestReport;
import com.mop.entity.Testcase2;
import com.mop.dao.BaseDao;
import com.mop.service.Base;
import com.mop.utils.ConfigUtil;
import org.apache.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

public class Execute {

    private final static Logger log = Logger.getLogger(Execute.class);
    public JSONObject isNullJson;

    private URLClassLoader classLoader;

    public Execute(IConfigService configService,String tableName) {
        String json = ConfigUtil.getIsNullConfig(configService,tableName);
        isNullJson = JSONObject.parseObject(json);
        try {
            classLoader = URLClassLoader.newInstance(new URL[]{
                    new URL("file:/data/LogRule-1.0-SNAPSHOT.jar")
            });
        }catch (MalformedURLException e){
            log.info("规则jar包未找到");
            e.printStackTrace();
        }
    }

    public Map<String,Object> params = null;

    public final Base base = new BaseDao();

    int one = 0;
    String[] tablehead;
    int[] successCount;
    int[] failCount;
    int[] nullCount;
    double[] successPercent;
    double[] failPercent;
    double[] nullPercent;

    //isReturn各值统计，部分日志用不到
    Map<String,Integer> isReturnList = null;
    //network各值统计，公共参数很多日志都有
    Map<String,Integer> networkList = null;

    /**
     * 处理数据
     * @param data 数据
     * @param tableName 表名
     * @return Map
     */
    public Map<String, Object> execute(String data, String tableName)
    {
        one = 0;
        params = new HashMap<>();
        networkList = new HashMap<>();
        JSONArray array = JSONArray.parseArray(data);
        switch (tableName)
        {
            case "log_test_app_shareinstall":
            case "log_test_app_install":
            case "log_test_app_open":
            case "log_app_shareinstall":
            case "log_app_install":
            case "log_app_open":
                isReturnList = new HashMap<>();
                break;
            default:
                isReturnList = null;
        }
        //字段数量
        int length = ((JSONObject)((JSONObject)array.get(1)).get("a")).size();
        tablehead = new String[length];
        successCount = new int[length];
        failCount = new int[length];
        nullCount = new int[length];
        successPercent = new double[length];
        failPercent = new double[length];
        nullPercent = new double[length];
        FieldCount fc = new FieldCount();
        TestReport testReport = new TestReport();
        Testcase2 testcase2 = new Testcase2();
        Map<String ,Object> map = new HashMap<>();
        List<Testcase2> failList = new ArrayList<>();
        //这里减1是因为有表头存在
        int testcasenum = array.size() - 1;
        JSONObject obj = null;
        int count,success = 0,fail = 0;
        for(int arrayIndex = 1,num = array.size();arrayIndex < num;arrayIndex++)
        {
            count = 0;
            obj = (JSONObject) ((JSONObject)array.get(arrayIndex)).get("a");
            testcase2 = this.calculate(obj,tableName);
            if (testcase2 == null)
            {
                return null;
            }
            count = testcase2.getSuccessCount();
            if(count == length)
            {
                success++;
            }
            else if(count < length)
            {
                failList.add(testcase2);
                fail++;
            }
        }
        log.info("用例数：" + testcasenum + "\t字段数量：" + length);
        log.info("通过：" + success);
        log.info("失败：" + fail);
        log.info("通过率：" + base.countPercent(success, testcasenum));
        log.info("失败率：" + base.countPercent(fail, testcasenum));
        testReport.setTestcaseNum(testcasenum);
        testReport.setSuccessCount(success);
        testReport.setFailCount(fail);
        testReport.setSuccessPercent(base.countPercent(success, testcasenum));
        testReport.setFailPercent(base.countPercent(fail,testcasenum));
        for(int i = 0;i < length;i++)
        {
            successPercent[i] = (double)successCount[i] / testcasenum * 100;
            failPercent[i] = (double)failCount[i] / testcasenum * 100;
            nullPercent[i] = (double)nullCount[i] / testcasenum * 100;
        }
        fc.setTablehead(tablehead);
        fc.setSuccessCount(successCount);
        fc.setFailCount(failCount);
        fc.setNullCount(nullCount);
        fc.setSuccessPercent(successPercent);
        fc.setFailPercent(failPercent);
        fc.setNullPercent(nullPercent);
        //测试报告主要内容
        map.put("TestReport",testReport);
        //失败字段(生成失败附件)
        map.put("failList",failList);
        //每个字段的统计
        map.put("fieldcount", fc);
        //isreturn各值分布情况
        map.put("isReturnCount", isReturnList);
        //network各值分布情况
        map.put("networkCount",networkList);
        return map;
    }

    /**
     * 计算每个字段的情况
     * @param obj 一条日志
     * @param tableName 表名
     * @return Testcase2
     */
    public Testcase2 calculate(JSONObject obj,String tableName)
    {
        //校验字段
        Testcase2 testcase2 = new Testcase2();
        int count = 0,fail = 0;
        ArrayList<String> failColumn = new ArrayList<>();
        int countIndex = 0;
        //将整个json解析后把key、value参数装载
        params.put("tableName",tableName);
        for (Map.Entry<String,Object> mapEntry : obj.entrySet())
        {
            params.put(mapEntry.getKey(),mapEntry.getValue() + "");
        }
        for(Map.Entry<String , Object> entry : obj.entrySet())
        {
            params.put("key",entry.getKey());
            params.put("value",entry.getValue() + "");
            params.put("isNull",isNullJson.get(entry.getKey()));
            tablehead[countIndex] = entry.getKey();
            //统计isreturn分布情况
            if (entry.getKey().equals("isreturn"))
            {
                try {
                    isReturnList.put(entry.getValue().toString(), isReturnList.get(entry.getValue().toString()) + 1);
                }catch (NullPointerException e){
                    //出现null或者其他奇怪的东西？
                    isReturnList.put(entry.getValue().toString(), 1);
                }
            }
            //network统计
            try {
                if (entry.getKey().equals("network"))
                {
                    networkList.put(entry.getValue().toString(), networkList.get(entry.getValue().toString()) + 1);
                }
            }catch (NullPointerException e){
                networkList.put(entry.getValue().toString(), 1);
            }
            //null统计
            try{
                if(entry.getValue().equals("null"))
                {
                    nullCount[countIndex] += 1;
                }
            }catch(NullPointerException e){
                nullCount[countIndex] += 1;
            }
            //加载外部jar包
            try {
                Class<?> clazz = classLoader.loadClass("com.Match");
                Object o = clazz.newInstance();
                Method method = o.getClass().getMethod("test",Map.class);
                Object result = method.invoke(o,params);
                fail = (int) result;
            }catch (ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e){
                e.printStackTrace();
            }
            //fail = this.match(params);
            //fail=1\0:都是需要校验的字段，2:是下面寻找方法没有这个key的校验方法;作用是为了同步字段个数与校验次数,3:没有找到这个表\日志的检测规则(一般不会为3)
            if(fail == 1 || fail == 0)
            {
                successCount[countIndex] += fail;
                count += fail;
                if(fail == 0)
                {
                    failCount[countIndex] += 1;
                    failColumn.add(entry.getKey());
                    //直接写入带有null的value会导致key和value在完整的json中不显示，需要序列化如下
                    testcase2.setFullCase(JSONObject.toJSONString(obj,SerializerFeature.WriteMapNullValue));
                }
            }
            else if(fail == 2)
            {
                successCount[countIndex] += 1;
                count += 1;
            }
            else if (fail == 3)
            {
                return null;
            }
            countIndex++;
        }
        one++;
        testcase2.setSuccessCount(count);
        testcase2.setFailColumn(failColumn);
        return testcase2;
    }

}
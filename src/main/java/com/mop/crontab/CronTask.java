package com.mop.crontab;

import com.mop.async.AsyncConfiguration;
import com.mop.entity.*;
import com.mop.service.IConfigService;
import com.mop.service.IMonitorService;
import com.mop.service.IRollbackTaskService;
import com.mop.service.ITestresultService;
import com.mop.test.Execute;
import com.mop.utils.FileUtil;
import com.mop.utils.HttpUtil;
import com.mop.utils.MailUtil;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@SuppressWarnings("unchecked")
public class CronTask {

    private final static Logger log = Logger.getLogger(CronTask.class);

    @Resource
    private IRollbackTaskService IRollbackTaskService;

    @Resource
    private ITestresultService testResultService;

    @Resource
    private IMonitorService monitorService;

    @Resource
    private IConfigService configService;

    @Resource
    private AsyncConfiguration asyncConfiguration;

    @Scheduled(cron = "0 0 12 * * ?")
    private void cronTask() {
        Executor cronTaskExecutor = asyncConfiguration.doCronTaskExecutor();
        cronTaskExecutor.execute(() -> {
            Testresult testresult = new Testresult();
            ArrayList<RollbackTask> rollbackList = null;
            String database = null,tableName = null,appTypeId = null,os = null,appVersion = null,data = null;
            int testNum = 0;
            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            String today = dateFormat.format(date);
            rollbackList = IRollbackTaskService.getNotRunTask();
            log.info("回归队列数量：" + rollbackList.size());
            Execute exec;
            if (!rollbackList.isEmpty())
            {
                log.info("开始回归测试！");
                for (RollbackTask rollbackCase : rollbackList)
                {
                    try {
                        database = rollbackCase.getTableName().split("\\.")[0];
                        tableName = rollbackCase.getTableName().split("\\.")[1];
                    }catch (NullPointerException e){
                        log.info("回归任务表中有null的表名");
                        continue;
                    }
                    exec = new Execute(configService,tableName);
                    testNum = rollbackCase.getTestNum();
                    appTypeId = rollbackCase.getAppTypeId();
                    os = rollbackCase.getOs();
                    appVersion = rollbackCase.getAppVersion();
                    //基本数据
                    testresult.setLogname(database + "." + tableName);
                    testresult.setAppname(appTypeId);
                    testresult.setOs(os);
                    testresult.setAppversion(appVersion);
                    testresult.setDateline(today);
                    data = HttpUtil.getData_Prod(database,tableName,testNum,today,today,os,appTypeId,appVersion);
                    if (data.length() == 3)
                    {
                        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String noData_today = dateFormat.format(date);
                        Testresult noData = new Testresult(appTypeId,database + "." + tableName,os,appVersion,0,today,0.0,0,null,null,0,noData_today);
                        testResultService.addRollBackTestResult(noData);
                        log.info("正式表无测试数据");
                        continue;
                    }
                    Map<String,Object> map = exec.execute(data,tableName);
                    if (map == null)
                    {
                        log.info("找不到日志表");
                        continue;
                    }
                    //生成失败附件
                    List<Testcase2> failList = (List<Testcase2>) map.get("failList");
                    String filePath = FileUtil.failedCase_attachment(failList,tableName);
                    FieldCount fieldCount = (FieldCount) map.get("fieldcount");
                    TestReport testReport = (TestReport) map.get("TestReport");
                    //详细字段测试结果
                    Map<String, Integer> networkCount = (Map<String, Integer>) map.get("networkCount");
                    Map<String, Integer> isReturnCount = (Map<String, Integer>) map.get("isReturnCount");
                    StringBuffer body = MailUtil.html_Subject(fieldCount, appTypeId, testReport, isReturnCount, networkCount);
                    testresult.setPasspercent(testReport.getSuccessPercent());
                    testresult.setTestnum(testReport.getTestcaseNum());
                    testresult.setTestdetail(body.toString());
                    testresult.setFilepath(filePath);
                    double passPercent = getPassPercent(rollbackCase.getTableName(),appTypeId);
                    if (testReport.getSuccessPercent() >= passPercent) {
                        testresult.setIspass(1);
                    } else {
                        testresult.setIspass(0);
                    }
                    Date now = new Date();
                    SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String nowTime = dateFormat1.format(now);
                    testresult.setCreatetime(nowTime);
                    //改变任务的执行状态
                    RollbackTask rollbackTask = new RollbackTask();
                    rollbackTask.setAppTypeId(appTypeId);
                    rollbackTask.setOs(os);
                    rollbackTask.setAppVersion(appVersion);
                    rollbackTask.setTableName(database + "." + tableName);
                    rollbackTask.setUpdateTime(nowTime);
                    //查看任务执行了多少次；正常1天1次
                    int rollbackDay = IRollbackTaskService.getTaskRollbackDay(rollbackTask) + 1;
                    if (rollbackDay == 3)
                    {
                        rollbackTask.setIsRun(1);
                    }
                    else
                    {
                        rollbackTask.setIsRun(null);
                    }
                    IRollbackTaskService.updateTaskToIsRun(rollbackTask);
                    //新增回归记录
                    testresult.setRollbackDay(rollbackDay);
                    testResultService.addRollBackTestResult(testresult);
                }
            }
            else
            {
                log.info("没有回归任务");
            }
        });
    }

    @Scheduled(cron = "0 0 12 * * ?")
    public void dailyTest() {
        Executor cronTaskExecutor = asyncConfiguration.doCronTaskExecutor();
        cronTaskExecutor.execute(() -> {
            MonitorResult monitorResult;
            //取出所有监控任务
            ArrayList<MonitorTask> monitorTasks = monitorService.getTask();
            log.info("监控队列：" + monitorTasks.size());
            String database,tableName,appTypeId,appVersion,data,today,undone_createTime,done_createTime;
            int testNum;
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            today = format.format(new Date());
            Execute execute;
            for (MonitorTask monitorTask : monitorTasks)
            {
                database = monitorTask.getTableName().split("\\.")[0];
                tableName = monitorTask.getTableName().split("\\.")[1];
                appTypeId = monitorTask.getAppTypeId();
                testNum = monitorTask.getTestNum();
                execute = new Execute(configService,tableName);
                data = HttpUtil.getData_Prod(database,tableName,testNum,today,today,"",appTypeId,"");
                double passPercent = getPassPercent(monitorTask.getTableName(),appTypeId);
                if (data.length() == 3)
                {
                    format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    undone_createTime = format.format(new Date());
                    monitorResult = new MonitorResult(monitorTask.getTableName(),appTypeId,0,0,0,0,0,passPercent,0,"null","null",today,undone_createTime);
                    monitorService.addResult(monitorResult);
                    continue;
                }
                Map<String,Object> map = execute.execute(data,tableName);
                if (map == null)
                {
                    log.info("找不到日志表");
                    continue;
                }
                //生成失败附件
                List<Testcase2> failList = (List<Testcase2>) map.get("failList");
                String filePath = FileUtil.failedCase_attachment(failList,tableName);
                FieldCount fieldCount = (FieldCount) map.get("fieldcount");
                TestReport testReport = (TestReport) map.get("TestReport");
                //详细字段测试结果
                Map<String, Integer> networkCount = (Map<String, Integer>) map.get("networkCount");
                Map<String, Integer> isReturnCount = (Map<String, Integer>) map.get("isReturnCount");
                StringBuffer body = MailUtil.html_Subject(fieldCount, appTypeId, testReport, isReturnCount, networkCount);
                monitorResult = new MonitorResult();
                monitorResult.setLogName(monitorTask.getTableName());
                monitorResult.setAppTypeId(appTypeId);
                monitorResult.setTestCount(testReport.getTestcaseNum());
                monitorResult.setPassCount(testReport.getSuccessCount());
                monitorResult.setPassPercent(testReport.getSuccessPercent());
                monitorResult.setFailedCount(testReport.getFailCount());
                monitorResult.setFailedPercent(testReport.getFailPercent());
                monitorResult.setIsPassPercent(passPercent);
                if (testReport.getSuccessPercent() >= passPercent)
                {
                    monitorResult.setIsError(1);
                }
                else
                {
                    monitorResult.setIsError(0);
                }
                monitorResult.setTestResult_detail(body.toString());
                monitorResult.setFailedCase_path(filePath);
                monitorResult.setTestData_time(today);
                format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                done_createTime = format.format(new Date());
                monitorResult.setCreateTime(done_createTime);
                monitorService.addResult(monitorResult);
                data = null;
            }
        });
    }


    /**
     * 根据线上表查询到测试表，再通过测试表查询到日志阈值（临时方法）
     * @param tableName
     *      表名
     * @param appTypeId
     *      appTypeId
     * @return
     *      double
     */
    public double getPassPercent(String tableName,String appTypeId){
        Map<String,Object> hashmap = new HashMap<>();
        hashmap.put("tableName",tableName);
        if (configService.isNeedAppTypeId(tableName) == 1)
        {
            hashmap.put("appTypeId",appTypeId);
        }
        else
        {
            hashmap.put("appTypeId",null);
        }
        String testTableName = configService.getTestTableName(hashmap);
        return Double.parseDouble(testResultService.getPassPercent(testTableName).split("%")[0]);
    }
}

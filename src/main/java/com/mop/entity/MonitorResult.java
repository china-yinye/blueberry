package com.mop.entity;

public class MonitorResult {

    private int id;
    private String logName;
    private String appTypeId;
    private int testCount;
    private int passCount;
    private double passPercent;
    private int failedCount;
    private double failedPercent;
    private double isPassPercent;
    private int isError;
    private String testResult_detail;
    private String failedCase_path;
    private String testData_time;
    private String createTime;
    private String updateTime;
    private int isDelete;

    public MonitorResult(){}

    public MonitorResult(String logName,String appTypeId,int testCount,int passCount,double passPercent,int failedCount,double failedPercent,double isPassPercent,
                  int isError,String testResult_detail,String failedCase_path,String testData_time,String createTime){
        this.logName = logName;
        this.appTypeId = appTypeId;
        this.testCount = testCount;
        this.passCount = passCount;
        this.passPercent = passPercent;
        this.failedCount = failedCount;
        this.isPassPercent = isPassPercent;
        this.isError = isError;
        this.testResult_detail = testResult_detail;
        this.failedCase_path = failedCase_path;
        this.testData_time = testData_time;
        this.createTime = createTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogName() {
        return logName;
    }

    public void setLogName(String logName) {
        this.logName = logName;
    }

    public String getAppTypeId() {
        return appTypeId;
    }

    public void setAppTypeId(String appTypeId) {
        this.appTypeId = appTypeId;
    }

    public int getTestCount() {
        return testCount;
    }

    public void setTestCount(int testCount) {
        this.testCount = testCount;
    }

    public int getPassCount() {
        return passCount;
    }

    public void setPassCount(int passCount) {
        this.passCount = passCount;
    }

    public double getPassPercent() {
        return passPercent;
    }

    public void setPassPercent(double passPercent) {
        this.passPercent = passPercent;
    }

    public int getFailedCount() {
        return failedCount;
    }

    public void setFailedCount(int failedCount) {
        this.failedCount = failedCount;
    }

    public double getFailedPercent() {
        return failedPercent;
    }

    public void setFailedPercent(double failedPercent) {
        this.failedPercent = failedPercent;
    }

    public double getIsPassPercent() {
        return isPassPercent;
    }

    public void setIsPassPercent(double isPassPercent) {
        this.isPassPercent = isPassPercent;
    }

    public int getIsError() {
        return isError;
    }

    public void setIsError(int isError) {
        this.isError = isError;
    }

    public String getTestResult_detail() {
        return testResult_detail;
    }

    public void setTestResult_detail(String testResult_detail) {
        this.testResult_detail = testResult_detail;
    }

    public String getFailedCase_path() {
        return failedCase_path;
    }

    public void setFailedCase_path(String failedCase_path) {
        this.failedCase_path = failedCase_path;
    }

    public String getTestData_time() {
        return testData_time;
    }

    public void setTestData_time(String testData_time) {
        this.testData_time = testData_time;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public int getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(int isDelete) {
        this.isDelete = isDelete;
    }

}

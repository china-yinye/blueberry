package com.mop.entity;

public class RollbackTask {

    private Integer id;
    private String appTypeId;
    private String tableName;
    private String os;
    private String appVersion;
    private Integer testNum;
    private String createTime;
    private String updateTime;
    private Integer isRun;
    private Integer rollbackDay;

    public Integer getRollbackDay() {
        return rollbackDay;
    }

    public void setRollbackDay(Integer rollbackDay) {
        this.rollbackDay = rollbackDay;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAppTypeId() {
        return appTypeId;
    }

    public void setAppTypeId(String appTypeId) {
        this.appTypeId = appTypeId;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public Integer getTestNum() {
        return testNum;
    }

    public void setTestNum(Integer testNum) {
        this.testNum = testNum;
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

    public Integer getIsRun() {
        return isRun;
    }

    public void setIsRun(Integer isRun) {
        this.isRun = isRun;
    }
}

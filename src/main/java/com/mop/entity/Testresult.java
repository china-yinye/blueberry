package com.mop.entity;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "testresult")
public class Testresult {

    public Testresult() {}

    public Testresult(long id, String logname, String os, String appname, String appversion, long testnum, String dateline,
                      double passpercent, long ispass, String testdetail , String filepath, long isdelete, String createtime, String updatetime) {
        this.tid = id;
        this.os= os;
        this.logname = logname;
        this.appname = appname;
        this.appversion = appversion;
        this.testnum = testnum;
        this.dateline = dateline;
        this.passpercent = passpercent;
        this.ispass = ispass;
        this.filepath = filepath;
        this.isdelete = isdelete;
        this.createtime = createtime;
        this.updatetime = updatetime;
        this.testdetail = testdetail;
    }

    public Testresult(String appTypeId,String tableName, String os,String appVersion, int testNum,String testDate, double passPercent,int isPass,
                      String testDetail,String filePath, int rollbackDay,String createTime){
        this.appname = appTypeId;
        this.logname = tableName;
        this.os = os;
        this.appversion = appVersion;
        this.testnum = testNum;
        this.dateline = testDate;
        this.passpercent = passPercent;
        this.ispass = isPass;
        this.testdetail = testDetail;
        this.filepath = filePath;
        this.rollbackDay = rollbackDay;
        this.createtime = createTime;
    }


    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public long getTid() {
        return tid;
    }

    public void setTid(long tid) {
        this.tid = tid;
    }

    public String getLogname() {
        return logname;
    }

    public void setLogname(String logname) {
        this.logname = logname;
    }

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public String getAppversion() {
        return appversion;
    }

    public void setAppversion(String appversion) {
        this.appversion = appversion;
    }

    public long getTestnum() {
        return testnum;
    }

    public void setTestnum(long testnum) {
        this.testnum = testnum;
    }

    public String getDateline() {
        return dateline;
    }

    public void setDateline(String dateline) {
        this.dateline = dateline;
    }

    public double getPasspercent() {
        return passpercent;
    }

    public void setPasspercent(double passpercent) {
        this.passpercent = passpercent;
    }

    public long getIspass() {
        return ispass;
    }

    public void setIspass(long ispass) {
        this.ispass = ispass;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public long getIsdelete() {
        return isdelete;
    }

    public void setIsdelete(long isdelete) {
        this.isdelete = isdelete;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    private String owner;
    private String os;
    private long tid;
    private String logname;
    private String appname;
    private String appversion;
    private long testnum;
    private String dateline;
    private double passpercent;
    private long ispass;
    private String filepath;
    private long isdelete;
    private String createtime;
    private String updatetime;
    private String testdetail;
    private int rollbackDay;

    public int getRollbackDay() {
        return rollbackDay;
    }

    public void setRollbackDay(int rollbackDay) {
        this.rollbackDay = rollbackDay;
    }

    public String getTestdetail() {
        return testdetail;
    }

    public void setTestdetail(String testdetail) {
        this.testdetail = testdetail;
    }



}

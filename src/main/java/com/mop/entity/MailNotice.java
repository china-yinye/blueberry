package com.mop.entity;

import java.util.ArrayList;

public class MailNotice {

    public static final String NO_DATA = "100";
    public static final String NO_RESPONSE = "200";
    public static final String TIMEOUT = "300";
    public static final String ERROR = "400";

    private String appTypeId;
    private int logSum;
    private int passSum;
    private int failSum;
    private ArrayList<String> passLog;
    private ArrayList<String> failLog;
    private ArrayList<String> errorLogName;
    private ArrayList<String> reason;

    public String getAppTypeId() {
        return appTypeId;
    }

    public void setAppTypeId(String appTypeId) {
        this.appTypeId = appTypeId;
    }

    public ArrayList<String> getPassLog() {
        return passLog;
    }

    public void setPassLog(ArrayList<String> passLog) {
        this.passLog = passLog;
    }

    public ArrayList<String> getFailLog() {
        return failLog;
    }

    public void setFailLog(ArrayList<String> failLog) {
        this.failLog = failLog;
    }

    public ArrayList<String> getErrorLogName() {
        return errorLogName;
    }

    public void setErrorLogName(ArrayList<String> errorLogName) {
        this.errorLogName = errorLogName;
    }

    public ArrayList<String> getReason() {
        return reason;
    }

    public void setReason(ArrayList<String> reason) {
        this.reason = reason;
    }

    public int getLogSum() {
        return logSum;
    }

    public void setLogSum(int logSum) {
        this.logSum = logSum;
    }

    public int getPassSum() {
        return passSum;
    }

    public void setPassSum(int passSum) {
        this.passSum = passSum;
    }

    public int getFailSum() {
        return failSum;
    }

    public void setFailSum(int failSum) {
        this.failSum = failSum;
    }

}

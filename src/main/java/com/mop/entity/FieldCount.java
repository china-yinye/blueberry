package com.mop.entity;

public class FieldCount {

    private String[] tablehead;
    private int[] successCount;
    private int[] failCount;
    private int[] nullCount;
    private double[] successPercent;
    private double[] failPercent;
    private double[] nullPercent;


    public String[] getTablehead() {
        return tablehead;
    }
    public void setTablehead(String[] tablehead) {
        this.tablehead = tablehead;
    }
    public int[] getSuccessCount() {
        return successCount;
    }
    public void setSuccessCount(int[] successCount) {
        this.successCount = successCount;
    }
    public int[] getFailCount() {
        return failCount;
    }
    public void setFailCount(int[] failCount) {
        this.failCount = failCount;
    }
    public int[] getNullCount() {
        return nullCount;
    }
    public void setNullCount(int[] nullCount) {
        this.nullCount = nullCount;
    }
    public double[] getSuccessPercent() {
        return successPercent;
    }
    public void setSuccessPercent(double[] successPercent) {
        this.successPercent = successPercent;
    }
    public double[] getFailPercent() {
        return failPercent;
    }
    public void setFailPercent(double[] failPercent) {
        this.failPercent = failPercent;
    }
    public double[] getNullPercent() {
        return nullPercent;
    }
    public void setNullPercent(double[] nullPercent) {
        this.nullPercent = nullPercent;
    }





}

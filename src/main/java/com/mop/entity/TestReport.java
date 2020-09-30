package com.mop.entity;

public class TestReport {
	
	private int testcaseNum;
	private int successCount;
	private int failCount;
	private double successPercent;
	private double failPercent;
	
	public int getTestcaseNum() {
		return testcaseNum;
	}
	public void setTestcaseNum(int testcaseNum) {
		this.testcaseNum = testcaseNum;
	}
	public int getSuccessCount() {
		return successCount;
	}
	public void setSuccessCount(int successCount) {
		this.successCount = successCount;
	}
	public int getFailCount() {
		return failCount;
	}
	public void setFailCount(int failCount) {
		this.failCount = failCount;
	}
	public double getSuccessPercent() {
		return successPercent;
	}
	public void setSuccessPercent(double successPercent) {
		this.successPercent = successPercent;
	}
	public double getFailPercent() {
		return failPercent;
	}
	public void setFailPercent(double failPercent) {
		this.failPercent = failPercent;
	}

}

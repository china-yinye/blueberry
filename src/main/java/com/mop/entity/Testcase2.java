package com.mop.entity;

import java.util.ArrayList;

public class Testcase2 {
	
	private String fullCase;
	private ArrayList<String> failColumn;
	private int successCount;

	public Testcase2(){}

	public Testcase2(String fullCase)
	{
		this.fullCase = fullCase;
	}

	public String getFullCase() {
		return fullCase;
	}
	public void setFullCase(String fullCase) {
		this.fullCase = fullCase;
	}
	public ArrayList<String> getFailColumn() {
		return failColumn;
	}
	public void setFailColumn(ArrayList<String> failColumn) {
		this.failColumn = failColumn;
	}
	public int getSuccessCount() {
		return successCount;
	}
	public void setSuccessCount(int successCount) {
		this.successCount = successCount;
	}

}

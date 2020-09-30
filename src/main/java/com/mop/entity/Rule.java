package com.mop.entity;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "rule")
public class Rule {

    private Integer rid;
    private String tableName;
    private String ruleJson;
    private String createTime;
    private String updateTime;

    public Rule(){}

    Rule(String tableName, String ruleJson, String createTime, String updateTime){
        this.tableName = tableName;
        this.ruleJson = ruleJson;
        this.createTime = createTime;
        this.updateTime = updateTime;
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

    public Integer getRid() {
        return rid;
    }

    public void setRid(Integer rid) {
        this.rid = rid;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getRuleJson() {
        return ruleJson;
    }

    public void setRuleJson(String ruleJson) {
        this.ruleJson = ruleJson;
    }
}

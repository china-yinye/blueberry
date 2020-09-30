package com.mop.service;

public interface Base {

    boolean isDate(String val);

    /**
     * 常用检测
     * @param val
     * @param isNull
     * @return
     */
    public boolean nullCheck(String val,int isNull);

    /**
     * 判断是不是整数
     * @param val
     * @return
     */
    public boolean isNumber(String val);

    /**
     * 判断是不是浮点数
     * @param val
     * @return
     */
    public boolean isDouble(String val);

    /**
     * 判断是不是长整型数
     * @param val
     * @return
     */
    public boolean isLong(String val);

    /**
     * 判断字符串是不是json格式
     * @param val
     * @return
     */
    public boolean isJson(String val);

    /**
     * 判断字符串是不是json数组格式
     * @param val
     * @return
     */
    public boolean isJsonArray(String val);

    /**
     * 判断是不是ip地址
     * @param val
     * @return
     */
    public boolean isIP(String val);

    /**
     * 计算占比
     * @param num
     * @param num1
     * @return
     */
    public double countPercent(double num, double num1);

    /**
     * 判断是否为NULL、null、""
     * @param val
     * @return
     */
    public boolean NULL(String val);

    public boolean NULLOrElse(String val);

}

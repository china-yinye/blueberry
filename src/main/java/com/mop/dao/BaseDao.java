package com.mop.dao;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.mop.service.Base;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class BaseDao implements Base {

    @Override
    public boolean isDate(String val) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            simpleDateFormat.parse(val);
            return true;
        }catch (ParseException e){
            return false;
        }
    }

    @Override
    public boolean nullCheck(String val,int isNull)
    {
        if( isNull - 1 == 0)
        {
            return val != null && !val.equals("null") && !val.equals("") && !val.equals("NULL");
        }
        else
        {
            return val.equals("NULL") || val.equals("null") || val.length() > 0 || val.equals("") ;
        }
    }

    @Override
    public boolean isNumber(String val) {
        try{
            Integer.parseInt(val);
            return true;
        }catch(NumberFormatException e){
            return false;
        }
    }

    @Override
    public boolean isDouble(String val) {
        try{
            Double.parseDouble(val);
            return true;
        }catch(NumberFormatException e){
            return false;
        }
    }

    @Override
    public boolean isLong(String val) {
        try{
            Long.parseLong(val);
            return true;
        }catch(NumberFormatException e){
            return false;
        }
    }

    @Override
    public boolean isJson(String val) {
        try{
            JSONObject obj = JSONObject.parseObject(val);
            if(obj.size() > 0)
            {
                return true;
            }
            else return obj.toString().equals("{}");
        }catch(JSONException e){
            return false;
        }
    }

    @Override
    public boolean isJsonArray(String val) {
        try {
            JSONArray array = JSONArray.parseArray(val);
            if(array.size() > 0)
            {
                return true;
            }
            return false;
        } catch (JSONException e) {
            return false;
        }
    }

    @Override
    public boolean isIP(String val) {
        if(val.split("\\.").length == 4)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public double countPercent(double num, double num1) {
        double percent = num / num1;
        double per = Double.parseDouble(String.format("%.2f", percent * 100));
        return per;
    }

    @Override
    public boolean NULL(String val) {
        return val.equals("null") || val.equals("") || val.equals("NULL");
    }

    @Override
    public boolean NULLOrElse(String val) {
        return val.equals("null") || val.equals("") || val.equals("NULL") || val.length() > 0;
    }
}

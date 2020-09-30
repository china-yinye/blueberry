package com.mop.utils;

import com.mop.service.IConfigService;
import org.apache.log4j.Logger;

import java.util.Map;

public class ConfigUtil {

    private static final Logger log = Logger.getLogger(ConfigUtil.class);

    //读取是否为空配置
    public static String getIsNullConfig(IConfigService iConfigService, String tableName){
        try {
            Map<String,Object> jsonMap = iConfigService.getIsNullConfig(tableName);
            return jsonMap.get("json").toString();
        }catch (NullPointerException e){
            log.info(tableName + "非空配置不存在");
            return null;
        }
    }
}

package com.mop.service;

import com.mop.dao.ConfigDao;
import com.mop.entity.Config;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

@Service
public class IConfigService {

    @Resource
    private ConfigDao configDao;

    public Config getConfig(String configName){
        return configDao.getConfig(configName);
    }

    public Map<String,Object> getIsNullConfig(String tableName){
        return configDao.getIsNullConfig(tableName);
    }

    public String getEmailByOwner(String owner){
        return configDao.getEmailByOwner(owner);
    }

    //是否需要传递appTypeId
    public int isNeedAppTypeId(String tableName){
        return configDao.isNeedAppTypeId(tableName);
    }

    //获得测试表名
    public String getTestTableName(Map<String,Object> hashmap){
        return configDao.getTestTableName(hashmap);
    }
}

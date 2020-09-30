package com.mop.dao;

import com.mop.entity.Config;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface ConfigDao {

    Config getConfig(String configName);

    Map<String,Object> getIsNullConfig(String tableName);

    String getEmailByOwner(String owner);

    int isNeedAppTypeId(String tableName);

    String getTestTableName(Map<String,Object> hashmap);
}

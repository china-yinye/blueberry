package com.mop.service;

import com.mop.dao.RulesDao;
import com.mop.entity.Rule;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class IRulesService {

    @Resource
    private RulesDao rulesDao;

    public void addRule(Rule rule){
        rulesDao.addRule(rule);
    }

    public Rule getRule(String tableName){
        return rulesDao.getRule(tableName);
    }

    public void updateRule(Rule rule){
        rulesDao.updateRule(rule);
    }

}

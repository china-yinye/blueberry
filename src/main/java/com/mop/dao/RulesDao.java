package com.mop.dao;

import com.mop.entity.Rule;
import org.springframework.stereotype.Repository;

@Repository
public interface RulesDao {

    void addRule(Rule rule);

    Rule getRule(String tableName);

    void updateRule(Rule rule);
}

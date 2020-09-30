package com.mop.dao;

import com.mop.entity.MonitorResult;
import com.mop.entity.MonitorTask;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface MonitorDao {

    ArrayList<MonitorTask> getTask();

    void addResult(MonitorResult monitorResult);
}

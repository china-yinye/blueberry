package com.mop.service;

import com.mop.dao.MonitorDao;
import com.mop.entity.MonitorResult;
import com.mop.entity.MonitorTask;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;

@Service
public class IMonitorService {

    @Resource
    private MonitorDao monitorDao;

    public ArrayList<MonitorTask> getTask(){
        return monitorDao.getTask();
    }

    public void addResult(MonitorResult monitorResult){
        monitorDao.addResult(monitorResult);
    }
}

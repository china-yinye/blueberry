package com.mop.service;

import com.mop.dao.RollbackTaskDao;
import com.mop.entity.RollbackTask;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Map;

@Service
public class IRollbackTaskService {

    @Resource
    private RollbackTaskDao rollbackTaskDao;

    public void addRollbackTask(RollbackTask rollbackTask){
        rollbackTaskDao.addRollbackTask(rollbackTask);
    }

    public ArrayList<RollbackTask> getNotRunTask(){
        return rollbackTaskDao.getNotRunTask();
    }

    public void updateTaskToIsRun (RollbackTask rollbackTask){
        rollbackTaskDao.updateTaskToIsRun(rollbackTask);
    }

    public RollbackTask checkTaskIsExists(RollbackTask rollbackTask){
        return rollbackTaskDao.checkTaskIsExists(rollbackTask);
    }

    public String getOnlineTableName(Map<String,Object> hashmap){
        return rollbackTaskDao.getOnlineTableName(hashmap);
    }

    public void updateTask(RollbackTask rollbackTask){
        rollbackTaskDao.updateTask(rollbackTask);
    }

    public Integer getTaskRollbackDay(RollbackTask rollbackTask){
        return rollbackTaskDao.getTaskRollbackDay(rollbackTask);
    }

    public Integer getIsAppTypeId(String tableName){
        return rollbackTaskDao.getIsAppTypeId(tableName);
    }

    public String getTableName(Map<String,Object> hashmap){
        return rollbackTaskDao.getTableName(hashmap);
    }
}

package com.mop.dao;

import com.mop.entity.RollbackTask;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Map;

@Repository
public interface RollbackTaskDao {

    void addRollbackTask(RollbackTask rollbackTask);

    ArrayList<RollbackTask> getNotRunTask();

    void updateTaskToIsRun(RollbackTask rollbackTask);

    RollbackTask checkTaskIsExists(RollbackTask rollbackTask);

    String getOnlineTableName(Map<String,Object> hashmap);

    void updateTask(RollbackTask rollbackTask);

    Integer getTaskRollbackDay(RollbackTask rollbackTask);

    //是否需要传递appTypeId
    Integer getIsAppTypeId(String tableName);
    //通过上面的接口来判断是否需要传appTypeId
    String getTableName(Map<String,Object> hashmap);

}

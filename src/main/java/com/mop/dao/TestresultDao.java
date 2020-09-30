package com.mop.dao;

import com.mop.entity.Testresult;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestresultDao {

    int addTestresult(Testresult t);

    int addRollBackTestResult(Testresult t);

    List<Testresult> getTestresults();

    Testresult getTestresult(int id);

    String getPassPercent(String db_tb);



}

package com.mop.service;

import com.mop.dao.TestresultDao;
import com.mop.entity.Testresult;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ITestresultService {

    @Resource
    private TestresultDao testresultService;

    public int addcase(Testresult t) {
        return testresultService.addTestresult(t);
    }

    public List<Testresult> getTestresults() {
        return testresultService.getTestresults();
    }

    public Testresult getTestresult(int id) {
        return testresultService.getTestresult(id);
    }

    public TestresultDao getTestresultService() {
        return testresultService;
    }

    public void setTestresultService(TestresultDao testresultService) {
        this.testresultService = testresultService;
    }

    public String getPassPercent(String db_tb){
        return testresultService.getPassPercent(db_tb);
    }

    public int addRollBackTestResult(Testresult t){
        return testresultService.addRollBackTestResult(t);
    }


}

package org.helper.service;

import java.util.List;

import javax.annotation.Resource;

import org.helper.dao.FinancialTestDao;
import org.helper.model.FinancialTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:application-context.xml")
public class FinancialServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(FinancialServiceTest.class);

    @Resource
    FinancialTestDao financialTestDao;

    @Test
    public void getCount() throws Exception {
        Integer integer = financialTestDao.selectCount();
        logger.info("total count:{}", integer);
    }

    @Test
    public void getByPage() throws Exception {
        List<FinancialTest> financialTests = financialTestDao.selectByPage(0, 10);
        for (FinancialTest financialTest : financialTests) {
            logger.info("{}", financialTest);
        }
    }

}
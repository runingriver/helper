package org.helper.dao;

import org.helper.model.DateAndStringTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:application-context.xml")
public class DateAndStringTestDaoTest {
    private static final Logger logger = LoggerFactory.getLogger(DateAndStringTestDaoTest.class);

    @Resource
    DateAndStringTestDao dateAndStringTestDao;

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void selectAll() throws Exception {
        List<DateAndStringTest> dateAndStringTests = dateAndStringTestDao.selectAll();
        logger.info("{}",dateAndStringTests.toString());
    }

    @Test
    public void insertOne() throws Exception {
        DateAndStringTest model = buildModel();
        Integer integer = dateAndStringTestDao.insertOne(model);
        DateAndStringTest result = dateAndStringTestDao.selectOneById(model.getId());
        logger.info("{}", result.toString());
    }

    @Test
    public void insertOne2() throws Exception {
        DateAndStringTest model = buildModel();
        Integer integer = dateAndStringTestDao.insertOne2(model);
        DateAndStringTest result = dateAndStringTestDao.selectOneById(model.getId());
        logger.info("model:{}", result.toString());
    }


    private DateAndStringTest buildModel() {
        DateAndStringTest model = new DateAndStringTest();
        model.setSqlDateToJavaString("2017-02-02 12:12:12");
        model.setSqlDateToJavaDate(new Date());
        model.setSqlTimeStampToJavaString("2017-04-04 11:11:11");
        model.setSqlTimeStampToJavaDate(new Date());
        model.setJavaStringToSqlDate("2017-03-03 10:10:10");
        model.setJavaStringToSqlTimeStamp("2017-03-03 09:09:09");
        model.setSqlStringToJavaDate(new Date());
        return model;
    }

}
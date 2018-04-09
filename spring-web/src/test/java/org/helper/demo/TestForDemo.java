package org.helper.demo;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TestForDemo {
    private static final Logger logger = LoggerFactory.getLogger(TestForDemo.class);

    @Test
    public void testLocalData() {
        String time = "2015-01-02 12:13:02";
        LocalDate parse = LocalDate.parse(time, DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
        logger.info("{}", parse.toString());
    }

    @Test
    public void testSubList() {
        List<String> list = Lists.newArrayList("1", "2", "3", "4", "5", "11", "21", "31", "41", "51", "12", "22", "32",
                "42", "52", "13", "23", "33", "43", "53");
        logger.info("{}:{}", list.size(), list);
        // List<String> tempList1 = list.subList(15, 20);
        // logger.info("{}:{}", tempList1.size(), tempList1);
        for (int i = 0; i < 4; i++) {
            int n = i * 5;
            List<String> tempList = list.subList(n, n + 5);
            logger.info("{}:{}", tempList.size(), tempList);
            logger.info("{}:{}", list.size(), list);
        }
    }

    @Test
    public void testCountDownlatch() {
        ExecutorService service = Executors.newFixedThreadPool(6);
        final CountDownLatch latch = new CountDownLatch(6);
        for (int j = 6; j > 0; j--) {
            final int n = new Random().nextInt(10);
            service.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        logger.info("{},start.....", Thread.currentThread().getName());
                        logger.info("{} do something.....", Thread.currentThread().getName());
                        TimeUnit.SECONDS.sleep(n + 2);
                        latch.countDown();
                        logger.info("{},end.....", Thread.currentThread().getName());
                    } catch (Exception e) {
                        logger.error("---{} error.---", Thread.currentThread().getName(), e);
                    }
                }
            });
        }
        logger.info("enter wait.....");
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        logger.info("finish.....");
    }

    @Test
    public void testMath() {
        int exeCount = 100 / 10 + 100 % 10 == 0 ? 0 : 1;
        int n = 8 >> 1 == 3 + 1 ? 0 : 1;
        logger.info("{}", exeCount);
    }

    @Test
    public void testMath2() {
        int a = 10 % 10;


        int b = 11 / 1000 + (11 % 1000 == 0 ? 0 : 1);

    }

    @Test
    public void booleanTest() {
        boolean a = StringUtils.equals("-", "q") && StringUtils.equals("q", "q");

    }


    @Test
    public void theadTest() {
        for (int i = 0; i < 1000000000; i++) {
            newThread();
        }
    }

    public void newThread() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                logger.info("hello");
            }
        });
        thread.start();
        String name = thread.getName();
        long id = thread.getId();
        logger.info("name:{},id:{}", name, id);
    }


    @Test
    public void testWhiteSpace() {
        String phone = "86 123\t123123";
        String trimPhone = StringUtils.deleteWhitespace(phone);
        logger.info("{}:{}", phone, trimPhone);
    }

    @Test
    public void testUUID() {
        String prefix = RandomStringUtils.random(4, true, true);
        String dateTime = DateTime.now().toString("yyyyMMddHHmmss");
        logger.info("{}-{}", prefix, dateTime);
    }

}

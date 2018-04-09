package org.helper;


import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.primitives.UnsignedInteger;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class UtilTest {
    private static final Logger logger = LoggerFactory.getLogger(UtilTest.class);
    private volatile int count = 0;

    @Test
    public void testVolatile() {

        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                int val1 = 0;
                for (int i = 0; i < 100000000; i++) {
                    val1++;
                    count++;
                }
                logger.info("{} {}", val1, count);
            }
        });

        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                int val2 = 0;
                for (int i = 0; i < 100000000; i++) {
                    count++;
                    val2++;
                }
                logger.info("{},{}", val2, count);
            }
        });

        thread1.start();
        thread2.start();

        while (thread1.isAlive() || thread2.isAlive()) {
        }

        logger.info("{}", count);

    }

    @Test
    public void hashMapTest() {
        HashMap<String, String> map = new HashMap<>();
        map.put("1", "qqq");
        map.put("1", null);
        map.put("", "eee");
        map.put(null, "aaa");
        map.put(null, "bbb");
        logger.info("{}", map.toString());
    }

    @Test
    public void test() {
        List<String> accountList = Lists.newArrayList();
        String configAccounts = "qunar_train";
        String accounts = "qunar_uc_register";
        List<String> defaultAccountList = Splitter.on(",").omitEmptyStrings().trimResults().splitToList(configAccounts);
        List<String> list = Splitter.on(",").omitEmptyStrings().trimResults().splitToList(accounts);
        for (String s : list) {
            if (defaultAccountList.contains(s)) {
                accountList.add(s);
            }
        }

        logger.info("{}", accountList.toString());
    }

    @Test
    public void test2() {
        Random random = new Random(100);
        for (int i = 0; i < 100; i++) {
            int n = Math.abs(random.nextInt()) % 100;
            int a = n % 8;
            int b = n & 7;
            logger.info("n:{},{}:{}", n, a, b);
        }

    }

    @Test
    public void test3() {
        double log = Math.log(2);
        double v = Math.log10(2);

        logger.info("n:{},{}",log, v);
    }
}

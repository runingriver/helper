package org.helper;

import java.lang.instrument.Instrumentation;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zongzhehu on 17-2-7.
 */
public class Test {
    private static final Logger logger = LoggerFactory.getLogger(Test.class);

    public static void main(String[] args) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        try {
            logger.info("-----{}-------",System.getProperty("http.maxConnections", "5"));
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        logger.info("time:{}", stopwatch.stop().toString());

    }


}

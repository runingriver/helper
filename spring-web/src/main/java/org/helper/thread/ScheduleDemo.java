package org.helper.thread;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zongzhehu on 17-2-6.
 */
public class ScheduleDemo {
    private static final Logger logger = LoggerFactory.getLogger(ScheduleDemo.class);
    static boolean runOnce = true;

    private static ScheduledExecutorService service = Executors.newScheduledThreadPool(1);

    public static void main(String[] args) {

        service.scheduleWithFixedDelay(new Runnable() {
            public void run() {
                logger.info("scheduleWithFixedDelay:{}", Thread.currentThread().getName().toString());
            }
        }, 20, 10, TimeUnit.SECONDS);

        service.scheduleAtFixedRate(new Runnable() {
            public void run() {
                logger.info("scheduleAtFixedRate:{}", Thread.currentThread().getName().toString());
            }
        }, 10, 10, TimeUnit.SECONDS);

        logger.info("{}", Thread.currentThread().getName().toString());
        logger.info("当前程序总线程数{}", Thread.activeCount());
        // getMethodInfo();
        //
        // ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        // service.scheduleAtFixedRate(new Runnable() {
        // public void run() {
        // logger.info("当前线程{}", Thread.currentThread().getName().toString());
        // logger.info("当前程序总线程数{}", Thread.activeCount());
        // logger.info("run scheduleAtFixedRate...");
        //
        // while (runOnce) {
        // getMethodInfo();
        // runOnce = false;
        // }
        // }
        // }, 5, 10, TimeUnit.SECONDS);
    }

    private static void getMethodInfo() {
        Map<Thread, StackTraceElement[]> allStackTraces = Thread.getAllStackTraces();
        for (Map.Entry<Thread, StackTraceElement[]> entry : allStackTraces.entrySet()) {
            Thread key = entry.getKey();
            logger.info("{}", key.getName().toString());
            StackTraceElement[] value = entry.getValue();
            for (StackTraceElement element : value) {
                logger.info("{}", element.toString());
            }
        }
    }

}

package org.helper;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Created by zongzhehu on 17-8-4.
 */
public class AQSTest {
    private static final Logger logger = LoggerFactory.getLogger(AQSTest.class);

    @Test
    public void testSemaphore() throws InterruptedException {
        final Semaphore semaphore = new Semaphore(2, true);
        ExecutorService service = Executors.newFixedThreadPool(10);

        for (int i = 0; i < 1000; i++) {
            service.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        semaphore.acquire();
                        logger.info("success.remain permit:{},queue:{},has wait thread:{}",
                                semaphore.availablePermits(), semaphore.getQueueLength(), semaphore.hasQueuedThreads());
                        TimeUnit.SECONDS.sleep(3);
                    } catch (InterruptedException e) {
                        logger.error("Semaphoreacquire exception.", e);
                    } finally {
                        semaphore.release();
                    }
                }
            });
        }

        TimeUnit.SECONDS.sleep(50);
    }
}

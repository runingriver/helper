package org.helper.thread;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 多线程将任务分片执行
 */
public class multiThread {
    private static final Logger logger = LoggerFactory.getLogger(multiThread.class);


    public void cutTask(final List<String> mobileLists) {
        int size = mobileLists.size();
        final Set<String> finalCellNums = Sets.newConcurrentHashSet();
        final int n = size / 100000 + (size % 100000 == 0 ? 0 : 1);
        ExecutorService service = Executors.newFixedThreadPool(n);
        final CountDownLatch latch = new CountDownLatch(n);
        logger.info("thread count：{},mobile size:{}", n, size);
        for (int i = 0; i < n; i++) {
            final int fromIndex = i * 100000;
            final int toIndex = (fromIndex + 100000) > size ? size : fromIndex + 100000;
            service.execute(new Runnable() {
                @Override
                public void run() {
                    Stopwatch threadWatch = Stopwatch.createStarted();
                    List<String> executeList = mobileLists.subList(fromIndex, toIndex);
                    //List<String> decryptMobiles = decrypt.decryptMobile(executeList);
                    for (String decryptMobile : executeList) {
                        finalCellNums.add(decryptMobile.substring(2, 9));
                    }
                    latch.countDown();
                    threadWatch.stop();
                    logger.info("---thread {} finish,cost:{}---", Thread.currentThread().getName(), threadWatch);
                }
            });
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            logger.error("---thread synchronize error.---", e);
        }

        logger.info("decrypt total {} mobiles, final cell count:{}", size, finalCellNums.size());

        service.shutdown();
    }

}

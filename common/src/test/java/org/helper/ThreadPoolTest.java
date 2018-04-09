package org.helper;

import com.google.common.collect.Lists;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zongzhehu on 17-8-4.
 */
public class ThreadPoolTest {
    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolTest.class);

    /**
     * 测试runnable和callable的异常情况
     */
    @Test
    public void testRunnableAndCallable() {
        final ExecutorService service1 = Executors.newSingleThreadExecutor();
        ExecutorService service2 = Executors.newSingleThreadExecutor();
        service2.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    service1.execute(new Runnable() {
                        @Override
                        public void run() {
                            logger.info("enter runnable.");
                            throw new RuntimeException("mock unchecked exception.");
                        }
                    });
                    logger.info("service2 finish.");
                } catch (Exception e) {
                    logger.error("error:{}", e);
                }
            }
        });

//        Future<String> future = service1.submit(new Callable<String>() {
//            @Override
//            public String call() throws Exception {
//                //throw new Exception("mock checked exception.");
//                throw new RuntimeException("mock unchecked exception.");
//                //return "hello";
//            }
//        });
//
//        try {
//            String result = future.get();
//            logger.info("result:{}", result);
//        } catch (InterruptedException e) {
//            logger.error("error:{}", e);
//        } catch (ExecutionException e) {
//            logger.error("error:{}", e);
//        }

        logger.info("finish test.");
    }

    private static final AtomicInteger poolNumber = new AtomicInteger(1);

    /**
     * 模拟线程频繁创建问题
     */
    @Test
    public void testThreadCreate() throws InterruptedException {
        final ExecutorService service1 = Executors.newFixedThreadPool(5, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                logger.info("create thread,current thread:{},poolNumber:{}", Thread.currentThread().getName(), poolNumber.get());
                return new Thread(r, "MQConsumer-Thread-" + poolNumber.getAndIncrement());
            }
        });

        for (int i = 0; i < 10; i++) {
            service1.execute(new Runnable() {
                @Override
                public void run() {
                    logger.info("enter service1 runnable.");
                    throw new RuntimeException("exception");
                }
            });
        }

        TimeUnit.SECONDS.sleep(100);
    }

    /**
     * 模拟线程异常,是否会导致其他任务不执行
     * 如果是Runnable中执行异常,后面的任务会执行.
     * 在Callable下,实际结果与很多博客有差异,我这里各个任务是互不影响,而且线程不会销毁！
     * 因为,异常被封装到future结果中了!
     */
    @Test
    public void testThreadException() throws InterruptedException {
        ExecutorService service = Executors.newCachedThreadPool(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                logger.info("create thread,current thread:{},poolNumber:{}", Thread.currentThread().getName(), poolNumber.get());
                return new Thread(r, "MQConsumer-Thread-" + poolNumber.getAndIncrement());
            }
        });

        List<Future<String>> futureList = Lists.newArrayListWithCapacity(10);
        for (int i = 0; i < 10; i++) {
            final int k = i;
            Future<String> future = service.submit(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    if (k == 5) {
                        throw new Exception("runtime exception " + Thread.currentThread().getName());
                    }
                    TimeUnit.SECONDS.sleep(10);
                    logger.info("after long time execute.");
                    return Thread.currentThread().getName() + " success" + k;
                }
            });
            futureList.add(future);
        }

        for (Future<String> future : futureList) {
            try {
                String result = future.get();
                logger.info("result:{}", result);
            } catch (InterruptedException e) {
                logger.error("error:", e);
            } catch (ExecutionException e) {
                logger.error("error:", e);
            }
        }

        TimeUnit.SECONDS.sleep(100);
    }

    /**
     * 模拟线程阻塞
     * 如果抛出Reject异常,整个线程池都不会执行了
     */
    @Test
    public void testThreadBlock() throws InterruptedException {
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(1, 5,
                30L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(),
                //new SynchronousQueue<Runnable>(),
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        logger.info("create thread,current thread:{},poolNumber:{}", Thread.currentThread().getName(), poolNumber.get());
                        return new Thread(r, "MQConsumer-Thread-" + poolNumber.getAndIncrement());
                    }
                });
        poolExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        for (int i = 0; i < 1000; i++) {
            final int k = i;
            poolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        logger.error("error:", e);
                    }
                    logger.info("{} running {}.", Thread.currentThread().getName(), k);
                }
            });
        }
        poolExecutor.shutdown();
        logger.info("main finished.");
        TimeUnit.SECONDS.sleep(100);
    }

    @Test
    public void testIsTerminated() throws InterruptedException {
        ThreadPoolExecutor service = new ThreadPoolExecutor(1, 1,
                30L, TimeUnit.MILLISECONDS,
                //new LinkedBlockingQueue<Runnable>(),
                new SynchronousQueue<Runnable>());
        service.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        service.execute(new Runnable() {
            @Override
            public void run() {
                logger.info("mock running a task.");
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    logger.error("error:", e);
                }
                logger.info("long task exe finish.");
            }
        });

        service.shutdown();
        //TimeUnit.SECONDS.sleep(1);
        boolean terminated = service.isTerminated();

        logger.info("service is terminated:{}", terminated);
        if (terminated) {
            service.execute(new Runnable() {
                @Override
                public void run() {
                    logger.info("mock after Terminated whether can be reuse.");
                }
            });
        }

        TimeUnit.SECONDS.sleep(5);
    }


    /**
     * 测试如何关闭线程池
     *
     * @throws InterruptedException
     */
    @Test
    public void testShutdownAndAwaitTermination() throws InterruptedException {
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(new Runnable() {
            @Override
            public void run() {
                logger.info("enter pool thread exe task.");
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    logger.error("error:", e);
                }
                logger.info("long task exe finish.");
            }
        });

        //service.shutdown();
        //logger.info("shutdown executed.");
        service.awaitTermination(1, TimeUnit.SECONDS);
        logger.info("awaitTermination executed.");

        //TimeUnit.SECONDS.sleep(10);
    }

    /**
     * 测试关闭线程池过程中,新任务到来的处理
     * 结果:按照设定的Reject策略执行。
     */
    @Test
    public void shutdownRejectTest() throws InterruptedException {
        final ThreadPoolExecutor service = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(10),
                new ThreadPoolExecutor.CallerRunsPolicy());

        ExecutorService service2 = Executors.newSingleThreadExecutor();

        service2.execute(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10000; i++) {
                    final int k = i;
                    service.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                logger.info("{} do something......", k);
                                TimeUnit.MILLISECONDS.sleep(100);
                            } catch (InterruptedException e) {
                                logger.error("error:", e);
                            }
                        }
                    });
                }
            }
        });

        TimeUnit.MILLISECONDS.sleep(1000);
        //logger.info("sleep 1 seconds.");
        //service2.shutdownNow();

        service.shutdown();
        logger.info("service.shutdown()");
        service.awaitTermination(1, TimeUnit.HOURS);
    }

    /**
     * 多个定时周期任务，且执行时间长，如何执行！
     */
    @Test
    public void singleScheduleWithMultiTaskTest1() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                logger.info("1 task running...");
                //sleep(3);
            }
        }, 1, 1, TimeUnit.SECONDS);

        scheduler.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                logger.info("2 task running...");
                //sleep(3);
            }
        }, 1, 2, TimeUnit.SECONDS);

        logger.info("here is main thread.");
        sleep(50);
        logger.info("finish test.");
    }

    @Test
    public void singleScheduleWithMultiTaskTest2() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                logger.info("first task sleep 20 second ...");
                sleep(20);
            }
        }, 0L, TimeUnit.MILLISECONDS);

        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                logger.info("second task...");
            }
        }, 10L, TimeUnit.SECONDS);

        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                logger.info("third task...");
            }
        }, 5L, TimeUnit.SECONDS);

        logger.info("here is main thread.");
        sleep(50);
        logger.info("finish test.");
    }

    private void sleep(long seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            logger.error("exception.", e);
        }
    }
}

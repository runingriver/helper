package org.helper.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.JdkFutureAdapters;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.common.util.concurrent.ListenableScheduledFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

/**
 * 测试guava提供listening相关的类的使用方法
 */
public class ListenableFutureDemoTest {
    private static final Logger logger = LoggerFactory.getLogger(ListenableFutureDemoTest.class);

    /**
     * 测试guava,线程处理
     */
    @Test
    public void test18() throws InterruptedException {
        // 创建一个指定线程名的线程池
        ExecutorService threadPool = Executors.newFixedThreadPool(3, new ThreadFactoryBuilder()
                .setNameFormat("commons-thread-%d").build());

        ListeningExecutorService listeningExecutorService = MoreExecutors.listeningDecorator(threadPool);
        ListenableFuture<String> listenableFuture = listeningExecutorService.submit(new Callable<String>() {
            public String call() throws Exception {
                Thread.sleep(3000);
                logger.info("execute task...");
                //throw new Exception("test call exception");
                return "world";
            }
        });

        //添加监听方法,directExecutor表示使用线程池中执行任务的线程
        listenableFuture.addListener(new Runnable() {
            public void run() {
                logger.info("can't get return value");
            }
        }, MoreExecutors.directExecutor());


        Futures.addCallback(listenableFuture, new FutureCallback<String>() {
            public void onSuccess(String result) {
                try {
                    logger.info("enter onSuccess");
                    Thread.sleep(1000);
                    logger.info("leave onSuccess");
                } catch (InterruptedException e) {
                    logger.error("error", e);
                }
                logger.info("the result of future is: {}", result);
            }

            public void onFailure(Throwable t) {
                logger.info("exception:{}", t.getMessage());
            }
        });

        Thread.sleep(5000);
    }

    /**
     * 演示定时调度线程池装饰成listen的效果,且无返回值情况Runnable
     */
    @Test
    public void test19() throws InterruptedException {
        ListeningScheduledExecutorService listeningScheduledExecutorService = MoreExecutors
                .listeningDecorator(Executors.newScheduledThreadPool(3));

        // 只有callable才对应有future
        ListenableScheduledFuture<?> listenableScheduledFuture = listeningScheduledExecutorService
                .scheduleAtFixedRate(new Runnable() {
                    public void run() {
                        logger.info("hello world");
                    }
                }, 2, 3, TimeUnit.SECONDS);
        // 5秒延时后,没个3秒执行一次

        // 因为上面的传的是runnable，所以没有返回值，没有返回值就不会触发future的callBack
        Futures.addCallback(listenableScheduledFuture, new FutureCallback<Object>() {
            public void onSuccess(Object result) {
                logger.info("no success result:{}", result);
            }

            public void onFailure(Throwable t) {
                logger.info("no failure result:", t);
            }
        });

        Thread.sleep(12000);
    }

    /**
     * 测试guava,ListenableScheduledFuture调度有返回值情况Callable
     */
    @Test
    public void test20() throws InterruptedException {
        ListeningScheduledExecutorService listeningScheduledExecutorService = MoreExecutors
                .listeningDecorator(Executors.newScheduledThreadPool(3));
        ListenableScheduledFuture<String> schedule = listeningScheduledExecutorService.schedule(new Callable<String>() {
            public String call() throws Exception {
                // return "world";
                throw new Exception("call exception:");
            }
        }, 3, TimeUnit.SECONDS);
        Futures.addCallback(schedule, new FutureCallback<String>() {
            public void onSuccess(String result) {
                logger.info("success:{}", result);
            }

            public void onFailure(Throwable t) {
                logger.info("error:{}", t);
            }
        });

        Thread.sleep(4000);
    }

    /**
     * 测试jdk线程池的缺陷,按照常理这个线程在3秒的睡眠中应该是可以取消的,其实不能取消!
     *
     * @throws InterruptedException
     * @throws ExecutionException
     */
    @Test
    public void test21() throws InterruptedException {
        FutureTask<String> futureTask = new FutureTask<String>(new Callable<String>() {
            public String call() throws Exception {
                logger.info("execute call");
                return "world";
            }
        });

        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(3);
        ScheduledFuture<?> schedule = scheduledExecutorService.schedule(futureTask, 3, TimeUnit.SECONDS);
        Thread.sleep(2000);
        // 如果执行下面的代码就抛出异常，事实上主线程等2s，这个task还没有执行，是应该可以取消的。但是却抛出异常,实际上还是取消了
        logger.info("futureTask.isDone:{}", futureTask.isDone());
        if (!futureTask.isDone()) {
            logger.info("futureTask.isDone:{}", futureTask.isDone());
            futureTask.cancel(true);
        }

        try {
            String result = futureTask.get();
            logger.info(result);
        } catch (Exception e) {
            logger.error("futureTask.get() exception", e);
        }

        logger.info("futureTask.isDone:{}", futureTask.isDone());
        Thread.sleep(9000);
    }

    /**
     * guava弥补上面的不足 应该主线程睡2s,而schedule线程睡3s,所以schedule线程任务可以取消掉.直接返回onFailure!
     * 原理就是guava将所有异常都拦截下来了！而future.get中没有拦截且以异常的方式抛出了。
     *
     * @throws InterruptedException
     */
    @Test
    public void test22() throws InterruptedException {
        ListenableFutureTask<String> listenableFutureTask = ListenableFutureTask.create(new Callable<String>() {
            public String call() throws Exception {
                logger.info("execute call");
                return "world";
            }
        });

        ListeningScheduledExecutorService listeningScheduledExecutorService = MoreExecutors
                .listeningDecorator(Executors.newScheduledThreadPool(3));
        listeningScheduledExecutorService.schedule(listenableFutureTask, 3, TimeUnit.SECONDS);
        Thread.sleep(2000);

        logger.info("listenableFutureTask.isCancelled:{}", listenableFutureTask.isCancelled());
        if (!listenableFutureTask.isCancelled()) {
            // cancel中的参数:true-正在执行的任务会中断;false-正在执行的任务继续执行，直到完成
            if (listenableFutureTask.cancel(false)) {
                logger.info("task is cancelled..");
            }
            logger.info("listenableFutureTask.isCancelled:{}", listenableFutureTask.isCancelled());
        }

        Futures.addCallback(listenableFutureTask, new FutureCallback<String>() {
            public void onSuccess(String result) {
                logger.info("hello {}", result);
            }

            public void onFailure(Throwable t) {
                logger.error("failure:", t);
            }
        });

        Thread.sleep(4000);
    }

    /**
     * 演示将jdk的future转换为ListenableFuture
     */
    @Test
    public void test24() {
        ExecutorService threadPool = Executors.newFixedThreadPool(3);
        logger.info("main thread:{}", Thread.currentThread().getName());

        Future<String> future = threadPool.submit(new Callable<String>() {
            public String call() throws Exception {
                logger.info("call thread:{}", Thread.currentThread().getName());
                return "world";
            }
        });
        ListenableFuture<String> listenableFuture = JdkFutureAdapters.listenInPoolThread(future);
        Futures.addCallback(listenableFuture, new FutureCallback<String>() {
            public void onSuccess(String result) {
                logger.info("success thread:{}: hello {}", Thread.currentThread().getName(), result);
            }

            public void onFailure(Throwable t) {
            }
        }, threadPool);
        // 注:如果这里使用MoreExecutors.directExecutor();这种方式拿到的线程还是当前的现场线程(即call线程)，而且还是同步的。
    }

    /**
     * 模拟先从数据库获取主键id，然后通过id获得一批数据。 实现了Integer->List<String>变换，变换通过Futures.transform()实现
     */
    @Test
    public void test26() throws InterruptedException {
        ListeningExecutorService listeningExecutorService = MoreExecutors
                .listeningDecorator(Executors.newFixedThreadPool(3));
        // 1.获取主键
        ListenableFuture<Integer> idFuture = listeningExecutorService.submit(new Callable<Integer>() {
            public Integer call() throws Exception {
                // 获取主键id
                return 1;
            }
        });

        // 2.转换数据方法
        AsyncFunction<Integer, List<String>> idToResult = new AsyncFunction<Integer, List<String>>() {
            public ListenableFuture<List<String>> apply(Integer input) throws Exception {
                // 根据主键id,从数据库中查询数据,放入List中
                logger.info("input：{}", input);
                Thread.sleep(3000);
                List<String> result = Lists.newArrayList();
                result.add("zhangsan");
                result.add("lisi");
                result.add("wangwu");
                return Futures.immediateFuture(result);
            }
        };

        ListenableFuture<List<String>> resultFuture = Futures.transformAsync(idFuture, idToResult,
                Executors.newCachedThreadPool());

        // 3.处理回调
        Futures.addCallback(resultFuture, new FutureCallback<List<String>>() {

            public void onSuccess(List<String> result) {
                logger.info("get the result: {}", result.toString());
            }

            public void onFailure(Throwable t) {
                logger.error("error:", t);
            }
        });

        Thread.sleep(5000);
    }

    /**
     * 演示guava的fan-out（扇出） 扇出:是电路中的定义,就是从一个节点输出的信号,分发到N个节点中. 在线程中的含义是,一个线程执行完成返回的结果,分发到多个线程中去执行.
     * 模拟:程序发出一条信息,需要以短信和app的方式给用户,短信和app的线程可以同步处理!
     */
    @Test
    public void test27() throws InterruptedException {
        ListeningExecutorService listeningExecutorService = MoreExecutors
                .listeningDecorator(Executors.newCachedThreadPool());

        ListenableFuture<String> future = listeningExecutorService.submit(new Callable<String>() {
            public String call() throws Exception {
                logger.info("current call thread:{}", Thread.currentThread().getName());
                return "message content";
            }
        });

        // 一个ListenableFuture 驱动了两个回调（扇出）fan-out
        Futures.addCallback(future, new FutureCallback<String>() {
            public void onSuccess(String result) {
                logger.info("{}: send message to user phone: {}", Thread.currentThread().getName(), result);
            }

            public void onFailure(Throwable t) {
                logger.error("error:", t);
            }
        }, Executors.newCachedThreadPool());

        Futures.addCallback(future, new FutureCallback<String>() {
            public void onSuccess(String result) {
                logger.info("{}: send message as app notify: {}", Thread.currentThread().getName(), result);
            }

            public void onFailure(Throwable t) {
            }
        }, Executors.newCachedThreadPool());

        logger.info("main test27 thread:{}", Thread.currentThread().getName());
        Thread.sleep(3000);
    }

    /**
     * 演示fan in(扇入) 多个线程执行路径相同的情况 扇入就可以看成是多个线程执行在某处进行汇聚 扇入分两种: 1. 所有汇聚线程执行任务相同 2.
     * 所有汇聚线程执行任务不完全相同(如,QQ的说说,获取点赞,图片,评论这些信息,这些信息通过不同的线程获取,然后交给说说线程显示)
     */
    @Test
    public void test29() throws InterruptedException {
        // 模拟:技术员的key
        List<Integer> technicianIds = Lists.newArrayList(1, 2, 3, 4, 5);

        final ListeningExecutorService listeningExecutorService = MoreExecutors
                .listeningDecorator(Executors.newCachedThreadPool());

        ArrayList<ListenableFuture<String>> listenableFutures = Lists.newArrayList(
                FluentIterable.from(technicianIds).transform(new Function<Integer, ListenableFuture<String>>() {
                    public ListenableFuture<String> apply(final Integer technicianId) {
                        logger.info("apply thread:{}", Thread.currentThread().getName());
                        return listeningExecutorService.submit(new Callable<String>() {
                            public String call() throws Exception {
                                // 模拟:获取技术员的员工信息
                                logger.info("call thread:{}", Thread.currentThread().getName());
                                if (technicianId == 2) {
                                    // throw new Exception("测试错误");
                                }
                                return "technicianInfo" + technicianId;
                            }
                        });
                    }
                }));

        // 只要输入的List中有一个Future失败，就会触发fanInFuture失败
        ListenableFuture<List<String>> fanInFuture = Futures.allAsList(listenableFutures);

        Futures.addCallback(fanInFuture, new FutureCallback<List<String>>() {
            public void onSuccess(List<String> result) {
                logger.info("onSuccess thread:{}", Thread.currentThread().getName());
                logger.info("success get result: {}", result.toString());
            }

            public void onFailure(Throwable t) {
                logger.info("onFailure thread:{}", Thread.currentThread().getName());
                logger.info("exception: {}", t.getMessage());
            }
        }, Executors.newCachedThreadPool());

        Thread.sleep(2000);
    }

}
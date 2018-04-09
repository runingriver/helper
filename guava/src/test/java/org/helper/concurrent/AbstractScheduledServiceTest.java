package org.helper.concurrent;

import static com.google.common.util.concurrent.AbstractScheduledService.Scheduler.newFixedDelaySchedule;
import static com.google.common.util.concurrent.MoreExecutors.directExecutor;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.AbstractScheduledService;
import com.google.common.util.concurrent.Service;

/**
 * Service接口用于封装一个服务对象的运行状态、包括start和stop等方法
 */
public class AbstractScheduledServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(AbstractScheduledServiceTest.class);

    volatile AbstractScheduledService.Scheduler configuration = newFixedDelaySchedule(0, 10, TimeUnit.MILLISECONDS);
    volatile ScheduledFuture<?> future = null;

    volatile boolean atFixedRateCalled = false;
    volatile boolean withFixedDelayCalled = false;
    volatile boolean scheduleCalled = false;

    final ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(10) {
        @Override
        public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay,
                TimeUnit unit) {
            return future = super.scheduleWithFixedDelay(command, initialDelay, delay, unit);
        }
    };

    @Test
    public void testServiceStartStop() throws Exception {
        NullService service = new NullService();
        service.startAsync().awaitRunning();
        logger.info("future isDone:{}", future.isDone());
        service.stopAsync().awaitTerminated();
        logger.info("future isCancelled:{}", future.isCancelled());
    }

    private class NullService extends AbstractScheduledService {
        @Override
        protected void runOneIteration() throws Exception {
        }

        @Override
        protected Scheduler scheduler() {
            return configuration;
        }

        @Override
        protected ScheduledExecutorService executor() {
            return executor;
        }
    }

    @Test
    public void testFailOnExceptionFromRun() throws Exception {
        TestService service = new TestService();
        //实例化一个异常.在runOneIteration中触发异常!
        service.runException = new Exception();
        service.startAsync().awaitRunning();
        service.runFirstBarrier.await();
        service.runSecondBarrier.await();
        try {
            future.get();
        } catch (CancellationException expected) {
            logger.error("运行错误:", expected);
        }
        // An execution exception holds a runtime exception (from throwables.propogate) that holds our
        // original exception.
        logger.info("service.runException {} == {}", service.runException, service.failureCause());
        logger.info("service.state:{} == {}", service.state(), Service.State.FAILED);

    }

    @Test
    public void testFailOnExceptionFromStartUp() {
        TestService service = new TestService();
        //实例化一个异常,在startUp中触发
        service.startUpException = new Exception();
        try {
            service.startAsync().awaitRunning();
        } catch (IllegalStateException e) {
            logger.error("运行错误:{} == {}", service.startUpException, e.getCause());
        }
        logger.info("{} == {}",0, service.numberOfTimesRunCalled.get());
        logger.info("{} == {}",Service.State.FAILED, service.state());
    }

    public void testFailOnErrorFromStartUpListener() throws InterruptedException {
        final Error error = new Error();
        final CountDownLatch latch = new CountDownLatch(1);
        TestService service = new TestService();
        service.addListener(new Service.Listener() {
            @Override public void running() {
                throw error;
            }
            @Override public void failed(Service.State from, Throwable failure) {
                logger.info("{} == {}",Service.State.RUNNING, from);
                logger.info("{} == {}",error, failure);
                latch.countDown();
            }
        }, directExecutor());
        service.startAsync();
        latch.await();

        logger.info("0 == {}",service.numberOfTimesRunCalled.get());
        logger.info("{} == {}",Service.State.FAILED, service.state());
    }


    private class TestService extends AbstractScheduledService {
        CyclicBarrier runFirstBarrier = new CyclicBarrier(2);
        CyclicBarrier runSecondBarrier = new CyclicBarrier(2);

        volatile boolean startUpCalled = false;
        volatile boolean shutDownCalled = false;
        AtomicInteger numberOfTimesRunCalled = new AtomicInteger(0);
        AtomicInteger numberOfTimesExecutorCalled = new AtomicInteger(0);
        AtomicInteger numberOfTimesSchedulerCalled = new AtomicInteger(0);
        volatile Exception runException = null;
        volatile Exception startUpException = null;
        volatile Exception shutDownException = null;

        @Override
        protected void runOneIteration() throws Exception {
            logger.info("startUpCalled:{}", startUpCalled);
            logger.info("shutDownCalled:{}", shutDownCalled);
            numberOfTimesRunCalled.incrementAndGet();
            logger.info("{} == {}", State.RUNNING, state());
            runFirstBarrier.await();
            runSecondBarrier.await();
            if (runException != null) {
                throw runException;
            }
        }

        @Override
        protected void startUp() throws Exception {
            logger.info("startUpCalled:{}", startUpCalled);
            logger.info("shutDownCalled:{}", shutDownCalled);
            startUpCalled = true;
            logger.info("{} == {}", State.RUNNING, state());
            if (startUpException != null) {
                throw startUpException;
            }
        }

        @Override
        protected void shutDown() throws Exception {
            logger.info("startUpCalled:{}", startUpCalled);
            logger.info("shutDownCalled:{}", shutDownCalled);
            shutDownCalled = true;
            if (shutDownException != null) {
                throw shutDownException;
            }
        }

        @Override
        protected ScheduledExecutorService executor() {
            numberOfTimesExecutorCalled.incrementAndGet();
            return executor;
        }

        @Override
        protected Scheduler scheduler() {
            numberOfTimesSchedulerCalled.incrementAndGet();
            return configuration;
        }
    }
}

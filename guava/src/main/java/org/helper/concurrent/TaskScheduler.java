package org.helper.concurrent;

import com.google.common.util.concurrent.AbstractScheduledService;
import com.google.common.util.concurrent.Service;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class TaskScheduler extends AbstractScheduledService {
    private static final Logger logger = LoggerFactory.getLogger(TaskScheduler.class);
    public static final int THREAD_NUM = 2;
    private String taskName;
    private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(THREAD_NUM);

    @Override
    public ScheduledExecutorService executor() {
//        addListener(
//                new Listener() {
//                    @Override
//                    public void terminated(State from) {
//                        logger.info("terminated.....");
//                        executorService.shutdown();
//                    }
//
//                    @Override
//                    public void failed(State from, Throwable failure) {
//                        logger.info("failed.....");
//                        executorService.shutdown();
//                    }
//                },
//                executorService);
        return executorService;
    }

    @Override
    protected String serviceName() {
        return taskName;
    }

    private long delay;
    private long period;

    public TaskScheduler(String taskName, long delay, long period) {
        this.taskName = taskName;
        this.delay = delay;
        this.period = period;
    }

    @Override
    protected void runOneIteration() throws Exception {
        String string = DateTime.now().toString("HH:mm:ss");
        logger.info("{},time:{},thread:{}", taskName, string, Thread.currentThread().getName());
        TimeUnit.SECONDS.sleep(period * 2);
    }

    protected Scheduler scheduler() {
        //Scheduler.newFixedDelaySchedule(1,1, TimeUnit.SECONDS);
        return Scheduler.newFixedRateSchedule(delay, period, TimeUnit.SECONDS);
    }

    public static void main(String[] args) throws InterruptedException {
        TaskScheduler scheduler1 = new TaskScheduler("scheduler1", 1, 5);
        TaskScheduler scheduler2 = new TaskScheduler("scheduler2", 1, 2);
        TaskScheduler scheduler3 = new TaskScheduler("scheduler3", 1, 2);
        TaskScheduler scheduler4 = new TaskScheduler("scheduler4", 1, 2);
        TaskScheduler scheduler5 = new TaskScheduler("scheduler5", 1, 2);
        TaskScheduler scheduler6 = new TaskScheduler("scheduler6", 1, 2);
        scheduler1.startAsync().awaitRunning();
        scheduler2.startAsync().awaitRunning();
        scheduler3.startAsync().awaitRunning();
        scheduler4.startAsync().awaitRunning();
        scheduler5.startAsync().awaitRunning();
        scheduler6.startAsync().awaitRunning();
        TimeUnit.SECONDS.sleep(1);
        //scheduler1.stopAsync().awaitTerminated();
    }

    private static class ScheduleListener extends Service.Listener {
        public void failed(Service.State from, Throwable failure) {}
        public void running() { logger.info("Listener:开始运行,监听线程:{}", Thread.currentThread().getName()); }
        public void starting() { logger.info("Listener:任务启动,当前监听定时任务线程:{}", Thread.currentThread().getName()); }
        //只可能有两种状态:RUNNING,STARTING
        public void stopping(Service.State from) { logger.info("Listener: 开始停止,线程:{}", Thread.currentThread().getName()); }
        //State:转换成TERMINATED状态,之前的状态
        public void terminated(Service.State from) {
            logger.info("Listener:任务终止. State:{}", from.toString());
        }
    }

}

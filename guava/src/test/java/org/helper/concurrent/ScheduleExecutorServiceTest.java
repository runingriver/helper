package org.helper.concurrent;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import org.junit.Test;

/**
 * Created by zongzhehu on 17-1-19.
 */
public class ScheduleExecutorServiceTest {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Test
    public void beepForAnHour() {
        final Runnable beeper = new Runnable() {
            public void run() {
                System.out.println("beep");
            }
        };

        final ScheduledFuture<?> beeperHandle = scheduler.scheduleAtFixedRate(beeper, 1, 5, SECONDS);

        scheduler.schedule(new Runnable() {
            public void run() {
                System.out.println("cancel.");
                beeperHandle.cancel(true);
            }
        }, 8, SECONDS);

        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

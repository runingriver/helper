package org.helper.main;


import java.util.concurrent.TimeUnit;

public class Test2Main {

    private Integer a = 1;

    public static void main(String[] args) {
        final Test2Main test2Main = new Test2Main();
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("enter thread1," + Thread.currentThread().getName());
                test2Main.getAndset();
            }
        });

        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("enter thread2," + Thread.currentThread().getName());
                test2Main.set();
            }
        });

        thread1.start();
        thread2.start();
    }

    public synchronized void getAndset() {
        System.out.println("thread1:" + Thread.currentThread().getName() + " " + a);
        sleep(2);
        System.out.println("thread1:" + Thread.currentThread().getName() + " " + a);
        a = 3;
        sleep(2);
        System.out.println("thread1:" + Thread.currentThread().getName() + " " + a);
    }

    public synchronized void set() {
        System.out.println("thread2:" + Thread.currentThread().getName() + " " + a);
        sleep(1);
        System.out.println("thread2:" + Thread.currentThread().getName() + " " + a);
        a = 2;
        System.out.println("thread2:" + Thread.currentThread().getName() + " " + a);
        sleep(2);
        System.out.println("thread2:" + Thread.currentThread().getName() + " " + a);
    }

    public static void sleep(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

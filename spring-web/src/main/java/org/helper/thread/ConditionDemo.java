package org.helper.thread;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 多个线程读写
 */
public class ConditionDemo {

    static class NumberWrapper {
        public int value = 1;
    }

    public static void main(String[] args) {
        //初始化可重入锁
        final Lock lock = new ReentrantLock();

        //第一个条件当屏幕上输出到3
        final Condition reachThreeCondition = lock.newCondition();
        //第二个条件当屏幕上输出到6
        final Condition reachSixCondition = lock.newCondition();

        //NumberWrapper只是为了封装一个数字，一边可以将数字对象共享，并可以设置为final
        //注意这里不要用Integer, Integer 是不可变对象
        final NumberWrapper num = new NumberWrapper();
        //初始化A线程
        Thread threadA = new Thread(new Runnable() {
            public void run() {
                //需要先获得锁
                lock.lock();
                try {
                    System.out.println("threadA start write");
                    //A线程先输出前3个数
                    while (num.value <= 3) {
                        System.out.println(num.value);
                        num.value++;
                    }
                    //输出到3时要signal，告诉B线程可以开始了
                    reachThreeCondition.signal();
                } finally {
                    lock.unlock();
                }

                //再等待输出7,8,9
                lock.lock();
                try {
                    //等待输出6的条件
                    reachSixCondition.await();
                    System.out.println("threadA start write");
                    //输出剩余数字
                    while (num.value <= 9) {
                        System.out.println(num.value);
                        num.value++;
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }

        });

        Thread threadB = new Thread(new Runnable() {
            public void run() {
                try {
                    lock.lock();

                    while (num.value <= 3) {
                        //等待3输出完毕的信号
                        reachThreeCondition.await();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }

                try {
                    lock.lock();
                    //已经收到信号，开始输出4，5，6
                    System.out.println("threadB start write");
                    while (num.value <= 6) {
                        System.out.println(num.value);
                        num.value++;
                    }
                    //4，5，6输出完毕，告诉A线程6输出完了
                    reachSixCondition.signal();
                } finally {
                    lock.unlock();
                }
            }

        });

        //启动两个线程
        threadB.start();
        threadA.start();
    }


    // 两个线程进行读写操作,官方示例
    final static class BoundedBuffer {
        final Lock lock = new ReentrantLock();
        final Condition notFull = lock.newCondition();//写线程条件
        final Condition notEmpty = lock.newCondition();//读线程条件

        final Object[] items = new Object[1];
        int putptr, takeptr, count;

        public void put(Object x) throws InterruptedException {
            lock.lock();
            try {
                while (count == items.length) {
                    notFull.await();//阻塞写线程
                }
                items[putptr] = x;
                if (++putptr == items.length) {
                    putptr = 0;
                }
                ++count;
                notEmpty.signal();//唤醒读线程
            } finally {
                lock.unlock();
            }
        }

        public Object take() throws InterruptedException {
            lock.lock();
            try {
                while (count == 0) {
                    notEmpty.await();//阻塞读线程
                }
                Object x = items[takeptr];
                if (++takeptr == items.length) {
                    takeptr = 0;
                }
                --count;
                notFull.signal();//唤醒写线程
                return x;
            } finally {
                lock.unlock();
            }
        }


        public static void main(String[] args) {
            final BoundedBuffer boundedBuffer = new BoundedBuffer();

            Thread t1 = new Thread(new Runnable() {
                public void run() {
                    System.out.println("t1 running...");
                    for (int i = 0; i < 10; i++) {
                        try {
                            System.out.println("putting " + i + "into buffer...");
                            boundedBuffer.put(Integer.valueOf(i));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

            });

            Thread t2 = new Thread(new Runnable() {
                public void run() {
                    for (int i = 0; i < 10; i++) {
                        try {
                            Object val = boundedBuffer.take();
                            System.out.println("take " + val + " out buffer...");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

            });

            t1.start();
            t2.start();
        }
    }

}

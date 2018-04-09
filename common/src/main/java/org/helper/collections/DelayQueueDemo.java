package org.helper.collections;

import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * 模拟一个考试，考试时间为120分钟，30分钟后才可交卷，当时间到了，或学生都交完卷了考试结束。
 * 这个场景中几个点需要注意：
 * 1.考试时间为120分钟，30分钟后才可交卷
 * 2.对于120分钟内没有完成考试的考生，在120分钟考试时间到后需要让他们强制交卷.
 * 3.在所有的考生都交完卷后，需要将控制线程关闭
 *
 * 一个不错的DelayQueue使用示例!
 */
public class DelayQueueDemo {

    public static void main(String[] args) throws InterruptedException {
        int studentNumber = 20;
        CountDownLatch countDownLatch = new CountDownLatch(studentNumber + 1);
        DelayQueue<Student> students = new DelayQueue<>();
        Random random = new Random();

        //为了更形象,30分钟-3秒,120分钟-12秒!
        for (int i = 0; i < studentNumber; i++) {
            students.put(new Student("student" + (i + 1), 3 + random.nextInt(12), countDownLatch));
        }
        Thread teacherThread = new Thread(new Teacher(students));

        //设置如果时间到,强制交卷
        students.put(new EndExam(students, 12, countDownLatch, teacherThread));

        teacherThread.start();
        countDownLatch.await();
        System.out.println(" 考试时间到，全部交卷！");
    }

    /**
     * 学生实体类,进行考试
     */
    public static class Student implements Runnable, Delayed {
        private String name;
        private long workTime;
        private long submitTime;
        private boolean isForce = false;
        private CountDownLatch countDownLatch;

        public Student() {
        }

        public Student(String name, long workTime, CountDownLatch countDownLatch) {
            this.name = name;
            this.workTime = workTime;
            this.submitTime = TimeUnit.MILLISECONDS.convert(workTime, TimeUnit.SECONDS) + System.currentTimeMillis();
            this.countDownLatch = countDownLatch;
        }

        public int compareTo(Delayed o) {
            if (o == null || !(o instanceof Student))
                return 1;
            if (o == this)
                return 0;
            Student s = (Student) o;
            if (this.workTime > s.workTime) {
                return 1;
            } else if (this.workTime == s.workTime) {
                return 0;
            } else {
                return -1;
            }
        }

        public long getDelay(TimeUnit unit) {
            long time = unit.convert(submitTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
            return time;
        }

        public void run() {
            if (isForce) {
                System.out.println(name + " 交卷, 希望用时" + workTime + "分钟" + " ,实际用时 120分钟");
            } else {
                System.out.println(name + " 交卷, 希望用时" + workTime + "分钟" + " ,实际用时 " + workTime + " 分钟");
            }
            countDownLatch.countDown();
        }

        public boolean isForce() {
            return isForce;
        }

        public void setForce(boolean isForce) {
            this.isForce = isForce;
        }

    }

    /**
     * 结束考试类
     */
    public static class EndExam extends Student {
        private DelayQueue<Student> students;
        private CountDownLatch countDownLatch;
        private Thread teacherThread;

        public EndExam(DelayQueue<Student> students, long workTime, CountDownLatch countDownLatch,
                       Thread teacherThread) {
            super("强制收卷", workTime, countDownLatch);
            this.students = students;
            this.countDownLatch = countDownLatch;
            this.teacherThread = teacherThread;
        }

        @Override
        public void run() {
            teacherThread.interrupt();
            Student tmpStudent;
            //迭代剩下的每个同学,调用他们的run方法,直接交卷!不管delayqueue中元素时间有没有到
            for (Iterator<Student> iterator2 = students.iterator(); iterator2.hasNext(); ) {
                tmpStudent = iterator2.next();
                tmpStudent.setForce(true);
                tmpStudent.run();
            }
            countDownLatch.countDown();
        }
    }

    /**
     * 监考老师
     */
    public static class Teacher implements Runnable {
        private DelayQueue<Student> students;

        public Teacher(DelayQueue<Student> students) {
            this.students = students;
        }

        //循环检测,交卷的学生
        public void run() {
            try {
                System.out.println("test start");
                while (!Thread.interrupted()) {
                    students.take().run();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}

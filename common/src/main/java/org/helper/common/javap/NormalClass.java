package org.helper.common.javap;

import java.util.concurrent.TimeUnit;

/**
 * 普通类的分析
 */
public class NormalClass extends AbstractClass {

    private static final String Field1 = "hello world.";
    private volatile int field2 = 20;
    private int field3;
    private Integer field4;

    public NormalClass(int field22, int field33, Integer field44) {
        this.field2 = field22;
        this.field3 = field33;
        this.field4 = field44;
    }

    public int getField2() {
        return field2;
    }

    public void setField2(int field2) {
        this.field2 = field2;
    }

    public synchronized int method(int a) {
        try {
            System.out.println("method sleep...");
            TimeUnit.SECONDS.sleep(30);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return a + 10;
    }

    /**
     * volatile变量
     */
    public int method1(int a) {
        int i = 0;
        field2 = 0;
        synchronized (this) {
            a = a + 10;
            field2 = a + i;
        }

        return a;
    }

    public synchronized static int method2(int a) {
        return a + 10;
    }

    public static int method4(int a) {
        synchronized (NormalClass.class) {
            try {
                System.out.println("method4 sleep...");
                TimeUnit.SECONDS.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            a = a + 10;
        }
        return a;
    }

    public int method5(int a) {
        synchronized (NormalClass.class) {
            a = a + 10;
        }
        return a;
    }


    public void exceptionMethod() throws Exception {
        try {
            throw new Exception("error exception1.");
        } catch (Exception e) {
            throw new Exception("error exception2.");
        }
    }

    @Override
    public int process(int a, int b) {
        return a + b;
    }

    /**
     * 虽然这样定义的返回值能够运行，但编译时会提示warning。不推荐这样使用，除非参数中有T的申明！
     * 编译阶段将类型擦除，转换成Object对象。
     * String a = NormalClass.testTReturn("123")能运行且不需要显示类型转换。
     * 但是容易报类型转换错误异常！
     */
    public static <T> T testTReturn(String string) {
        System.out.println("enter testTReturn:" + string);
        return (T) string;
    }


    public static void main(String[] args) {
        final NormalClass normalClass = new NormalClass(1, 2, 3);
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("thread1 start...");
                int i = normalClass.method(10);
                System.out.println("thread1 method :" + i);
            }
        });

        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("thread2 start...");
                int i = normalClass.method1(20);
                System.out.println("thread2 method1:" + i);
            }
        });

        thread1.start();
        thread2.start();

        int add = normalClass.add(4, 5);
        System.out.println("add:" + add);
    }
}

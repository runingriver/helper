package org.helper.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 总结:
 * 1. callable能返回异常,在future.get()的时候获得异常!
 * 2. submit,execute的调用都是将执行任务放入线程池的队列中.
 * 3. Runnable和Callable接口只是定义一个执行任务的单元,Executor定义的execute()是线程池的入口
 */
public class ThreadPoolDemo {
    public static void main(String[] args) {
        // 创建一个执行器
        ExecutorService executor = Executors.newFixedThreadPool(3);
        // 1.Runnable通过Future返回结果为空
        Future<?> future1 = executor.submit(new Runnable() {
            public void run() {
                System.out.println("runnable running.");
            }
        });

        executor.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println("execute method running...");
            }
        });

        // 2.Callable通过Future能返回结果
        Future<String> future2 = executor.submit(new Callable<String>() {
            public String call() throws Exception {
                System.out.println("callable running.");
                throw new Exception("异常............");
                // return "CallableResult";
            }
        });
        // 获得任务的结果
        try {
            System.out.println("Runnable return:" + future1.get()); // Runnable return:null
            System.out.println("Callable return:" + future2.get()); // Callable return:CallableResult
        } catch (Exception e) {
            System.out.println("get会抛出InterruptedException, ExecutionException异常");
            System.out.println(e.toString());
        }
        executor.shutdown();
    }
}

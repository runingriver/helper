package org.helper.shell;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 通过java调用shell执行
 * output可以通过多线程方式实现实时输出,但是会带来较高的上下文切换和系统中断
 * 因为shell将标准输出和标准错误输出发送给java时,java获取到内容后处理,然后阻塞,且这个频率较高!
 *
 * @author hzz 17-12-27
 */
public class ShellHelper {
    private static final Logger logger = LoggerFactory.getLogger(ShellHelper.class);

    private static volatile ThreadPoolExecutor executor = null;

    /**
     * 不支持全局变量(eg:echo $RANDOM)
     * 注:调用sh文件时,sh中必须处理路径,环境变量等问题!
     *
     * @param command     命令
     * @param realTimeLog 是否实时输出日志
     * @return 命令返回结果
     */
    public static String execShellCommand(String command, boolean realTimeLog) {
        Runtime runtime = Runtime.getRuntime();
        String[] cmd = {"/bin/sh", "-c", command};
        try {
            Process process = runtime.exec(cmd);
            return getShellOutputByAsync(process, realTimeLog);
        } catch (Exception e) {
            logger.error("exec shell command exception.command:{}", command, e);
        }
        return "error";
    }

    /**
     * 获取shell的标准输出和标准错误输出.
     * 由于系统标准输出和标准错误输出缓存区大小有限,这里采用异步获取输出,防止大量输出导致waitFor方法block住
     * 注:如果采用实时输出日志的方式,由于多线程,可能会导致标准错误和标准输出的时间点不一致
     *
     * @param process     process
     * @param realTimeLog 实时输出日志
     * @return 输出的结果
     */
    private static String getShellOutputByAsync(final Process process, final boolean realTimeLog) {
        checkExecutor();

        Future<String> stdFuture = executor.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return getStream(process.getInputStream(), realTimeLog);
            }
        });
        Future<String> stdErrFuture = executor.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return getStream(process.getErrorStream(), realTimeLog);
            }
        });
        StringBuilder builder = new StringBuilder();
        builder.append(System.lineSeparator()).append("standard output:").append(System.lineSeparator());
        try {
            builder.append(stdFuture.get());
        } catch (InterruptedException | ExecutionException e) {
            logger.error("get std output stream exception.", e);
            builder.append("standard output exception!");
        }

        builder.append("error output:").append(System.lineSeparator());
        try {
            builder.append(stdErrFuture.get());
        } catch (InterruptedException | ExecutionException e) {
            logger.error("get error output stream exception.", e);
            builder.append("error output exception!");
        }
        try {
            int result = process.waitFor();
            builder.append(result == 0 ? "exec success!" : "exec failed:" + result);
        } catch (InterruptedException e) {
            logger.error("get result waitFor exception.", e);
            builder.append("result:get shell exe result exception!");
        }
        return builder.toString();
    }

    /**
     * 只需要2个线程,因为多个shell的输入输出都共用相同的流(参见java.lang.UNIXProcess#initStreams(int[])方法)
     */
    private static void checkExecutor() {
        if (executor == null) {
            synchronized (ShellHelper.class) {
                if (executor == null) {
                    executor = new ThreadPoolExecutor(2, 2, 0L,
                            TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>(100),
                            new ThreadFactoryBuilder().setNameFormat("shell-exe-%d").build(),
                            new ThreadPoolExecutor.CallerRunsPolicy());
                }
            }
        }
    }

    /**
     * 注:由调用线程不停地获取标准输出,但是大量的标准错误输出可能会导致调用线程无限期的block住
     * 当确定输入输出很少,且想节约系统线程资源时使用
     * Tip:realTimeLog=true时只实时输出标准输出的日志
     */
    private static String getShellOutput(Process process, boolean realTimeLog) {
        StringBuilder builder = new StringBuilder();
        builder.append(System.lineSeparator()).append("standard output:").append(System.lineSeparator());
        builder.append(getStream(process.getInputStream(), realTimeLog));
        builder.append("error output:").append(System.lineSeparator());
        builder.append(getStream(process.getErrorStream(), realTimeLog));

        try {
            int result = process.waitFor();
            builder.append(result == 0 ? "exec success!" : "exec failed:" + result);
        } catch (InterruptedException e) {
            logger.error("get result waitFor exception.", e);
            builder.append("result:exception!");
        }
        return builder.toString();
    }


    /**
     * 从流中获取输出,直到得到流终止,读完缓冲区内容后都会让出cpu(如果该线程没有其他任务执行则会block住)
     *
     * @param stream      流
     * @param realTimeLog 是否实时输出日志
     * @return 流的所有日志
     */
    private static String getStream(InputStream stream, boolean realTimeLog) {
        StringBuilder builder = new StringBuilder();
        String line;
        BufferedReader output = null;
        try {
            output = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            while ((line = output.readLine()) != null) {
                if (realTimeLog) {
                    logger.info("{}", line);
                }
                builder.append(line).append(System.lineSeparator());
            }
        } catch (IOException e) {
            logger.error("read shell stream exception.", e);
        } finally {
            try {
                if (output != null) {
                    output.close();
                }
            } catch (IOException e) {
                logger.error("close shell stream exception.", e);
            }
        }
        return builder.toString();
    }

    /**
     * 一个简单的使用,script可以是sh的全路径
     */
    public static String runLinuxShell(String script) {
        Process pl;
        String line;
        StringBuilder result = new StringBuilder();
        try {
            pl = Runtime.getRuntime().exec(script);
            BufferedReader in = new BufferedReader(new InputStreamReader(pl.getInputStream()));

            //标准输出
            while ((line = in.readLine()) != null) {
                result.append(System.getProperty("line.separator") + line);
            }

            in.close();
            //标准错误
            BufferedReader err = new BufferedReader(new InputStreamReader(pl.getErrorStream()));
            while ((line = err.readLine()) != null) {
                result.append(System.getProperty("line.separator") + line);
            }
            err.close();
            result.append(System.getProperty("line.separator") + "exit value:" + pl.exitValue());
            result.append(System.getProperty("line.separator") + "xx:" + pl.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }
}

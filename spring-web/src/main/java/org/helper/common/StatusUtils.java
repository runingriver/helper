package org.helper.common;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import org.helper.entity.ClientStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 获取当前程序运行信息
 */
public class StatusUtils {
    public static Logger logger = LoggerFactory.getLogger(StatusUtils.class);
    private static ClientStatus clientStatus = new ClientStatus();

    public static ClientStatus getClientStatus(String projectName) {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        Runtime runtime = Runtime.getRuntime();
        // 空闲内存
        long freeMemory = runtime.freeMemory();
        clientStatus.setFreeMemory(byteToM(freeMemory));
        // 内存总量
        long totalMemory = runtime.totalMemory();
        clientStatus.setTotalMemory(byteToM(totalMemory));
        // 最大允许使用的内存
        long maxMemory = runtime.maxMemory();
        clientStatus.setMaxMemory(byteToM(maxMemory));
        // 操作系统
        clientStatus.setOsName(System.getProperty("os.name"));
        InetAddress localHost;
        String ipAddress = "";
        try {
            localHost = InetAddress.getLocalHost();
            String hostName = localHost.getHostName();
            clientStatus.setHost(hostName);
            ipAddress = localHost.getHostAddress();
        } catch (UnknownHostException e) {
            logger.error("无法获取当前主机的主机名与Ip地址");
            clientStatus.setHost("未知");
        }
        // ip
        clientStatus.setIpAddress(ipAddress);
        clientStatus.setId(makeClientId(projectName, ipAddress));
        // 程序启动时间
        long startTime = runtimeMXBean.getStartTime();
        Date startDate = new Date(startTime);
        clientStatus.setStartTime(startDate);
        // 类所在路径
        clientStatus.setClassPath(runtimeMXBean.getBootClassPath());
        // 程序运行时间
        clientStatus.setRuntime(runtimeMXBean.getUptime());
        // 线程总数
        clientStatus.setThreadCount(ManagementFactory.getThreadMXBean().getThreadCount());
        clientStatus.setProjectPath(new File("").getAbsolutePath());
        clientStatus.setPid(getPid());
        return clientStatus;
    }

    /**
     * 把byte转换成M
     */
    public static long byteToM(long bytes) {
        long kb = (bytes / 1024 / 1024);
        return kb;
    }

    /**
     * 创建一个客户端ID
     */
    public static String makeClientId(String projectName, String ipAddress) {
        String t = projectName + ipAddress + new File("").getAbsolutePath();
        int client_id = t.hashCode();
        client_id = Math.abs(client_id);
        return String.valueOf(client_id);
    }

    /**
     * 获取进程号，适用于windows与linux
     */
    public static long getPid() {
        try {
            String name = ManagementFactory.getRuntimeMXBean().getName();
            String pid = name.split("@")[0];
            return Long.parseLong(pid);
        } catch (NumberFormatException e) {
            logger.warn("无法获取进程Id");
            return 0;
        }
    }

}

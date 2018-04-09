package org.helper.entity;

import java.util.Date;

public class ClientStatus {
    private String id;
    /**
     * 当前进程运行的主机名
     */
    private String host;
    /**
     * 当前进程所在的IP地址
     */
    private String ipAddress;
    /**
     * 空闲内存
     */
    private long freeMemory;
    /**
     * 内存总量
     */
    private long totalMemory;
    /**
     * java虚拟机允许开启的最大的内存
     */
    private long maxMemory;

    /**
     * 操作系统名称
     */
    private String osName;
    /**
     * 进程号
     */
    private long pid;

    /**
     * 程序启动时间
     */
    private Date startTime;

    /**
     * 类所在路径
     */
    private String classPath;

    private String projectPath;

    /**
     * 程序运行时间，单位毫秒
     */
    private long runtime;
    /**
     * 线程总量
     */
    private int threadCount;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClassPath() {
        return classPath;
    }

    public void setClassPath(String classPath) {
        this.classPath = classPath;
    }

    public long getFreeMemory() {
        return freeMemory;
    }

    public void setFreeMemory(long freeMemory) {
        this.freeMemory = freeMemory;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public long getMaxMemory() {
        return maxMemory;
    }

    public void setMaxMemory(long maxMemory) {
        this.maxMemory = maxMemory;
    }

    public String getOsName() {
        return osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public long getPid() {
        return pid;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }

    public String getProjectPath() {
        return projectPath;
    }

    public void setProjectPath(String projectPath) {
        this.projectPath = projectPath;
    }

    public long getRuntime() {
        return runtime;
    }

    public void setRuntime(long runtime) {
        this.runtime = runtime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public long getTotalMemory() {
        return totalMemory;
    }

    public void setTotalMemory(long totalMemory) {
        this.totalMemory = totalMemory;
    }

    @Override
    public String toString() {
        return "ClientStatus{" +
                "classPath='" + classPath + '\'' +
                ", host='" + host + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", freeMemory=" + freeMemory +
                ", totalMemory=" + totalMemory +
                ", maxMemory=" + maxMemory +
                ", osName='" + osName + '\'' +
                ", pid=" + pid +
                ", startTime=" + startTime +
                ", projectPath='" + projectPath + '\'' +
                ", runtime=" + runtime +
                ", threadCount=" + threadCount +
                '}';
    }
}

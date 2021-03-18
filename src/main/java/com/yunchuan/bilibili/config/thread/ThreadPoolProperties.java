package com.yunchuan.bilibili.config.thread;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
@ConfigurationProperties(prefix = "thread.thread-pool")
public class ThreadPoolProperties {

    /**
     * 核心线程数大小
     */
    public int core;

    /**
     * 最大线程数大小
     */
    public int maxThread;

    /**
     * 空闲线程存活时间
     */
    public int keepAliveTime;

    /**
     * 阻塞队列大小
     */
    public int blockingQueueSize;

    public int getCore() {
        return core;
    }

    public void setCore(int core) {
        this.core = core;
    }

    public int getMaxThread() {
        return maxThread;
    }

    public void setMaxThread(int max) {
        this.maxThread = max;
    }

    public int getKeepAliveTime() {
        return keepAliveTime;
    }

    public void setKeepAliveTime(int keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }

    public int getBlockingQueueSize() {
        return blockingQueueSize;
    }

    public void setBlockingQueueSize(int blockingQueueSize) {
        this.blockingQueueSize = blockingQueueSize;
    }
}

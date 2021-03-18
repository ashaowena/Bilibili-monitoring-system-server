package com.yunchuan.bilibili.config.thread;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import java.util.concurrent.*;

@Configuration
public class ThreadPoolConfig {

    private final ThreadPoolProperties threadPoolProperties;

    public ThreadPoolConfig(ThreadPoolProperties threadPoolProperties) {
        this.threadPoolProperties = threadPoolProperties;
    }

    @Bean
    public ThreadPoolExecutor threadPool() {
        int[] threadNum = {0};
        return new ThreadPoolExecutor(threadPoolProperties.core, threadPoolProperties.maxThread, threadPoolProperties.keepAliveTime, TimeUnit.SECONDS, new LinkedBlockingDeque<>(threadPoolProperties.blockingQueueSize), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setDaemon(true);
                thread.setName("线程-" + threadNum[0]++);
                return thread;
            }
        }, new ThreadPoolExecutor.DiscardPolicy());
    }

}

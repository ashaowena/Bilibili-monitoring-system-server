package com.yunchuan.bilibili.schedule;



import com.yunchuan.bilibili.serviver.UpsManagerService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;


/**
 * 开启定时任务
 */

/**
 * 异步任务
 * 1、@EnableAsync开启异步任务
 * 2、@Async给希望异步的方法上标注
 * 3、自动配置类TaskExecutionAutoConfiguration;
 */
@Slf4j
@Configuration
@EnableScheduling
@EnableAsync/*开启异步注解*/
public class ScheduledTask {

    @Autowired
    UpsManagerService upsManagerService;

    /**
     *  cron 秒 分 时 日 月 周几 （月和周其中一个打问号占位）
     *  * /5 表示第*秒启动，每5秒执行一次
     *  异步定时任务方式：
     *  1、默认线程池大小为1，异步任务需要设置线程池大小（易失效）
     *  2、使用CompletableFuture工具类
     *  3、使用注解@EnableAsync,定时方法上加上注解@Async
     */
    @Async
    @Scheduled(cron = "5 0 0 * * ?") // 整点执行会有BUG，导致日期多选而无法正常返回
    public void scheduled() throws Exception {
        log.info("正在进行批量删除过期信息操作。。。。。");
        upsManagerService.cleanExpiredUps();
        log.info("正在进行更新过期信息操作。。。。。");
        upsManagerService.upDateUps();
    }



}

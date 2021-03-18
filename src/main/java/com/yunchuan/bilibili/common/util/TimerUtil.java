package com.yunchuan.bilibili.common.util;

import java.util.concurrent.atomic.AtomicInteger;

public class TimerUtil {

    private TimerUtil(){};

    public static long startTime;

    public static int endTime;

    public static int maxNum;

    public static AtomicInteger currentNum = new AtomicInteger();

    public volatile static boolean isNotInit;

    public static int getDuration() {
        return (int)(endTime - startTime / 1000);
    }

    public static void setEndTime() {
        TimerUtil.endTime = (int)System.currentTimeMillis();
    }

    public static int  incAndGet() {
        return currentNum.incrementAndGet();
    }

}

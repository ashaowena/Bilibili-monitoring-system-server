package com.yunchuan.bilibili.common.date;

import com.yunchuan.bilibili.entity.UpStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public enum  DateUtil {

    DAY(1),
    WEEK(7),
    A_MOUNT(30),
    TWO_MOUNT(60),
    THREE_MOUNT(90);

    DateUtil(int period) {
        this.PERIOD = period;
    }

    public final int PERIOD;

//    private static final DateTimeFormatter formatter;
//
//    public static String toDateString() {
//
//    }

    public static Date getBeforeDate(int period) {
        Date now = new Date();
        LocalDate nowLocalDate = Instant.ofEpochMilli(now.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate beforeLocalDate = nowLocalDate.plusDays(-period);
        Date before = Date.from(beforeLocalDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        return before;
    }

    public static Date getNowDate() {
        LocalDate nowLocalDate = LocalDate.now();
        Date now = Date.from(nowLocalDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        return now;
    }

    // 判断日期是否是连续的
    public static boolean isContinuous(List<UpStatus> upStatuses) {
        for (int i = 0; i < upStatuses.size(); i++) {
            UpStatus upStatus = upStatuses.get(i);
            Date realDate = upStatus.getDate();
            Date beforeDate = getBeforeDate(i);
            LocalDate realLocalDate = Instant.ofEpochMilli(realDate.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate beforeLocalDate = Instant.ofEpochMilli(beforeDate.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
            if (!realLocalDate.equals(beforeLocalDate)) {
                return false;
            }
        }
        return true;
    }
}

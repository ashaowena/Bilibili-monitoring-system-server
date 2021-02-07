package com.yunchuan.bilibili.vo;

import com.yunchuan.bilibili.entity.UpStatus;
import lombok.Data;

import java.util.List;

@Data
public class PeriodDetailResponseVo {
    /**
     * 周期性增量数据
     */
    List<UpStatusAfterTranslatedVo> translatedVos;

    /**
     * 表格存量数据
     */
//    List<UpStatus> dailyUpStatuses;

    /**
     * 表格增量数据
     */
//    List<UpStatusAfterTranslatedVo> dailyTranslatedVos;

    ChartWrapper dailyUpStatuses;

    ChartWrapper dailyTranslatedVos;



}

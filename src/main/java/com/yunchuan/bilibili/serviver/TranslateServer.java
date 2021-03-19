package com.yunchuan.bilibili.serviver;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yunchuan.bilibili.common.util.DateUtil;
import com.yunchuan.bilibili.dao.UpStatusDAO;
import com.yunchuan.bilibili.entity.UpStatus;
import com.yunchuan.bilibili.serviver.translate.TranslateProcess;
import com.yunchuan.bilibili.vo.ChartWrapper;
import com.yunchuan.bilibili.vo.UpStatusAfterTranslatedVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;


@Service
@Slf4j
public class TranslateServer {

    @Autowired
    UpStatusDAO upStatusDAO;

    @Autowired
    TranslateProcess translateProcess;


    public ChartWrapper translateToChar(List<UpStatus> dailyStatus, Integer period) {
        ChartWrapper wrapper = new ChartWrapper();
        if (dailyStatus == null || dailyStatus.size() != period) {
            for (int i = 0, j = 10 - wrapper.getChart_X().size(); i < j; i++) {
                wrapper.getChart_X().add("-");
            }
            return wrapper;
        }
        for (UpStatus status : dailyStatus) {
            wrapper.add(status);
        }


        return wrapper;
    }

    public ChartWrapper translateToIncChar(List<UpStatusAfterTranslatedVo> dailyTranslatedVos, Integer period) {
        ChartWrapper wrapper = new ChartWrapper();
//        if (dailyTranslatedVos == null || dailyTranslatedVos.size() != period) {
//            for (int i = 0, j = 10 - wrapper.getChart_X().size(); i < j; i++) {
//                wrapper.getChart_X().add("-");
//            }
//            return wrapper;
//        }
        if (dailyTranslatedVos == null) {
            return null;
        }

        for (UpStatusAfterTranslatedVo dailyTranslatedVo : dailyTranslatedVos) {
            wrapper.add(dailyTranslatedVo);
        }

        for (int i = 0, j = 10 - wrapper.getChart_X().size(); i < j; i++) {
            wrapper.getChart_X().add("-");
        }
        return wrapper;
    }

    public List<UpStatusAfterTranslatedVo> getDailyTranslated(List<UpStatus> dailyStatus, Integer period) {
        if (CollectionUtils.isEmpty(dailyStatus)) {
            log.info("获取日期数据失败");
            return null;
        }
        if (dailyStatus.size() < period) {
            log.info("日期数据不足周期，无补齐必要");
            return doSmartTranslate(dailyStatus);
        }
        UpStatus upStatus = dailyStatus.get(0);
        UpStatus status = upStatusDAO.selectOne(new QueryWrapper<UpStatus>().eq("uid", upStatus.getUid()).eq("date", DateUtil.getBeforeDate(period + 1)));
        if (status == null) {
            log.info("无符合条件的顺延日期");
            return doSmartTranslate(dailyStatus);
        }
        List<UpStatus> temp = dailyStatus.subList(0, dailyStatus.size());
        temp.add(status);
        return doSmartTranslatePeriod(temp);

    }

    public List<UpStatus> getDailyStatus(String uid, Integer period) {
        Date beforeDate = DateUtil.getBeforeDate(period);
        List<UpStatus> upStatuses = upStatusDAO.selectList(new QueryWrapper<UpStatus>().eq("uid", uid).gt("date", beforeDate).orderByDesc("date"));
        if (CollectionUtils.isEmpty(upStatuses)) {
            return null;
        }
        List<UpStatus> subList = null;

        // 对获取到的每日信息做后置处理,默认会把中间缺少的天数线性填充，也可以自己
        // 重写TranslateProcess接口的实现类
        translateProcess.upStatusesProcess(upStatuses);

        for (int i = 0; i < upStatuses.size(); i++) {
            subList = upStatuses.subList(0, i + 1);
            if (!DateUtil.isContinuous(subList)) {
                subList = upStatuses.subList(0, i);
                break;
            }
        }


        return subList;
    }

    public List<UpStatusAfterTranslatedVo> smartTranslate(String uid, Integer period) {
        DateUtil ePeriod = null;
        if (period == null) {
            period = 7;
        }
        switch (period) {
            case 1:
                ePeriod = DateUtil.DAY;
            case 7:
                ePeriod = DateUtil.WEEK;
                break;
            case 30:
                ePeriod = DateUtil.A_MOUNT;
                break;
            case 60:
                ePeriod = DateUtil.TWO_MOUNT;
                break;
            case 90:
                ePeriod = DateUtil.THREE_MOUNT;
                break;
            default:
                log.info("周期信息错误");
                throw new RuntimeException("周期信息错误！");
        }
        List<UpStatusAfterTranslatedVo> translatedVos = smartTranslate(uid, ePeriod);
        return translatedVos == null ? new ArrayList<UpStatusAfterTranslatedVo>() : translatedVos;
    }

    public List<UpStatusAfterTranslatedVo> smartTranslate(String uid, DateUtil period) {
        List<UpStatus> upStatuses = smartGetUpStatus(uid, period);
        List<UpStatusAfterTranslatedVo> translatedVos = doSmartTranslate(upStatuses);
        return translatedVos;
    }

    public List<UpStatusAfterTranslatedVo> doSmartTranslate(List<UpStatus> upStatuses) {

        if (CollectionUtils.isEmpty(upStatuses)) {
            return null;
        }

        List<UpStatusAfterTranslatedVo> translatedVos = new ArrayList<UpStatusAfterTranslatedVo>();
        for (int i = 0; i < upStatuses.size(); i++) {
            UpStatusAfterTranslatedVo usat = UpStatusAfterTranslatedVo.build(upStatuses.get(i));
            translatedVos.add(usat);
        }

        return translatedVos;

    }

    public List<UpStatusAfterTranslatedVo> doSmartTranslatePeriod(List<UpStatus> upStatuses) {

        if (CollectionUtils.isEmpty(upStatuses)) {
            return null;
        }

        List<UpStatusAfterTranslatedVo> translatedVos = new ArrayList<UpStatusAfterTranslatedVo>();
        for (int i = 0; i < upStatuses.size() - 1; i++) {
            UpStatusAfterTranslatedVo usat = UpStatusAfterTranslatedVo.build(upStatuses.get(i),upStatuses.get(i + 1));
            translatedVos.add(usat);
        }

        return translatedVos;

    }

    private List<UpStatus> smartGetUpStatus(String uid, DateUtil period) {
        Date now = DateUtil.getNowDate();
        Date before = DateUtil.getBeforeDate(period.PERIOD * 2 - 1);
        Date preBefore = DateUtil.getBeforeDate(period.PERIOD * 3 - 1);
        UpStatus nowData = upStatusDAO.selectOne(new QueryWrapper<UpStatus>().eq("uid", uid).eq("date", now));
        UpStatus beforeData = upStatusDAO.selectOne(new QueryWrapper<UpStatus>().eq("uid", uid).eq("date", before));
        UpStatus preBeforeData = upStatusDAO.selectOne(new QueryWrapper<UpStatus>().eq("uid", uid).eq("date", preBefore));

        if (nowData == null && beforeData == null && preBeforeData == null) {
            log.info("不足两个周期，无法计算");
            return null;
        }


        if (nowData != null && beforeData != null && preBeforeData != null) {
            return agg3PeriodUpStatus(nowData, beforeData, preBeforeData);
        } else if (nowData != null && beforeData != null) {
            return agg2PeriodUpStatus(nowData, beforeData);
        }

        log.info("条件不满足周期");
        return new ArrayList<UpStatus>();

    }


    // 获取2个周期内的总和数据
    private List<UpStatus> agg2PeriodUpStatus(UpStatus now, UpStatus before) {


        List<UpStatus> periodUpStatus = new ArrayList<>();
        UpStatus periodHeadUpStatus = new UpStatus().init();

        periodHeadUpStatus.setFavorite(now.getFavorite() - before.getFavorite());
        periodHeadUpStatus.setShare(now.getShare() - before.getShare());
        periodHeadUpStatus.setReply(now.getReply() - before.getReply());
        periodHeadUpStatus.setDanmaku(now.getDanmaku() - before.getDanmaku());
        periodHeadUpStatus.setCoin(now.getCoin() - before.getCoin());
        periodHeadUpStatus.setProductions(now.getProductions() - before.getProductions());
        periodHeadUpStatus.setFans(now.getFans() - before.getFans());
        periodHeadUpStatus.setLike(now.getLike() - before.getLike());
        periodHeadUpStatus.setView(now.getView() - before.getView());


        periodUpStatus.add(periodHeadUpStatus);
        return periodUpStatus;
    }

    // 获取3个周期内的总和数据
    private List<UpStatus> agg3PeriodUpStatus(UpStatus now, UpStatus before, UpStatus preBefore) {


        List<UpStatus> periodUpStatus = new ArrayList<>();
        UpStatus periodHeadUpStatus = new UpStatus().init();

        periodHeadUpStatus.setFavorite(now.getFavorite() - before.getFavorite());
        periodHeadUpStatus.setShare(now.getShare() - before.getShare());
        periodHeadUpStatus.setReply(now.getReply() - before.getReply());
        periodHeadUpStatus.setDanmaku(now.getDanmaku() - before.getDanmaku());
        periodHeadUpStatus.setCoin(now.getCoin() - before.getCoin());
        periodHeadUpStatus.setProductions(now.getProductions() - before.getProductions());
        periodHeadUpStatus.setFans(now.getFans() - before.getFans());
        periodHeadUpStatus.setLike(now.getLike() - before.getLike());
        periodHeadUpStatus.setView(now.getView() - before.getView());


        UpStatus periodMiddleUpStatus = new UpStatus().init();

        periodMiddleUpStatus.setFavorite(before.getFavorite() - preBefore.getFavorite());
        periodMiddleUpStatus.setShare(before.getShare() - preBefore.getShare());
        periodMiddleUpStatus.setReply(before.getReply() - preBefore.getReply());
        periodMiddleUpStatus.setDanmaku(before.getDanmaku() - preBefore.getDanmaku());
        periodMiddleUpStatus.setCoin(before.getCoin() - preBefore.getCoin());
        periodMiddleUpStatus.setProductions(before.getProductions() - preBefore.getProductions());
        periodMiddleUpStatus.setFans(before.getFans() - preBefore.getFans());
        periodMiddleUpStatus.setLike(before.getLike() - preBefore.getLike());
        periodMiddleUpStatus.setView(before.getView() - preBefore.getView());


        periodUpStatus.add(periodHeadUpStatus);
        periodUpStatus.add(periodMiddleUpStatus);
        return periodUpStatus;
    }

    private List<UpStatus> getUp3DayStatus(String uid) throws Exception {

        List<UpStatus> upStatuses = new ArrayList<>(3);

        // 查找今天的即时数据
//        UpStatus upStatus = monitorServer.doMonitorUp(uid,false);

        UpStatus upStatus = upStatusDAO.selectOne(new QueryWrapper<UpStatus>().eq("uid", uid).eq("date", DateUtil.getNowDate()));
        if (upStatus == null) {
            log.error("获取今日数据失败");
            return null;
        }

        upStatuses.add(upStatus);
        // 从数据库中查找出前三天的数据
        LocalDate localDate = LocalDate.now();
        LocalDate before2Day = localDate.plusDays(-2);
        Date localDate0 = Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        Date before2Days = Date.from(before2Day.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        List<UpStatus> beforeStatus = upStatusDAO.selectList(new QueryWrapper<UpStatus>().eq("uid", uid).between("date", before2Days, localDate0).orderByDesc("date"));
        if (beforeStatus.size() > 2) {
            // 刚好踏中时间点导致多选,移除最大的日期
            beforeStatus.remove(0);
        }
        upStatuses.addAll(beforeStatus);
        return upStatuses;
    }

    public List<UpStatusAfterTranslatedVo> translate(String uid) throws Exception {
        List<UpStatusAfterTranslatedVo> translatedVos = new ArrayList<>();
        List<UpStatus> up3DateStatus = getUp3DayStatus(uid);
        if (up3DateStatus.size() != 2 && up3DateStatus.size() != 3) {
            log.info("不满足2天或3天");

            return null;
        }
        // 必须是今天和昨天的数据
        if (up3DateStatus.size() == 2) {
            UpStatus upStatus0 = up3DateStatus.get(0);
            UpStatus upStatus1 = up3DateStatus.get(1);
            Date date0 = upStatus0.getDate();
            Date date1 = upStatus1.getDate();
            LocalDate localDate0 = Instant.ofEpochMilli(date0.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate localDate1 = Instant.ofEpochMilli(date1.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
            if (!localDate0.equals(LocalDate.now()) || !localDate1.equals(LocalDate.now().plusDays(-1L))) {
                // 有两天的数据，但不是昨天和今天
                log.info("不满足连续2天");
                return null;
            }
            UpStatusAfterTranslatedVo translatedVo = UpStatusAfterTranslatedVo.build(upStatus0, upStatus1);
            translatedVos.add(translatedVo);
            return translatedVos;
        } else {
            UpStatus upStatus0 = up3DateStatus.get(0);
            UpStatus upStatus1 = up3DateStatus.get(1);
            UpStatus upStatus2 = up3DateStatus.get(2);
            Date date0 = upStatus0.getDate();
            Date date1 = upStatus1.getDate();
            Date date2 = upStatus2.getDate();
            LocalDate localDate0 = Instant.ofEpochMilli(date0.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate localDate1 = Instant.ofEpochMilli(date1.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate localDate2 = Instant.ofEpochMilli(date2.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
            if (!localDate0.equals(LocalDate.now()) || !localDate1.equals(LocalDate.now().plusDays(-1L))
                    || !localDate2.equals(LocalDate.now().plusDays(-2L))) {
                // 有三天的数据，但不是连续3天
                log.info("不满足连续3天");
                return null;
            }
            UpStatusAfterTranslatedVo translatedVo0 = UpStatusAfterTranslatedVo.build(upStatus0, upStatus1);
            UpStatusAfterTranslatedVo translatedVo1 = UpStatusAfterTranslatedVo.build(upStatus1, upStatus2);
            translatedVos.add(translatedVo0);
            translatedVos.add(translatedVo1);
            return translatedVos;
        }
    }


}

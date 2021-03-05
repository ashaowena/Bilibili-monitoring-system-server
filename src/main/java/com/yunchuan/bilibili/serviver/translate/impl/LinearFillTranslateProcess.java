package com.yunchuan.bilibili.serviver.translate.impl;

import com.yunchuan.bilibili.common.util.DateUtil;
import com.yunchuan.bilibili.entity.UpStatus;
import com.yunchuan.bilibili.serviver.translate.TranslateProcess;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.util.Date;
import java.util.List;

import static java.time.temporal.ChronoUnit.*;


@Component
@Slf4j
public class LinearFillTranslateProcess implements TranslateProcess {


    @Override
    public List<UpStatus> upStatusesProcess(List<UpStatus> upStatuses) {

        if (upStatuses == null || upStatuses.size() < 1) {
            log.error("日期数据异常或只有一个日期的数据，无法自动填充");
            return upStatuses;
        }

        if (DateUtil.isContinuous(upStatuses)) {
            return upStatuses;
        }

        int head, tail = 1;
        boolean debugInfo = true;
        while (tail < upStatuses.size()) {

            if (!DateUtil.isContinuous(upStatuses.subList(0,tail + 1))) {

                if (debugInfo) {
                    debugInfo = false;
                    log.info("不是连续的周期，进行线性填充处理");
                }

                head = tail - 1;
                int span = fillData(upStatuses, head, tail);
                tail += span;
            }

            tail++;


        }

        return upStatuses;
    }

    private int fillData(List<UpStatus> upStatuses, int head, int tail) {

        UpStatus headNode = upStatuses.get(head);
        UpStatus tailNode = upStatuses.get(tail);

        Duration duration0 = Duration.ofMillis(tailNode.getDate().getTime());
        Duration duration1 = Duration.ofMillis(headNode.getDate().getTime());
        Duration minus = duration1.minus(duration0);

        long days = minus.toDays() - 1;



        for (int i = 0; i < days; i++) {
            UpStatus upStatus = generateData(headNode, tailNode, (int) days, i);
            Duration duration = Duration.ofMillis(tailNode.getDate().getTime());
            Date date = new Date(duration.plus(i + 1, DAYS).toMillis());
            upStatus.setDate(date);
            upStatuses.add(tail,upStatus);
        }


        return (int)days;
    }

    private UpStatus generateData(UpStatus headNode, UpStatus tailNode, int days ,int i) {
        UpStatus upStatus = new UpStatus();
        upStatus.setUid(headNode.getUid());
        upStatus.setLike(headNode.getLike() + (tailNode.getLike() - headNode.getLike()) * i / days);
        upStatus.setShare(headNode.getShare() + (tailNode.getShare() - headNode.getShare()) * i / days);
        upStatus.setFavorite(headNode.getFavorite() + (tailNode.getFavorite() - headNode.getFavorite()) * i / days);
        upStatus.setReply(headNode.getReply() + (tailNode.getReply() - headNode.getReply()) * i / days);
        upStatus.setDanmaku(headNode.getDanmaku() + (tailNode.getDanmaku() - headNode.getDanmaku()) * i / days);
        upStatus.setFans(headNode.getFans() + (tailNode.getFans() - headNode.getFans()) * i / days);
        upStatus.setView(headNode.getView() + (tailNode.getView() - headNode.getView()) * i / days);
        upStatus.setCoin(headNode.getCoin() + (tailNode.getCoin() - headNode.getCoin()) * i / days);
        upStatus.setProductions(headNode.getProductions() + (tailNode.getProductions() - headNode.getProductions()) * i / days);

        return upStatus;
    }



}

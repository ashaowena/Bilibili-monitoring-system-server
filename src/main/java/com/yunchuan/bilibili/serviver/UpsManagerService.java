package com.yunchuan.bilibili.serviver;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yunchuan.bilibili.dao.UpStatusDAO;
import com.yunchuan.bilibili.entity.UpStatus;
import com.yunchuan.bilibili.vo.videos.UpDateVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
public class UpsManagerService {

    @Autowired
    UpStatusDAO upStatusDAO;

    @Autowired
    MonitorServer monitorServer;

    /**
     * 通过定时任务清除过期的up主信息
     */
    public void cleanExpiredUps() {
        LocalDate expireDate = LocalDate.now().plusDays(-90);
        Date date = Date.from(expireDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        upStatusDAO.delete(new QueryWrapper<UpStatus>().lt("date", date));
    }

    /**
     * 通过定时任务新增up主信息
     */
    public void upDateUps() throws Exception {
        List<UpDateVo> allUpUidsAndDates = upStatusDAO.getAllUpUidsAndDate();
        for (UpDateVo allUpUidAndDate : allUpUidsAndDates) {
            if (shouldUpdateUp(allUpUidAndDate)) {
                monitorServer.saveMonitorUp(allUpUidAndDate.getUid());
            }
        }
    }

    public boolean shouldUpdateUp(UpDateVo allUpUid) {
        Date date = allUpUid.getDate();
        LocalDate upDate = Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate now = LocalDate.now();
        return now.isAfter(upDate);
    }
}

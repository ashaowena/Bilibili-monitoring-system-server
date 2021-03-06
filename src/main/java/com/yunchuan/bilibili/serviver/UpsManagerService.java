package com.yunchuan.bilibili.serviver;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yunchuan.bilibili.common.util.TimerUtil;
import com.yunchuan.bilibili.dao.UpStatusDAO;
import com.yunchuan.bilibili.entity.UpStatus;
import com.yunchuan.bilibili.vo.videos.UpDateVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jni.Time;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

@Service
@Slf4j
public class UpsManagerService {

    @Autowired
    UpStatusDAO upStatusDAO;

    @Autowired
    MonitorServer monitorServer;

    @Autowired
    ThreadPoolExecutor executor;

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
    public void upDateUps()  {
        List<UpDateVo> allUpUidsAndDates = upStatusDAO.getAllUpUidsAndDate();

//        for (int i = 0; i < allUpUidsAndDates.size(); i++) {
//            UpDateVo allUpUidAndDate = allUpUidsAndDates.get(i);
//            if (shouldUpdateUp(allUpUidAndDate)) {
//                monitorServer.saveMonitorUpAsync(allUpUidAndDate.getUid());
//            }
//        }

        for (UpDateVo allUpUidAndDate : allUpUidsAndDates) {
            if (shouldUpdateUp(allUpUidAndDate)) {
                monitorServer.saveMonitorUpAsync(allUpUidAndDate.getUid());
            }
        }
    }

    /**
     * 通过刷新按钮触发的更新up操作,应该是同步执行的
     * @param uid
     * @throws Exception
     */
    public void upDateUpImmediately(String uid) throws Exception {
        UpStatus upStatus = monitorServer.doMonitorUp(uid, true);
        upStatusDAO.updateById(upStatus);
    }

    /**
     * 判断今日是否已经更新过Up
     * @param allUpUid
     * @return
     */
    public boolean shouldUpdateUp(UpDateVo allUpUid) {
        Date date = allUpUid.getDate();
        LocalDate upDate = Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate now = LocalDate.now();
        return now.isAfter(upDate);
    }
}

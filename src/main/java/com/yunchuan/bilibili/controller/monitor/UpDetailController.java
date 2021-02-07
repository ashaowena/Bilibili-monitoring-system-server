package com.yunchuan.bilibili.controller.monitor;


import com.alibaba.fastjson.JSON;
import com.yunchuan.bilibili.entity.UpStatus;
import com.yunchuan.bilibili.serviver.MonitorServer;
import com.yunchuan.bilibili.serviver.TranslateServer;
import com.yunchuan.bilibili.vo.*;
import com.yunchuan.bilibili.vo.videos.UpVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;


@Controller
public class UpDetailController {

    @Autowired
    MonitorServer monitorServer;

    @Autowired
    TranslateServer translateServer;

    @RequestMapping("/detail/{uid}")
    public String detail(@PathVariable("uid") String uid , HttpSession session) throws Exception {
        MonitorResponseVo vo = (MonitorResponseVo)session.getAttribute("monitorResponse");
        List<UpGroupVo> upgroups = vo.getUpgroups();
        UpVo upVo = null;
        for (UpGroupVo upgroup : upgroups) {
            List<UpVo> upVos = upgroup.getUpVos();
            for (UpVo upVo0 : upVos) {
                if (upVo0.getUid().equalsIgnoreCase(uid)) {
                    upVo = upVo0;
                }
            }
        }
        if (upVo == null) {
            upVo = new UpVo();
        }
        UpDetailResponseVo upDetail = monitorServer.getUpDetail(upVo,null);
        session.setAttribute("UpDetail",upDetail);
        return "detail";
    }

    @ResponseBody
    @RequestMapping("/periodDetail")
    public PeriodDetailResponseVo getPeriodDetail(@RequestParam Integer period,@RequestParam String uid ,HttpSession session) {
        MonitorResponseVo vo = (MonitorResponseVo)session.getAttribute("monitorResponse");
        PeriodDetailResponseVo periodDetailResponseVo = new PeriodDetailResponseVo();
        // 设置概况数据
        List<UpStatusAfterTranslatedVo> translatedVos = translateServer.smartTranslate(uid, period);
        periodDetailResponseVo.setTranslatedVos(translatedVos);
        // 设置图表总量数据
        List<UpStatus> dailyStatus = translateServer.getDailyStatus(uid, period);
        ChartWrapper chartWrapper = translateServer.translateToChar(dailyStatus);
        periodDetailResponseVo.setDailyUpStatuses(chartWrapper);
        // 设置图表增量数据
        List<UpStatusAfterTranslatedVo> dailyTranslatedVos = translateServer.getDailyTranslated(dailyStatus,period);
        ChartWrapper incChartWrapper = translateServer.translateToIncChar(dailyTranslatedVos);
        periodDetailResponseVo.setDailyTranslatedVos(incChartWrapper);
        return periodDetailResponseVo;
    }

    @ResponseBody
    @RequestMapping(value = "/bfs/face/{path}/{index}",produces = "image/jpeg")
    public byte[] getImage(@PathVariable String path,@PathVariable String index) throws Exception {
        byte[] face = monitorServer.getFace(path,index);
        return face;
    }






}

package com.yunchuan.bilibili.controller.monitor;




import com.yunchuan.bilibili.common.response.R;
import com.yunchuan.bilibili.entity.UpStatus;
import com.yunchuan.bilibili.serviver.MonitorServer;
import com.yunchuan.bilibili.serviver.TranslateServer;
import com.yunchuan.bilibili.serviver.UpsManagerService;
import com.yunchuan.bilibili.vo.*;
import com.yunchuan.bilibili.vo.up.UpInfoVo;
import com.yunchuan.bilibili.vo.videos.UpVo;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpSession;
import java.util.List;


@Controller
public class UpDetailController {

    @Autowired
    MonitorServer monitorServer;

    @Autowired
    UpsManagerService upsManagerService;

    @Autowired
    TranslateServer translateServer;

    @ResponseBody
    @RequestMapping("/searchUp")
    public R searchUp(@RequestParam("uid") String up) throws Exception {
        UpInfoVo upInfo = monitorServer.getUpInfo(up);
        if (upInfo != null) {
            return R.ok().setData(upInfo);
        }
        return R.error("无此用户!");

    }

    @ResponseBody
    @RequestMapping("/details")
    public R detail(HttpSession session) {
        MonitorResponseVo vo = (MonitorResponseVo)session.getAttribute("monitorResponse");
        if (vo == null) {
            return R.error(HttpStatus.SC_FORBIDDEN,"请先登录");
        }
        List<UpGroupVo> upgroups = vo.getUpgroups();
        List<UpDetailResponseVo> upDetails = monitorServer.getUpDetails(upgroups);
        return R.ok().setData(upDetails);
    }

    @ResponseBody
    @RequestMapping("/periodTabDetail")
    public R getPeriodTabDetail(@RequestParam Integer period,@RequestParam String uid) {
        List<UpStatusAfterTranslatedVo> vos = monitorServer.getTab(uid, period);
        return R.ok().setData(vos);
    }

    @ResponseBody
    @RequestMapping("/periodDetail")
    public R getPeriodDetail(@RequestParam Integer period,@RequestParam String uid ,HttpSession session) {
        MonitorResponseVo vo = (MonitorResponseVo)session.getAttribute("monitorResponse");
        PeriodDetailResponseVo periodDetailResponseVo = new PeriodDetailResponseVo();
        // 设置概况数据(移至上面接口实现)
//        List<UpStatusAfterTranslatedVo> translatedVos = translateServer.smartTranslate(uid, period);
//        periodDetailResponseVo.setTranslatedVos(translatedVos);
        // 设置图表总量数据
        List<UpStatus> dailyStatus = translateServer.getDailyStatus(uid, period);
        ChartWrapper chartWrapper = translateServer.translateToChar(dailyStatus);
        periodDetailResponseVo.setDailyUpStatuses(chartWrapper);
        // 设置图表增量数据
        List<UpStatusAfterTranslatedVo> dailyTranslatedVos = translateServer.getDailyTranslated(dailyStatus,period);
        ChartWrapper incChartWrapper = translateServer.translateToIncChar(dailyTranslatedVos);
        periodDetailResponseVo.setDailyTranslatedVos(incChartWrapper);
        return R.ok().setData(periodDetailResponseVo);
    }

    /**
     * 刷新按钮即使跟新Up
     * @param uid
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("/updateStatus")
    public R updateDetail(String uid) throws Exception {
        upsManagerService.upDateUpImmediately(uid);
        return R.ok();
    }


    /**
     * 代理B站的图片下载
     * @param path
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value = "/proxyImg",produces = "image/jpeg")
    public byte[] getImage(@RequestParam String path) throws Exception {
        byte[] face = monitorServer.getFace(path);
        return face;
    }






}

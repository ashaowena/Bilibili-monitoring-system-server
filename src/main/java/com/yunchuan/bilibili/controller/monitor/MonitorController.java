package com.yunchuan.bilibili.controller.monitor;


import com.yunchuan.bilibili.common.response.R;
import com.yunchuan.bilibili.serviver.GroupServer;
import com.yunchuan.bilibili.serviver.MonitorServer;
import com.yunchuan.bilibili.vo.MonitorResponseVo;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;


@Controller
public class MonitorController {

    @Autowired
    MonitorServer monitorServer;

    @Autowired
    GroupServer groupServer;


    @ResponseBody
    @RequestMapping("/blogger")
    public R blogger(HttpServletRequest request) {

        MonitorResponseVo vo = (MonitorResponseVo) request.getSession().getAttribute("monitorResponse");
        if (vo == null) {
            return R.error(HttpStatus.SC_FORBIDDEN,"请先登录!");
        }

        // 获取用户的分组
        groupServer.getUserGroups(vo);
        // 获取分组下的up
        groupServer.getMonitorUp(vo);

        return R.ok().setData(vo.getUpgroups());
    }

//    @ResponseBody
//    @RequestMapping("/updateGroup")
//    public R updateGroup(HttpServletRequest request) {
//        MonitorResponseVo vo = (MonitorResponseVo) request.getSession().getAttribute("monitorResponse");
//        if (vo == null) {
//            return R.error(400,"请先登录!");
//        }
//
//        // 获取用户的分组
//        monitorServer.getUserGroups(vo);
//
//
//        return R.ok().setData(vo.getUpgroups());
//    }






}

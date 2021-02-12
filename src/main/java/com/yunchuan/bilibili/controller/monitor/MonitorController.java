package com.yunchuan.bilibili.controller.monitor;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunchuan.bilibili.common.response.R;
import com.yunchuan.bilibili.entity.UpStatus;
import com.yunchuan.bilibili.entity.User;
import com.yunchuan.bilibili.serviver.MonitorServer;
import com.yunchuan.bilibili.vo.MonitorResponseVo;
import com.yunchuan.bilibili.vo.up.UpInfoVo;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
public class MonitorController {

    @Autowired
    MonitorServer monitorServer;


    @ResponseBody
    @RequestMapping("/addMonitorGroup")
    public String addMonitorGroup(String groupName, HttpSession session) {
        MonitorResponseVo vo = (MonitorResponseVo) session.getAttribute("monitorResponse");
        Map<String, Object> data = new HashMap<>();
        if (vo != null) {
            User user = vo.getUser();
            int i = monitorServer.addMonitorGroup(user, groupName);
            if (i == 200) {
                data.put("code", 200);
                data.put("message", "success");
                String s = JSON.toJSONString(data);
                return s;
            }
            data.put("code", 0);
            data.put("msg", "非法的用户名！");
            String s = JSON.toJSONString(data);
            return s;
        }
        data.put("code", 0);
        data.put("msg", "fail");
        String s = JSON.toJSONString(data);
        return s;
    }

    @ResponseBody
    @RequestMapping("/deleteMonitorGroup")
    public String deleteMonitorGroup(Integer groupId, HttpSession session) {
        MonitorResponseVo vo = (MonitorResponseVo) session.getAttribute("monitorResponse");
        Map<String, Object> data = new HashMap<>();
        if (vo != null) {
            User user = vo.getUser();
            monitorServer.deleteMonitorGroup(user,groupId);
            data.put("code", 200);
            data.put("message", "success");
            String s = JSON.toJSONString(data);
            return s;
        }
        data.put("code", 0);
        data.put("msg", "fail");
        String s = JSON.toJSONString(data);
        return s;
    }

    @ResponseBody
    @RequestMapping("/moveMonitorGroup")
    public R moveMonitorGroup(Integer groupId,String uid, HttpSession session) {
        MonitorResponseVo vo = (MonitorResponseVo) session.getAttribute("monitorResponse");
        if (vo != null) {
            User user = vo.getUser();
            monitorServer.moveMonitorGroup(user,groupId,uid);
            return R.ok();
        }
        return R.error(HttpStatus.SC_FORBIDDEN,"请先登录！");
    }

    @ResponseBody
    @RequestMapping("/addUp")
    public R addMonitorUp(@RequestParam(defaultValue = "0") Integer groupId,@RequestParam("uid") String up, HttpSession session) throws Exception {
        MonitorResponseVo vo = (MonitorResponseVo) session.getAttribute("monitorResponse");
        Map<String, Object> data = new HashMap<>();
        if (vo != null) {
            User user = vo.getUser();
            int i = monitorServer.addMonitorUp(groupId, up, user);
            if (i == 200) {
                return R.ok();
            }
            return R.error("无此用户！");
        }
        return R.error(HttpStatus.SC_FORBIDDEN,"请先登录！");
    }


    @ResponseBody
    @RequestMapping("/deleteMonitorUp")
    public String deleteMonitorUp(String uid, HttpSession session) {
        MonitorResponseVo vo = (MonitorResponseVo) session.getAttribute("monitorResponse");
        Map<String, Object> data = new HashMap<>();
        if (vo != null) {
            User user = vo.getUser();
            int i = monitorServer.deleteMonitorUp(uid, user);
            data.put("code", 200);
            data.put("message", "success");
            String s = JSON.toJSONString(data);
            return s;
        }
        data.put("code", 0);
        data.put("message", "fail");
        String s = JSON.toJSONString(data);
        return s;
    }

    @ResponseBody
    @RequestMapping("/blogger")
    public R blogger(HttpServletRequest request) {

        MonitorResponseVo vo = (MonitorResponseVo) request.getSession().getAttribute("monitorResponse");
        if (vo == null) {
            return R.error(HttpStatus.SC_FORBIDDEN,"请先登录!");
        }

        // 获取用户的分组
        monitorServer.getUserGroups(vo);
        // 获取分组下的up
        monitorServer.getMonitorUp(vo);

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

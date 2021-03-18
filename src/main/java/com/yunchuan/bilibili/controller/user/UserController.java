package com.yunchuan.bilibili.controller.user;



import com.alibaba.fastjson.JSON;
import com.yunchuan.bilibili.common.response.R;
import com.yunchuan.bilibili.entity.User;
import com.yunchuan.bilibili.serviver.GroupServer;
import com.yunchuan.bilibili.serviver.LoginService;
import com.yunchuan.bilibili.vo.MonitorResponseVo;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;


@Controller
public class UserController {

    @Autowired
    LoginService loginService;

    @Autowired
    GroupServer groupServer;

    /**
     * 登录
     * @param data
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping("/login")
    public R loginByPassword(@RequestBody Map<String,String> data, HttpServletRequest request) {
        MonitorResponseVo vo = loginService.login(data.get("username"), data.get("password"));
        if (vo != null) {
            // 登陆成功，跳转业务
            request.getSession().setAttribute("monitorResponse",vo);
            String response = JSON.toJSONString(vo.getUser());
            return R.ok().setData(response);
        }
        return R.error( "账号密码错误！");

    }


    /**
     * 新增分组
     * @param groupName
     * @param session
     * @return
     */
    @ResponseBody
    @RequestMapping("/addMonitorGroup")
    public R addMonitorGroup(String groupName, HttpSession session) {
        MonitorResponseVo vo = (MonitorResponseVo) session.getAttribute("monitorResponse");
        if (vo != null) {
            User user = vo.getUser();
            groupServer.addMonitorGroup(user, groupName);
            return R.ok();
        }
        return R.error(HttpStatus.SC_FORBIDDEN,"请先登录！");
    }


    /**
     * 删除分组
     * @param groupId
     * @param session
     * @return
     */
    @ResponseBody
    @RequestMapping("/deleteMonitorGroup")
    public R deleteMonitorGroup(@RequestParam Integer groupId, HttpSession session) {
        MonitorResponseVo vo = (MonitorResponseVo) session.getAttribute("monitorResponse");
        if (vo != null) {
            User user = vo.getUser();
            groupServer.deleteMonitorGroup(user,groupId);
            return R.ok();
        }
        return R.error(HttpStatus.SC_FORBIDDEN,"请先登录！");
    }

    /**
     * 删除监控的UP
     * @param uid
     * @param session
     * @return
     */
    @ResponseBody
    @RequestMapping("/deleteMonitorUp")
    public R deleteMonitorUp(String uid, HttpSession session) throws IOException {
        MonitorResponseVo vo = (MonitorResponseVo) session.getAttribute("monitorResponse");
        if (vo != null) {
            User user = vo.getUser();
            groupServer.deleteMonitorUp(uid, user);
            return R.ok();
        }
        return R.error(HttpStatus.SC_FORBIDDEN,"请先登录！");
    }

    /**
     * 移动UP的所属分组
     * @param groupId
     * @param uid
     * @param session
     * @return
     */
    @ResponseBody
    @RequestMapping("/moveMonitorGroup")
    public R moveMonitorGroup(Integer groupId,String uid, HttpSession session) {
        MonitorResponseVo vo = (MonitorResponseVo) session.getAttribute("monitorResponse");
        if (vo != null) {
            User user = vo.getUser();
            groupServer.moveMonitorGroup(user,groupId,uid);
            return R.ok();
        }
        return R.error(HttpStatus.SC_FORBIDDEN,"请先登录！");
    }

    /**
     * 新增UP
     * @param groupId
     * @param up
     * @param session
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("/addUp")
    public R addMonitorUp(@RequestParam(defaultValue = "0") Integer groupId, @RequestParam("uid") String up, HttpSession session) throws Exception {
        MonitorResponseVo vo = (MonitorResponseVo) session.getAttribute("monitorResponse");
        if (vo != null) {
            User user = vo.getUser();
            int i = groupServer.addMonitorUp(groupId, up, user);
            if (i == 200) {
                return R.ok();
            }
            return R.error("无此用户！");
        }
        return R.error(HttpStatus.SC_FORBIDDEN,"请先登录！");
    }




}

package com.yunchuan.bilibili.controller.user;



import com.alibaba.fastjson.JSON;
import com.yunchuan.bilibili.common.response.R;
import com.yunchuan.bilibili.serviver.LoginService;
import com.yunchuan.bilibili.serviver.MonitorServer;
import com.yunchuan.bilibili.vo.MonitorResponseVo;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


@Controller
public class UserController {

    @Autowired
    LoginService loginService;


    @RequestMapping("login.html")
    public String login(){
        return "login";
    }


    @ResponseBody
    @Transactional
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



}

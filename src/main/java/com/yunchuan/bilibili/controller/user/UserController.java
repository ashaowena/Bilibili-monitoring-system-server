package com.yunchuan.bilibili.controller.user;



import com.alibaba.fastjson.JSON;
import com.yunchuan.bilibili.serviver.LoginService;
import com.yunchuan.bilibili.serviver.MonitorServer;
import com.yunchuan.bilibili.vo.MonitorResponseVo;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;


@Controller
public class UserController {

    @Autowired
    LoginService loginService;


    @RequestMapping("login.html")
    public String login(){
        return "login";
    }


    @ResponseBody
    @RequestMapping("login")
    public String loginByPassword(@RequestParam("username") String username, @RequestParam("password") String password , HttpServletRequest request) throws InterruptedException {
        MonitorResponseVo vo = loginService.login(username, password);
        HashMap<String, Object> data = new HashMap<>();
        if (vo != null) {
            request.getSession().setAttribute("monitorResponse",vo);
            // 登陆成功，跳转业务
            data.put("code",200);
            data.put("status",true);
            String s = JSON.toJSONString(data);
            return s;
        }
        data.put("status",false);
        data.put("code",400);
        data.put("msg","用户名或密码错误!");
        String s = JSON.toJSONString(data);
        return s;

    }

}

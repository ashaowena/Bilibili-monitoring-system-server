package com.yunchuan.bilibili.serviver;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yunchuan.bilibili.dao.UpGroupDAO;
import com.yunchuan.bilibili.dao.UserDAO;

import com.yunchuan.bilibili.dao.UserToGroupDAO;
import com.yunchuan.bilibili.entity.UpGroup;
import com.yunchuan.bilibili.entity.User;
import com.yunchuan.bilibili.entity.UserToGroup;
import com.yunchuan.bilibili.vo.MonitorResponseVo;
import com.yunchuan.bilibili.vo.UpGroupVo;
import com.yunchuan.bilibili.vo.videos.UpVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Service
public class LoginService {

    @Autowired
    UserDAO userDao;

    @Autowired
    UserToGroupDAO userToGroupDAO;

    @Autowired
    UpGroupDAO upGroupDAO;

    public MonitorResponseVo login(String username, String password) {
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            return null;
        }
        MonitorResponseVo responseVo = new MonitorResponseVo();
        User userInfo = userDao.selectOne(new QueryWrapper<User>().eq("username",username).eq("password",password));
        if (userInfo == null) {
            // 账号密码错误
            return null;
        }
        responseVo.setUser(userInfo);
        return responseVo;
    }
}

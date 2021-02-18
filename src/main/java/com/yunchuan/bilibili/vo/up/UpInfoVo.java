package com.yunchuan.bilibili.vo.up;


import lombok.Data;

import java.util.Date;

@Data
public class UpInfoVo {
    // uid
    private String mid;
    // 昵称
    private String name;
    // 性别
    private String sex;
    // 头像
    private String face;
    // 个性签名
    private String sign;
    // 等级
    private Integer level;
    // 视频总数   *
    private Integer videos;
    // 会员类型
    private String vip;
    // 粉丝总数   *
    private Integer fans;
    // 最新更新时间
    private Date date;
    // 认证类型
    private String official;
}

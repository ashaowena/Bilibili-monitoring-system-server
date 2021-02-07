package com.yunchuan.bilibili.entity;

import java.io.Serializable;
import lombok.Data;

/**
 * @author 
 * 
 */
@Data
public class UpDetail implements Serializable {
    /**
     * 主键
     */
    private Integer id;

    /**
     * uid
     */
    private String uid;

    /**
     * 昵称
     */
    private String name;

    /**
     * 头像
     */
    private String image;

    /**
     * 等级
     */
    private Byte level;

    /**
     * 认证类型
     */
    private String official;

    /**
     * 会员类型
     */
    private String vip;

    /**
     * 个性签名
     */
    private String sign;

    private static final long serialVersionUID = 1L;
}
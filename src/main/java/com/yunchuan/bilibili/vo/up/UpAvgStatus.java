package com.yunchuan.bilibili.vo.up;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.Date;

@Data
public class UpAvgStatus {

    private Integer id;

    /**
     * up主id
     */
    private String uid;

    /**
     * 日期
     */
    private Date date;

    /**
     * 粉丝数
     */
    private Integer fans;

    /**
     * 作品数
     */
    private Integer productions;

    /**
     * 观看数
     */
    private Integer view;

    /**
     * 弹幕数
     */
    private Integer danmaku;

    /**
     * 评论数
     */
    private Integer reply;

    /**
     * 收藏数
     */
    private Integer favorite;

    /**
     * 投币数
     */
    private Integer coin;

    /**
     * 分享数
     */
    private Integer share;

    /**
     * 点赞数
     */
    private Integer like;
}

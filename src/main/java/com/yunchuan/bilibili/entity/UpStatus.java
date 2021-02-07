package com.yunchuan.bilibili.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author 
 * 
 */
@Data
@TableName("up_status")
public class UpStatus implements Serializable {
    @TableId(type = IdType.AUTO)
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
    @TableField("`like`")
    private Integer like;

    private static final long serialVersionUID = 1L;

    public UpStatus init() {
        UpStatus upStatus = new UpStatus();
        upStatus.setView(0);
        upStatus.setFans(0);
        upStatus.setProductions(0);
        upStatus.setCoin(0);
        upStatus.setDanmaku(0);
        upStatus.setReply(0);
        upStatus.setFavorite(0);
        upStatus.setShare(0);
        upStatus.setLike(0);
        return upStatus;
    }
}
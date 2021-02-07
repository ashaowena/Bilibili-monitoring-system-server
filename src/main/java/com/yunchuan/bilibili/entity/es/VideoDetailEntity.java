package com.yunchuan.bilibili.entity.es;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class VideoDetailEntity {

    private String bvid;
    // 新增视频id
    private String aid;
    // up主id
    private String mid;
    // oid
    private String cid;

    private Date ctime;

    private Integer view;

    private Integer danmaku;

    private Integer reply;

    private Integer favorite;

    private Integer coin;

    private Integer share;

    private Integer like;

    private String pic;

    private String title;

    private String description;
    // 是否自制 1、自制 2、转载
    private Integer copyright;
    // 是否联合创作
    private Boolean is_union_video;

    private String length;

    private Date update_date;
    // 视频分区
    private String tName;
    // 视频标签
    private String tag;
    // 视频评论
    private VideoReplyWrapper reply_text;
    // 弹幕评论
    private List<String> danmaku_text;

}

package com.yunchuan.bilibili.vo.videodetail;

import lombok.Data;

@Data
public class VideosAbstractResponseVo {

    // up主信息
    private String uid;
    // 0:平均数 1：中位数
    private Integer type;
    // 作品数
    private Integer productions;
    // 观看
    private Integer view;
    // 弹幕
    private Integer danmaku;
    // 评论
    private Integer reply;
    // 收藏
    private Integer favorite;
    // 投币
    private Integer coin;
    // 分享
    private Integer share;
    // 点赞
    private Integer like;
}

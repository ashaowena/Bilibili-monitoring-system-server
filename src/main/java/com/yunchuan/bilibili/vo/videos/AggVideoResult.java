package com.yunchuan.bilibili.vo.videos;

import com.yunchuan.bilibili.entity.es.VideoDetailEntity;

import lombok.Data;



@Data
public class AggVideoResult {
    // up主信息
    private int uid;
    // 观看
    private int view;
    // 弹幕
    private int danmaku;
    // 评论
    private int reply;
    // 收藏
    private int favorite;
    // 投币
    private int coin;
    // 分享
    private int share;
    // 点赞
    private int like;


    public void buildResult(VideoDetailEntity detail){
        view += detail.getView();
        danmaku += detail.getDanmaku();
        reply += detail.getReply();
        favorite += detail.getFavorite();
        coin += detail.getCoin();
        share += detail.getShare();
        like += detail.getLike();
    }
}

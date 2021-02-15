package com.yunchuan.bilibili.vo.videos.video;

import lombok.Data;

import java.util.List;

@Data
public class VideoReply {

    // 评论用户id
    private Integer mid;
    // 评论用户等级
    private Integer current_level;
    // 评论视频id
    private Integer oid;
    // 评论
    private String message;
    // 点赞
    private Integer like;
    // 子评论
//    private List<VideoReply> replies;
    // 是否被删除(0,未删，1,以删)
    private boolean isDeleted;

}

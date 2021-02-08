package com.yunchuan.bilibili.vo.videos.video;

import lombok.Data;

import java.util.List;

@Data
public class VideoReply {

    // 评论id
    private Long rpid;
    // 评论
    private String message;
    // 点赞
    private Integer like;
    // 子评论
//    private List<VideoReply> replies;
    // 是否被删除(0,未删，1,以删)
    private boolean isDeleted;

}

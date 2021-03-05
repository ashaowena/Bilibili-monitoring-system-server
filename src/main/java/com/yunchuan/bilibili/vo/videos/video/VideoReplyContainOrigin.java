package com.yunchuan.bilibili.vo.videos.video;


import lombok.Data;

@Data
public class VideoReplyContainOrigin extends VideoReply {
    // 视频标题
    private String title;
    // 视频id
    private String bvid;
    // Up主的uid
    private String uid;

}

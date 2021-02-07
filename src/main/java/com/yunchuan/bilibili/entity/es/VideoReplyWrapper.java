package com.yunchuan.bilibili.entity.es;

import com.yunchuan.bilibili.vo.videos.video.VideoReply;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoReplyWrapper {
    // 普通评论
    private List<VideoReply> replies;
    // 顶置评论
    private VideoReply upper;
    // 热评
    private List<VideoReply> hot;
}

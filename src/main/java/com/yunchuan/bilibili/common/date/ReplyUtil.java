package com.yunchuan.bilibili.common.date;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.yunchuan.bilibili.entity.es.VideoDetailEntity;
import com.yunchuan.bilibili.entity.es.VideoReplyWrapper;
import com.yunchuan.bilibili.vo.videos.video.VideoReply;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;



public class ReplyUtil {

    private ReplyUtil(){}

    public static VideoReplyWrapper getVideoReplies(String content) {
        if (StringUtils.isEmpty(content)) {
            return null;
        }

        JSONObject data = JSONObject.parseObject(content).getJSONObject("data");

        List<VideoReply> replies = parseReplies(data.getJSONArray("replies"));
        List<VideoReply> hots = parseReplies(data.getJSONArray("hots"));
        VideoReply upper = parseReply(data.getString("upper"));

        return new VideoReplyWrapper(replies,upper,hots);
    }

    public static List<VideoReply> parseReplies(JSONArray array) {
        if (CollectionUtils.isEmpty(array)) {
            return null;
        }
        List<VideoReply> videoReplies = new ArrayList<>();

        for (int i = 0; i < array.size(); i++) {
            VideoReply videoReply = new VideoReply();
            JSONObject item = array.getJSONObject(i);
            String message = item.getJSONObject("content").getString("message");
            Long rpid = item.getLong("rpid");
            Integer like = item.getInteger("like");
            // 递归获取评论下的子评论
            List<VideoReply> subReplys = parseReplies(item.getJSONArray("replies"));
            videoReply.setRpid(rpid);
            videoReply.setMessage(message);
            videoReply.setLike(like);
            videoReply.setReplies(subReplys);
            videoReplies.add(videoReply);
        }

        return videoReplies;
    }

    public static VideoReply parseReply(String upperReply) {
        String reply = null;
        if (upperReply == null && (reply = JSONObject.parseObject(upperReply).getString("top")) == null) {
            return null;
        }
        return JSONObject.parseObject(reply, new TypeReference<VideoReply>() {
        });
    }



    public static void pageableAdapt(VideoDetailEntity bvid, VideoReplyWrapper replyWrapper) {
        synchronized (bvid) {
            if (bvid.getReply_text() == null) {
                bvid.setReply_text(replyWrapper);
            } else {
                VideoReplyWrapper reply_text = bvid.getReply_text();
                reply_text.getReplies().addAll(replyWrapper.getReplies());
            }

        }
    }
}

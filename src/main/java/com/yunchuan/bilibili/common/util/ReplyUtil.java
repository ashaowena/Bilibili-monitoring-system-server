package com.yunchuan.bilibili.common.util;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yunchuan.bilibili.entity.es.VideoDetailEntity;
import com.yunchuan.bilibili.vo.videos.video.VideoReply;
import com.yunchuan.bilibili.vo.videos.video.VideoReplyContainOrigin;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;


public class ReplyUtil {

    private ReplyUtil(){}

    public static List<VideoReply> getVideoReplies(String content) {
        if (StringUtils.isEmpty(content)) {
            return null;
        }
        List<VideoReply> videoReplies = new ArrayList<>();

        JSONObject data = JSONObject.parseObject(content).getJSONObject("data");

        List<VideoReply> replies = parseReplies(data.getJSONArray("replies"), null);
        List<VideoReply> hots = parseReplies(data.getJSONArray("hots"), null);
        List<VideoReply> upper = parseReply(data.getJSONObject("upper"));

        if (replies != null) {
            videoReplies.addAll(replies);
        }

        if (hots != null) {
            videoReplies.addAll(hots);
        }

        if (upper != null) {
            videoReplies.addAll(upper);
        }


        return videoReplies;
    }

    public static List<VideoReply> parseReplies(JSONArray array, List<VideoReply> replies) {
        if (CollectionUtils.isEmpty(array)) {
            return null;
        }

        if (replies == null) {
            replies = new ArrayList<>();
        }


        for (int i = 0; i < array.size(); i++) {
            VideoReply videoReply = new VideoReply();
            JSONObject item = array.getJSONObject(i);
            if (item.getJSONObject("content") == null) {
                return new ArrayList<VideoReply>();
            }
            String message = item.getJSONObject("content").getString("message");
            Integer mid = item.getInteger("mid");
            Integer oid = item.getInteger("oid");
            Integer level = item.getJSONObject("member").getJSONObject("level_info").getInteger("current_level");
            Integer like = item.getInteger("like");
            Date ctime = item.getDate("ctime");
            String face = item.getJSONObject("member").getString("avatar");
            String name = item.getJSONObject("member").getString("uname");
            String rpid = item.getString("rpid");
            // 递归获取评论下的子评论
            parseReplies(item.getJSONArray("replies"), replies);

            videoReply.setRpid(rpid);
            videoReply.setMid(mid);
            videoReply.setOid(oid);
            videoReply.setCurrent_level(level);
            videoReply.setMessage(message);
            videoReply.setLike(like);
            videoReply.setCtime(ctime);
            videoReply.setFace(face);
            videoReply.setName(name);
            replies.add(videoReply);
        }

        return replies;
    }

    public static List<VideoReply> parseReply(JSONObject upperReply) {
        if (upperReply == null || upperReply.getString("top") == null) {
            return null;
        }
        JSONArray array = new JSONArray();
        array.add(upperReply);

        List<VideoReply> videoReplies = parseReplies(array, null);

        return videoReplies;
    }



    public static void pageableAdapt(VideoDetailEntity bvid, List<VideoReply> replies) {
        synchronized (bvid) {
            if (CollectionUtils.isEmpty(bvid.getReply_text())) {
                bvid.setReply_text(replies);
            } else {
                bvid.getReply_text().addAll(replies);
            }

        }
    }

    public static List<VideoReplyContainOrigin> flattingReplies(Set<VideoDetailEntity> bvids) {
        List<VideoReplyContainOrigin> replies = new ArrayList<>(bvids.size() * 10);
        for (VideoDetailEntity bvid : bvids) {
            for (VideoReply videoReply : bvid.getReply_text()) {
                VideoReplyContainOrigin containOrigin = new VideoReplyContainOrigin();
                BeanUtils.copyProperties(videoReply,containOrigin);
                containOrigin.setTitle(bvid.getTitle());
                containOrigin.setBvid(bvid.getBvid());
                containOrigin.setUid(bvid.getMid());
                replies.add(containOrigin);
            }
        }
        return replies;
    }
}

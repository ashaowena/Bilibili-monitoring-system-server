package com.yunchuan.bilibili.controller.monitor;


import com.yunchuan.bilibili.common.response.R;
import com.yunchuan.bilibili.entity.es.VideoDetailEntity;
import com.yunchuan.bilibili.serviver.VideoService;
import com.yunchuan.bilibili.vo.publicoptions.PublicOptionsResponseVo;
import com.yunchuan.bilibili.vo.videodetail.VideoKeywordQueryWrapper;
import com.yunchuan.bilibili.vo.videodetail.VideosAbstractResponseVo;
import com.yunchuan.bilibili.vo.videos.video.VideoReply;
import com.yunchuan.bilibili.vo.videos.video.VideoReplyContainOrigin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.List;


@Controller
public class VideoListController {

    @Autowired
    VideoService videoService;

    /**
     * 作品列表
     * @return
     */
    @ResponseBody
    @RequestMapping("/VideoList")
    public R showVideoList(@RequestBody VideoKeywordQueryWrapper wrapper) throws IOException {
        List<VideoDetailEntity> videos  = videoService.getVideoList(wrapper);
        return R.ok().setData(videos);
    }

    /**
     * 作品标签
     * @return
     */
    @ResponseBody
    @RequestMapping("/VideoTags")
    public R showVideosTName(@RequestParam String uid) throws IOException {
        List<String> tags = videoService.getVideosTag(uid);
        return R.ok().setData(tags);
    }


    /**
     * 视频分类
     * @return
     */
    @ResponseBody
    @RequestMapping("/VideoTNames")
    public R showVideoTag(@RequestParam String uid) throws IOException {
        List<String> tags = videoService.getVideosTNames(uid);
        return R.ok().setData(tags);
    }



    /**
     * 作品概览
     * @param uid
     * @param type
     * @param period
     * @return
     */
    @ResponseBody
    @RequestMapping("/ProductionsAbstract")
    public R showVideoAbstract(@RequestParam String uid,@RequestParam Integer type,@RequestParam(defaultValue = "0") Integer period) throws IOException {
        VideosAbstractResponseVo videosAbstract = videoService.getVideoAbstract(uid,type,period);
        return R.ok().setData(videosAbstract);
    }


    /**
     * Up主弹幕与评论
     * @param uid
     * @return
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping("/PublicOption")
    public R getPublicOptions(@RequestParam String uid) throws IOException {
        PublicOptionsResponseVo vo = videoService.getPublicOptions(uid);
        return R.ok().setData(vo);
    }

    /**
     * 单个视频评论
     * @param uid
     * @param bvid
     * @return
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping("/PublicReplyOption")
    public R getVideoReplyPublicOptions(@RequestParam String uid,@RequestParam String bvid) throws IOException {
        PublicOptionsResponseVo vo = videoService.getVideoPublicReplyOptions(uid, bvid);
        return R.ok().setData(vo);
    }

    /**
     * 单个视频弹幕
     * @param uid
     * @param bvid
     * @return
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping("/PublicDanmakuOption")
    public R getVideoDanmakuPublicOptions(@RequestParam String uid,@RequestParam String bvid) throws IOException {
        PublicOptionsResponseVo vo = videoService.getPublicDanmakuOptions(uid, bvid);
        return R.ok().setData(vo);
    }

    /**
     * 单个视频评论具体内容
     * @param uid
     * @param period
     * @param keyword
     * @param bvid
     * @return
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping("/ReplyList")
    public R getReplies(@RequestParam String uid, @RequestParam(required = false) Integer period, @RequestParam(required = false) String keyword, @RequestParam(required = false) String bvid,@RequestParam Integer from, @RequestParam Integer size) throws IOException {
        List<VideoReplyContainOrigin> replyList = videoService.getReplyList(uid,keyword,period, bvid,from,size);
        return R.ok().setData(replyList);
    }

    /**
     * 单个视频弹幕
     * @param uid
     * @param bvid
     * @return
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping("/DanmakuList")
    public R getDanmaku(@RequestParam String uid, @RequestParam String bvid) throws IOException {
        List<String> danmakuList = videoService.getDanmakuList(uid, bvid);
        return R.ok().setData(danmakuList);
    }


}

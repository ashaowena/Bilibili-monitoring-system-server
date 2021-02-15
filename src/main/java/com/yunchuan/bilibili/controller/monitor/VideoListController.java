package com.yunchuan.bilibili.controller.monitor;

import com.alibaba.fastjson.JSONObject;
import com.yunchuan.bilibili.common.response.R;
import com.yunchuan.bilibili.entity.es.VideoDetailEntity;
import com.yunchuan.bilibili.serviver.VideoService;
import com.yunchuan.bilibili.vo.videodetail.VideoKeywordQueryWrapper;
import com.yunchuan.bilibili.vo.videodetail.VideosAbstractResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
     * 作品分类
     * @return
     */
    @ResponseBody
    @RequestMapping("/VideoTName")
    public R showVideoTName() {
        List<String> tName = videoService.getVideoTName();
        return R.ok().setData(tName);
    }

    /**
     * 视频标签
     * @return
     */
    @ResponseBody
    @RequestMapping("/VideoTag")
    public R showVideoTag() {

        return R.ok();
    }

    /**
     * 作品概览
     * @param uid
     * @param type
     * @param period
     * @return
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping("/ProductionsAbstract")
    public R showVideoAbstract(@RequestParam String uid,@RequestParam Integer type,@RequestParam Integer period) throws IOException {
        VideosAbstractResponseVo videosAbstract = videoService.getVideoAbstract(uid,type,period);
        return R.ok().setData(videosAbstract);
    }

}

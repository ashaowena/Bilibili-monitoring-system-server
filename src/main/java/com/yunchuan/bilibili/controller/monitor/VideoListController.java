package com.yunchuan.bilibili.controller.monitor;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class VideoListController {

    @RequestMapping("/VideoList")
    public String showVideoList(@RequestParam String BloggerId) {
        return "videoList";
    }

    @RequestMapping("/ShowVideoInfo")
    public String showVideoInfo() {
        return "videoInfo";
    }

}

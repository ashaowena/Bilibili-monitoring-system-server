package com.yunchuan.bilibili.vo.videos;

import lombok.Data;

import java.util.Date;

@Data
public class VideoDetailFromList {

    private String bvid;

    private String aid;

    private String mid;

    private String tName;

    private String pic;

    private String length;

    private Date created;

    private String title;

    private String description;

    private Boolean is_union_video;


}

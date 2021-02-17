package com.yunchuan.bilibili.vo.videodetail;

import com.yunchuan.bilibili.common.util.Page;
import lombok.Data;

import java.util.Date;

@Data
public class VideoKeywordQueryWrapper {

    private Page page;
    // Up主id
    private Integer uid;
    // 视频分类
    private String tName;
    // 是否团队视频 0：不查找 1：是
    private Integer is_union_video;
    // 是否自制 0：不查找 1：是
    private Integer copyright;

    private Date begin;

    private Date end;
    // 0:最新   1：最热
    private Integer order;
    // 关键字查询
    private VideoKeyword keywordWrapper;




}

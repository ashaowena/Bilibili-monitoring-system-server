/**
  * Copyright 2020 json.cn 
  */
package com.yunchuan.bilibili.vo.videos.video;

import lombok.Data;
import java.util.Date;

/**
 * Auto-generated: 2020-12-30 6:29:16
 *
 * @author json.cn (i@json.cn)
 * @website http://www.json.cn/java2pojo/
 */
@Data
public class Stat {

    private Integer aid;

    private Integer view;

    private Date ctime;

    private Integer danmaku;

    private Integer reply;

    private Integer favorite;

    private Integer coin;

    private Integer share;

    private Integer like;
    // 是否自制 0：不是 1：是
    private Integer is_cooperation;


}
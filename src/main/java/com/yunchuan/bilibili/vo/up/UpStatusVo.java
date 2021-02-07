package com.yunchuan.bilibili.vo.up;


import com.yunchuan.bilibili.entity.UpStatus;
import com.yunchuan.bilibili.vo.up.UpFollowerVo;
import com.yunchuan.bilibili.vo.up.UpInfoVo;
import lombok.Data;

@Data
public class UpStatusVo {
    /**
     * 用户关注与粉丝数
     */
    private UpInfoVo info;
    /**
     * up总播放等其他信息
     */
    private UpStatus upStatus;

}

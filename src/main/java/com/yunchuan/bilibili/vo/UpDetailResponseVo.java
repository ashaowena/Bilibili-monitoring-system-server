package com.yunchuan.bilibili.vo;

import com.yunchuan.bilibili.entity.UpStatus;
import com.yunchuan.bilibili.vo.up.UpAvgStatus;
import com.yunchuan.bilibili.vo.up.UpInfoVo;

import lombok.Data;

import java.util.List;

@Data
public class UpDetailResponseVo {

    /**
     * 用户关注与粉丝数
     */
    private UpInfoVo info;
    /**
     * up最新总播放等其他信息
     */
    private UpStatus upStatus;
    /**
     * up平均数据
     */
    private UpAvgStatus upAvgStatus;
    /**
     * 带周期的信息
     */
    private List<UpStatusAfterTranslatedVo> periodUpStatus;


}

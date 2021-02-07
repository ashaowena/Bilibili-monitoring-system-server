package com.yunchuan.bilibili.vo.videos;

import com.yunchuan.bilibili.entity.UpStatus;
import com.yunchuan.bilibili.vo.UpStatusAfterTranslatedVo;
import com.yunchuan.bilibili.vo.up.UpInfoVo;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class UpVo {

    /**
     * up主基本信息
     */
    private String uid;

    private UpInfoVo info;

    private UpStatus upStatus;

    private List<UpStatusAfterTranslatedVo> usat;

}

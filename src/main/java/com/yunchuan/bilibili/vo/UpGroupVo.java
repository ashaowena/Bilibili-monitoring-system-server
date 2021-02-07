package com.yunchuan.bilibili.vo;

import com.yunchuan.bilibili.vo.videos.UpVo;
import lombok.Data;

import java.util.List;

@Data
public class UpGroupVo {

    private Integer id;

    private String groupName;

    private List<UpVo> upVos;


}

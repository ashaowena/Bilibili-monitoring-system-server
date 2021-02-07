package com.yunchuan.bilibili.vo;



import com.yunchuan.bilibili.entity.User;
import lombok.Data;

import java.util.List;


@Data
public class MonitorResponseVo {

    private User user;

    private List<UpGroupVo> upgroups;


}

package com.yunchuan.bilibili.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author 
 * 
 */
@Data
@TableName("group_to_up")
public class GroupToUp implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer groupId;

    private Integer upId;

    private static final long serialVersionUID = 1L;
}
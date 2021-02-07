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
@TableName("user_to_group")
public class UserToGroup implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer userId;

    private Integer groupId;

    private static final long serialVersionUID = 1L;
}
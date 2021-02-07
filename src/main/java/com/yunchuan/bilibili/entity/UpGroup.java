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
@TableName("up_group")
public class UpGroup implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String groupName;

    private String up;

    private static final long serialVersionUID = 1L;
}
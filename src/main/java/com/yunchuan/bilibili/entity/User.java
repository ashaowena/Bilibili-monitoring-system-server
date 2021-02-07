package com.yunchuan.bilibili.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author 
 * 用户信息
 */
@Data
@TableName("user")
public class User implements Serializable {
    private Integer id;

    private String username;

    private static final long serialVersionUID = 1L;
}
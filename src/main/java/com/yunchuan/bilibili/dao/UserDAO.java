package com.yunchuan.bilibili.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yunchuan.bilibili.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * UserDAO继承基类
 */
@Mapper
@Repository
public interface UserDAO extends MyBatisBaseDao<User, Long>, BaseMapper<User> {
}
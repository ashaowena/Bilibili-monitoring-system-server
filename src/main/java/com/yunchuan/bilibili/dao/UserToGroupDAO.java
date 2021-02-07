package com.yunchuan.bilibili.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yunchuan.bilibili.entity.UserToGroup;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * UserToGroupDAO继承基类
 */
@Mapper
@Repository
public interface UserToGroupDAO extends MyBatisBaseDao<UserToGroup, Integer>, BaseMapper<UserToGroup> {
}
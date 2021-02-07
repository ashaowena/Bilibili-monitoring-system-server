package com.yunchuan.bilibili.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yunchuan.bilibili.entity.UpGroup;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * UpGroupDAO继承基类
 */
@Mapper
@Repository
public interface UpGroupDAO extends MyBatisBaseDao<UpGroup, Integer>, BaseMapper<UpGroup> {
}
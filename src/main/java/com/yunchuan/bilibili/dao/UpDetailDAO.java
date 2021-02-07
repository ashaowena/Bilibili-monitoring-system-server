package com.yunchuan.bilibili.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yunchuan.bilibili.entity.UpDetail;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * UpDetailDAO继承基类
 */
@Mapper
@Repository
public interface UpDetailDAO extends MyBatisBaseDao<UpDetail, Integer>, BaseMapper<UpDetail> {
}
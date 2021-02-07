package com.yunchuan.bilibili.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yunchuan.bilibili.entity.UpStatus;
import com.yunchuan.bilibili.vo.up.UpAvgStatus;
import com.yunchuan.bilibili.vo.videos.UpDateVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * UpStatusDAO继承基类
 */
@Mapper
@Repository
public interface UpStatusDAO extends BaseMapper<UpStatus> {

    @Select("select uid from up_status group by uid")
    List<String> getAllUpUids();

    @Select("select uid,max(date) date from up_status group by uid")
    List<UpDateVo> getAllUpUidsAndDate();

    @Select("select * from up_status where uid=#{uid} order by date desc limit 1")
    UpStatus getNewUpdate(@Param("uid") String uid);

//    @Select("SELECT AVG(fans) fans,AVG(productions) fans,AVG(fans) productions,AVG(VIEW) VIEW,AVG(danmaku) danmaku,AVG(reply) reply ,AVG(favorite) favorite,AVG(coin) coin,AVG(SHARE) SHARE,AVG(`like`) `like` FROM up_status WHERE uid=#{uid} ")
//    UpAvgStatus getUpAvgStatus(@Param("uid") String uid);

}
package com.yunchuan.bilibili.vo;

import com.yunchuan.bilibili.entity.UpStatus;
import lombok.Data;

import java.util.Date;

@Data
public class UpStatusAfterTranslatedVo {

    /**
     * up主基本信息
     */
    private String uid;

    private Date date;
    // 观看新增
    private int dView;
    // 弹幕新增
    private int dDanmaku;
    // 评论新增
    private int dReply;//
    // 收藏新增
    private int dFavorite;//
    // 投币新增
    private int dCoin;//
    // 分享新增
    private int dShare;
    // 点赞新增
    private int dLike;//
    // 新增粉丝
    private int dFans;//
    // 新增视频
    private int dProductions;

//    public void build0(UpStatus upStatus0, UpStatus upStatus1) {
//        uid = upStatus0.getUid();
//        dFans = upStatus0.getFans() - upStatus1.getFans();
//        dView = upStatus0.getView() - upStatus1.getView();
//        dDanmaku = upStatus0.getDanmaku() - upStatus1.getDanmaku();
//        dReply = upStatus0.getReply() - upStatus1.getReply();
//        dFavorite = upStatus0.getFavorite() - upStatus1.getFavorite();
//        dCoin = upStatus0.getCoin() - upStatus1.getCoin();
//        dShare = upStatus0.getShare() - upStatus1.getShare();
//        dLike = upStatus0.getLike() - upStatus1.getLike();
//        dProductions = upStatus0.getProductions() - upStatus1.getProductions();
//    }

    public static UpStatusAfterTranslatedVo build(UpStatus upStatus0, UpStatus upStatus1) {
        UpStatusAfterTranslatedVo vo = new UpStatusAfterTranslatedVo();
        vo.date = upStatus0.getDate();
        vo.uid = upStatus0.getUid();
        vo.dFans = upStatus0.getFans() - upStatus1.getFans();
        vo.dView = upStatus0.getView() - upStatus1.getView();
        vo.dDanmaku = upStatus0.getDanmaku() - upStatus1.getDanmaku();
        vo.dReply = upStatus0.getReply() - upStatus1.getReply();
        vo.dFavorite = upStatus0.getFavorite() - upStatus1.getFavorite();
        vo.dCoin = upStatus0.getCoin() - upStatus1.getCoin();
        vo.dShare = upStatus0.getShare() - upStatus1.getShare();
        vo.dLike = upStatus0.getLike() - upStatus1.getLike();
        vo.dProductions = upStatus0.getProductions() - upStatus1.getProductions();
        return vo;
    }
}

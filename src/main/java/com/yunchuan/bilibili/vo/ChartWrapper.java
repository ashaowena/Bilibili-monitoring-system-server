package com.yunchuan.bilibili.vo;
import com.yunchuan.bilibili.entity.UpStatus;
import lombok.Data;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Data
public class ChartWrapper {


    private List<String> chart_X = new ArrayList<>();

    private List<Integer> fans = new ArrayList<>();

    private List<Integer> productions = new ArrayList<>();

    private List<Integer> view = new ArrayList<>();

    private List<Integer> danmaku = new ArrayList<>();

    private List<Integer> reply = new ArrayList<>();

    private List<Integer> favorite = new ArrayList<>();

    private List<Integer> coin = new ArrayList<>();

    private List<Integer> share = new ArrayList<>();

    private List<Integer> like = new ArrayList<>();

    public void add(UpStatus upStatus) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");
        chart_X.add(formatter.format(upStatus.getDate()));
        fans.add(upStatus.getFans());
        productions.add(upStatus.getProductions());
        view.add(upStatus.getView());
        danmaku.add(upStatus.getDanmaku());
        reply.add(upStatus.getReply());
        favorite.add(upStatus.getFavorite());
        coin.add(upStatus.getCoin());
        share.add(upStatus.getShare());
        like.add(upStatus.getLike());
    }

    public void add(UpStatusAfterTranslatedVo usat) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");
        chart_X.add(formatter.format(usat.getDate()));
        fans.add(usat.getDFans());
        productions.add(usat.getDProductions());
        view.add(usat.getDView());
        danmaku.add(usat.getDDanmaku());
        reply.add(usat.getDReply());
        favorite.add(usat.getDFavorite());
        coin.add(usat.getDCoin());
        share.add(usat.getDShare());
        like.add(usat.getDLike());
    }

}

package com.yunchuan.bilibili.serviver;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.yunchuan.bilibili.client.RequestFactory;
import com.yunchuan.bilibili.client.httpclient.HttpUtils;
import com.yunchuan.bilibili.common.es.ElasticSearchUtil;
import com.yunchuan.bilibili.common.util.ReplyUtil;
import com.yunchuan.bilibili.common.util.TListUtil;
import com.yunchuan.bilibili.config.ElasticSearchConfig;
import com.yunchuan.bilibili.dao.UpStatusDAO;
import com.yunchuan.bilibili.entity.UpStatus;
import com.yunchuan.bilibili.client.RequestPath;
import com.yunchuan.bilibili.entity.es.VideoDetailEntity;
import com.yunchuan.bilibili.vo.UpDetailResponseVo;
import com.yunchuan.bilibili.vo.UpGroupVo;
import com.yunchuan.bilibili.vo.UpStatusAfterTranslatedVo;
import com.yunchuan.bilibili.vo.up.UpAvgStatus;
import com.yunchuan.bilibili.vo.up.UpFollowerVo;
import com.yunchuan.bilibili.vo.up.UpInfoVo;
import com.yunchuan.bilibili.vo.videos.AggVideoResult;
import com.yunchuan.bilibili.vo.videos.UpVo;
import com.yunchuan.bilibili.vo.videos.VideoDetailFromList;
import com.yunchuan.bilibili.vo.videos.video.Stat;

import com.yunchuan.bilibili.vo.videos.video.VideoReply;
import com.yunchuan.bilibili.vo.videos.video.VideoReplyContainOrigin;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpUriRequest;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;


@SuppressWarnings("rawtypes")
@Slf4j
@Service
public class MonitorServer {



    @Autowired
    UpStatusDAO upStatusDAO;

    @Autowired
    TranslateServer translateServer;

    @Autowired
    HttpUtils client;

    @Autowired
    RestHighLevelClient esClient;

    @Autowired
    ThreadPoolExecutor executor;





    /**
     * 获取单个用户的所有信息，不保存
     *
     * @param uid
     * @throws InterruptedException
     */
    public UpStatus doMonitorUp(String uid, boolean shouldQueryDetail) throws Exception {

        // 1、获取用户的关注数、粉丝数
        UpStatus upStatus = existingUpStatus(uid);
        if (upStatus == null) {
            return null;
        }
        // 2、获取用户的点赞数，视频播放
        HttpUriRequest request0 = RequestFactory.getApacheRequest(RequestPath.UP_STAT, uid);
        String content0 = client.httpsGet(request0);
        JSONObject map0 = JSONObject.parseObject(content0);
        JSONObject data0 = map0.getJSONObject("data");
        JSONObject archive0 = data0.getJSONObject("archive");
        Integer view = archive0.getInteger("view");
        Integer likes = data0.getInteger("likes");
        upStatus.setView(view);
        upStatus.setLike(likes);
        // 3、获取用户所有投稿视频
        HttpUriRequest request1 = RequestFactory.getApacheRequest(RequestPath.VIDEO_PATH, uid);
        String content1 = client.httpsGet(request1);
        JSONObject upVideoList = JSON.parseObject(content1);
        if (upVideoList.getJSONObject("data").getJSONObject("list").getJSONObject("tlist") == null) {
            return null; // 用户没有发布任何视频，无需保存，直接返回
        }
        //获取所有视频分类
        Set<TListUtil.TWrapper> tWrapperSet = TListUtil.getTWrapperSet(content1);
        Set<VideoDetailEntity> sortableBvids = Collections.synchronizedSet(new HashSet<>());
        getVideoList(uid, tWrapperSet, sortableBvids);
        //分页查询用户所有投稿视频
        int count = upVideoList.getJSONObject("data").getJSONObject("page").getInteger("count");
        upStatus.setProductions(count);
        //4、查询所有视频信息
        getVideoDetails(sortableBvids);
        upStatus.setUid(uid);
        //5、查询所有视频详细信息，并存入ElasticSearch中
        if (shouldQueryDetail) {
            getAndSaveVideoDetail(sortableBvids);
        }
        //6、归并所有视频信息
        AggVideoResult result = aggVideoDetails(sortableBvids);
        BeanUtils.copyProperties(result, upStatus, "view", "like");
        upStatus.setDate(new Date());
        return upStatus;
    }


    private void getAndSaveVideoDetail(Set<VideoDetailEntity> bvids) throws Exception {

        if (CollectionUtils.isEmpty(bvids)) {
            return;
        }

        Set<CompletableFuture> syncSet = new HashSet<>();
        getVideoTag(bvids, syncSet);
        getVideoDanmaku(bvids, syncSet);
        getVideoReply(bvids, syncSet);
        CompletableFuture.allOf(syncSet.toArray(new CompletableFuture[0])).join();

        // 删除ES中的旧数据
        BulkRequest bulkDeleteRequest = new BulkRequest();
        for (VideoDetailEntity entity : bvids) {
            DeleteRequest deleteRequest = new DeleteRequest(ElasticSearchUtil.VIDEO_DETAIL_INDEX);
            deleteRequest.id(entity.getBvid());
            bulkDeleteRequest.add(deleteRequest);
        }
        esClient.bulk(bulkDeleteRequest, ElasticSearchConfig.COMMON_OPTIONS);

        // 插入新数据
        BulkRequest bulkAddRequest = new BulkRequest();
        for (VideoDetailEntity entity : bvids) {
            entity.setUpdate_date(new Date());
            IndexRequest indexRequest = new IndexRequest(ElasticSearchUtil.VIDEO_DETAIL_INDEX);
            indexRequest.id(entity.getBvid());
            indexRequest.source(JSONObject.toJSONString(entity), XContentType.JSON);
            bulkAddRequest.add(indexRequest);
        }
        BulkResponse bulk = esClient.bulk(bulkAddRequest, ElasticSearchConfig.COMMON_OPTIONS);
        BulkItemResponse[] items = bulk.getItems();

        for (BulkItemResponse item : items) {
            if (item.getFailureMessage() != null) {
                log.error(item.getFailureMessage());
            }
        }

        // 为了让评论也能够进行关键词检索(如果只是以数组的方式存入每个视频下，则不行)，保存评论数据
        saveRepliesDetail(bvids);

    }

    /**
     * 单独存入评论信息，做搜索
     * @param bvids
     */
    public void saveRepliesDetail(Set<VideoDetailEntity> bvids) throws IOException {
        List<VideoReplyContainOrigin> replyList = ReplyUtil.flattingReplies(bvids);

        if (CollectionUtils.isEmpty(replyList)) {
            return;
        }

        // 删除ES中的旧数据
        BulkRequest bulkDeleteRequest = new BulkRequest();
        for (VideoReplyContainOrigin entity : replyList) {
            DeleteRequest deleteRequest = new DeleteRequest(ElasticSearchUtil.REPLIES_INDEX);
            deleteRequest.id(entity.getRpid());
            bulkDeleteRequest.add(deleteRequest);
        }

        esClient.bulk(bulkDeleteRequest, ElasticSearchConfig.COMMON_OPTIONS);


        // 插入新数据
        BulkRequest bulkAddRequest = new BulkRequest();
        for (VideoReplyContainOrigin entity : replyList) {
            IndexRequest indexRequest = new IndexRequest(ElasticSearchUtil.REPLIES_INDEX);
            indexRequest.id(entity.getRpid());
            indexRequest.source(JSONObject.toJSONString(entity), XContentType.JSON);
            bulkAddRequest.add(indexRequest);
        }
        BulkResponse bulk = esClient.bulk(bulkAddRequest, ElasticSearchConfig.COMMON_OPTIONS);

        BulkItemResponse[] items = bulk.getItems();
        for (BulkItemResponse item : items) {
            if (item.getFailureMessage() != null) {
                log.error(item.getFailureMessage());
            }
        }

    }

    public void deleteUpFromEs(String uid) throws IOException {
        DeleteByQueryRequest deleteVideos = new DeleteByQueryRequest();
        deleteVideos.indices(ElasticSearchUtil.VIDEO_DETAIL_INDEX);
        deleteVideos.setQuery(QueryBuilders.termQuery("uid", uid));
        BulkByScrollResponse videosResponse = esClient.deleteByQuery(deleteVideos, ElasticSearchConfig.COMMON_OPTIONS);

        for (BulkItemResponse.Failure res : videosResponse.getBulkFailures()) {
            if (res.getCause() != null) {
                log.error(res.getMessage());
            }
        }

        DeleteByQueryRequest deleteReplies = new DeleteByQueryRequest();
        deleteReplies.indices(ElasticSearchUtil.REPLIES_INDEX);
        deleteReplies.setQuery(QueryBuilders.termQuery("uid", uid));
        BulkByScrollResponse repliesResponse = esClient.deleteByQuery(deleteReplies, ElasticSearchConfig.COMMON_OPTIONS);

        for (BulkItemResponse.Failure res : repliesResponse.getBulkFailures()) {
            if (res.getMessage() != null) {
                log.error(res.getMessage());
            }
        }

    }

    private void getVideoList(String uid, Set<TListUtil.TWrapper> tids, Set<VideoDetailEntity> sortableBvids) {
        Set<CompletableFuture> syncSet = Collections.synchronizedSet(new HashSet<>());
        for (TListUtil.TWrapper tid : tids) {
            CompletableFuture<Void> async = CompletableFuture.runAsync(() -> {
                try {
                    HttpUriRequest apacheRequest = RequestFactory.getApacheRequest(RequestPath.VIDEO_PATH, uid, 1, tid.getTid());
                    String content2 = client.httpsGet(apacheRequest);
                    JSONObject upVideoListAfterSort = JSONObject.parseObject(content2);
                    JSONArray jsonArray = upVideoListAfterSort.getJSONObject("data").getJSONObject("list").getJSONArray("vlist");
                    for (int i = 0; i < jsonArray.size(); i++) {
                        VideoDetailEntity videoDetailEntity = new VideoDetailEntity();
                        VideoDetailFromList vdfl = jsonArray.getObject(i, VideoDetailFromList.class);
                        BeanUtils.copyProperties(vdfl, videoDetailEntity);
                        videoDetailEntity.setTName(tid.getTname());
                        sortableBvids.add(videoDetailEntity);
                    }
                    Integer count = upVideoListAfterSort.getJSONObject("data").getJSONObject("page").getInteger("count");
                    if (count > 30) {
                        videoListPageHelper(uid, sortableBvids, count, tid.getTid(), syncSet);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            },executor);
            syncSet.add(async);
        }
        CompletableFuture.allOf(syncSet.toArray(new CompletableFuture[0])).join();
    }



    private void videoListPageHelper(String uid, Set<VideoDetailEntity> bvids, int count, String tid, Set<CompletableFuture> syncSet) {
        if (count <= 30) {
            return;
        }
        int[] page = new int[1];
        for (page[0] = 2; page[0] < count / 30 + 1; page[0] = page[0] + 1) {
            CompletableFuture<Void> async = CompletableFuture.runAsync(() -> {
                try {
                    HttpUriRequest request2 = RequestFactory.getApacheRequest(RequestPath.VIDEO_PATH, uid, page[0], tid);
                    String content = client.httpsGet(request2);
                    JSONObject upVideoListAfterSort = JSONObject.parseObject(content);
                    JSONArray jsonArray = upVideoListAfterSort.getJSONObject("data").getJSONObject("list").getJSONArray("vlist");
                    for (int i = 0; i < jsonArray.size(); i++) {
                        VideoDetailEntity videoDetailEntity = new VideoDetailEntity();
                        VideoDetailFromList vdfl = jsonArray.getObject(i, VideoDetailFromList.class);
                        BeanUtils.copyProperties(vdfl, videoDetailEntity);
                        bvids.add(videoDetailEntity);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            },executor);
            syncSet.add(async);
        }

    }


    public void getVideoDetails(Set<VideoDetailEntity> bvids) {
        //4、获取用户每个视频的所有信息
        int num = 0;
        CompletableFuture[] future = new CompletableFuture[bvids.size()];
        for (VideoDetailEntity bvid : bvids) {
            CompletableFuture<Void> async = CompletableFuture.runAsync(() -> {
                try {
                    HttpUriRequest request = RequestFactory.getApacheRequest(RequestPath.VIDEO_DETAIL_PATH_X, bvid.getAid());
                    String content = client.httpsGet(request);
                    JSONObject jsonObject = JSONObject.parseObject(content);
                    Stat stat = jsonObject.getJSONObject("data").getObject("stat", Stat.class);
                    BeanUtils.copyProperties(stat, bvid);

                    String cid = jsonObject.getJSONObject("data").getString("cid");
                    Date ctime = jsonObject.getJSONObject("data").getDate("pubdate");
                    Integer copyright = jsonObject.getJSONObject("data").getInteger("copyright");
                    Integer cooperation = jsonObject.getJSONObject("data").getJSONObject("rights").getInteger("is_cooperation");

                    bvid.setCopyright(copyright);
                    bvid.setCid(cid);
                    bvid.setCtime(ctime);
                    bvid.setIs_union_video(cooperation);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            },executor);
            future[num++] = async;
        }

        CompletableFuture.allOf(future).join();
    }

    public void getVideoTag(Set<VideoDetailEntity> bvids, Set<CompletableFuture> syncSet) {

        for (VideoDetailEntity bvid : bvids) {
            CompletableFuture<Void> async = CompletableFuture.runAsync(() -> {
                try {
                    HttpUriRequest request = RequestFactory.getApacheRequest(RequestPath.VIDEO_TAG, bvid.getBvid());
                    String content = client.httpsGet(request);
                    JSONObject tag = JSON.parseObject(content);
                    JSONArray datas = tag.getJSONArray("data");
                    String[] arr = new String[datas.size()];
                    for (int i = 0; i < datas.size(); i++) {
                        JSONObject data = datas.getJSONObject(i);
                        String tag_name = data.getString("tag_name");
                        arr[i] = tag_name;
                    }
                    bvid.setTag(arr);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            },executor);
            syncSet.add(async);
        }

    }

    public void getVideoReply(Set<VideoDetailEntity> bvids, Set<CompletableFuture> syncSet) {
        for (VideoDetailEntity bvid : bvids) {
            CompletableFuture<Void> async = CompletableFuture.runAsync(() -> {
                try {
                    HttpUriRequest request = RequestFactory.getApacheRequest(RequestPath.VIDEO_REPLY, bvid.getAid(), 1);
                    String content = client.httpsGet(request);
                    JSONObject jsonObject = JSONObject.parseObject(content);
                    List<VideoReply> videoReplies = ReplyUtil.getVideoReplies(content);
                    Integer count = jsonObject.getJSONObject("data").getJSONObject("page").getInteger("count");
                    videoReplyPageHelper(syncSet, bvid, count);
                    // 需要保证线程安全性
                    ReplyUtil.pageableAdapt(bvid, videoReplies);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            },executor);
            syncSet.add(async);
        }

    }

    private void videoReplyPageHelper(Set<CompletableFuture> syncSet, VideoDetailEntity bvid, Integer count) {
        int[] page = new int[1];
        for (page[0] = 2; count > 20 && page[0] <= count / 20 + 1; page[0] = page[0] + 1) {
            CompletableFuture<Void> async = CompletableFuture.runAsync(() -> {
                try {
                    HttpUriRequest request2 = RequestFactory.getApacheRequest(RequestPath.VIDEO_REPLY, bvid.getAid(), page[0]);
                    String content0 = client.httpsGet(request2);
                    List<VideoReply> videoReplies = ReplyUtil.getVideoReplies(content0);
                    ReplyUtil.pageableAdapt(bvid, videoReplies);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            },executor);
            syncSet.add(async);
        }
    }

    public void getVideoDanmaku(Set<VideoDetailEntity> bvids, Set<CompletableFuture> syncSet) throws Exception {
        int i = 0;
        for (VideoDetailEntity bvid : bvids) {
            CompletableFuture<Void> async = CompletableFuture.runAsync(() -> {
                try {
                    HttpUriRequest request2 = RequestFactory.getApacheRequest(RequestPath.VIDEO_DANMAKU, bvid.getCid());
                    String content = client.httpsGet(request2);
                    Document doc = new SAXReader().read(new ByteArrayInputStream(content.getBytes("UTF-8")));
                    List<String> danmakus = new ArrayList<>();
                    Element rootElement = doc.getRootElement();
                    Iterator<Element> d = rootElement.elementIterator("d");
                    while (d.hasNext()) {
                        danmakus.add(d.next().getText());
                    }
                    bvid.setDanmaku_text(danmakus);

                } catch (Exception e) {
                    log.info("弹幕请求被拦截");
                }

            },executor);
            syncSet.add(async);
        }

    }


    /**
     * 保存查询到的Up信息
     *
     * @param uid
     * @throws InterruptedException
     */
    public UpStatus saveMonitorUp(String uid) throws Exception {
        UpStatus upStatus = doMonitorUp(uid, true);
        if (upStatus == null) {
            return null;
        }
        upStatus.setDate(new Date());
        upStatusDAO.insert(upStatus);
        return upStatus;
    }

    /**
     * 异步更新up主信息
     * @param uid
     */
    public void saveMonitorUpAsync(String uid) {
        CompletableFuture.runAsync(() -> {
            try {
                UpStatus upStatus = doMonitorUp(uid, true);
                upStatus.setDate(new Date());
                upStatusDAO.insert(upStatus);
            } catch (Exception e) {
                log.error("更新Up主信息出错，uid: {}",uid,e.getCause());
            }
        },executor);
    }

    private AggVideoResult aggVideoDetails(Set<VideoDetailEntity> videoDetails) {
        AggVideoResult result = new AggVideoResult();
        for (VideoDetailEntity videoDetail : videoDetails) {
            result.buildResult(videoDetail);
        }
        return result;
    }

    public UpStatus existingUpStatus(String up) throws Exception {
        // 1、判断有无此up，若有，则获取粉丝数,若无,直接返回
        HttpUriRequest statRequest = RequestFactory.getApacheRequest(RequestPath.STAT_PATH, up);
        String statResponse = client.httpsGet(statRequest);
        if (statResponse == null) {
            log.warn("Up查询异常");
            return null;
        }
        Map<String, String> statMap = JSONObject.parseObject(statResponse, new TypeReference<Map<String, String>>() {
        });
        if (Integer.parseInt(statMap.get("code")) != 0) {
            log.warn("Up不存在");
            return null;
        }
        UpFollowerVo followerVo = JSONObject.parseObject(statMap.get("data"), new TypeReference<UpFollowerVo>() {
        });
        UpStatus upStatus = new UpStatus();
        upStatus.setFans(followerVo.getFollower());// 设置粉丝
        return upStatus;
    }

    public UpInfoVo getUpInfo(String up) throws Exception {
        // 2、获取基本昵称、性别、头像、等级、生日（暂不开发）等基本信息
        HttpUriRequest infoRequest = RequestFactory.getApacheRequest(RequestPath.UP_INFO_PATH, up);
        String infoResponse = client.httpsGet(infoRequest);
        JSONObject infoMap = JSONObject.parseObject(infoResponse);
        if (infoMap.getInteger("code") == 0) {
            UpInfoVo upInfoVo = infoMap.getObject("data", UpInfoVo.class);
            String vip = infoMap.getJSONObject("data").getJSONObject("vip").getJSONObject("label").getString("text");
            String official = infoMap.getJSONObject("data").getJSONObject("official").getString("title");
            upInfoVo.setVip(vip);
            upInfoVo.setOfficial(official);
            return upInfoVo;
        }
        log.warn("获取用户详细信息失败");
        return null;
    }




    public List<UpDetailResponseVo> getUpDetails(List<UpGroupVo> upgroups) {
        List<UpDetailResponseVo> vos = new ArrayList<>();
        for (UpGroupVo upgroup : upgroups) {
            List<UpVo> upVos = upgroup.getUpVos();
            for (UpVo upVo0 : upVos) {
                UpDetailResponseVo upDetail = getUpDetail(upVo0, null);
                vos.add(upDetail);
            }
        }
        return vos;
    }

    public UpDetailResponseVo getUpDetail(UpVo upVo, Integer period) {
        UpDetailResponseVo detail = new UpDetailResponseVo();
        detail.setInfo(upVo.getInfo());
        UpStatus newStatus = upStatusDAO.getNewUpdate(upVo.getUid());
        detail.setUpStatus(newStatus);
        UpAvgStatus avgStatus = getUpAvgStatus(newStatus);
        detail.setUpAvgStatus(avgStatus);
//        getTab(upVo, period, detail);
        return detail;
    }

    public List<UpStatusAfterTranslatedVo> getTab(String uid, Integer period) {
        List<UpStatusAfterTranslatedVo> translatedVos = translateServer.smartTranslate(uid, period);
        return translatedVos;
    }

    private UpAvgStatus getUpAvgStatus(UpStatus upStatus) {
        UpAvgStatus avgStatus = new UpAvgStatus();
        avgStatus.setCoin(upStatus.getCoin() / upStatus.getProductions());
        avgStatus.setDanmaku(upStatus.getDanmaku() / upStatus.getProductions());
        avgStatus.setLike(upStatus.getLike() / upStatus.getProductions());
        avgStatus.setFavorite(upStatus.getFavorite() / upStatus.getProductions());
        avgStatus.setReply(upStatus.getReply() / upStatus.getProductions());
        avgStatus.setShare(upStatus.getShare() / upStatus.getProductions());
        avgStatus.setView(upStatus.getView() / upStatus.getProductions());
        return avgStatus;
    }

    public byte[] getFace(String path) throws Exception {
        HttpUriRequest request = RequestFactory.getApacheRequest(RequestPath.FACE, null, path);
        byte[] bytes = client.httpsGetByte(request);
        return bytes;
    }


}

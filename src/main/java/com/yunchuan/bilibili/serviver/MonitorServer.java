package com.yunchuan.bilibili.serviver;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.yunchuan.bilibili.client.RequestFactory;
import com.yunchuan.bilibili.client.httpclient.HttpUtils;
import com.yunchuan.bilibili.common.es.ElasticSearchUtil;
import com.yunchuan.bilibili.common.util.ReplyUtil;
import com.yunchuan.bilibili.common.util.TListUtil;
import com.yunchuan.bilibili.config.ElasticSearchConfig;
import com.yunchuan.bilibili.dao.UpGroupDAO;
import com.yunchuan.bilibili.dao.UpStatusDAO;
import com.yunchuan.bilibili.dao.UserToGroupDAO;
import com.yunchuan.bilibili.entity.UpGroup;
import com.yunchuan.bilibili.entity.UpStatus;
import com.yunchuan.bilibili.entity.User;
import com.yunchuan.bilibili.entity.UserToGroup;
import com.yunchuan.bilibili.client.netty.RequestPath;
import com.yunchuan.bilibili.entity.es.VideoDetailEntity;
import com.yunchuan.bilibili.vo.MonitorResponseVo;
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
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;


@SuppressWarnings("rawtypes")
@Slf4j
@Service
public class MonitorServer {

    @Autowired
    UserToGroupDAO userToGroupDAO;

    @Autowired
    UpGroupDAO upGroupDAO;

    @Autowired
    UpStatusDAO upStatusDAO;

    @Autowired
    TranslateServer translateServer;

    @Autowired
    HttpUtils client;

    @Autowired
    RestHighLevelClient esClient;


    @Transactional
    public int addMonitorGroup(User user, String groupName) {
        if (groupName.startsWith("default:")) {
            return 0;
        }
        // 添加组
        UpGroup upGroup = new UpGroup();
        upGroup.setGroupName(groupName);
        upGroupDAO.insert(upGroup);
        // 添加对应关系
        UserToGroup userToGroup = new UserToGroup();
        userToGroup.setUserId(user.getId());
        userToGroup.setGroupId(upGroup.getId());
        userToGroupDAO.insert(userToGroup);
        return 200;
    }

    @Transactional
    public void deleteMonitorGroup(User user, Integer groupId) {
        userToGroupDAO.delete(new QueryWrapper<UserToGroup>().eq("group_id", groupId));
        upGroupDAO.deleteById(groupId);

    }

    @Transactional
    public synchronized int addMonitorUp(Integer groupId, String uid, User user) throws Exception {
        // 判断Up是否存在
        UpStatus upStatus = existingUpStatus(uid);
        if (upStatus == null) {
            return 0;
        }
        // 判断是否指定了分组
        if (groupId.equals(0)) {
            // 没有指定分组，添加至默认的分组，默认分组统一格式default:uid
            UpGroup upGroup = upGroupDAO.selectOne(new QueryWrapper<UpGroup>().eq("group_name", "default:" + user.getId()));
            if (upGroup == null) {
                // 没有分组，则创建默认分组
                UpGroup defaultUpGroup = new UpGroup();
                defaultUpGroup.setGroupName("default:" + user.getId());
                defaultUpGroup.setUp(";" + uid);
                upGroupDAO.insert(defaultUpGroup);
                UserToGroup userToGroup = new UserToGroup();
                userToGroup.setGroupId(defaultUpGroup.getId());
                userToGroup.setUserId(user.getId());
                userToGroupDAO.insert(userToGroup);
                return 200;
            }
            String ups = upGroup.getUp();
            String replace = ups.replace(";" + uid, "");
            upGroup.setUp(replace + ";" + uid);
            upGroupDAO.update(upGroup, new UpdateWrapper<UpGroup>().eq("group_name", "default:" + user.getId()));
            return 200;
        }
        // 修改数据，增加up
        UpGroup upGroup = upGroupDAO.selectOne(new QueryWrapper<UpGroup>().eq("id", groupId));
        String ups = upGroup.getUp();
        String replace = ups.replace(";" + uid, "");
        upGroup.setUp(replace + ";" + uid);
        upGroupDAO.updateById(upGroup);
        return 200;
    }


    public void getMonitorUp(MonitorResponseVo vo) {
        List<UpGroupVo> upgroups = vo.getUpgroups();
        for (UpGroupVo upGroupVo : upgroups) {
            List<UpVo> upVos = upGroupVo.getUpVos();
            for (UpVo upVo : upVos) {
                List<UpStatusAfterTranslatedVo> translated = null;
                UpInfoVo upInfo = null;
                try {
                    upInfo = getUpInfo(upVo.getUid());
                    // 转化为增长量
                    translated = translateServer.translate(upVo.getUid());
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    log.warn("获取信息错误");
                }
                upVo.setInfo(upInfo);
                upVo.setUsat(translated);
            }
        }
    }


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
            System.out.println("response:" + item.getFailureMessage());
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
            });
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

            });
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
                    bvid.setCid(cid);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
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
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < datas.size(); i++) {
                        JSONObject data = datas.getJSONObject(i);
                        String tag_name = data.getString("tag_name");
                        stringBuilder.append(tag_name + ";");
                    }
                    bvid.setTag(stringBuilder.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
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

            });
            syncSet.add(async);
        }

    }

    private void videoReplyPageHelper(Set<CompletableFuture> syncSet, VideoDetailEntity bvid, Integer count) {
        int[] page = new int[1];
        for (page[0] = 2; count > 20 && page[0] <= count / 20 + 1; page[0] = page[0] + 1) {
            CompletableFuture<Void> async = CompletableFuture.runAsync(() -> {
                try {
                    HttpUriRequest request2 = RequestFactory.getApacheRequest(RequestPath.VIDEO_REPLY, bvid.getAid(), page[0]);
                    System.out.println("reply:" + request2);
                    String content0 = client.httpsGet(request2);
                    List<VideoReply> videoReplies = ReplyUtil.getVideoReplies(content0);
                    ReplyUtil.pageableAdapt(bvid, videoReplies);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
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
                    e.printStackTrace();
                }

            });
            syncSet.add(async);
        }

    }


    /**
     * 保存查询到的Up信息
     *
     * @param uid
     * @throws InterruptedException
     */
    public void saveMonitorUp(String uid) throws Exception {
        UpStatus upStatus = doMonitorUp(uid, true);
        upStatus.setDate(new Date());
        upStatusDAO.insert(upStatus);
    }

    private AggVideoResult aggVideoDetails(Set<VideoDetailEntity> videoDetails) {
        AggVideoResult result = new AggVideoResult();
        for (VideoDetailEntity videoDetail : videoDetails) {
            result.buildResult(videoDetail);
        }
        return result;
    }

    private UpStatus existingUpStatus(String up) throws Exception {
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
        Map<String, String> infoMap = JSONObject.parseObject(infoResponse, new TypeReference<Map<String, String>>() {
        });
        if (Integer.parseInt(infoMap.get("code")) == 0) {
            UpInfoVo upInfoVo = JSONObject.parseObject(infoMap.get("data"), new TypeReference<UpInfoVo>() {
            });
            upInfoVo.setFace(upInfoVo.getFace().substring(19) + "/" + upInfoVo.getFace().charAt(8) + "");
            return upInfoVo;
        }
        log.warn("获取用户详细信息失败");
        return null;
    }


    public int deleteMonitorUp(String up, User user) {
        List<UserToGroup> userToGroups = userToGroupDAO.selectList(new QueryWrapper<UserToGroup>().eq("user_id", user.getId()));
        HashSet<Integer> groupIds = new HashSet<>();
        userToGroups.forEach((utg) -> {
            groupIds.add(utg.getGroupId());
        });
        List<UpGroup> upGroups = upGroupDAO.selectBatchIds(groupIds);
        upGroups.forEach((upGroup) -> {
            String ups = upGroup.getUp();
            String replace = ups.replace(";" + up, "");
            upGroup.setUp(replace);
            upGroupDAO.updateById(upGroup);
        });
        upStatusDAO.delete(new QueryWrapper<UpStatus>().eq("uid", up));
        return 200;
    }

    public MonitorResponseVo getUserGroups(MonitorResponseVo responseVo) {
        User userInfo = responseVo.getUser();
        Integer userId = userInfo.getId();
        List<UserToGroup> userToGroups = userToGroupDAO.selectList(new QueryWrapper<UserToGroup>().eq("user_id", userId));
        if (userToGroups == null) {
            // 账户没有分组，提早返回
            return responseVo;
        }
        List<UpGroupVo> upGroupVos = new ArrayList<>();
        userToGroups.forEach((e) -> {
            UpGroupVo upGroupVo = new UpGroupVo();
            UpGroup upGroup = upGroupDAO.selectOne(new QueryWrapper<UpGroup>().eq("id", e.getGroupId()));
            if (upGroup != null) {
                List<UpVo> upVos = new ArrayList<>();
                String ups = upGroup.getUp();
                if (ups != null) {
                    String[] uids = ups.split(";");
                    BeanUtils.copyProperties(upGroup, upGroupVo);
                    // 设置每个分组下的所有up主的uid
                    for (String uid : uids) {
                        if (!StringUtils.isEmpty(uid)) {
                            UpVo upVo = new UpVo();
                            upVo.setUid(uid);
                            upVos.add(upVo);
                        }
                    }
                }
                upGroupVo.setId(upGroup.getId());
                upGroupVo.setUpVos(upVos);
                upGroupVo.setGroupName(upGroup.getGroupName());
            }
            upGroupVos.add(upGroupVo);
        });
        responseVo.setUpgroups(upGroupVos);
        return responseVo;
    }

    @Transactional
    public void moveMonitorGroup(User user, Integer groupId, String uid) {
        List<UserToGroup> userToGroups = userToGroupDAO.selectList(new QueryWrapper<UserToGroup>().eq("user_id", user.getId()));
        HashSet<Integer> groupIds = new HashSet<>();
        userToGroups.forEach(utg -> {
            groupIds.add(utg.getGroupId());
        });
        List<UpGroup> upGroups = upGroupDAO.selectBatchIds(groupIds);
        for (UpGroup upGroup : upGroups) {
            if (upGroup.getId().equals(groupId)) {
                String ups = upGroup.getUp();
                String newUps = ups.replace(";" + uid,"");
                upGroup.setUp(newUps + ";" + uid);
                upGroupDAO.updateById(upGroup);
            } else {
                String ups = upGroup.getUp();
                String replace = ups.replace(";" + uid, "");
                upGroup.setUp(replace);
                upGroupDAO.updateById(upGroup);
            }
        }

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
        if (upStatus.getProductions() == 0) {
            return avgStatus;
        }
        avgStatus.setCoin(upStatus.getCoin() / upStatus.getProductions());
        avgStatus.setDanmaku(upStatus.getDanmaku() / upStatus.getProductions());
        avgStatus.setLike(upStatus.getLike() / upStatus.getProductions());
        avgStatus.setFavorite(upStatus.getFavorite() / upStatus.getProductions());
        avgStatus.setReply(upStatus.getReply() / upStatus.getProductions());
        avgStatus.setShare(upStatus.getShare() / upStatus.getProductions());
        avgStatus.setView(upStatus.getView() / upStatus.getProductions());
        return avgStatus;
    }

    public byte[] getFace(String path, String index) throws Exception {
        HttpUriRequest request = RequestFactory.getApacheRequest(RequestPath.FACE, path, "http://i" + index + ".hdslb.com");
        byte[] bytes = client.httpsGetByte(request);
        return bytes;
    }


}

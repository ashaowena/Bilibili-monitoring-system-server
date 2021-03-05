package com.yunchuan.bilibili.serviver;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.mysql.cj.x.protobuf.MysqlxDatatypes;
import com.yunchuan.bilibili.common.constant.VideoQueryType;
import com.yunchuan.bilibili.common.es.ElasticSearchUtil;
import com.yunchuan.bilibili.common.util.DateUtil;
import com.yunchuan.bilibili.common.util.Page;
import com.yunchuan.bilibili.config.ElasticSearchConfig;

import com.yunchuan.bilibili.entity.es.VideoDetailEntity;
import com.yunchuan.bilibili.vo.BarWrapper;
import com.yunchuan.bilibili.vo.publicoptions.PublicOptionsResponseVo;
import com.yunchuan.bilibili.vo.videodetail.VideoKeywordQueryWrapper;
import com.yunchuan.bilibili.vo.videodetail.VideosAbstractResponseVo;
import com.yunchuan.bilibili.vo.videos.video.VideoReply;
import com.yunchuan.bilibili.vo.videos.video.VideoReplyContainOrigin;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.json.JsonXContentParser;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.ConstantScoreQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.IncludeExclude;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.ParsedAvg;
import org.elasticsearch.search.aggregations.metrics.ParsedPercentiles;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 主要进行ElasticSearch相关操作
 */
@Service
@Slf4j
public class VideoService {

    @Autowired
    RestHighLevelClient esClient;


    public VideosAbstractResponseVo getVideoAbstract(String uid, Integer type, Integer period) throws IOException {
        Date beforeDate = DateUtil.getBeforeDate(period);
        SearchRequest request = null;
        if (type != null && type.equals(0)) {
            request = videoAbstractAVGSearchRequestBuilder(uid, beforeDate);
        }

        if (type != null && type.equals(1)) {
            request = videoAbstractMedSearchRequestBuilder(uid, beforeDate);
        }

        if (request == null) {
            return null;
        }

        SearchResponse result = esClient.search(request, ElasticSearchConfig.COMMON_OPTIONS);
        VideosAbstractResponseVo responseVo = videoAbstractSearchResponseBuilder(result);
        responseVo.setUid(uid);
        responseVo.setType(type);

        return responseVo;
    }

    private VideosAbstractResponseVo videoAbstractSearchResponseBuilder(SearchResponse result) {
        Integer count = (int) result.getHits().getTotalHits().value;

        if (count == 0) {
            VideosAbstractResponseVo vo = VideosAbstractResponseVo.init();
            return vo;
        }

        Aggregations aggregations = result.getAggregations();
        Map<String, Double> map = new HashMap<>();
        for (Aggregation aggObject : aggregations.getAsMap().values()) {
            if (aggObject instanceof ParsedAvg) {
                map.put(aggObject.getName(), ((ParsedAvg) aggObject).getValue());
            } else if (aggObject instanceof ParsedPercentiles) {
                map.put(aggObject.getName(), ((ParsedPercentiles) aggObject).iterator().next().getValue());
            } else {
                log.error("类型异常");
            }
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("vo", map);
        VideosAbstractResponseVo vo = jsonObject.getObject("vo", VideosAbstractResponseVo.class);
        vo.setProductions(count);
        return vo;
    }

    private SearchRequest videoAbstractAVGSearchRequestBuilder(String uid, Date beforeDate) {
        SearchSourceBuilder builder = new SearchSourceBuilder();

        SearchRequest searchRequest = new SearchRequest(ElasticSearchUtil.VIDEO_DETAIL_INDEX);
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.filter(QueryBuilders.rangeQuery("ctime").gte(beforeDate.getTime() / 1000));
        boolQuery.filter(QueryBuilders.termsQuery("mid", uid));

        builder.query(boolQuery);
        builder.aggregation(AggregationBuilders.avg("view").field("view"));
        builder.aggregation(AggregationBuilders.avg("like").field("like"));
        builder.aggregation(AggregationBuilders.avg("favorite").field("favorite"));
        builder.aggregation(AggregationBuilders.avg("reply").field("reply"));
        builder.aggregation(AggregationBuilders.avg("danmaku").field("danmaku"));
        builder.aggregation(AggregationBuilders.avg("coin").field("coin"));
        builder.aggregation(AggregationBuilders.avg("share").field("share"));

        return searchRequest.source(builder);
    }

    private SearchRequest videoAbstractMedSearchRequestBuilder(String uid, Date beforeDate) {
        SearchSourceBuilder builder = new SearchSourceBuilder();

        SearchRequest searchRequest = new SearchRequest(ElasticSearchUtil.VIDEO_DETAIL_INDEX);
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.filter(QueryBuilders.rangeQuery("ctime").gte(beforeDate.getTime() / 1000));
        boolQuery.filter(QueryBuilders.termsQuery("mid", uid));

        builder.query(boolQuery);
        builder.aggregation(AggregationBuilders.percentiles("view").field("view").percentiles(50));
        builder.aggregation(AggregationBuilders.percentiles("like").field("like").percentiles(50));
        builder.aggregation(AggregationBuilders.percentiles("favorite").field("favorite").percentiles(50));
        builder.aggregation(AggregationBuilders.percentiles("reply").field("reply").percentiles(50));
        builder.aggregation(AggregationBuilders.percentiles("danmaku").field("danmaku").percentiles(50));
        builder.aggregation(AggregationBuilders.percentiles("coin").field("coin").percentiles(50));
        builder.aggregation(AggregationBuilders.percentiles("share").field("share").percentiles(50));

        return searchRequest.source(builder);
    }

    public List<VideoDetailEntity> getVideoList(VideoKeywordQueryWrapper wrapper) throws IOException {
        List<VideoDetailEntity> entities = new ArrayList<>();
        SearchRequest request = videoListRequestBuilder(wrapper);
        SearchResponse response = esClient.search(request, ElasticSearchConfig.COMMON_OPTIONS);
        SearchHit[] hits = response.getHits().getHits();
        for (SearchHit hit : hits) {
            VideoDetailEntity entity = JSONObject.parseObject(hit.getSourceAsString(), VideoDetailEntity.class);
            entities.add(entity);
        }

        return entities;
    }

    private SearchRequest videoListRequestBuilder(VideoKeywordQueryWrapper wrapper) {
        if (wrapper == null) {
            return null;
        }
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        boolQuery.filter(QueryBuilders.termQuery("mid", wrapper.getUid()));

        if (!StringUtils.isEmpty(wrapper.getTName())) {
            boolQuery.filter(QueryBuilders.termsQuery("tName", wrapper.getTName()));
        }
        if (wrapper.getBegin() != null) {
            boolQuery.filter(QueryBuilders.rangeQuery("ctime").gte(wrapper.getBegin().getTime() / 1000));
        }
        if (wrapper.getEnd() != null) {
            boolQuery.filter(QueryBuilders.rangeQuery("ctime").lte(wrapper.getEnd().getTime() / 1000));
        }
        if (wrapper.getIs_union_video() != null && wrapper.getIs_union_video() != 0) {
            boolQuery.filter(QueryBuilders.termQuery("is_union_video", 1));
        }
        if (wrapper.getCopyright() != null && wrapper.getCopyright() != 0) {     // 这里强转解决编译器报多方法匹配异常BUG
            boolQuery.filter(QueryBuilders.termsQuery("copyright", (Object) 1));
        }

        if (wrapper.getKeywordWrapper() != null && !StringUtils.isEmpty(wrapper.getKeywordWrapper().getKeyword())) {
            String keyword = wrapper.getKeywordWrapper().getKeyword();
            BoolQueryBuilder keywordQuery = QueryBuilders.boolQuery();
            switch (wrapper.getKeywordWrapper().getSearchType()) {
                case VideoQueryType.TOTAL:
                    keywordQuery.should(QueryBuilders.matchQuery("tag", keyword));
                    keywordQuery.should(QueryBuilders.matchQuery("description", keyword));
                    keywordQuery.should(QueryBuilders.matchQuery("danmaku_text", keyword));
                    keywordQuery.should(QueryBuilders.matchQuery("tag", keyword));
                    keywordQuery.should(QueryBuilders.matchQuery("reply_text.message", keyword));

                    break;
                case VideoQueryType.TITLE_AND_DESCRIPTION:
                    keywordQuery.should(QueryBuilders.matchQuery("title", keyword));
                    keywordQuery.should(QueryBuilders.matchQuery("description", keyword));
                    break;
                case VideoQueryType.DANMAKU:
                    keywordQuery.should(QueryBuilders.matchQuery("danmaku_text", keyword));
                    break;
                case VideoQueryType.TAG:
                    keywordQuery.should(QueryBuilders.matchQuery("tag", keyword));
                    break;
                case VideoQueryType.REPLY:
                    keywordQuery.should(QueryBuilders.matchQuery("reply_text.message", keyword));
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + wrapper.getKeywordWrapper().getSearchType());
            }
            boolQuery.must(keywordQuery);
        }

        SearchSourceBuilder builder = new SearchSourceBuilder();
        // 去除评分系统节省内存
        builder.query(new ConstantScoreQueryBuilder(boolQuery));
        // 分页处理
        esPageHelper(builder, wrapper.getPage());
        // 排序处理
        order(builder, wrapper.getOrder());

        return new SearchRequest(ElasticSearchUtil.VIDEO_DETAIL_INDEX).source(builder);
    }

    private void esPageHelper(SearchSourceBuilder searchSourceBuilder, Page page) {
        if (page != null && page.getCurrPage() != null && page.getPageSize() != null) {
            searchSourceBuilder.from(page.getCurrPage());
            searchSourceBuilder.size(page.getPageSize());
        }
    }

    private void order(SearchSourceBuilder searchSourceBuilder, Integer type) {
        if (type == null || type == 0) {
            searchSourceBuilder.sort("ctime", SortOrder.DESC);
        } else if (type == 1) {
            // 保留自定义热度排序
        } else {
            searchSourceBuilder.sort("view", SortOrder.DESC);
        }
    }

    public List<String> getVideosTName(String uid) throws IOException {

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.filter(QueryBuilders.termQuery("mid", uid));

        TermsAggregationBuilder term_agg = AggregationBuilders.terms("tag").field("tag");


        SearchSourceBuilder builder = new SearchSourceBuilder();

        builder.query(boolQuery);
        builder.aggregation(term_agg);

        SearchResponse response = esClient.search(new SearchRequest(ElasticSearchUtil.VIDEO_DETAIL_INDEX).source(builder), ElasticSearchConfig.COMMON_OPTIONS);

        Aggregations aggregations = response.getAggregations();
        ParsedTerms tag = aggregations.get("tag");

        List<String> tags = new ArrayList<>(16);
        for (Terms.Bucket bucket : tag.getBuckets()) {
            tags.add(bucket.getKeyAsString());
        }

        return tags;
    }


    public List<String> getVideosTagsName(String uid) throws IOException {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.filter(QueryBuilders.termQuery("mid", uid));

        TermsAggregationBuilder term_agg = AggregationBuilders.terms("tName").field("tName");


        SearchSourceBuilder builder = new SearchSourceBuilder();

        builder.query(boolQuery);
        builder.aggregation(term_agg);

        SearchResponse response = esClient.search(new SearchRequest(ElasticSearchUtil.VIDEO_DETAIL_INDEX).source(builder), ElasticSearchConfig.COMMON_OPTIONS);

        Aggregations aggregations = response.getAggregations();
        ParsedTerms tName = aggregations.get("tName");

        List<String> tNames = new ArrayList<>(16);
        for (Terms.Bucket bucket : tName.getBuckets()) {
            tNames.add(bucket.getKeyAsString());
        }

        return tNames;
    }

    public PublicOptionsResponseVo getPublicOptions(String uid) throws IOException {
        PublicOptionsResponseVo vo = new PublicOptionsResponseVo();
        BarWrapper replyBar = getWordCloud("reply_text.message", uid);
        BarWrapper danmakuBar = getWordCloud("danmaku_text", uid);
        vo.setReplyBar(replyBar);
        vo.setDanmakuBar(danmakuBar);
        return vo;
    }

    private BarWrapper getWordCloud(String field, String uid) throws IOException {
        TermQueryBuilder termUID = QueryBuilders.termQuery("mid", uid);

        StringBuilder regExp = new StringBuilder("[\u4E00-\u9FA5][\u4E00-\u9FA5]");
        Map<String, Integer> docNumMap = new HashMap<>();
        for (int i = 0; i < 3; i++) {
            SearchRequest request = new SearchRequest();
            SearchSourceBuilder builder = new SearchSourceBuilder();
            builder.size(0);
            TermsAggregationBuilder term_agg = AggregationBuilders.terms(field + "_agg").field(field);
            term_agg.size(10);
            term_agg.includeExclude(new IncludeExclude(regExp.toString(), null));
            regExp.append("[\u4E00-\u9FA5]");
            builder.query(termUID);
            builder.aggregation(term_agg);
            request.source(builder);
            SearchResponse response = esClient.search(request, ElasticSearchConfig.COMMON_OPTIONS);
            ParsedTerms aggregation = response.getAggregations().get(field + "_agg");
            aggregation.getBuckets().forEach(item -> docNumMap.put(item.getKeyAsString(), (int) item.getDocCount()));
        }
        BarWrapper wrapper = new BarWrapper();
        docNumMap.entrySet().stream().sorted((o, n) -> {
            if (n.getValue().equals(o.getValue())) {
                return n.getKey().length() - o.getKey().length();
            }

            return n.getValue() - o.getValue();
        }).limit(10).forEach(item -> {
            wrapper.getBar_X().add(item.getKey());
            wrapper.getBar_Y().add(item.getValue());
        });

        for (int i = 0, j = 10 - wrapper.getBar_X().size(); i < j; i++) {
            wrapper.getBar_X().add("-");
        }


        return wrapper;
    }


    public List<VideoReplyContainOrigin> getReplyList(String uid, String keyword, Integer period) throws IOException {
        SearchRequest request = new SearchRequest(ElasticSearchUtil.REPLIES_INDEX);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.filter(QueryBuilders.termQuery("uid", uid));
        if (!StringUtils.isEmpty(keyword)) {
            boolQuery.filter(QueryBuilders.matchQuery("message", keyword));
        }
        if (period != null && period > 0) {
            boolQuery.filter(QueryBuilders.rangeQuery("ctime").gte(DateUtil.getBeforeDate(period).getTime() / 1000));
        }
        builder.query(boolQuery);
        request.source(builder);

        SearchResponse response = esClient.search(request, ElasticSearchConfig.COMMON_OPTIONS);
        SearchHit[] hits = response.getHits().getHits();
        List<VideoReplyContainOrigin> list = new ArrayList<>();
        for (SearchHit hit : hits) {

            VideoReplyContainOrigin videoReply = JSONObject.parseObject(hit.getSourceAsString(), VideoReplyContainOrigin.class);
            list.add(videoReply);

        }

        list.sort((o, n) -> n.getLike() - o.getLike());

        return list;
    }
}
















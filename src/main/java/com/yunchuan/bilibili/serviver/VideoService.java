package com.yunchuan.bilibili.serviver;


import com.alibaba.fastjson.JSONObject;
import com.yunchuan.bilibili.common.constant.VideoQueryType;
import com.yunchuan.bilibili.common.es.ElasticSearchUtil;
import com.yunchuan.bilibili.common.util.DateUtil;
import com.yunchuan.bilibili.common.util.Page;
import com.yunchuan.bilibili.config.ElasticSearchConfig;

import com.yunchuan.bilibili.entity.es.VideoDetailEntity;
import com.yunchuan.bilibili.vo.videodetail.VideoKeywordQueryWrapper;
import com.yunchuan.bilibili.vo.videodetail.VideosAbstractResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.ConstantScoreQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.metrics.ParsedAvg;
import org.elasticsearch.search.aggregations.metrics.ParsedPercentiles;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;


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

        SearchRequest request = videoListRequestBuilder(wrapper);
        SearchResponse response = esClient.search(request, ElasticSearchConfig.COMMON_OPTIONS);
        SearchHit[] hits = response.getHits().getHits();
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsMap());
        }

        return null;
    }

    private SearchRequest videoListRequestBuilder(VideoKeywordQueryWrapper wrapper) {
        if (wrapper == null) {
            return null;
        }
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        if (!StringUtils.isEmpty(wrapper.getTName())) {
            boolQuery.filter(QueryBuilders.termsQuery("tName", wrapper.getTName()));
        }
        if (wrapper.getBegin() != null) {
            boolQuery.filter(QueryBuilders.rangeQuery("ctime").gte(wrapper.getBegin().getTime() / 1000));
        }
        if (wrapper.getEnd() != null) {
            boolQuery.filter(QueryBuilders.rangeQuery("ctime").lte(wrapper.getEnd().getTime() / 1000));
        }
        if (wrapper.getIs_union_video() != null) {
            boolQuery.filter(QueryBuilders.termQuery("is_union_video", wrapper.getIs_union_video()));
        }
        if (wrapper.getCopyright() != null) {                           // 这里强转解决编译器报多方法匹配异常BUG
            boolQuery.filter(QueryBuilders.termsQuery("copyright", ((Object)wrapper.getCopyright())));
        }

        if (wrapper.getKeywordWrapper() != null) {
            String keyword = wrapper.getKeywordWrapper().getKeyword();
            switch (wrapper.getKeywordWrapper().getSearchType()) {
                case VideoQueryType.TOTAL:
                    boolQuery.should(QueryBuilders.matchQuery("tag", keyword));
                    boolQuery.should(QueryBuilders.matchQuery("description", keyword));
                    boolQuery.should(QueryBuilders.matchQuery("danmaku_text", keyword));
                    boolQuery.should(QueryBuilders.matchQuery("tag", keyword));
                    boolQuery.should(QueryBuilders.matchQuery("reply_text.message",keyword));
                case VideoQueryType.TITLE_AND_DESCRIPTION:
                    boolQuery.should(QueryBuilders.matchQuery("tag", keyword));
                    boolQuery.should(QueryBuilders.matchQuery("description",  keyword));
                    break;
                case VideoQueryType.DANMAKU:
                    boolQuery.should(QueryBuilders.matchQuery("danmaku_text", keyword));
                    break;
                case VideoQueryType.TAG:
                    boolQuery.should(QueryBuilders.matchQuery("tag", keyword));
                    break;
                case VideoQueryType.REPLY:
                    boolQuery.should(QueryBuilders.matchQuery("reply_text.message",keyword));
                default:
                    throw new IllegalStateException("Unexpected value: " + wrapper.getKeywordWrapper().getSearchType());
            }
        }

        SearchSourceBuilder builder = new SearchSourceBuilder();
        // 去除评分系统节省内存
        builder.query(new ConstantScoreQueryBuilder(boolQuery));
        // 分页处理
        esPageHelper(builder, wrapper.getPage());

        return new SearchRequest().source(builder);
    }

    private void esPageHelper(SearchSourceBuilder searchSourceBuilder, Page page) {
        if (page != null && page.getCurrPage() !=null && page.getPageSize() != null) {
            searchSourceBuilder.from(page.getCurrPage());
            searchSourceBuilder.size(page.getPageSize());
        }
    }

    public List<String> getVideoTName() {
        return null;
    }
}
















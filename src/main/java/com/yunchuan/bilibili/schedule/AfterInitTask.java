package com.yunchuan.bilibili.schedule;


import com.yunchuan.bilibili.common.es.ElasticSearchUtil;
import com.yunchuan.bilibili.serviver.UpsManagerService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import java.io.IOException;


@Service
@Slf4j
public class AfterInitTask implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    UpsManagerService upsManagerService;

    @Autowired
    RestHighLevelClient esClient;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("开始执行初始化方法。。。。。");


        CreateIndexRequest videos_index = new CreateIndexRequest(ElasticSearchUtil.VIDEO_DETAIL_INDEX);//创建索引
        CreateIndexRequest replies_index = new CreateIndexRequest(ElasticSearchUtil.REPLIES_INDEX);
        //创建的每个索引都可以有与之关联的特定设置。
//        request.settings(Settings.builder()
//                .put("index.number_of_shards", 3)
//                .put("index.number_of_replicas", 2)
//        );
        //创建索引时创建文档类型映射
        videos_index.source(ElasticSearchUtil.VIDEO_INDEX_MAPPING, XContentType.JSON);
        replies_index.source(ElasticSearchUtil.REPLIES_INDEX_MAPPING,XContentType.JSON);
        //可选参数
//        request.timeout(TimeValue.timeValueMinutes(2));//超时,等待所有节点被确认(使用TimeValue方式)
        //request.timeout("2m");//超时,等待所有节点被确认(使用字符串方式)

//        request.masterNodeTimeout(TimeValue.timeValueMinutes(1));//连接master节点的超时时间(使用TimeValue方式)
        //request.masterNodeTimeout("1m");//连接master节点的超时时间(使用字符串方式)

//        request.waitForActiveShards(2);//在创建索引API返回响应之前等待的活动分片副本的数量，以int形式表示。
        //request.waitForActiveShards(ActiveShardCount.DEFAULT);//在创建索引API返回响应之前等待的活动分片副本的数量，以ActiveShardCount形式表示。

        //同步执行
        try {
            esClient.indices().create(videos_index, RequestOptions.DEFAULT);
        } catch (ElasticsearchStatusException e) {
            log.info("\"videos_index\"索引已存在");
        } catch (IOException e) {
            log.info("ES连接失败");
        }

        try {
            esClient.indices().create(replies_index, RequestOptions.DEFAULT);
        } catch (ElasticsearchStatusException e) {
            log.info("\"replies_index\"索引已存在");
        } catch (IOException e) {
            log.info("ES连接失败");
        }




        //异步执行
        //异步执行创建索引请求需要将CreateIndexRequest实例和ActionListener实例传递给异步方法：
        //CreateIndexResponse的典型监听器如下所示：
        //异步方法不会阻塞并立即返回。
//        ActionListener<CreateIndexResponse> listener = new ActionListener<CreateIndexResponse>() {
//            @Override
//            public void onResponse(CreateIndexResponse createIndexResponse) {
//                //如果执行成功，则调用onResponse方法;
//            }
//            @Override
//            public void onFailure(Exception e) {
//                //如果失败，则调用onFailure方法。
//            }
//        };
//        client.indices().createAsync(request, listener);//要执行的CreateIndexRequest和执行完成时要使用的ActionListener

        //返回的CreateIndexResponse允许检索有关执行的操作的信息，如下所示：
//        boolean acknowledged = createIndexResponse.isAcknowledged();//指示是否所有节点都已确认请求
//        boolean shardsAcknowledged = createIndexResponse.isShardsAcknowledged();//指示是否在超时之前为索引中的每个分片启动了必需的分片副本数

        try {
            upsManagerService.cleanExpiredUps();
            upsManagerService.upDateUps();
        } catch (Exception e) {
            e.printStackTrace();
        }

//        ArrayList<Object> objects = new ArrayList<>();
//        while (true) {
//            objects.add(new Object());
//        }
    }
}

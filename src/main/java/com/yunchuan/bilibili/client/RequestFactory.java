package com.yunchuan.bilibili.client;


import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.springframework.util.CollectionUtils;


import java.net.URI;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.LongAdder;

@Slf4j
public class RequestFactory {

    public static final String URL = "http://api.bilibili.com";
    /**
     * 反爬虫请求头
     */
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.121 Safari/537.36";

    public static final String HOST = "i2.hdslb.com";

    /**
     * 重要，换成自己B站的Cookie!
     */
    public static final String COOKIE = "buvid3=4F8681D8-0FB5-4EE1-9EBE-FB40263F2379143093infoc; blackside_state=1; rpdid=|(J~RYulJm|)0J'uY|k)kJ)Ju; LIVE_BUVID=AUTO1716092323509462; fingerprint3=daf258adb06fd18fa47d435617da81ba; fingerprint_s=acfba511d9282ca18d9aacb7153e3970; _uuid=CDD8688E-5AEB-A9A2-B8A0-BE08AD429D9502191infoc; CURRENT_QUALITY=0; video_page_version=v_old_home; PVID=1; CURRENT_BLACKGAP=0; i-wanna-go-back=-1; buvid_fp_plain=undefined; buvid4=E7E8C5EC-EAEC-0D96-BEA8-DA3DC18EF9BC14340-022020102-zfc++opW3ruCBegYO4b1Bw%3D%3D; nostalgia_conf=-1; innersign=0; b_lsid=6F8B410CB_180474B25CC; b_ut=7; fingerprint=8811623532fd27bba7948e9371446fa8; buvid_fp=4F8681D8-0FB5-4EE1-9EBE-FB40263F2379143093infoc; SESSDATA=61cf9ff6%2C1666015569%2C9f4f7%2A41; bili_jct=d997ee53f9ecdd771923576531c0fc01; DedeUserID=20686463; DedeUserID__ckMd5=5c3e0bf99a3fb5aa; sid=j3p0200t; CURRENT_FNVAL=80";
    /**
     * 用户个人信息
     */
    public static final String UP_INFO_PATH = "/x/space/acc/info";

    /**
     * 用户关注数、粉丝数
     */
    public static final String STAT_PATH = "/x/relation/stat";

    /**
     * 用户点赞总数、播放总数
     */
    public static final String UP_STAT = "/x/space/upstat";

    /**
     * 用户所有投稿视频
     */
    public static final String VIDEO_PATH = "/x/space/arc/search";

    /**
     * 视频基本信息
     */
    public static final String VIDEO_DETAIL_PATH = "/x/web-interface/archive/stat";

    /**
     * 视频基本信息
     */
    public static final String VIDEO_DETAIL_PATH_X = "/x/web-interface/view";

    /**
     * 视频标签
     */
    public static final String VIDEO_TAG = "/x/web-interface/view/detail/tag";

    /**
     * 用户头像
     */
    public static final String FACE = "";

    /**
     * 评论
     */
    public static final String VIDEO_REPLY = "/x/v2/reply";

    /**
     * 弹幕内容
     */
    public static final String VIDEO_DANMAKU = "/x/v1/dm/list.so";

    private static LongAdder adder = new LongAdder();

//    public static FullHttpRequest getNettyRequest(RequestPath path, String uid, int page) throws Exception {
//        String fullPath = addParam(uid,path,page,"0");
//        System.out.println("路径：" + fullPath);
//        URI url = new URI(fullPath);
//        String meg = "hello";
//
//        FullHttpRequest request = new DefaultFullHttpRequest(
//                HttpVersion.HTTP_1_1, HttpMethod.GET, url.toASCIIString(), Unpooled.wrappedBuffer(meg.getBytes("UTF-8")));
//
//        request.headers()
//                .set(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=UTF-8")
//                //开启长连接
//                .set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE)
//                //设置传递请求内容的长度
//                .set(HttpHeaderNames.CONTENT_LENGTH, request.content().readableBytes())
//                //设置请浏览器，躲避B站反爬虫
//                .set(HttpHeaderNames.USER_AGENT, RequestFactory.USER_AGENT)
//
//                .set(HttpHeaderNames.HOST, "api.bilibili.com");
//
//        return request;
//    }

    public static HttpUriRequest getApacheRequest(RequestPath path, String uid) throws Exception {
        HttpUriRequest request = getApacheRequest(path, uid, 1, null,"0");
        return request;
    }

    public static HttpUriRequest getApacheRequest(RequestPath path, String uid, String url) throws Exception {
        HttpUriRequest request = getApacheRequest(path, uid, 1, url,"0");
        return request;
    }

    public static HttpUriRequest getApacheRequest(RequestPath path, String uid, int page) throws Exception {
        HttpUriRequest request = getApacheRequest(path, uid, page, null,"0");
        return request;
    }

    public static HttpUriRequest getApacheRequest(RequestPath path, String uid, int page, String tid) throws Exception {
        HttpUriRequest request = getApacheRequest(path, uid, page, null,tid);
        return request;
    }

    public static HttpUriRequest getApacheRequest(RequestPath path, String uid, int page, String url,String tid) throws Exception {
        String fullPath = null;
        if (url == null) {
            fullPath = URL.concat(addParam(uid,path,page,tid));
        } else {
            fullPath = url.concat(addParam(uid,path,page,tid));
        }
        URI uri = new URI(fullPath);

        log.info("访问路径：" + fullPath);

        adder.increment();
        log.info(adder.longValue() + "   " + LocalTime.now().toString());

        HttpUriRequest request = RequestBuilder.create(HttpGet.METHOD_NAME)
                .setUri(uri)
                .build();
        addHeader(request,path);
        return request;
    }

//    public static FullHttpRequest getNettyRequest(RequestPath path, String uid) throws Exception {
//        FullHttpRequest request = getNettyRequest(path, uid, 1);
//        return request;
//    }

    private static String addParam(String url, Map<String,String> paramMap) {

        if (CollectionUtils.isEmpty(paramMap)) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder(url);
        boolean flag = false;
        if (url.contains("?")) {
            flag = true;
        } else {
            stringBuilder.append('?');
        }
        for (Map.Entry<String, String> entry : paramMap.entrySet()) {
            if (flag) {
                stringBuilder.append('&');
            }
            stringBuilder.append(entry.getKey() + '=' + entry.getValue());
            flag = true;
        }
        return stringBuilder.toString();
    }

    private static String addParam(String id,RequestPath path,int page,String tid) {
        Map<String, String> param = new HashMap<>();


        if (path == RequestPath.STAT_PATH) {
            param.put("jsonp","jsonp");
            param.put("vmid",id);
            return addParam(path.toString(),param);
        }

        if (path == RequestPath.VIDEO_PATH) {
            param.put("ps","50");
            param.put("tid",tid);
            param.put("keyword","");
            param.put("order","pubdate");
            param.put("jsonp","jsonp");
            param.put("mid",id);
            param.put("pn",String.valueOf(page));
            return addParam(path.toString(),param);
        }

        if (path == RequestPath.VIDEO_DETAIL_PATH_X) {
            param.put("aid",id);
            return addParam(path.toString(),param);
        }

        if (path == RequestPath.VIDEO_DETAIL_PATH) {
            param.put("bvid",id);
            return addParam(path.toString(),param);
        }

        if (path == RequestPath.UP_INFO_PATH) {
            param.put("jsonp","jsonp");
            param.put("mid",id);
            return addParam(path.toString(),param);
        }

        if (path == RequestPath.UP_STAT) {
            param.put("jsonp","jsonp");
            param.put("mid",id);
            return addParam(path.toString(),param);
        }

        if (path == RequestPath.VIDEO_TAG) {
            param.put("bvid",id);
            return addParam(path.toString(),param);
        }

        if (path == RequestPath.FACE) {
            return path + "";
        }

        if (path == RequestPath.VIDEO_REPLY) {
            param.put("jsonp","jsonp");
            param.put("type","1");
            param.put("oid",id);
            param.put("pn",String.valueOf(page));
            param.put("sort","2");
            return addParam(path.toString(),param);
        }

        if (path == RequestPath.VIDEO_DANMAKU) {
            param.put("oid",id);
            return addParam(path.toString(),param);
        }


        throw new RuntimeException("路径错误！");

    }

    private static HttpUriRequest addHeader(HttpUriRequest request, RequestPath path) {
        if (path == RequestPath.UP_STAT) {
            request.addHeader("cookie",RequestFactory.COOKIE);
        }
        if (path == RequestPath.FACE) {
            request.addHeader("HOST",RequestFactory.HOST);
        }

        return request;
    }


}

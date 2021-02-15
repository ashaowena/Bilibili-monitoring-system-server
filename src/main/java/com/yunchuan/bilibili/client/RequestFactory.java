package com.yunchuan.bilibili.client;


import com.yunchuan.bilibili.client.netty.RequestPath;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.springframework.util.CollectionUtils;


import java.net.URI;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class RequestFactory {

    public static final String URL = "http://api.bilibili.com";
    /**
     * 反爬虫请求头
     */
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.121 Safari/537.36";

    public static final String HOST = "i2.hdslb.com";

    public static final String COOKIE = "rpdid=|(J~RYulJ)l~0J'uY|~kukm)m; _uuid=A42877F8-AC1C-03B9-4779-A701396DC64951124infoc; sid=jz20xwgt; fingerprint3=fc09a567f5774a036bcd6cede5d52e66; fingerprint=e7a01a0b0c91961fabe0a16607c48070; CURRENT_FNVAL=80; blackside_state=1; CURRENT_QUALITY=64; LIVE_BUVID=AUTO6516090784803684; buvid3=B0D867F6-3793-4545-9429-A3CFA9C8141E95183infoc; buivd_fp=B0D867F6-3793-4545-9429-A3CFA9C8141E95183infoc; buvid_fp_plain=B0D867F6-3793-4545-9429-A3CFA9C8141E95183infoc; DedeUserID=20686463; DedeUserID__ckMd5=5c3e0bf99a3fb5aa; SESSDATA=5259c9dd%2C1624832297%2Cd8cc7*c1; bili_jct=345caf8df143762d0be8d08b96b79214; buvid_fp=B0D867F6-3793-4545-9429-A3CFA9C8141E95183infoc; fingerprint_s=21c51e69a053984361fa9b7600f53c19; PVID=2; bp_video_offset_20686463=487938547175044113; bfe_id=6f285c892d9d3c1f8f020adad8bed553";
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
    public static final String FACE = "/bfs/face/";

    /**
     * 评论
     */
    public static final String VIDEO_REPLY = "/x/v2/reply";

    /**
     * 弹幕内容
     */
    public static final String VIDEO_DANMAKU = "/x/v1/dm/list.so";


    public static FullHttpRequest getNettyRequest(RequestPath path, String uid, int page) throws Exception {
        String fullPath = addParam(uid,path,page,"0");
        System.out.println("路径：" + fullPath);
        URI url = new URI(fullPath);
        String meg = "hello";

        FullHttpRequest request = new DefaultFullHttpRequest(
                HttpVersion.HTTP_1_1, HttpMethod.GET, url.toASCIIString(), Unpooled.wrappedBuffer(meg.getBytes("UTF-8")));

        request.headers()
                .set(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=UTF-8")
                //开启长连接
                .set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE)
                //设置传递请求内容的长度
                .set(HttpHeaderNames.CONTENT_LENGTH, request.content().readableBytes())
                //设置请浏览器，躲避B站反爬虫
                .set(HttpHeaderNames.USER_AGENT, RequestFactory.USER_AGENT)

                .set(HttpHeaderNames.HOST, "api.bilibili.com");

        return request;
    }

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
        System.out.println("路径：" + fullPath);
        HttpUriRequest request = RequestBuilder.create(HttpGet.METHOD_NAME)
                .setUri(uri)
                .build();
        addHeader(request,path);
        return request;
    }

    public static FullHttpRequest getNettyRequest(RequestPath path, String uid) throws Exception {
        FullHttpRequest request = getNettyRequest(path, uid, 1);
        return request;
    }

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
            param.put("ps","100");
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
            return path + id;
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

package com.yunchuan.bilibili.client;



public enum RequestPath {

    STAT_PATH(RequestFactory.STAT_PATH,0),
    UP_STAT(RequestFactory.UP_STAT, 1),
    UP_INFO_PATH(RequestFactory.UP_INFO_PATH,2),
    VIDEO_PATH(RequestFactory.VIDEO_PATH,3),
    VIDEO_DETAIL_PATH(RequestFactory.VIDEO_DETAIL_PATH,4),
    VIDEO_DETAIL_PATH_X(RequestFactory.VIDEO_DETAIL_PATH_X,4),
    VIDEO_TAG(RequestFactory.VIDEO_TAG,5),
    VIDEO_REPLY(RequestFactory.VIDEO_REPLY,6),
    VIDEO_DANMAKU(RequestFactory.VIDEO_DANMAKU,7),
    FACE(RequestFactory.FACE,6)
    ;

    private String path;

    private int code;

    RequestPath(String path, int i) {
        this.path = path;
        this.code = i;
    }

    @Override
    public String toString() {
        return path;
    }
}

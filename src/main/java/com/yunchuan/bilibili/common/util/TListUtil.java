package com.yunchuan.bilibili.common.util;


import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;


import java.util.HashSet;
import java.util.Set;

public class TListUtil {

    public static Set<TWrapper> getTWrapperSet(String content) {
        if (StringUtils.isEmpty(content)) {
            return null;
        }
        JSONObject tList = JSONObject.parseObject(content).getJSONObject("data").getJSONObject("list").getJSONObject("tlist");
        Set<TWrapper> tWrappers = new HashSet<>();
        for (Object item : tList.values()) {
            String tid = ((JSONObject) item).getString("tid");
            String name = ((JSONObject) item).getString("name");
            TWrapper tWrapper = new TWrapper(tid,name);
            tWrappers.add(tWrapper);
        }
        return tWrappers;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TWrapper {

        private String tid;

        private String tname;

    }
}

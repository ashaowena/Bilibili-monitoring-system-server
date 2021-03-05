package com.yunchuan.bilibili.common.es;


public class ElasticSearchUtil {

    public static final String VIDEO_DETAIL_INDEX = "video_detail";

    public static final String REPLIES_INDEX = "replies_index";

    public static final String REPLIES_INDEX_MAPPING = "{\n" +
            "    \"mappings\":{\n" +
            "        \"properties\":{\n" +
            "\t\t\t\t\t\t\"rpid\": {\"type\": \"keyword\"},\n" +
            "\t\t\t\t\t\t\"mid\": {\"type\": \"integer\"},\n" +
            "\t\t\t\t\t\t\"name\": {\"type\": \"keyword\"},\n" +
            "\t\t\t\t\t\t\"current_level\": {\"type\": \"integer\"},\n" +
            "\t\t\t\t\t\t\"oid\": {\"type\": \"integer\"},\n" +
            "\t\t\t\t\t\t\"message\": {\"type\": \"text\",\n" +
            "\t\t\t\t\t\t\t\t\t\"fielddata\": true,\n" +
            "\t\t\t\t\t\t\t\t\t\"analyzer\": \"ik_max_word\",\n" +
            "\t\t\t\t\t\t\t\t\t\"search_analyzer\": \"ik_max_word\"\n" +
            "\t\t\t\t\t\t\t\t},\n" +
            "\t\t\t\t\t\t\"like\": {\"type\": \"integer\"},\n" +
            "\t\t\t\t\t\t\"ctime\": {\"type\": \"date\"},\n" +
            "\t\t\t\t\t\t\"face\": {\"type\": \"keyword\"},\n" +
            "\t\t\t\t\t\t\"title\": {\"type\": \"keyword\"},\n" +
            "\t\t\t\t\t\t\"bvid\": {\"type\": \"keyword\"},\n" +
            "\t\t\t\t\t\t\"uid\": {\"type\": \"keyword\"}\n" +
            "\t\t    }\n" +
            "\t\t}\n" +
            "}";

    public static final String VIDEO_INDEX_MAPPING = "{\n" +
            "    \"mappings\":{\n" +
            "        \"properties\":{\n" +
            "            \"bvid\": {\n" +
            "                    \"type\": \"keyword\"\n" +
            "                },\n" +
            "\t\t\t\t\"aid\": {\n" +
            "\t\t\t\t\t\"type\": \"integer\"\n" +
            "\t\t\t\t},\n" +
            "\t\t\t\t\"cid\": {\n" +
            "\t\t\t\t\t\"type\": \"integer\"\n" +
            "\t\t\t\t},\n" +
            "                \"coin\": {\n" +
            "                    \"type\": \"integer\"\n" +
            "                },\n" +
            "                \"copyright\": {\n" +
            "                    \"type\": \"integer\"\n" +
            "                },\n" +
            "                \"ctime\": {\n" +
            "                    \"type\": \"date\"\n" +
            "                },\n" +
            "                \"danmaku\": {\n" +
            "                    \"type\": \"integer\"\n" +
            "                },\n" +
            "                \"danmaku_text\": {\n" +
            "                    \"type\": \"text\",\n" +
            "\t\t\t\t\t\"fielddata\": true,\n" +
            "\t\t\t\t\t\"analyzer\": \"ik_max_word\",\n" +
            "\t\t\t\t\t\"search_analyzer\": \"ik_max_word\"\n" +
            "                },\n" +
            "                \"description\": {\n" +
            "                    \"type\": \"text\",\n" +
            "\t\t\t\t\t\"fielddata\": true,\n" +
            "\t\t\t\t\t\"analyzer\": \"ik_max_word\",\n" +
            "\t\t\t\t\t\"search_analyzer\": \"ik_max_word\"\n" +
            "                },\n" +
            "                \"favorite\": {\n" +
            "                    \"type\": \"integer\"\n" +
            "                },\n" +
            "                \"is_union_video\": {\n" +
            "                    \"type\": \"integer\"\n" +
            "                },\n" +
            "                \"length\": {\n" +
            "                    \"type\": \"keyword\"\n" +
            "                },\n" +
            "                \"like\": {\n" +
            "                    \"type\": \"integer\"\n" +
            "                },\n" +
            "                \"pic\": {\n" +
            "                    \"type\": \"keyword\"\n" +
            "                },\n" +
            "                \"reply\": {\n" +
            "                    \"type\": \"integer\"\n" +
            "                },\n" +
            "                \"reply_text\": {\n" +
            "                    \"properties\": {\n" +
            "\t\t\t\t\t\t\"mid\": {\"type\": \"integer\"},\n" +
            "\t\t\t\t\t\t\"name\": {\"type\": \"keyword\"},\n" +
            "\t\t\t\t\t\t\"current_level\": {\"type\": \"integer\"},\n" +
            "\t\t\t\t\t\t\"oid\": {\"type\": \"integer\"},\n" +
            "\t\t\t\t\t\t\"message\": {\"type\": \"text\",\n" +
            "\t\t\t\t\t\t\t\t\t\"fielddata\": true,\n" +
            "\t\t\t\t\t\t\t\t\t\"analyzer\": \"ik_max_word\",\n" +
            "\t\t\t\t\t\t\t\t\t\"search_analyzer\": \"ik_max_word\"\n" +
            "\t\t\t\t\t\t\t\t},\n" +
            "\t\t\t\t\t\t\"like\": {\"type\": \"integer\"},\n" +
            "\t\t\t\t\t\t\"ctime\": {\"type\": \"date\"},\n" +
            "\t\t\t\t\t\t\"face\": {\"type\": \"keyword\"}\n" +
            "\t\t\t\t\t}\n" +
            "                },\n" +
            "                \"share\": {\n" +
            "                    \"type\": \"integer\"\n" +
            "                },\n" +
            "                \"tName\": {\n" +
            "                    \"type\": \"keyword\"\n" +
            "                },\n" +
            "                \"tag\": {\n" +
            "                    \"type\": \"keyword\"\n" +
            "                },\n" +
            "                \"title\": {\n" +
            "                    \"type\": \"text\"\n" +
            "                },\n" +
            "                \"update_date\": {\n" +
            "                    \"type\": \"date\"\n" +
            "                },\n" +
            "                \"view\": {\n" +
            "                    \"type\": \"integer\"\n" +
            "                },\n" +
            "\t\t\t\t\"hot\": {\n" +
            "\t\t\t\t\t\"type\": \"float\"\n" +
            "\t\t\t\t}\n" +
            "        }\n" +
            "    }\n" +
            "}";

}

package com.yunchuan.bilibili.common.ES;


public class ElasticSearchUtil {

    public static final String VIDEO_DETAIL_INDEX = "video_detail";

    public static final String[] VIDEO_DETAIL_INDEX_ARR = { VIDEO_DETAIL_INDEX };

    public static final String VIDEO_INDEX_DEFINITION = "{\n" +
            "    \"mappings\":{\n" +
            "        \"properties\":{\n" +
            "            \"bvid\": {\n" +
            "                    \"type\": \"keyword\"\n" +
            "                },\n" +
            "\t\t\t\t\"aid\": {\n" +
            "\t\t\t\t\t\"type\": \"integer\"\n" +
            "\t\t\t\t},\n" +
            "\t\t\t\t\"mid\": {\n" +
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
            "                    \"type\": \"text\"\n" +
            "                },\n" +
            "                \"description\": {\n" +
            "                    \"type\": \"text\"\n" +
            "                },\n" +
            "                \"favorite\": {\n" +
            "                    \"type\": \"integer\"\n" +
            "                },\n" +
            "                \"is_union_video\": {\n" +
            "                    \"type\": \"boolean\"\n" +
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
            "\t\t\t\t\t\t\"replies\": {\n" +
            "\t\t\t\t\t\t\t\"properties\": {\n" +
            "\t\t\t\t\t\t\t\t\"rpid\": {\"type\": \"integer\"},\n" +
            "\t\t\t\t\t\t\t\t\"message\": {\"type\": \"text\"},\n" +
            "\t\t\t\t\t\t\t\t\"like\": {\"type\": \"integer\"},\n" +
            "\t\t\t\t\t\t\t\t\"replies\": {\n" +
            "\t\t\t\t\t\t\t\t\t\"type\": \"object\",\n" +
            "\t\t\t\t\t\t\t\t\t\"dynamic\": \"true\"\n" +
            "\t\t\t\t\t\t\t\t}\n" +
            "\t\t\t\t\t\t\t}\n" +
            "\t\t\t\t\t\t},\n" +
            "\t\t\t\t\t\t\"upper\": {\n" +
            "\t\t\t\t\t\t\t\"properties\": {\n" +
            "\t\t\t\t\t\t\t\t\"rpid\": {\"type\": \"integer\"},\n" +
            "\t\t\t\t\t\t\t\t\"message\": {\"type\": \"text\"},\n" +
            "\t\t\t\t\t\t\t\t\"like\": {\"type\": \"integer\"},\n" +
            "\t\t\t\t\t\t\t\t\"replies\": {\n" +
            "\t\t\t\t\t\t\t\t\t\"type\": \"object\",\n" +
            "\t\t\t\t\t\t\t\t\t\"dynamic\": \"true\"\n" +
            "\t\t\t\t\t\t\t\t}\n" +
            "\t\t\t\t\t\t\t}\n" +
            "\t\t\t\t\t\t},\n" +
            "\t\t\t\t\t\t\"hot\": {\n" +
            "\t\t\t\t\t\t\t\"properties\": {\n" +
            "\t\t\t\t\t\t\t\t\"rpid\": {\"type\": \"integer\"},\n" +
            "\t\t\t\t\t\t\t\t\"message\": {\"type\": \"text\"},\n" +
            "\t\t\t\t\t\t\t\t\"like\": {\"type\": \"integer\"},\n" +
            "\t\t\t\t\t\t\t\t\"replies\": {\n" +
            "\t\t\t\t\t\t\t\t\t\"type\": \"object\",\n" +
            "\t\t\t\t\t\t\t\t\t\"dynamic\": \"true\"\n" +
            "\t\t\t\t\t\t\t\t}\n" +
            "\t\t\t\t\t\t\t}\n" +
            "\t\t\t\t\t\t}\n" +
            "\t\t\t\t\t}\n" +
            "                },\n" +
            "                \"share\": {\n" +
            "                    \"type\": \"integer\"\n" +
            "                },\n" +
            "                \"t_name\": {\n" +
            "                    \"type\": \"keyword\"\n" +
            "                },\n" +
            "                \"tag\": {\n" +
            "                    \"type\": \"text\"\n" +
            "                },\n" +
            "                \"title\": {\n" +
            "                    \"type\": \"text\"\n" +
            "                },\n" +
            "                \"update_date\": {\n" +
            "                    \"type\": \"date\"\n" +
            "                },\n" +
            "                \"view\": {\n" +
            "                    \"type\": \"integer\"\n" +
            "                }\n" +
            "        }\n" +
            "    }\n" +
            "}";

}

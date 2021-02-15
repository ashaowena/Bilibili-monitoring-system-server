package com.yunchuan.bilibili.common.util;


import lombok.Data;

@Data
public class Page {

    /**
     * 总记录数
     */
    private Integer totalCount;
    /**
     * 每页记录数
     */
    private Integer pageSize;
    /**
     * 总页数
     */
    private Integer totalPage;
    /**
     * 当前页数
     */
    private Integer currPage;


}

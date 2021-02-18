package com.yunchuan.bilibili.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BarWrapper {

    private List<String> bar_X = new ArrayList<>(10);

    private List<Integer> bar_Y = new ArrayList<>(10);
}

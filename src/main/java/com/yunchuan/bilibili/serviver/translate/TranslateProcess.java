package com.yunchuan.bilibili.serviver.translate;

import com.yunchuan.bilibili.entity.UpStatus;
import java.util.List;


public interface TranslateProcess {

    List<UpStatus> upStatusesProcess(List<UpStatus> upStatuses);

}

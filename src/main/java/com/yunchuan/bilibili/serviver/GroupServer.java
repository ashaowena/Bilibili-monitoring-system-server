package com.yunchuan.bilibili.serviver;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.yunchuan.bilibili.dao.UpGroupDAO;
import com.yunchuan.bilibili.dao.UpStatusDAO;
import com.yunchuan.bilibili.dao.UserToGroupDAO;
import com.yunchuan.bilibili.entity.UpGroup;
import com.yunchuan.bilibili.entity.UpStatus;
import com.yunchuan.bilibili.entity.User;
import com.yunchuan.bilibili.entity.UserToGroup;
import com.yunchuan.bilibili.vo.MonitorResponseVo;
import com.yunchuan.bilibili.vo.UpGroupVo;
import com.yunchuan.bilibili.vo.UpStatusAfterTranslatedVo;
import com.yunchuan.bilibili.vo.up.UpInfoVo;
import com.yunchuan.bilibili.vo.videos.UpVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
@Slf4j
public class GroupServer {

    @Autowired
    UserToGroupDAO userToGroupDAO;

    @Autowired
    UpGroupDAO upGroupDAO;

    @Autowired
    UpStatusDAO upStatusDAO;

    @Autowired
    MonitorServer monitorServer;



    @Transactional
    public int addMonitorGroup(User user, String groupName) {
        if (groupName.startsWith("default:")) {
            return 0;
        }
        // 添加组
        UpGroup upGroup = new UpGroup();
        upGroup.setGroupName(groupName);
        upGroupDAO.insert(upGroup);
        // 添加对应关系
        UserToGroup userToGroup = new UserToGroup();
        userToGroup.setUserId(user.getId());
        userToGroup.setGroupId(upGroup.getId());
        userToGroupDAO.insert(userToGroup);
        return 200;
    }

    @Transactional
    public void deleteMonitorGroup(User user, Integer groupId) {
        int num = userToGroupDAO.delete(new QueryWrapper<UserToGroup>().eq("group_id", groupId).eq("user_id", user.getId()));
        if (num > 0) upGroupDAO.deleteById(groupId);
    }


    @Transactional
    public synchronized int addMonitorUp(Integer groupId, String uid, User user) throws Exception {
        // 判断Up是否存在
        UpStatus upStatus = monitorServer.existingUpStatus(uid);
        if (upStatus == null) {
            return 0;
        }
        // 判断是否指定了分组
        if (groupId.equals(0)) {
            // 没有指定分组，添加至默认的分组，默认分组统一格式default:uid
            UpGroup upGroup = upGroupDAO.selectOne(new QueryWrapper<UpGroup>().eq("group_name", "default:" + user.getId()));
            if (upGroup == null) {
                // 没有分组，则创建默认分组
                UpGroup defaultUpGroup = new UpGroup();
                defaultUpGroup.setGroupName("default:" + user.getId());
                defaultUpGroup.setUp(";" + uid);
                upGroupDAO.insert(defaultUpGroup);
                UserToGroup userToGroup = new UserToGroup();
                userToGroup.setGroupId(defaultUpGroup.getId());
                userToGroup.setUserId(user.getId());
                userToGroupDAO.insert(userToGroup);
                // 跟新数据库
                monitorServer.saveMonitorUp(uid);
                return 200;
            }
            String ups = upGroup.getUp();
            String replace = ups.replace(";" + uid, "");
            upGroup.setUp(replace + ";" + uid);
            upGroupDAO.update(upGroup, new UpdateWrapper<UpGroup>().eq("group_name", "default:" + user.getId()));
            // 跟新数据库
            monitorServer.saveMonitorUp(uid);
            return 200;
        }
        // 修改数据，增加up
        UpGroup upGroup = upGroupDAO.selectOne(new QueryWrapper<UpGroup>().eq("id", groupId));
        String ups = upGroup.getUp();
        String replace = ups.replace(";" + uid, "");
        upGroup.setUp(replace + ";" + uid);
        upGroupDAO.updateById(upGroup);
        // 跟新数据库
        monitorServer.saveMonitorUp(uid);
        return 200;
    }


    public void getMonitorUp(MonitorResponseVo vo) {
        List<UpGroupVo> upgroups = vo.getUpgroups();
        for (UpGroupVo upGroupVo : upgroups) {
            List<UpVo> upVos = upGroupVo.getUpVos();
            for (UpVo upVo : upVos) {
                List<UpStatusAfterTranslatedVo> translated = null;
                UpInfoVo upInfo = null;
                try {
                    upInfo = monitorServer.getUpInfo(upVo.getUid());
                    // 转化为增长量
                    translated = monitorServer.translateServer.translate(upVo.getUid());
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    log.warn("获取信息错误");
                }
                upVo.setInfo(upInfo);
                upVo.setUsat(translated);
            }
        }
    }


    public void deleteMonitorUp(String up, User user) throws IOException {
        List<UserToGroup> userToGroups = userToGroupDAO.selectList(new QueryWrapper<UserToGroup>().eq("user_id", user.getId()));
        HashSet<Integer> groupIds = new HashSet<>();
        userToGroups.forEach((utg) -> {
            groupIds.add(utg.getGroupId());
        });
        List<UpGroup> upGroups = upGroupDAO.selectBatchIds(groupIds);
        upGroups.forEach((upGroup) -> {
            String ups = upGroup.getUp();
            String replace = ups.replace(";" + up, "");
            upGroup.setUp(replace);
            upGroupDAO.updateById(upGroup);
        });
        monitorServer.deleteUpFromEs(up);
        upStatusDAO.delete(new QueryWrapper<UpStatus>().eq("uid", up));
    }

    public MonitorResponseVo getUserGroups(MonitorResponseVo responseVo) {
        User userInfo = responseVo.getUser();
        Integer userId = userInfo.getId();
        List<UserToGroup> userToGroups = userToGroupDAO.selectList(new QueryWrapper<UserToGroup>().eq("user_id", userId));
        if (userToGroups == null) {
            // 账户没有分组，提早返回
            return responseVo;
        }
        List<UpGroupVo> upGroupVos = new ArrayList<>();
        userToGroups.forEach((e) -> {
            UpGroupVo upGroupVo = new UpGroupVo();
            UpGroup upGroup = upGroupDAO.selectOne(new QueryWrapper<UpGroup>().eq("id", e.getGroupId()));
            if (upGroup != null) {
                List<UpVo> upVos = new ArrayList<>();
                String ups = upGroup.getUp();
                if (ups != null) {
                    String[] uids = ups.split(";");
                    BeanUtils.copyProperties(upGroup, upGroupVo);
                    // 设置每个分组下的所有up主的uid
                    for (String uid : uids) {
                        if (!StringUtils.isEmpty(uid)) {
                            UpVo upVo = new UpVo();
                            upVo.setUid(uid);
                            upVos.add(upVo);
                        }
                    }
                }
                upGroupVo.setId(upGroup.getId());
                upGroupVo.setUpVos(upVos);
                upGroupVo.setGroupName(upGroup.getGroupName());
            }
            upGroupVos.add(upGroupVo);
        });
        responseVo.setUpgroups(upGroupVos);
        return responseVo;
    }

    @Transactional
    public void moveMonitorGroup(User user, Integer groupId, String uid) {
        List<UserToGroup> userToGroups = userToGroupDAO.selectList(new QueryWrapper<UserToGroup>().eq("user_id", user.getId()));
        HashSet<Integer> groupIds = new HashSet<>();
        userToGroups.forEach(utg -> {
            groupIds.add(utg.getGroupId());
        });
        List<UpGroup> upGroups = upGroupDAO.selectBatchIds(groupIds);
        for (UpGroup upGroup : upGroups) {
            if (upGroup.getId().equals(groupId)) {
                String ups = upGroup.getUp();
                String newUps = ups.replace(";" + uid, "");
                upGroup.setUp(newUps + ";" + uid);
                upGroupDAO.updateById(upGroup);
            } else {
                String ups = upGroup.getUp();
                String replace = ups.replace(";" + uid, "");
                upGroup.setUp(replace);
                upGroupDAO.updateById(upGroup);
            }
        }

    }
}

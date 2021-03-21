# ！ 仿飞瓜 ！ B站账号监控平台

## 功能概要

仿飞瓜B站数据监控平台
提供基于多维度的B站账号监控，包括账号维度和单个视频维度下的点赞总数、投币总数、收藏总数、评论总数、收藏总数、转发总数、作品总数、弹幕总数以及平均数和中位数；账号与单个视频下的评论与弹幕中出现最多的热点词云，以及点赞量最多的单条评论甚至追溯到评论的具体用户

## 效果和布局

![](https://gulimall-shao.oss-cn-guangzhou.aliyuncs.com/png/01.png "")
先登陆，默认账号： admin 密码： admin

--

![](https://gulimall-shao.oss-cn-guangzhou.aliyuncs.com/png/02.png "")
这里显示所有已关注的账号

--

![](https://gulimall-shao.oss-cn-guangzhou.aliyuncs.com/png/03.png "")
点击账户管理

--

![](https://gulimall-shao.oss-cn-guangzhou.aliyuncs.com/png/04.png "")
可以通过"添加账户"按钮添加新的监控账号，只需要填入账号uid即可

--


![](https://gulimall-shao.oss-cn-guangzhou.aliyuncs.com/png/05.png "")
点击账号下的查看详情

--

![](https://gulimall-shao.oss-cn-guangzhou.aliyuncs.com/png/06.png "")
里面分为 "核心一览"、"作品列表" 和 "舆情分析"三大模块，核心一览可以查看账号的各项基本数据，但前提是至少每天启动一次服务器并收集足够的天数，若中途有一天少了可以自动线性补全

--

![](https://gulimall-shao.oss-cn-guangzhou.aliyuncs.com/png/07.png "")
里面的可以查看指定的周期内UP的各项指标，以及指标下的增量数据和存量数据

--

![](https://gulimall-shao.oss-cn-guangzhou.aliyuncs.com/png/08.png "")
更改周期为30天后坐标轴也会相应的改变

--

![](https://gulimall-shao.oss-cn-guangzhou.aliyuncs.com/png/09.png "")
作品列表模块，是统计所有在 "给定周期内" 发布所有作品

--

![](https://gulimall-shao.oss-cn-guangzhou.aliyuncs.com/png/10.png "")
还可以根据关键词搜索符合条件的视频，还有分页加载功能不方便演示

--

![](https://gulimall-shao.oss-cn-guangzhou.aliyuncs.com/png/10-1.png "")
进入“分析视频”模块后，可以看到单个视频的基本信息

--

![](https://gulimall-shao.oss-cn-guangzhou.aliyuncs.com/png/10-2.png "")
和该视频与Up的其他视频的平均水平相比较的数据差异

--

![](https://gulimall-shao.oss-cn-guangzhou.aliyuncs.com/png/10-3.png "")
分析该视频的评论以及热词

--

![](https://gulimall-shao.oss-cn-guangzhou.aliyuncs.com/png/10-4.png "")
分析该视频的所有弹幕及热词

--

![](https://gulimall-shao.oss-cn-guangzhou.aliyuncs.com/png/11.png "")
舆情分析模块，可以统计该账号下的所有评论和弹幕中出现最多的关键词并显示出现的次数

--

![](https://gulimall-shao.oss-cn-guangzhou.aliyuncs.com/png/12.png "")
按点赞数对评论进行排序并显示每个评论的视频来源和评论者的ID

## 如何安装？

### 环境搭建
需要预先安装ES 7.10.1 和 mysql,然后从项目的 resources/db 中拿到bilibili.sql文件并执行mysql脚本，ES索引会在每次项目启动时自动创建，故不需要自己手动创建
线程池大小需要在配置文件中根据自己监控Up的数量进行调配
### 拉取仓库:
直接克隆客户端Bilibili-monitoring-system-client和服务端Bilibili-monitoring-system-server到本地开发工具运行
### 注意事项
建议监控的Up流量不要太大，否则会被B站反爬虫屏蔽导致缺少弹幕或评论，线程池按需设置
## 项目架构

![](项目架构图.svg "")

## 性能测试
查询模块接口在Win7+ide环境下吞吐量基本可达1000-1500qps，监控模块虽然使用了多线程爬虫，但由于线程之间还是需要依赖关系（例如需要先爬去视频信息然后才能遍历视频的评论和弹幕，因此做不到每次访问都单独开辟一个线程）的原因最终吞吐量只有10-12qps，实测B站接口反爬虫策略在单个IP地址下500次左右会触发，建议爬取Up的视频不要有太多互动，或者自己购买商用或免费IP池做负载均衡



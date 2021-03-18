/*
SQLyog Ultimate v10.00 Beta1
MySQL - 5.5.15 : Database - bilibili
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`bilibili` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `bilibili`;

/*Table structure for table `up_group` */

DROP TABLE IF EXISTS `up_group`;

CREATE TABLE `up_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `group_name` varchar(64) DEFAULT NULL,
  `up` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `GROUP_NAME` (`group_name`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

/*Table structure for table `up_status` */

DROP TABLE IF EXISTS `up_status`;

CREATE TABLE `up_status` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uid` varchar(11) DEFAULT NULL COMMENT 'up主id',
  `date` date DEFAULT NULL COMMENT '日期',
  `fans` int(11) DEFAULT NULL COMMENT '粉丝数',
  `productions` int(11) DEFAULT NULL COMMENT '作品数',
  `view` int(11) DEFAULT NULL COMMENT '观看数',
  `danmaku` int(11) DEFAULT NULL COMMENT '弹幕数',
  `reply` int(11) DEFAULT NULL COMMENT '评论数',
  `favorite` int(11) DEFAULT NULL COMMENT '收藏数',
  `coin` int(11) DEFAULT NULL COMMENT '投币数',
  `share` int(11) DEFAULT NULL COMMENT '分享数',
  `like` int(11) DEFAULT NULL COMMENT '点赞数',
  PRIMARY KEY (`id`),
  KEY `UID` (`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=356 DEFAULT CHARSET=utf8;

/*Table structure for table `user` */

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(64) NOT NULL,
  `password` varchar(64) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `USER` (`username`,`password`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COMMENT='用户信息';

/*Table structure for table `user_to_group` */

DROP TABLE IF EXISTS `user_to_group`;

CREATE TABLE `user_to_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL,
  `group_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `USER_TO_GROUP` (`user_id`,`group_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

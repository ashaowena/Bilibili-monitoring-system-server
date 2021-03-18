/*
 Navicat Premium Data Transfer

 Source Server         : root
 Source Server Type    : MySQL
 Source Server Version : 50515
 Source Host           : localhost:3306
 Source Schema         : bilibili

 Target Server Type    : MySQL
 Target Server Version : 50515
 File Encoding         : 65001

 Date: 18/03/2021 12:26:56
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for up_group
-- ----------------------------
DROP TABLE IF EXISTS `up_group`;
CREATE TABLE `up_group`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `group_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `up` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `GROUP_NAME`(`group_name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of up_group
-- ----------------------------
INSERT INTO `up_group` VALUES (1, 'admin', ';50244520;512308600');
INSERT INTO `up_group` VALUES (3, 'default:1', ';335515254');

-- ----------------------------
-- Table structure for up_status
-- ----------------------------
DROP TABLE IF EXISTS `up_status`;
CREATE TABLE `up_status`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uid` varchar(11) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'up主id',
  `date` date NULL DEFAULT NULL COMMENT '日期',
  `fans` int(11) NULL DEFAULT NULL COMMENT '粉丝数',
  `productions` int(11) NULL DEFAULT NULL COMMENT '作品数',
  `view` int(11) NULL DEFAULT NULL COMMENT '观看数',
  `danmaku` int(11) NULL DEFAULT NULL COMMENT '弹幕数',
  `reply` int(11) NULL DEFAULT NULL COMMENT '评论数',
  `favorite` int(11) NULL DEFAULT NULL COMMENT '收藏数',
  `coin` int(11) NULL DEFAULT NULL COMMENT '投币数',
  `share` int(11) NULL DEFAULT NULL COMMENT '分享数',
  `like` int(11) NULL DEFAULT NULL COMMENT '点赞数',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `UID`(`uid`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 419 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of up_status
-- ----------------------------
INSERT INTO `up_status` VALUES (29, '50244520', '2021-01-04', 17, 3, 268, 0, 17, 15, 9, 0, 35);
INSERT INTO `up_status` VALUES (30, '512308600', '2021-01-04', 2, 3, 22, 1, 1, 2, 4, 0, 5);
INSERT INTO `up_status` VALUES (31, '50244520', '2021-01-05', 17, 3, 268, 0, 17, 15, 9, 0, 35);
INSERT INTO `up_status` VALUES (32, '512308600', '2021-01-05', 2, 3, 22, 1, 1, 2, 4, 0, 5);
INSERT INTO `up_status` VALUES (33, '50244520', '2021-01-06', 18, 3, 270, 0, 17, 15, 9, 0, 35);
INSERT INTO `up_status` VALUES (34, '512308600', '2021-01-06', 2, 3, 22, 1, 1, 2, 4, 0, 5);
INSERT INTO `up_status` VALUES (35, '50244520', '2021-01-07', 18, 3, 394, 0, 17, 15, 9, 0, 41);
INSERT INTO `up_status` VALUES (36, '512308600', '2021-01-07', 2, 3, 22, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (37, '50244520', '2021-01-08', 18, 3, 394, 0, 17, 15, 9, 0, 42);
INSERT INTO `up_status` VALUES (38, '512308600', '2021-01-08', 2, 3, 22, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (41, '512308600', '2021-01-01', 1, 1, 1, 1, 1, 1, 1, 1, 1);
INSERT INTO `up_status` VALUES (42, '512308600', '2020-12-31', 1, 1, 1, 1, 1, 1, 1, 1, 1);
INSERT INTO `up_status` VALUES (43, '512308600', '2020-12-30', 1, 1, 1, 1, 1, 1, 1, 1, 1);
INSERT INTO `up_status` VALUES (44, '512308600', '2020-12-29', 1, 1, 1, 1, 1, 1, 1, 1, 1);
INSERT INTO `up_status` VALUES (45, '512308600', '2020-12-28', 1, 1, 1, 1, 1, 1, 1, 1, 1);
INSERT INTO `up_status` VALUES (46, '512308600', '2020-12-27', 1, 1, 1, 1, 1, 1, 1, 1, 1);
INSERT INTO `up_status` VALUES (47, '512308600', '2020-12-26', 1, 1, 1, 1, 1, 1, 1, 1, 1);
INSERT INTO `up_status` VALUES (48, '512308600', '2020-12-25', 1, 1, 1, 1, 1, 1, 1, 1, 1);
INSERT INTO `up_status` VALUES (49, '512308600', '2020-12-24', 1, 1, 1, 1, 1, 1, 1, 1, 1);
INSERT INTO `up_status` VALUES (50, '512308600', '2020-12-23', 1, 1, 1, 1, 1, 1, 1, 1, 1);
INSERT INTO `up_status` VALUES (51, '512308600', '2020-12-22', 1, 1, 1, 1, 1, 1, 1, 1, 1);
INSERT INTO `up_status` VALUES (52, '512308600', '2020-12-21', 1, 1, 1, 1, 1, 1, 1, 1, 1);
INSERT INTO `up_status` VALUES (53, '512308600', '2020-12-20', 1, 1, 1, 1, 1, 1, 1, 1, 1);
INSERT INTO `up_status` VALUES (54, '512308600', '2020-12-19', 1, 1, 1, 1, 1, 1, 1, 1, 1);
INSERT INTO `up_status` VALUES (55, '512308600', '2020-12-18', 1, 1, 1, 1, 1, 1, 1, 1, 1);
INSERT INTO `up_status` VALUES (120, '50244520', '2021-01-03', 1, 1, 1, 1, 1, 1, 1, 1, 1);
INSERT INTO `up_status` VALUES (121, '50244520', '2021-01-02', 1, 1, 1, 1, 1, 1, 1, 1, 1);
INSERT INTO `up_status` VALUES (122, '50244520', '2021-01-01', 1, 1, 1, 1, 1, 1, 1, 1, 1);
INSERT INTO `up_status` VALUES (123, '50244520', '2020-12-31', 1, 1, 1, 1, 1, 1, 1, 1, 1);
INSERT INTO `up_status` VALUES (124, '50244520', '2020-12-30', 1, 1, 1, 1, 1, 1, 1, 1, 1);
INSERT INTO `up_status` VALUES (125, '50244520', '2020-12-29', 1, 1, 1, 1, 1, 1, 1, 1, 1);
INSERT INTO `up_status` VALUES (126, '50244520', '2020-12-28', 1, 1, 1, 1, 1, 1, 1, 1, 1);
INSERT INTO `up_status` VALUES (127, '50244520', '2020-12-27', 1, 1, 1, 1, 1, 1, 1, 1, 1);
INSERT INTO `up_status` VALUES (128, '50244520', '2020-12-26', 1, 1, 1, 1, 1, 1, 1, 1, 1);
INSERT INTO `up_status` VALUES (129, '50244520', '2020-12-25', 1, 1, 1, 1, 1, 1, 1, 1, 1);
INSERT INTO `up_status` VALUES (130, '50244520', '2020-12-24', 1, 1, 1, 1, 1, 1, 1, 1, 1);
INSERT INTO `up_status` VALUES (131, '50244520', '2020-12-23', 1, 1, 1, 1, 1, 1, 1, 1, 1);
INSERT INTO `up_status` VALUES (132, '50244520', '2020-12-22', 1, 1, 1, 1, 1, 1, 1, 1, 1);
INSERT INTO `up_status` VALUES (133, '50244520', '2020-12-21', 1, 1, 1, 1, 1, 1, 1, 1, 1);
INSERT INTO `up_status` VALUES (134, '50244520', '2020-12-20', 1, 1, 1, 1, 1, 1, 1, 1, 1);
INSERT INTO `up_status` VALUES (135, '50244520', '2020-12-19', 1, 1, 1, 1, 1, 1, 1, 1, 1);
INSERT INTO `up_status` VALUES (136, '50244520', '2020-12-18', 1, 1, 1, 1, 1, 1, 1, 1, 1);
INSERT INTO `up_status` VALUES (201, '50244520', '2021-01-09', 18, 3, 394, 0, 17, 15, 9, 0, 42);
INSERT INTO `up_status` VALUES (202, '512308600', '2021-01-09', 2, 3, 22, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (203, '50244520', '2021-01-10', 18, 3, 401, 0, 17, 15, 9, 0, 42);
INSERT INTO `up_status` VALUES (204, '512308600', '2021-01-10', 2, 3, 22, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (205, '50244520', '2021-01-11', 18, 3, 401, 0, 17, 15, 9, 0, 42);
INSERT INTO `up_status` VALUES (206, '512308600', '2021-01-11', 2, 3, 22, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (207, '50244520', '2021-01-12', 18, 3, 401, 0, 17, 15, 9, 0, 42);
INSERT INTO `up_status` VALUES (208, '512308600', '2021-01-12', 2, 3, 22, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (209, '50244520', '2021-01-24', 18, 3, 402, 0, 17, 15, 9, 0, 42);
INSERT INTO `up_status` VALUES (210, '512308600', '2021-01-28', 2, 3, 22, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (211, '512308600', '2021-01-27', 2, 3, 22, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (212, '512308600', '2021-01-26', 2, 3, 22, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (213, '512308600', '2021-01-13', 2, 3, 22, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (214, '512308600', '2021-01-15', 2, 3, 22, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (215, '512308600', '2021-01-29', 2, 3, 22, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (216, '512308600', '2021-01-30', 2, 3, 22, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (217, '512308600', '2021-02-01', 2, 3, 22, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (218, '512308600', '2021-01-02', 2, 3, 22, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (219, '512308600', '2021-01-03', 2, 3, 22, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (221, '512308600', '2021-01-25', 2, 3, 22, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (222, '512308600', '2021-01-24', 2, 3, 22, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (223, '512308600', '2021-01-19', 2, 3, 22, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (224, '512308600', '2021-01-14', 2, 3, 22, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (225, '512308600', '2021-01-17', 2, 3, 22, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (226, '512308600', '2021-01-21', 2, 3, 22, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (227, '512308600', '2021-01-23', 2, 3, 22, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (229, '512308600', '2021-01-16', 2, 3, 22, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (230, '512308600', '2021-01-18', 2, 3, 22, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (231, '512308600', '2021-01-20', 2, 3, 22, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (232, '512308600', '2021-01-22', 2, 3, 22, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (233, '512308600', '2021-01-31', 2, 3, 22, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (234, '50244520', '2021-01-25', 18, 3, 402, 0, 17, 15, 9, 0, 42);
INSERT INTO `up_status` VALUES (235, '50244520', '2021-01-26', 18, 3, 402, 0, 17, 15, 9, 0, 42);
INSERT INTO `up_status` VALUES (236, '50244520', '2021-01-27', 18, 3, 402, 0, 17, 15, 9, 0, 42);
INSERT INTO `up_status` VALUES (237, '50244520', '2021-01-28', 18, 3, 402, 0, 17, 15, 9, 0, 42);
INSERT INTO `up_status` VALUES (238, '50244520', '2021-01-29', 18, 3, 402, 0, 17, 15, 9, 0, 42);
INSERT INTO `up_status` VALUES (239, '50244520', '2021-01-30', 18, 3, 402, 0, 17, 15, 9, 0, 42);
INSERT INTO `up_status` VALUES (240, '50244520', '2021-01-31', 18, 3, 402, 0, 17, 15, 9, 0, 42);
INSERT INTO `up_status` VALUES (241, '50244520', '2021-02-01', 18, 3, 402, 0, 17, 15, 9, 0, 42);
INSERT INTO `up_status` VALUES (242, '50244520', '2021-02-02', 18, 3, 402, 0, 17, 15, 9, 0, 42);
INSERT INTO `up_status` VALUES (243, '50244520', '2021-02-03', 18, 3, 402, 0, 17, 15, 9, 0, 42);
INSERT INTO `up_status` VALUES (244, '50244520', '2021-02-04', 18, 3, 402, 0, 17, 15, 9, 0, 42);
INSERT INTO `up_status` VALUES (245, '50244520', '2021-01-23', 18, 3, 402, 0, 17, 15, 9, 0, 42);
INSERT INTO `up_status` VALUES (246, '50244520', '2021-01-20', 18, 3, 402, 0, 17, 15, 9, 0, 42);
INSERT INTO `up_status` VALUES (247, '50244520', '2021-01-14', 18, 3, 402, 0, 17, 15, 9, 0, 42);
INSERT INTO `up_status` VALUES (248, '50244520', '2021-01-15', 18, 3, 402, 0, 17, 15, 9, 0, 42);
INSERT INTO `up_status` VALUES (249, '50244520', '2021-01-16', 18, 3, 402, 0, 17, 15, 9, 0, 42);
INSERT INTO `up_status` VALUES (250, '50244520', '2021-01-17', 18, 3, 402, 0, 17, 15, 9, 0, 42);
INSERT INTO `up_status` VALUES (251, '50244520', '2021-01-18', 18, 3, 402, 0, 17, 15, 9, 0, 42);
INSERT INTO `up_status` VALUES (252, '50244520', '2021-01-19', 18, 3, 402, 0, 17, 15, 9, 0, 42);
INSERT INTO `up_status` VALUES (253, '50244520', '2021-01-22', 18, 3, 402, 0, 17, 15, 9, 0, 42);
INSERT INTO `up_status` VALUES (254, '50244520', '2021-01-13', 18, 3, 402, 0, 17, 15, 9, 0, 42);
INSERT INTO `up_status` VALUES (255, '50244520', '2021-01-21', 18, 3, 402, 0, 17, 15, 9, 0, 42);
INSERT INTO `up_status` VALUES (256, '50244520', '2021-02-05', 18, 3, 402, 0, 17, 15, 9, 0, 42);
INSERT INTO `up_status` VALUES (257, '50244520', '2021-02-06', 19, 3, 413, 0, 17, 20, 9, 0, 44);
INSERT INTO `up_status` VALUES (260, '512308600', '2021-02-06', 2, 3, 23, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (261, '512308600', '2021-02-05', 2, 3, 23, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (262, '512308600', '2021-02-04', 2, 3, 23, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (263, '512308600', '2021-02-03', 2, 3, 23, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (264, '512308600', '2021-02-02', 2, 3, 23, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (278, '50244520', '2021-02-07', 19, 3, 413, 0, 17, 20, 9, 0, 44);
INSERT INTO `up_status` VALUES (279, '512308600', '2021-02-07', 2, 3, 23, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (280, '50244520', '2021-02-08', 19, 3, 413, 0, 17, 20, 9, 0, 44);
INSERT INTO `up_status` VALUES (281, '512308600', '2021-02-08', 2, 3, 23, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (282, '50244520', '2021-02-09', 19, 4, 417, 0, 19, 20, 9, 0, 51);
INSERT INTO `up_status` VALUES (283, '512308600', '2021-02-09', 2, 3, 23, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (284, '50244520', '2021-02-10', 22, 4, 479, 0, 22, 23, 9, 1, 75);
INSERT INTO `up_status` VALUES (285, '512308600', '2021-02-10', 2, 3, 23, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (286, '50244520', '2021-02-11', 22, 4, 1013, 0, 22, 25, 9, 1, 78);
INSERT INTO `up_status` VALUES (287, '512308600', '2021-02-11', 2, 3, 23, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (288, '50244520', '2021-02-12', 37, 4, 1784, 1, 31, 41, 8, 2, 134);
INSERT INTO `up_status` VALUES (289, '512308600', '2021-02-12', 2, 3, 24, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (290, '50244520', '2021-02-13', 45, 4, 1784, 1, 34, 43, 10, 2, 144);
INSERT INTO `up_status` VALUES (291, '512308600', '2021-02-13', 2, 3, 24, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (292, '50244520', '2021-02-14', 49, 4, 2152, 1, 38, 50, 12, 2, 155);
INSERT INTO `up_status` VALUES (293, '512308600', '2021-02-14', 2, 3, 24, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (294, '50244520', '2021-02-15', 52, 4, 2407, 1, 39, 52, 12, 2, 161);
INSERT INTO `up_status` VALUES (295, '512308600', '2021-02-15', 2, 3, 24, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (296, '50244520', '2021-02-16', 55, 4, 2525, 1, 43, 54, 13, 3, 169);
INSERT INTO `up_status` VALUES (297, '512308600', '2021-02-16', 2, 3, 24, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (298, '50244520', '2021-02-17', 59, 4, 2678, 1, 45, 53, 15, 3, 175);
INSERT INTO `up_status` VALUES (299, '512308600', '2021-02-17', 2, 3, 24, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (302, '50244520', '2021-02-18', 59, 4, 2863, 1, 45, 54, 15, 3, 177);
INSERT INTO `up_status` VALUES (303, '512308600', '2021-02-18', 2, 3, 24, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (308, '50244520', '2021-02-19', 60, 4, 2943, 1, 46, 54, 15, 3, 178);
INSERT INTO `up_status` VALUES (309, '512308600', '2021-02-19', 2, 3, 24, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (310, '50244520', '2021-02-20', 61, 4, 2943, 1, 46, 54, 15, 3, 178);
INSERT INTO `up_status` VALUES (311, '512308600', '2021-02-20', 2, 3, 24, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (312, '50244520', '2021-02-21', 62, 4, 3006, 2, 46, 54, 15, 3, 178);
INSERT INTO `up_status` VALUES (313, '512308600', '2021-02-21', 2, 3, 24, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (314, '50244520', '2021-02-22', 66, 4, 3067, 2, 46, 55, 17, 3, 183);
INSERT INTO `up_status` VALUES (315, '512308600', '2021-02-22', 2, 3, 24, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (316, '50244520', '2021-02-23', 68, 4, 3168, 2, 46, 56, 17, 3, 184);
INSERT INTO `up_status` VALUES (317, '512308600', '2021-02-23', 2, 3, 24, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (318, '50244520', '2021-02-24', 68, 4, 3168, 2, 46, 56, 17, 3, 184);
INSERT INTO `up_status` VALUES (319, '512308600', '2021-02-24', 2, 3, 24, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (320, '50244520', '2021-02-25', 68, 5, 3186, 2, 48, 56, 17, 4, 188);
INSERT INTO `up_status` VALUES (321, '512308600', '2021-02-25', 2, 3, 25, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (322, '50244520', '2021-02-26', 69, 5, 3238, 2, 48, 60, 19, 4, 189);
INSERT INTO `up_status` VALUES (323, '512308600', '2021-02-26', 2, 3, 25, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (324, '50244520', '2021-02-27', 70, 5, 3445, 2, 48, 61, 19, 4, 197);
INSERT INTO `up_status` VALUES (325, '512308600', '2021-02-27', 2, 3, 25, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (326, '512308600', '2021-02-28', 2, 3, 25, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (327, '50244520', '2021-02-28', 70, 5, 3445, 2, 48, 62, 19, 4, 197);
INSERT INTO `up_status` VALUES (328, '50244520', '2021-03-01', 73, 5, 3594, 2, 48, 62, 19, 4, 201);
INSERT INTO `up_status` VALUES (329, '512308600', '2021-03-01', 2, 3, 25, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (330, '512308600', '2021-03-02', 2, 3, 25, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (331, '50244520', '2021-03-02', 74, 5, 3594, 2, 48, 62, 19, 4, 202);
INSERT INTO `up_status` VALUES (332, '512308600', '2021-03-03', 2, 3, 26, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (333, '50244520', '2021-03-03', 74, 5, 3634, 2, 48, 62, 19, 4, 202);
INSERT INTO `up_status` VALUES (348, '512308600', '2021-03-05', 2, 3, 26, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (349, '50244520', '2021-03-05', 74, 5, 3681, 2, 48, 63, 19, 4, 203);
INSERT INTO `up_status` VALUES (350, '512308600', '2021-03-04', 2, 3, 26, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (351, '50244520', '2021-03-04', 74, 5, 3681, 2, 48, 63, 19, 4, 203);
INSERT INTO `up_status` VALUES (352, '50244520', '2021-03-06', 74, 5, 3705, 2, 48, 63, 19, 4, 204);
INSERT INTO `up_status` VALUES (353, '512308600', '2021-03-06', 2, 3, 26, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (354, '50244520', '2021-03-07', 74, 5, 3732, 2, 48, 63, 19, 4, 204);
INSERT INTO `up_status` VALUES (355, '512308600', '2021-03-07', 2, 3, 26, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (357, '335515254', '2021-03-07', 758, 4, 510, 1, 26, 15, 43, 17, 82);
INSERT INTO `up_status` VALUES (358, '512308600', '2021-03-09', 2, 3, 26, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (359, '335515254', '2021-03-09', 852, 4, 689, 1, 28, 21, 58, 17, 97);
INSERT INTO `up_status` VALUES (360, '50244520', '2021-03-09', 75, 5, 3779, 2, 48, 63, 19, 4, 204);
INSERT INTO `up_status` VALUES (361, '335515254', '2021-03-10', 899, 5, 792, 2, 29, 23, 68, 17, 118);
INSERT INTO `up_status` VALUES (362, '512308600', '2021-03-10', 2, 3, 26, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (363, '50244520', '2021-03-10', 77, 5, 3795, 2, 48, 63, 19, 4, 204);
INSERT INTO `up_status` VALUES (364, '512308600', '2021-03-11', 2, 3, 26, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (365, '335515254', '2021-03-11', 923, 6, 891, 3, 31, 24, 78, 20, 126);
INSERT INTO `up_status` VALUES (366, '50244520', '2021-03-11', 77, 5, 3814, 2, 48, 63, 19, 4, 205);
INSERT INTO `up_status` VALUES (367, '335515254', '2021-03-12', 934, 6, 1035, 3, 31, 26, 80, 22, 130);
INSERT INTO `up_status` VALUES (368, '512308600', '2021-03-12', 2, 3, 26, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (369, '50244520', '2021-03-12', 76, 5, 3860, 2, 48, 63, 19, 4, 205);
INSERT INTO `up_status` VALUES (370, '512308600', '2021-03-13', 2, 3, 26, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (371, '335515254', '2021-03-13', 932, 6, 1052, 3, 31, 27, 80, 22, 134);
INSERT INTO `up_status` VALUES (372, '50244520', '2021-03-13', 76, 5, 3876, 2, 48, 63, 19, 4, 205);
INSERT INTO `up_status` VALUES (397, '50244520', '2021-03-14', 78, 6, 3876, 2, 49, 63, 19, 4, 206);
INSERT INTO `up_status` VALUES (398, '335515254', '2021-03-14', 1155, 6, 1052, 3, 34, 27, 80, 22, 134);
INSERT INTO `up_status` VALUES (399, '512308600', '2021-03-14', 2, 3, 26, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (400, '512308600', '2021-03-15', 2, 3, 26, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (401, '335515254', '2021-03-15', 1225, 6, 1086, 3, 34, 27, 80, 22, 143);
INSERT INTO `up_status` VALUES (402, '50244520', '2021-03-15', 79, 6, 3893, 2, 50, 64, 19, 4, 210);
INSERT INTO `up_status` VALUES (403, '335515254', '2021-03-16', 1260, 7, 1319, 5, 39, 28, 88, 23, 161);
INSERT INTO `up_status` VALUES (404, '50244520', '2021-03-16', 79, 6, 3931, 2, 50, 64, 19, 4, 211);
INSERT INTO `up_status` VALUES (405, '512308600', '2021-03-16', 2, 3, 26, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (406, '50244520', '2021-03-17', 79, 6, 3940, 2, 50, 64, 19, 4, 211);
INSERT INTO `up_status` VALUES (407, '335515254', '2021-03-17', 1293, 7, 1480, 5, 39, 29, 88, 23, 167);
INSERT INTO `up_status` VALUES (408, '512308600', '2021-03-17', 2, 3, 26, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (414, '512308600', '2021-03-18', 2, 3, 26, 1, 1, 2, 4, 0, 11);
INSERT INTO `up_status` VALUES (415, '335515254', '2021-03-18', 1435, 7, 1480, 5, 39, 29, 88, 24, 173);
INSERT INTO `up_status` VALUES (416, '50244520', '2021-03-18', 79, 6, 3940, 2, 50, 65, 19, 4, 211);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `password` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `USER`(`username`, `password`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'admin', 'admin');

-- ----------------------------
-- Table structure for user_to_group
-- ----------------------------
DROP TABLE IF EXISTS `user_to_group`;
CREATE TABLE `user_to_group`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NULL DEFAULT NULL,
  `group_id` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `USER_TO_GROUP`(`user_id`, `group_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of user_to_group
-- ----------------------------
INSERT INTO `user_to_group` VALUES (1, 1, 1);
INSERT INTO `user_to_group` VALUES (3, 1, 3);

SET FOREIGN_KEY_CHECKS = 1;

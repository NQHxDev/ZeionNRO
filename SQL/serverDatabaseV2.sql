/*
 Navicat Premium Dump SQL

 Source Server         : MySQL
 Source Server Type    : MySQL
 Source Server Version : 90600 (9.6.0)
 Source Host           : localhost:3306
 Source Schema         : hashirama

 Target Server Type    : MySQL
 Target Server Version : 90600 (9.6.0)
 File Encoding         : 65001

 Date: 25/04/2026 08:49:34
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for account
-- ----------------------------
DROP TABLE IF EXISTS `account`;
CREATE TABLE `account` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `ban` smallint NOT NULL DEFAULT '0',
  `point_post` int NOT NULL DEFAULT '0',
  `last_post` int NOT NULL DEFAULT '0',
  `role` int NOT NULL DEFAULT '-1',
  `is_admin` tinyint(1) NOT NULL DEFAULT '0',
  `last_time_login` timestamp NOT NULL DEFAULT '2002-05-07 00:00:00',
  `last_time_logout` timestamp NOT NULL DEFAULT '2002-05-07 00:00:00',
  `ip_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '127.0.0.1',
  `active` int NOT NULL DEFAULT '0',
  `reward` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci,
  `thoi_vang` int NOT NULL DEFAULT '0',
  `server_login` int NOT NULL DEFAULT '1',
  `new_reg` int NOT NULL DEFAULT '0',
  `ip` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '127.0.0.1',
  `phone` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '123456789',
  `last_server_change_time` timestamp NOT NULL DEFAULT '2002-05-07 00:00:00',
  `ruby` int NOT NULL DEFAULT '0',
  `count_card` int DEFAULT '0',
  `type_bonus` int DEFAULT '0',
  `ref` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0',
  `diemgioithieu` int NOT NULL DEFAULT '0',
  `vnd` bigint NOT NULL DEFAULT '0',
  `tongnap` bigint NOT NULL DEFAULT '0',
  `tongnap_7ngay` bigint NOT NULL DEFAULT '0',
  `gioithieu` int NOT NULL DEFAULT '0',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `username` (`username`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of account
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for achivements
-- ----------------------------
DROP TABLE IF EXISTS `achivements`;
CREATE TABLE `achivements` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `detail` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `money` int NOT NULL,
  `max_count` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of achivements
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for adminpanel
-- ----------------------------
DROP TABLE IF EXISTS `adminpanel`;
CREATE TABLE `adminpanel` (
  `domain` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci,
  `title` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci,
  `logo` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci,
  `trangthai` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `android` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci,
  `iphone` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci,
  `windows` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci,
  `java` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci,
  `apikey` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci,
  `taikhoanmb` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci,
  `stkmb` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci,
  `tenmb` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of adminpanel
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for alert
-- ----------------------------
DROP TABLE IF EXISTS `alert`;
CREATE TABLE `alert` (
  `content` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_bin
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of alert
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for attribute_server
-- ----------------------------
DROP TABLE IF EXISTS `attribute_server`;
CREATE TABLE `attribute_server` (
  `id` int NOT NULL AUTO_INCREMENT,
  `attribute_template_id` int NOT NULL,
  `value` int NOT NULL,
  `time` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `attribute_template_id` (`attribute_template_id`),
  CONSTRAINT `attribute_server_ibfk_1` FOREIGN KEY (`attribute_template_id`) REFERENCES `attribute_template` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of attribute_server
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for attribute_template
-- ----------------------------
DROP TABLE IF EXISTS `attribute_template`;
CREATE TABLE `attribute_template` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` mediumtext CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci;

-- ----------------------------
-- Records of attribute_template
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for bang_tin
-- ----------------------------
DROP TABLE IF EXISTS `bang_tin`;
CREATE TABLE `bang_tin` (
  `id` int NOT NULL,
  `tieu_de` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci,
  `info` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of bang_tin
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for cai_trang
-- ----------------------------
DROP TABLE IF EXISTS `cai_trang`;
CREATE TABLE `cai_trang` (
  `id_temp` int DEFAULT NULL,
  `head` int DEFAULT NULL,
  `body` int DEFAULT NULL,
  `leg` int DEFAULT NULL,
  `bag` int DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of cai_trang
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for caption
-- ----------------------------
DROP TABLE IF EXISTS `caption`;
CREATE TABLE `caption` (
  `id` int NOT NULL AUTO_INCREMENT,
  `earth` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `saiya` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `namek` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `power` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of caption
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
  `id` int NOT NULL AUTO_INCREMENT,
  `tenchuyenmuc` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `mota` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of category
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for clan_sv1
-- ----------------------------
DROP TABLE IF EXISTS `clan_sv1`;
CREATE TABLE `clan_sv1` (
  `id` int NOT NULL,
  `NAME` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `slogan` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '',
  `img_id` int NOT NULL DEFAULT '0',
  `power_point` bigint NOT NULL DEFAULT '0',
  `max_member` smallint NOT NULL DEFAULT '10',
  `clan_point` int NOT NULL DEFAULT '0',
  `LEVEL` int NOT NULL DEFAULT '1',
  `members` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of clan_sv1
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for clan_sv2
-- ----------------------------
DROP TABLE IF EXISTS `clan_sv2`;
CREATE TABLE `clan_sv2` (
  `id` int NOT NULL,
  `NAME` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `slogan` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '',
  `img_id` int NOT NULL DEFAULT '0',
  `power_point` bigint NOT NULL DEFAULT '0',
  `max_member` smallint NOT NULL DEFAULT '10',
  `clan_point` int NOT NULL DEFAULT '0',
  `LEVEL` int NOT NULL DEFAULT '1',
  `members` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of clan_sv2
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for collection_book
-- ----------------------------
DROP TABLE IF EXISTS `collection_book`;
CREATE TABLE `collection_book` (
  `id` int NOT NULL AUTO_INCREMENT,
  `item_id` int NOT NULL,
  `name` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `info` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `icon` int NOT NULL,
  `rank` int NOT NULL,
  `max_amount` int NOT NULL,
  `type` int NOT NULL,
  `mob_id` int NOT NULL,
  `head` int NOT NULL,
  `body` int NOT NULL,
  `leg` int NOT NULL,
  `bag` int NOT NULL,
  `options` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `aura` int NOT NULL DEFAULT '-1',
  PRIMARY KEY (`id`),
  KEY `item_id` (`item_id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of collection_book
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment` (
  `id` int NOT NULL AUTO_INCREMENT,
  `id_post` int DEFAULT NULL,
  `id_user` int DEFAULT NULL,
  `noidung` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `ngaytao` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=335 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of comment
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for consignment_shop
-- ----------------------------
DROP TABLE IF EXISTS `consignment_shop`;
CREATE TABLE `consignment_shop` (
  `id` int NOT NULL AUTO_INCREMENT,
  `consignor_id` bigint DEFAULT NULL,
  `tab` int DEFAULT NULL,
  `item_id` smallint DEFAULT NULL,
  `gold` int DEFAULT NULL,
  `gem` int DEFAULT NULL,
  `quantity` int DEFAULT NULL,
  `item_options` text,
  `up_top` tinyint(1) DEFAULT '0',
  `sold` tinyint(1) DEFAULT '0',
  `time_consign` bigint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of consignment_shop
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for event
-- ----------------------------
DROP TABLE IF EXISTS `event`;
CREATE TABLE `event` (
  `server` int DEFAULT '0',
  `kame` int DEFAULT '0',
  `bill` int DEFAULT '0',
  `karin` int DEFAULT '0',
  `thuongde` int DEFAULT '0',
  `thanvutru` int DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- ----------------------------
-- Records of event
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for flag_bag
-- ----------------------------
DROP TABLE IF EXISTS `flag_bag`;
CREATE TABLE `flag_bag` (
  `id` int NOT NULL,
  `icon_data` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `NAME` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'flag_bag',
  `gold` int NOT NULL DEFAULT '-1',
  `gem` int NOT NULL DEFAULT '-1',
  `icon_id` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of flag_bag
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for gift_code_histories
-- ----------------------------
DROP TABLE IF EXISTS `gift_code_histories`;
CREATE TABLE `gift_code_histories` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `player_id` int NOT NULL,
  `gift_code_id` bigint unsigned NOT NULL,
  `code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `gift_code_histories_gift_code_id_foreign` (`gift_code_id`),
  CONSTRAINT `gift_code_histories_ibfk_1` FOREIGN KEY (`gift_code_id`) REFERENCES `gift_codes` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of gift_code_histories
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for gift_codes
-- ----------------------------
DROP TABLE IF EXISTS `gift_codes`;
CREATE TABLE `gift_codes` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `type` tinyint NOT NULL COMMENT '0: For personal, 1: For everyone',
  `code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `gold` int NOT NULL DEFAULT '0',
  `gem` int NOT NULL DEFAULT '0',
  `ruby` int NOT NULL DEFAULT '0',
  `items` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `status` tinyint NOT NULL COMMENT '0: Inactive, 1: Active, 2: Expired',
  `active` int NOT NULL DEFAULT '0' COMMENT '0: Kh?´ng y?ªu cáº§u k?­ch hoáº¡t , 1 : y?ªu cáº§u k?­ch hoáº¡t',
  `expires_at` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of gift_codes
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for head_avatar
-- ----------------------------
DROP TABLE IF EXISTS `head_avatar`;
CREATE TABLE `head_avatar` (
  `head_id` int DEFAULT NULL,
  `avatar_id` int DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of head_avatar
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for history_receive_goldbar
-- ----------------------------
DROP TABLE IF EXISTS `history_receive_goldbar`;
CREATE TABLE `history_receive_goldbar` (
  `player_id` int NOT NULL,
  `player_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `gold_before_receive` int NOT NULL,
  `gold_after_receive` int NOT NULL,
  `gold_bag_before` int NOT NULL,
  `gold_bag_after` int NOT NULL,
  `gold_box_before` int NOT NULL,
  `gold_box_after` int NOT NULL,
  `time_receive` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY `player_id` (`player_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of history_receive_goldbar
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for history_tambao
-- ----------------------------
DROP TABLE IF EXISTS `history_tambao`;
CREATE TABLE `history_tambao` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `id_player` bigint NOT NULL,
  `key_item_id` int NOT NULL,
  `item` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `won_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_player_time` (`id_player`,`won_at`),
  KEY `idx_key_time` (`key_item_id`,`won_at`),
  CONSTRAINT `history_tambao_chk_1` CHECK (json_valid(`item`))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of history_tambao
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for history_transaction
-- ----------------------------
DROP TABLE IF EXISTS `history_transaction`;
CREATE TABLE `history_transaction` (
  `player_1` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `player_2` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `item_player_1` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `item_player_2` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `bag_1_before_tran` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `bag_2_before_tran` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `bag_1_after_tran` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `bag_2_after_tran` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `time_tran` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of history_transaction
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for img_by_name
-- ----------------------------
DROP TABLE IF EXISTS `img_by_name`;
CREATE TABLE `img_by_name` (
  `id` int NOT NULL AUTO_INCREMENT,
  `NAME` varchar(55) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `n_frame` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `NAME` (`NAME`)
) ENGINE=InnoDB AUTO_INCREMENT=163 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of img_by_name
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for intrinsic
-- ----------------------------
DROP TABLE IF EXISTS `intrinsic`;
CREATE TABLE `intrinsic` (
  `id` int NOT NULL,
  `NAME` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `param_from_1` int NOT NULL DEFAULT '0',
  `param_to_1` int NOT NULL DEFAULT '0',
  `param_from_2` int NOT NULL DEFAULT '0',
  `param_to_2` int NOT NULL DEFAULT '0',
  `icon` int NOT NULL DEFAULT '0',
  `gender` smallint NOT NULL DEFAULT '3',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of intrinsic
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for item_option_template
-- ----------------------------
DROP TABLE IF EXISTS `item_option_template`;
CREATE TABLE `item_option_template` (
  `id` int NOT NULL,
  `NAME` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `TYPE` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of item_option_template
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for item_shop
-- ----------------------------
DROP TABLE IF EXISTS `item_shop`;
CREATE TABLE `item_shop` (
  `id` int NOT NULL AUTO_INCREMENT,
  `tab_id` int NOT NULL,
  `temp_id` int NOT NULL,
  `gold` int NOT NULL,
  `gem` int NOT NULL,
  `is_new` tinyint(1) NOT NULL DEFAULT '1',
  `is_sell` tinyint(1) NOT NULL DEFAULT '1',
  `item_exchange` int NOT NULL DEFAULT '-1',
  `quantity_exchange` int NOT NULL DEFAULT '0',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `tab_id` (`tab_id`) USING BTREE,
  KEY `temp_id` (`temp_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=983139161 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of item_shop
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for item_shop_option
-- ----------------------------
DROP TABLE IF EXISTS `item_shop_option`;
CREATE TABLE `item_shop_option` (
  `item_shop_id` int NOT NULL,
  `option_id` int NOT NULL,
  `param` int NOT NULL,
  KEY `item_shop_id` (`item_shop_id`),
  KEY `option_id` (`option_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of item_shop_option
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for item_template
-- ----------------------------
DROP TABLE IF EXISTS `item_template`;
CREATE TABLE `item_template` (
  `id` int NOT NULL,
  `TYPE` int NOT NULL,
  `gender` smallint NOT NULL,
  `NAME` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `icon_id` int NOT NULL,
  `part` int NOT NULL,
  `is_up_to_up` tinyint(1) NOT NULL,
  `power_require` int NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of item_template
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for kham_ngoc
-- ----------------------------
DROP TABLE IF EXISTS `kham_ngoc`;
CREATE TABLE `kham_ngoc` (
  `id` int NOT NULL,
  `options` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of kham_ngoc
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for map_template
-- ----------------------------
DROP TABLE IF EXISTS `map_template`;
CREATE TABLE `map_template` (
  `id` int NOT NULL,
  `NAME` varchar(55) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `data` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '[]',
  `zones` int NOT NULL DEFAULT '1',
  `max_player` int NOT NULL DEFAULT '15',
  `waypoints` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `mobs` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `npcs` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `effect_noel` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `eff_event` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `effect` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of map_template
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for mb_bank
-- ----------------------------
DROP TABLE IF EXISTS `mb_bank`;
CREATE TABLE `mb_bank` (
  `id` int NOT NULL AUTO_INCREMENT,
  `tid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `amount` int NOT NULL,
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `status` int NOT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of mb_bank
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for member_gift
-- ----------------------------
DROP TABLE IF EXISTS `member_gift`;
CREATE TABLE `member_gift` (
  `idd` bigint unsigned NOT NULL AUTO_INCREMENT,
  `player_idd` int NOT NULL,
  `typed` tinyint NOT NULL COMMENT '0: For personal, 1: For everyone',
  `coded` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `goldd` int NOT NULL DEFAULT '0',
  `gemd` int NOT NULL DEFAULT '0',
  `rubyd` int NOT NULL DEFAULT '0',
  `itemsd` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `statusd` tinyint NOT NULL COMMENT '0: Inactive, 1: Active, 2: Expired',
  `actived` int NOT NULL DEFAULT '0' COMMENT '0: Kh?´ng y?ªu cáº§u k?­ch hoáº¡t , 1 : y?ªu cáº§u k?­ch hoáº¡t',
  `expires_atd` timestamp NULL DEFAULT NULL,
  `created_atd` timestamp NULL DEFAULT NULL,
  `updated_atd` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`idd`)
) ENGINE=InnoDB AUTO_INCREMENT=257 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of member_gift
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for member_gift_lichsu
-- ----------------------------
DROP TABLE IF EXISTS `member_gift_lichsu`;
CREATE TABLE `member_gift_lichsu` (
  `idd` bigint unsigned NOT NULL AUTO_INCREMENT,
  `player_idd` int NOT NULL,
  `gift_code_idd` bigint unsigned NOT NULL,
  `coded` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_atd` timestamp NULL DEFAULT NULL,
  `updated_atd` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`idd`),
  KEY `member_gift_lichsu_gift_code_id_foreign` (`gift_code_idd`),
  CONSTRAINT `member_gift_lichsu_ibfk_1` FOREIGN KEY (`gift_code_idd`) REFERENCES `member_gift` (`idd`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of member_gift_lichsu
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for mini_pet
-- ----------------------------
DROP TABLE IF EXISTS `mini_pet`;
CREATE TABLE `mini_pet` (
  `id_temp` bigint DEFAULT NULL,
  `head` int DEFAULT '-1',
  `body` int DEFAULT '-1',
  `leg` int DEFAULT '-1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of mini_pet
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for mob_template
-- ----------------------------
DROP TABLE IF EXISTS `mob_template`;
CREATE TABLE `mob_template` (
  `id` int NOT NULL,
  `TYPE` int NOT NULL,
  `NAME` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `hp` int NOT NULL,
  `range_move` smallint NOT NULL,
  `speed` smallint NOT NULL,
  `dart_type` smallint NOT NULL,
  `percent_dame` smallint NOT NULL DEFAULT '5',
  `percent_tiem_nang` smallint NOT NULL DEFAULT '50',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of mob_template
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for moc_vong_quay
-- ----------------------------
DROP TABLE IF EXISTS `moc_vong_quay`;
CREATE TABLE `moc_vong_quay` (
  `id` int NOT NULL AUTO_INCREMENT,
  `max_value` int NOT NULL DEFAULT '0',
  `item_id` int NOT NULL DEFAULT '-1',
  `quantity` int NOT NULL DEFAULT '0',
  `item_options` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '[[73,1]]',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of moc_vong_quay
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for nclass
-- ----------------------------
DROP TABLE IF EXISTS `nclass`;
CREATE TABLE `nclass` (
  `id` int NOT NULL,
  `NAME` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of nclass
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for news_posts
-- ----------------------------
DROP TABLE IF EXISTS `news_posts`;
CREATE TABLE `news_posts` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `user_id` bigint unsigned NOT NULL,
  `category_id` bigint unsigned NOT NULL,
  `slug` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `title` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `short_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `thumbnail` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `views` int NOT NULL DEFAULT '0',
  `status` int NOT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `news_posts_user_id_foreign` (`user_id`) USING BTREE,
  KEY `news_posts_category_id_foreign` (`category_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of news_posts
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for notifications
-- ----------------------------
DROP TABLE IF EXISTS `notifications`;
CREATE TABLE `notifications` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` text,
  `content` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb3;

-- ----------------------------
-- Records of notifications
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for npc_template
-- ----------------------------
DROP TABLE IF EXISTS `npc_template`;
CREATE TABLE `npc_template` (
  `id` int NOT NULL,
  `NAME` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `head` int NOT NULL,
  `body` int NOT NULL,
  `leg` int NOT NULL,
  `avt` int NOT NULL,
  `id_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of npc_template
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for part
-- ----------------------------
DROP TABLE IF EXISTS `part`;
CREATE TABLE `part` (
  `id` int NOT NULL,
  `TYPE` int NOT NULL,
  `DATA` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of part
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for pet_follow
-- ----------------------------
DROP TABLE IF EXISTS `pet_follow`;
CREATE TABLE `pet_follow` (
  `id_temp` bigint DEFAULT '-1',
  `icon` int DEFAULT '-1',
  `width` int DEFAULT '-1',
  `height` int DEFAULT '-1',
  `frame` int DEFAULT '-1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of pet_follow
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for phong_thi_nghiem
-- ----------------------------
DROP TABLE IF EXISTS `phong_thi_nghiem`;
CREATE TABLE `phong_thi_nghiem` (
  `id` int NOT NULL,
  `name_tab` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci,
  `name_binh` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci,
  `items` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `thoi_gian` int NOT NULL DEFAULT '0',
  `item_nhan` int NOT NULL DEFAULT '0',
  `info` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci,
  `color` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of phong_thi_nghiem
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for phongchat
-- ----------------------------
DROP TABLE IF EXISTS `phongchat`;
CREATE TABLE `phongchat` (
  `id` int NOT NULL AUTO_INCREMENT,
  `id_user` int DEFAULT NULL,
  `noidung` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `ngaytao` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=282 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of phongchat
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for phuc_loi
-- ----------------------------
DROP TABLE IF EXISTS `phuc_loi`;
CREATE TABLE `phuc_loi` (
  `id` int NOT NULL,
  `name` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `max_tab` int NOT NULL,
  `id_tab` int NOT NULL DEFAULT '0',
  `info_phucloi` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci,
  `action` int NOT NULL DEFAULT '1',
  `tich_luy` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of phuc_loi
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for phuc_loi_tab
-- ----------------------------
DROP TABLE IF EXISTS `phuc_loi_tab`;
CREATE TABLE `phuc_loi_tab` (
  `id` int NOT NULL,
  `tab_id` int NOT NULL DEFAULT '0',
  `name` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `max_count` int NOT NULL DEFAULT '0',
  `list_item` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci,
  `active` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of phuc_loi_tab
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for player
-- ----------------------------
DROP TABLE IF EXISTS `player`;
CREATE TABLE `player` (
  `id` int NOT NULL AUTO_INCREMENT,
  `account_id` int DEFAULT NULL,
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `power` bigint NOT NULL DEFAULT '0',
  `head` int NOT NULL DEFAULT '102',
  `gender` int NOT NULL,
  `have_tennis_space_ship` tinyint(1) DEFAULT '0',
  `clan_id_sv1` int NOT NULL DEFAULT '-1',
  `clan_id_sv2` int NOT NULL DEFAULT '-1',
  `data_inventory` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `data_location` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `data_magic_tree` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `items_body` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `items_bag` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `items_box` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `items_box_lucky_round` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `friends` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `enemies` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `data_intrinsic` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `data_item_time` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `data_item_time_sieucap` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `data_mabu_egg` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `data_charm` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `skills` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `skills_shortcut` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `pet_info` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `pet_power` bigint NOT NULL DEFAULT '0',
  `pet_point` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `pet_body` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `pet_skill` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `data_black_ball` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `thoi_vang` int DEFAULT NULL,
  `data_side_task` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `new_reg` int NOT NULL DEFAULT '0',
  `event_point` int NOT NULL DEFAULT '0',
  `1sao` int NOT NULL DEFAULT '0',
  `2sao` int NOT NULL DEFAULT '0',
  `3sao` int NOT NULL DEFAULT '0',
  `top` int NOT NULL DEFAULT '0',
  `collection_book` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci,
  `challenge` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '[2000000,0,0]',
  `firstTimeLogin` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `sk_tet` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '[0,0,0,0,0]',
  `buy_limit` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '[0,0,0,0,0,0,0,0]',
  `moc_nap` int DEFAULT '0',
  `achivements` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci,
  `reward_limit` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '[0,0,0,0,0,0,0,0]',
  `hoivien_vip` int NOT NULL DEFAULT '0',
  `diemdanh` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci,
  `moc_nap2` int NOT NULL DEFAULT '0',
  `data_item_noel` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `chuyencan` int NOT NULL DEFAULT '0',
  `drop_vang_ngoc` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '[0,0]',
  `tong_nap` int NOT NULL DEFAULT '0',
  `nhan_moc_nap` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '[0,0,0,0,0]',
  `nhan_moc_nap2` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '[0,0,0,0,0]',
  `chuyen_sinh` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '[0,0,0,0,0]',
  `danh_hieu` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '[0,0,0,0,0,0,0,0]',
  `rank_sieu_hang` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '[0,0,0,0,0,0]',
  `so_may_man` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '[]',
  `data_offtrain` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '[0,0]',
  `reset_ngay` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '[0,0]',
  `active_phuc_loi` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '[]',
  `kill_boss` int NOT NULL DEFAULT '0',
  `check_qua_chuyencan` int NOT NULL DEFAULT '0',
  `naplandau` int NOT NULL DEFAULT '0',
  `tichluynap` int NOT NULL DEFAULT '0',
  `nhan_moc_nap3` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '[0,0,0,0,0]',
  `sukien_2thang9` bigint NOT NULL DEFAULT '0',
  `sukien_trungthu` bigint NOT NULL DEFAULT '0',
  `diem_quay` int NOT NULL DEFAULT '0',
  `active_vong_quay` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `kham_ngoc` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `active_kham_ngoc` int NOT NULL DEFAULT '0',
  `ruong_cai_trang` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `ruong_phu_kien` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `ruong_pet` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `ruong_linh_thu` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `ruong_thu_cuoi` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `active_ruong_suu_tam` int NOT NULL DEFAULT '0',
  `phut_online` int NOT NULL DEFAULT '0',
  `check_online` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '[]',
  `check_diem_danh` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '[]',
  `weektimelogin` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `dan_duoc` varchar(225) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '[0,0,0]',
  `phong_thi_nghiem` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `diemdanh` (`id`),
  UNIQUE KEY `account_id` (`account_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of player
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for player_point
-- ----------------------------
DROP TABLE IF EXISTS `player_point`;
CREATE TABLE `player_point` (
  `player_id` int NOT NULL,
  `power` bigint DEFAULT '0',
  `tiem_nang` bigint DEFAULT '0',
  `hp_goc` double DEFAULT '0',
  `mp_goc` double DEFAULT '0',
  `dame_goc` double DEFAULT '0',
  `def_goc` double DEFAULT '0',
  `crit_goc` int DEFAULT '0',
  `hp` double DEFAULT '0',
  `mp` double DEFAULT '0',
  `stamina` int DEFAULT '0',
  `max_stamina` int DEFAULT '0',
  `limit_power` int DEFAULT '0',
  PRIMARY KEY (`player_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of player_point
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for player_task
-- ----------------------------
DROP TABLE IF EXISTS `player_task`;
CREATE TABLE `player_task` (
  `player_id` int NOT NULL,
  `task_id` int DEFAULT '0',
  `sub_id` int DEFAULT '0',
  `task_count` int DEFAULT '0',
  PRIMARY KEY (`player_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of player_task
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for post
-- ----------------------------
DROP TABLE IF EXISTS `post`;
CREATE TABLE `post` (
  `id` int NOT NULL AUTO_INCREMENT,
  `id_user` int NOT NULL,
  `tieude` varchar(225) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `noidung` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci,
  `ngaytao` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `id_chuyenmuc` int DEFAULT NULL,
  `luotxem` int NOT NULL DEFAULT '0',
  `ghim` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=96 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of post
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for power_limit
-- ----------------------------
DROP TABLE IF EXISTS `power_limit`;
CREATE TABLE `power_limit` (
  `id` int NOT NULL AUTO_INCREMENT,
  `power` bigint NOT NULL,
  `hp` int NOT NULL,
  `mp` int NOT NULL,
  `damage` int NOT NULL,
  `defense` int NOT NULL,
  `critical` int NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=13 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of power_limit
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for qua_dang_nhap
-- ----------------------------
DROP TABLE IF EXISTS `qua_dang_nhap`;
CREATE TABLE `qua_dang_nhap` (
  `id` int NOT NULL AUTO_INCREMENT,
  `items` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `day` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of qua_dang_nhap
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for qua_dang_nhap_history
-- ----------------------------
DROP TABLE IF EXISTS `qua_dang_nhap_history`;
CREATE TABLE `qua_dang_nhap_history` (
  `id` int NOT NULL AUTO_INCREMENT,
  `account_id` int NOT NULL,
  `date_received` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `day` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of qua_dang_nhap_history
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for ruong_suu_tam
-- ----------------------------
DROP TABLE IF EXISTS `ruong_suu_tam`;
CREATE TABLE `ruong_suu_tam` (
  `id` int NOT NULL,
  `type` int NOT NULL,
  `id_item` int NOT NULL,
  `option_id` int NOT NULL,
  `param` int NOT NULL,
  `name` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of ruong_suu_tam
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for shop
-- ----------------------------
DROP TABLE IF EXISTS `shop`;
CREATE TABLE `shop` (
  `id` int NOT NULL AUTO_INCREMENT,
  `npc_id` int NOT NULL,
  `shop_order` int NOT NULL,
  PRIMARY KEY (`id`,`shop_order`) USING BTREE,
  KEY `npc_id` (`npc_id`) USING BTREE,
  CONSTRAINT `shop_ibfk_1` FOREIGN KEY (`npc_id`) REFERENCES `npc_template` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of shop
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for side_task_template
-- ----------------------------
DROP TABLE IF EXISTS `side_task_template`;
CREATE TABLE `side_task_template` (
  `id` int NOT NULL,
  `NAME` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `max_count_lv1` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `max_count_lv2` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `max_count_lv3` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `max_count_lv4` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `max_count_lv5` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of side_task_template
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for sieu_hang
-- ----------------------------
DROP TABLE IF EXISTS `sieu_hang`;
CREATE TABLE `sieu_hang` (
  `player_id` int NOT NULL,
  `point` int DEFAULT '100',
  `used_ticket` int DEFAULT '0',
  `last_time_ticket` bigint DEFAULT '0',
  PRIMARY KEY (`player_id`),
  CONSTRAINT `fk_sieu_hang_player` FOREIGN KEY (`player_id`) REFERENCES `player` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- ----------------------------
-- Records of sieu_hang
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for skill_template
-- ----------------------------
DROP TABLE IF EXISTS `skill_template`;
CREATE TABLE `skill_template` (
  `nclass_id` int NOT NULL,
  `id` int NOT NULL,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `max_point` smallint NOT NULL DEFAULT '7',
  `mana_use_type` smallint NOT NULL,
  `type` smallint NOT NULL,
  `icon_id` int NOT NULL,
  `dam_info` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `slot` int NOT NULL DEFAULT '7',
  `skills` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `desc` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`nclass_id`,`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of skill_template
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for tab_shop
-- ----------------------------
DROP TABLE IF EXISTS `tab_shop`;
CREATE TABLE `tab_shop` (
  `id` int NOT NULL AUTO_INCREMENT,
  `shop_id` int NOT NULL,
  `NAME` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `shop_id` (`shop_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=70 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of tab_shop
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for tambao_items
-- ----------------------------
DROP TABLE IF EXISTS `tambao_items`;
CREATE TABLE `tambao_items` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `key_item_id` int NOT NULL,
  `item_id` int DEFAULT NULL,
  `quantity` int DEFAULT NULL,
  `item_options` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `tile_trung_thuong` smallint unsigned NOT NULL DEFAULT '0' COMMENT 'Permille (0..1000). Dùng với Util.isTrue(tile, 1000)',
  `des` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci,
  `start_at` datetime DEFAULT NULL,
  `end_at` datetime DEFAULT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_key_enabled` (`key_item_id`,`enabled`),
  KEY `idx_key_time` (`key_item_id`,`start_at`,`end_at`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of tambao_items
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for task_main_template
-- ----------------------------
DROP TABLE IF EXISTS `task_main_template`;
CREATE TABLE `task_main_template` (
  `id` int NOT NULL,
  `NAME` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `detail` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of task_main_template
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for task_sub_template
-- ----------------------------
DROP TABLE IF EXISTS `task_sub_template`;
CREATE TABLE `task_sub_template` (
  `task_main_id` int NOT NULL,
  `NAME` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `max_count` int NOT NULL DEFAULT '-1',
  `notify` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '',
  `npc_id` int NOT NULL DEFAULT '-1',
  `map` int NOT NULL,
  KEY `task_main_id` (`task_main_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of task_sub_template
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for topup
-- ----------------------------
DROP TABLE IF EXISTS `topup`;
CREATE TABLE `topup` (
  `id` int NOT NULL AUTO_INCREMENT,
  `request_id` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci,
  `trangthai` int DEFAULT NULL,
  `vnd` int DEFAULT NULL,
  `username` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci,
  `seri` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci,
  `code` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci,
  `loaithe` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci,
  `time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of topup
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for trans_log
-- ----------------------------
DROP TABLE IF EXISTS `trans_log`;
CREATE TABLE `trans_log` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` text NOT NULL,
  `amount` bigint NOT NULL,
  `seri` text NOT NULL,
  `pin` text NOT NULL,
  `type` text NOT NULL,
  `status` int NOT NULL DEFAULT '0',
  `trans_id` text NOT NULL,
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=254 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of trans_log
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for transactions
-- ----------------------------
DROP TABLE IF EXISTS `transactions`;
CREATE TABLE `transactions` (
  `creator_id` int NOT NULL,
  `user_id` int NOT NULL,
  `vnd_before` int NOT NULL,
  `vnd_change` int NOT NULL,
  `vnd_after` int NOT NULL,
  `luong_change` int NOT NULL,
  `luong_before` int NOT NULL,
  `luong_after` int NOT NULL,
  `notes` text NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`creator_id`),
  KEY `creator_id` (`creator_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- ----------------------------
-- Records of transactions
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for type_map
-- ----------------------------
DROP TABLE IF EXISTS `type_map`;
CREATE TABLE `type_map` (
  `id` int NOT NULL,
  `NAME` varchar(55) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of type_map
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for weapon_store
-- ----------------------------
DROP TABLE IF EXISTS `weapon_store`;
CREATE TABLE `weapon_store` (
  `id` int NOT NULL AUTO_INCREMENT,
  `templateId` int NOT NULL,
  `sys` int NOT NULL,
  `xu` int NOT NULL DEFAULT '0',
  `luong` int NOT NULL DEFAULT '0',
  `yen` int NOT NULL DEFAULT '0',
  `expire` bigint NOT NULL DEFAULT '-1',
  `options` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=49 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of weapon_store
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for webshop
-- ----------------------------
DROP TABLE IF EXISTS `webshop`;
CREATE TABLE `webshop` (
  `id` int NOT NULL AUTO_INCREMENT,
  `ten_item` text NOT NULL,
  `chi_so_web` varchar(500) NOT NULL,
  `chi_so_game` varchar(500) NOT NULL,
  `gia_coin` int NOT NULL DEFAULT '0',
  `image` text NOT NULL,
  `cs_img` varchar(500) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=45 DEFAULT CHARSET=utf8mb3;

-- ----------------------------
-- Records of webshop
-- ----------------------------
BEGIN;
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;

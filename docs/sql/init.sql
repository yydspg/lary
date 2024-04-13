/*
Navicat Premium Data Transfer

 Source Server         : lilishop
 Source Server Type    : MySQL
 Source Server Version : 80028
 Source Host           : 192.168.0.108:30306
 Source Schema         : clerk

 Target Server Type    : MySQL
 Target Server Version : 80028
 File Encoding         : 65001

 Date: 22/11/2022 14:42:37
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for li_admin_user
-- ----------------------------
DROP TABLE IF EXISTS `li_admin_user`;
CREATE TABLE `li_admin_user`  (
                                  `id` bigint NOT NULL COMMENT 'ID',
                                  `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
                                  `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                  `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                  `update_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者',
                                  `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                  `avatar` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户头像',
                                  `department_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '所属部门ID',
                                  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
                                  `email` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '邮件',
                                  `is_super` bit(1) NULL DEFAULT NULL COMMENT '是否是超级管理员 超级管理员/普通管理员',
                                  `mobile` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '手机',
                                  `nick_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '昵称',
                                  `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '密码',
                                  `sex` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                  `status` bit(1) NULL DEFAULT NULL COMMENT '状态 默认true正常 false禁用',
                                  `username` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户名',
                                  `role_ids` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '角色ID集合',
                                  PRIMARY KEY (`id`) USING BTREE,
                                  UNIQUE INDEX `UK_sh2dyl78jk1vxtlyohwr5wht9`(`username` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_admin_user
-- ----------------------------
INSERT INTO `li_admin_user` VALUES (1337306110277476352, '', '2020-12-11 07:59:57.000000', b'0', 'admin', '2021-12-01 16:44:56.648000', 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/65e87ffa718b42bb9c201712566dbc9a.png', '1364415918628667392', 'aaa', 'aaa@aa.com', b'1', '13012345678', '初一', '$2a$10$sVnczXsvm2V0SBKCx3e96eRr6Ssl69bh56iD3RJNJE3o2LzqEO4qW', NULL, b'1', 'admin', '1394944593522327552');

-- ----------------------------
-- Table structure for li_after_sale
-- ----------------------------
DROP TABLE IF EXISTS `li_after_sale`;
CREATE TABLE `li_after_sale`  (
                                  `id` bigint NOT NULL COMMENT 'ID',
                                  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '创建者',
                                  `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                  `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '更新者',
                                  `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                  `account_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '账号类型',
                                  `actual_refund_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '实际退款金额',
                                  `after_sale_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '评价图片',
                                  `apply_refund_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '申请退款金额',
                                  `audit_remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '商家备注',
                                  `bank_account_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '银行开户名',
                                  `bank_account_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '银行账户',
                                  `bank_deposit_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '银行开户行',
                                  `flow_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '实际金额',
                                  `goods_id` bigint NULL DEFAULT NULL COMMENT '商品ID',
                                  `goods_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '商品图片',
                                  `goods_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '商品名称',
                                  `m_deliver_time` datetime(6) NULL DEFAULT NULL COMMENT '买家 发货时间',
                                  `m_logistics_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '买家 物流公司CODE',
                                  `m_logistics_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '买家 物流公司名称',
                                  `m_logistics_no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '买家 发货单号',
                                  `member_id` bigint NULL DEFAULT NULL COMMENT '用户ID',
                                  `member_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '用户名称',
                                  `num` int NULL DEFAULT NULL COMMENT '申请数量',
                                  `order_sn` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '订单编号',
                                  `pay_order_no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '订单支付方式返回的交易号',
                                  `problem_desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '问题描述',
                                  `reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '申请原因',
                                  `refund_point` int NULL DEFAULT NULL COMMENT '退还积分',
                                  `refund_time` datetime(6) NULL DEFAULT NULL COMMENT '退款时间',
                                  `refund_way` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '退款方式',
                                  `store_id` bigint NULL DEFAULT NULL COMMENT '店铺ID',
                                  `store_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '店铺名称',
                                  `service_status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '售后单状态',
                                  `service_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '售后类型',
                                  `sku_id` bigint NULL DEFAULT NULL COMMENT '货品ID',
                                  `sn` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '售后服务单号',
                                  `specs` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '规格json',
                                  `trade_sn` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '交易编号',
                                  `order_item_sn` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '订单货物编号',
                                  PRIMARY KEY (`id`) USING BTREE,
                                  INDEX `member_id`(`member_id` ASC) USING BTREE COMMENT '会员id索引',
                                  INDEX `store_id`(`store_id` ASC) USING BTREE COMMENT '店铺id索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for li_after_sale_log
-- ----------------------------
DROP TABLE IF EXISTS `li_after_sale_log`;
CREATE TABLE `li_after_sale_log`  (
                                      `id` bigint NOT NULL COMMENT 'ID',
                                      `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '创建者',
                                      `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                      `message` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '日志信息',
                                      `operator_id` bigint NULL DEFAULT NULL COMMENT '操作者ID(可以是店铺)',
                                      `operator_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '操作者名称',
                                      `operator_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '操作者类型',
                                      `sn` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '售后服务单号',
                                      PRIMARY KEY (`id`) USING BTREE,
                                      INDEX `sn`(`sn` ASC) USING BTREE COMMENT '售后服务单号索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for li_after_sale_reason
-- ----------------------------
DROP TABLE IF EXISTS `li_after_sale_reason`;
CREATE TABLE `li_after_sale_reason`  (
                                         `id` bigint NOT NULL COMMENT 'ID',
                                         `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '创建者',
                                         `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                         `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                         `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '更新者',
                                         `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                         `reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '售后原因',
                                         `service_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '原因类型',
                                         PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_after_sale_reason
-- ----------------------------
INSERT INTO `li_after_sale_reason` VALUES (1357581861634703360, 'admin', '2021-02-05 06:48:33.151000', b'0', NULL, NULL, '未收到货', 'COMPLAIN');
INSERT INTO `li_after_sale_reason` VALUES (1357581919558041600, 'admin', '2021-02-05 06:48:46.933000', b'0', NULL, NULL, '订单货物数量不对', 'COMPLAIN');
INSERT INTO `li_after_sale_reason` VALUES (1357582072222318592, 'admin', '2021-02-05 06:49:23.331000', b'0', NULL, NULL, '不按时发货', 'RETURN_MONEY');
INSERT INTO `li_after_sale_reason` VALUES (1357583466371219456, 'admin', '2021-02-05 06:54:55.722000', b'0', NULL, NULL, '地址或商品选择错误', 'RETURN_MONEY');
INSERT INTO `li_after_sale_reason` VALUES (1357583504233201664, 'admin', '2021-02-05 06:55:04.748000', b'0', NULL, NULL, '其他', 'RETURN_MONEY');
INSERT INTO `li_after_sale_reason` VALUES (1357583533337477120, 'admin', '2021-02-05 06:55:11.688000', b'0', NULL, NULL, '商品选择错误', 'CANCEL');
INSERT INTO `li_after_sale_reason` VALUES (1357583555026223104, 'admin', '2021-02-05 06:55:16.858000', b'0', NULL, NULL, '不想要了', 'CANCEL');
INSERT INTO `li_after_sale_reason` VALUES (1357583611645132800, 'admin', '2021-02-05 06:55:30.357000', b'0', NULL, NULL, '不合适', 'RETURN_GOODS');
INSERT INTO `li_after_sale_reason` VALUES (1357583649075101696, 'admin', '2021-02-05 06:55:39.282000', b'0', NULL, NULL, '不想要了', 'RETURN_GOODS');
INSERT INTO `li_after_sale_reason` VALUES (1357583690120560640, 'admin', '2021-02-05 06:55:49.067000', b'0', NULL, NULL, '其他原因', 'RETURN_GOODS');
INSERT INTO `li_after_sale_reason` VALUES (1416907707173408770, 'admin', '2021-07-19 07:48:16.920000', b'0', NULL, NULL, '不想要了', 'RETURN_GOODS');

-- ----------------------------
-- Table structure for li_app_version
-- ----------------------------
DROP TABLE IF EXISTS `li_app_version`;
CREATE TABLE `li_app_version`  (
                                   `id` bigint NOT NULL COMMENT 'ID',
                                   `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建者',
                                   `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                   `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新内容',
                                   `download_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '下载地址',
                                   `force_update` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '是否强制更新',
                                   `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '类型',
                                   `version` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '版本号',
                                   `version_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '版本名称',
                                   `version_update_date` datetime NULL DEFAULT NULL COMMENT '版本更新时间',
                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_app_version
-- ----------------------------
INSERT INTO `li_app_version` VALUES (1456542404861841409, 'admin', '2021-11-05 16:42:25', ' 1', '1', '1', 'ANDROID', '1', '1', '2021-11-05 16:42:07');

-- ----------------------------
-- Table structure for li_article
-- ----------------------------
DROP TABLE IF EXISTS `li_article`;
CREATE TABLE `li_article`  (
                               `id` bigint NOT NULL COMMENT 'ID',
                               `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建者',
                               `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                               `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                               `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新者',
                               `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                               `category_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类ID',
                               `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '文章内容',
                               `sort` int NULL DEFAULT NULL COMMENT '文章排序',
                               `title` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文章标题',
                               `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文章类型',
                               `open_status` bit(1) NULL DEFAULT NULL,
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of li_article
-- ----------------------------

-- ----------------------------
-- Table structure for li_article_category
-- ----------------------------
DROP TABLE IF EXISTS `li_article_category`;
CREATE TABLE `li_article_category`  (
                                        `id` bigint NOT NULL COMMENT 'ID',
                                        `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建者',
                                        `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                        `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                        `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新者',
                                        `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                                        `level` int NULL DEFAULT NULL COMMENT '层级',
                                        `parent_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '父分类ID',
                                        `sort` int NULL DEFAULT NULL COMMENT '排序',
                                        `article_category_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '分类名称',
                                        `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '分类类型',
                                        PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of li_article_category
-- ----------------------------

-- ----------------------------
-- Table structure for li_bill
-- ----------------------------
DROP TABLE IF EXISTS `li_bill`;
CREATE TABLE `li_bill`  (
                            `id` bigint NOT NULL COMMENT 'ID',
                            `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                            `bank_account_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '银行开户名',
                            `bank_account_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '公司银行账号',
                            `bank_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '支行联行号',
                            `bank_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '开户银行支行名称',
                            `bill_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '最终结算金额',
                            `bill_status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '状态 OUT(已出账),CHECK(已对账),EXAMINE(已审核),PAY(已付款)',
                            `commission_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '平台收取佣金',
                            `distribution_commission` decimal(10, 2) NULL DEFAULT NULL COMMENT '分销返现支出',
                            `distribution_refund_commission` decimal(10, 2) NULL DEFAULT NULL COMMENT '分销订单退还',
                            `order_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '结算周期内订单付款总金额',
                            `pay_time` datetime(6) NULL DEFAULT NULL COMMENT '平台付款时间',
                            `refund_commission_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '退单产生退还佣金金额',
                            `refund_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '退单金额',
                            `store_id` bigint NULL DEFAULT NULL COMMENT '店铺ID',
                            `store_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '店铺名称',
                            `site_coupon_commission` decimal(10, 2) NULL DEFAULT NULL COMMENT '平台优惠券补贴',
                            `site_coupon_refund_commission` decimal(10, 2) NULL DEFAULT NULL COMMENT '退货平台优惠券补贴返还',
                            `sn` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '账单号',
                            `end_time` datetime(6) NULL DEFAULT NULL COMMENT '结算结束时间',
                            `start_time` datetime(6) NULL DEFAULT NULL COMMENT '结算开始时间',
                            `point_settlement_price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '积分商品结算金额 ',
                            `kanjia_settlement_price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '砍价商品结算金额',
                            PRIMARY KEY (`id`) USING BTREE,
                            INDEX `store_id`(`store_id` ASC) USING BTREE COMMENT '店铺id索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_bill
-- ----------------------------
-- ----------------------------
-- Table structure for li_brand
-- ----------------------------
DROP TABLE IF EXISTS `li_brand`;
CREATE TABLE `li_brand`  (
                             `id` bigint NOT NULL COMMENT 'ID',
                             `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
                             `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                             `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                             `update_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者',
                             `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                             `logo` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '品牌图标',
                             `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '品牌名称',
                             PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of li_brand
-- ----------------------------

-- ----------------------------
-- Table structure for li_category
-- ----------------------------
DROP TABLE IF EXISTS `li_category`;
CREATE TABLE `li_category`  (
                                `id` bigint NOT NULL COMMENT 'ID',
                                `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '创建者',
                                `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '更新者',
                                `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                `commission_rate` decimal(10, 2) NULL DEFAULT NULL COMMENT '佣金比例',
                                `image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '分类图标',
                                `level` int NULL DEFAULT NULL COMMENT '层级',
                                `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '分类名称',
                                `parent_id` bigint NULL DEFAULT NULL COMMENT '父ID',
                                `sort_order` int NULL DEFAULT 0 COMMENT '排序值',
                                `support_channel` bit(1) NULL DEFAULT NULL COMMENT '是否支持频道',
                                PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_category
-- ----------------------------
-- ----------------------------
-- Table structure for li_category_brand
-- ----------------------------
DROP TABLE IF EXISTS `li_category_brand`;
CREATE TABLE `li_category_brand`  (
                                      `id` bigint NOT NULL COMMENT 'ID',
                                      `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
                                      `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                      `brand_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '品牌ID',
                                      `category_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '分类ID',
                                      PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_category_brand
-- ----------------------------

-- ----------------------------
-- Table structure for li_category_parameter_group
-- ----------------------------
DROP TABLE IF EXISTS `li_category_parameter_group`;
CREATE TABLE `li_category_parameter_group`  (
                                                `id` bigint NOT NULL COMMENT 'ID',
                                                `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
                                                `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                                `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                                `update_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者',
                                                `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                                `category_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '关联分类ID',
                                                `group_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '参数组名称',
                                                `sort` int NULL DEFAULT NULL COMMENT '排序',
                                                PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_category_parameter_group
-- ----------------------------

-- ----------------------------
-- Table structure for li_category_specification
-- ----------------------------
DROP TABLE IF EXISTS `li_category_specification`;
CREATE TABLE `li_category_specification`  (
                                              `id` bigint NOT NULL COMMENT 'ID',
                                              `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
                                              `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                              `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                              `update_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者',
                                              `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                              `category_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '分类ID',
                                              `specification_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '规格ID',
                                              PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_category_specification
-- ----------------------------
INSERT INTO `li_category_specification` VALUES (1440526235510771713, 'admin', '2021-09-22 11:59:52.887000', b'0', NULL, NULL, '1348576427264204944', '1376748749111427072');
INSERT INTO `li_category_specification` VALUES (1440573693875982338, 'admin', '2021-09-22 15:08:27.842000', b'0', NULL, NULL, '1348576427264204943', '1376371867132100608');
INSERT INTO `li_category_specification` VALUES (1440573693909536769, 'admin', '2021-09-22 15:08:27.851000', b'0', NULL, NULL, '1348576427264204943', '1376748749111427072');

-- ----------------------------
-- Table structure for li_clerk
-- ----------------------------
DROP TABLE IF EXISTS `li_clerk`;
CREATE TABLE `li_clerk`  (
                             `id` bigint NOT NULL COMMENT 'ID',
                             `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '创建者',
                             `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                             `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                             `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '更新者',
                             `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                             `clerk_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '店员名称',
                             `member_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '会员id',
                             `store_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '店铺id',
                             `department_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '部门id',
                             `role_ids` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '角色',
                             `shopkeeper` bit(1) NULL DEFAULT NULL COMMENT '是否是店主',
                             `is_super` bit(1) NULL DEFAULT NULL COMMENT '是否是超级管理员 超级管理员/普通管理员',
                             `status` bit(1) NULL DEFAULT NULL COMMENT '状态',
                             PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_clerk
-- ----------------------------
INSERT INTO `li_clerk` VALUES (1376369067769724928, '15810610731', '2022-11-22 00:00:00.000000', NULL, NULL, NULL, '15810610731', '1376364798236688384', '1376369067769724928', NULL, NULL, b'1', b'1', b'1');
INSERT INTO `li_clerk` VALUES (1376433565247471616, '13011111111', '2022-11-22 00:00:00.000000', NULL, NULL, NULL, '13011111111', '1376417684140326912', '1376433565247471616', NULL, NULL, b'1', b'1', b'1');

-- ----------------------------
-- Table structure for li_clerk_role
-- ----------------------------
DROP TABLE IF EXISTS `li_clerk_role`;
CREATE TABLE `li_clerk_role`  (
                                  `id` bigint NOT NULL COMMENT 'ID',
                                  `clerk_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '店员唯一id',
                                  `role_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '角色唯一id',
                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_clerk_role
-- ----------------------------

-- ----------------------------
-- Table structure for li_commodity
-- ----------------------------
DROP TABLE IF EXISTS `li_commodity`;
CREATE TABLE `li_commodity`  (
                                 `id` bigint NOT NULL,
                                 `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                                 `create_time` datetime(6) NULL DEFAULT NULL,
                                 `delete_flag` bit(1) NULL DEFAULT NULL,
                                 `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                                 `update_time` datetime(6) NULL DEFAULT NULL,
                                 `audit_id` bigint NULL DEFAULT NULL,
                                 `audit_status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                                 `goods_id` bigint NULL DEFAULT NULL,
                                 `goods_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                                 `live_goods_id` int NULL DEFAULT NULL,
                                 `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                                 `price` decimal(10, 2) NULL DEFAULT NULL,
                                 `price2` decimal(10, 2) NULL DEFAULT NULL,
                                 `price_type` int NULL DEFAULT NULL,
                                 `sku_id` bigint NULL DEFAULT NULL,
                                 `store_id` bigint NULL DEFAULT NULL,
                                 `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_commodity
-- ----------------------------
INSERT INTO `li_commodity` VALUES (1411890810665107458, '15810610731', '2021-07-05 11:32:55.582000', b'0', NULL, NULL, 510443646, '2', 1376836083676872704, 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/0c32095777704e2db7a5f858c32403e9.jpg?x-oss-process=style/200X200', 50, '三星Galaxy S21 ', 2, NULL, 1, 1386125116261269504, 1376369067769724928, 'pages/product/goods?id=1386125116261269504&goodsId=1376836083676872704');
INSERT INTO `li_commodity` VALUES (1411891969442250753, '13011111111', '2021-07-05 11:37:31.857000', b'0', NULL, NULL, 510443648, '2', 1376443041593688064, 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/54dcb9cd0fea4f1f8243e12d49c28af9.png?x-oss-process=style/200X200', 51, '百草味 蜜饯 原味芒果干 ', 15, NULL, 1, 1381791842915319808, 1376433565247471616, 'pages/product/goods?id=1381791842915319808&goodsId=1376443041593688064');
INSERT INTO `li_commodity` VALUES (1411892561510203394, '13011111111', '2021-07-05 11:39:53.016000', b'0', NULL, NULL, 510443651, '2', 1377057500825649152, 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/6ce1382ba55f4284a86f893cfc79eed8.jpg?x-oss-process=style/200X200', 52, '雀巢（Nestle）咖啡 ', 39, NULL, 1, 1381791962713030656, 1376433565247471616, 'pages/product/goods?id=1381791962713030656&goodsId=1377057500825649152');
INSERT INTO `li_commodity` VALUES (1411892607165202434, '13011111111', '2021-07-05 11:40:03.901000', b'0', NULL, NULL, 510443653, '2', 1377127936569638912, 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/389b2ce90fd0425a8a9d988890ad46d2.jpg?x-oss-process=style/200X200', 53, 'ARMANI阿玛尼 新款蓝', 666, NULL, 1, 1381789991541145600, 1376433565247471616, 'pages/product/goods?id=1381789991541145600&goodsId=1377127936569638912');
INSERT INTO `li_commodity` VALUES (1411893337494192129, '13011111111', '2021-07-05 11:42:58.025000', b'0', NULL, NULL, 510443655, '2', 1377127936569638912, 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/ab2876454f9b4e5ebd99218a82b45da5.jpg?x-oss-process=style/200X200', 54, 'ARMANI阿玛尼 新款蓝', 1, NULL, 1, 1381789991545339904, 1376433565247471616, 'pages/product/goods?id=1381789991545339904&goodsId=1377127936569638912');
INSERT INTO `li_commodity` VALUES (1411894391178854401, '13011111111', '2021-07-05 11:47:09.244000', b'0', NULL, NULL, 510443660, '2', 1377064344218501120, 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/5f9d06f672a047b78df128be989bece7.jpg?x-oss-process=style/200X200', 55, '肉类零食 白芝麻猪肉脯 百', 36, NULL, 1, 1381792263696285696, 1376433565247471616, 'pages/product/goods?id=1381792263696285696&goodsId=1377064344218501120');
INSERT INTO `li_commodity` VALUES (1412315783061929985, '15810610731', '2021-07-06 15:41:36.897000', b'0', NULL, NULL, 510443917, '2', 1377802526690115584, 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/b491452a01b44a099fef6bb2aa90327a.jpeg?x-oss-process=style/200X200', 56, '惠普(HP)星青春版', 2999, NULL, 1, 1386930238503518208, 1376369067769724928, 'pages/product/goods?id=1386930238503518208&goodsId=1377802526690115584');
INSERT INTO `li_commodity` VALUES (1416626400858066945, '13011111111', '2021-07-18 13:10:28.269000', b'0', NULL, NULL, 510446454, '2', 1377132926042374144, 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/28964af5041e42f4becd1af31d4782bd.png?x-oss-process=style/200X200', 58, '纳斯（NARS) Blus', 222, NULL, 1, 1377132926088511488, 1376433565247471616, 'pages/product/goods?id=1377132926088511488&goodsId=1377132926042374144');

-- ----------------------------
-- Table structure for li_connect
-- ----------------------------
DROP TABLE IF EXISTS `li_connect`;
CREATE TABLE `li_connect`  (
                               `id` bigint NOT NULL COMMENT 'ID',
                               `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
                               `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                               `union_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '联合登录ID',
                               `union_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '联合登录类型',
                               `user_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户ID',
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_connect
-- ----------------------------

-- ----------------------------
-- Table structure for li_connect_config
-- ----------------------------
DROP TABLE IF EXISTS `li_connect_config`;
CREATE TABLE `li_connect_config`  (
                                      `id` bigint NOT NULL COMMENT 'ID',
                                      `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
                                      `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                      `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                      `update_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者',
                                      `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                      `config_key` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '配置key',
                                      `config_value` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '配置',
                                      PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_connect_config
-- ----------------------------

-- ----------------------------
-- Table structure for li_coupon
-- ----------------------------
DROP TABLE IF EXISTS `li_coupon`;
CREATE TABLE `li_coupon`  (
                              `id` bigint NOT NULL COMMENT 'ID',
                              `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
                              `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                              `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                              `update_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者',
                              `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                              `end_time` datetime(6) NULL DEFAULT NULL COMMENT '活动结束时间',
                              `promotion_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '活动名称',
                              `store_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '店铺ID',
                              `store_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '店铺名称',
                              `start_time` datetime(6) NULL DEFAULT NULL COMMENT '活动开始时间',
                              `consume_threshold` decimal(10, 2) NULL DEFAULT NULL COMMENT '消费门槛',
                              `coupon_discount` decimal(10, 2) NULL DEFAULT NULL COMMENT '折扣',
                              `coupon_limit_num` int NULL DEFAULT NULL COMMENT '领取限制',
                              `coupon_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '优惠券名称',
                              `coupon_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '活动类型',
                              `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '活动描述',
                              `get_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '优惠券类型',
                              `price` decimal(10, 2) NULL DEFAULT NULL COMMENT '面额',
                              `publish_num` int NULL DEFAULT NULL COMMENT '发行数量',
                              `received_num` int NULL DEFAULT NULL COMMENT '已被领取的数量',
                              `scope_id` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '范围关联的ID',
                              `scope_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '关联范围类型',
                              `store_commission` decimal(10, 2) NULL DEFAULT NULL COMMENT '店铺承担比例',
                              `used_num` int NULL DEFAULT NULL COMMENT '已被使用的数量',
                              `range_day_type` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                              `effective_days` int NULL DEFAULT NULL,
                              PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_coupon
-- ----------------------------

-- ----------------------------
-- Table structure for li_coupon_activity
-- ----------------------------
DROP TABLE IF EXISTS `li_coupon_activity`;
CREATE TABLE `li_coupon_activity`  (
                                       `id` bigint NOT NULL,
                                       `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                                       `create_time` datetime(6) NULL DEFAULT NULL,
                                       `delete_flag` bit(1) NULL DEFAULT NULL,
                                       `update_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                                       `update_time` datetime(6) NULL DEFAULT NULL,
                                       `end_time` datetime(6) NULL DEFAULT NULL,
                                       `promotion_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                                       `start_time` datetime(6) NULL DEFAULT NULL,
                                       `store_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                                       `store_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                                       `activity_scope` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                       `activity_scope_info` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                                       `coupon_activity_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                       `scope_id` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '范围关联的ID',
                                       `scope_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'PORTION_GOODS' COMMENT '关联范围类型',
                                       `coupon_frequency_enum` varchar(255) COMMENT '领取周期',
                                       PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_coupon_activity
-- ----------------------------

-- ----------------------------
-- Table structure for li_coupon_activity_item
-- ----------------------------
DROP TABLE IF EXISTS `li_coupon_activity_item`;
CREATE TABLE `li_coupon_activity_item`  (
                                            `id` bigint NOT NULL,
                                            `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                                            `create_time` datetime(6) NULL DEFAULT NULL,
                                            `delete_flag` bit(1) NULL DEFAULT NULL,
                                            `update_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                                            `update_time` datetime(6) NULL DEFAULT NULL,
                                            `activity_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                                            `coupon_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                                            `num` int NULL DEFAULT NULL,
                                            PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_coupon_activity_item
-- ----------------------------

-- ----------------------------
-- Table structure for li_custom_words
-- ----------------------------
DROP TABLE IF EXISTS `li_custom_words`;
CREATE TABLE `li_custom_words`  (
                                    `id` bigint NOT NULL COMMENT 'ID',
                                    `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
                                    `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                    `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                    `update_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者',
                                    `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                    `disabled` int NULL DEFAULT NULL COMMENT '是否禁用',
                                    `name` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '名称',
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_custom_words
-- ----------------------------
INSERT INTO `li_custom_words` VALUES (1426013201641037826, 'admin', '2021-08-13 10:50:15.972000', b'0', NULL, NULL, NULL, '米');
INSERT INTO `li_custom_words` VALUES (1426014704225280002, 'admin', '2021-08-13 10:56:14.217000', b'0', 'admin', '2021-08-18 13:52:34.374000', NULL, '小');
INSERT INTO `li_custom_words` VALUES (1430197443518361601, 'admin', '2021-08-24 23:56:56.975000', b'0', NULL, NULL, NULL, '浪');
INSERT INTO `li_custom_words` VALUES (1434685838898536449, 'admin', '2021-09-06 09:12:13.825000', b'0', NULL, NULL, NULL, '测试');
INSERT INTO `li_custom_words` VALUES (1434685854694285314, 'admin', '2021-09-06 09:12:17.591000', b'0', NULL, NULL, NULL, '我');
INSERT INTO `li_custom_words` VALUES (1434859360199417858, 'admin', '2021-09-06 20:41:44.528000', b'0', NULL, NULL, NULL, '粽子');
INSERT INTO `li_custom_words` VALUES (1434859380810227714, 'admin', '2021-09-06 20:41:49.441000', b'0', NULL, NULL, NULL, '粽');
INSERT INTO `li_custom_words` VALUES (1438746947498635265, 'admin', '2021-09-17 14:09:37.567000', b'0', NULL, NULL, NULL, '手机');
INSERT INTO `li_custom_words` VALUES (1448636645448634370, 'admin', '2021-10-14 21:07:45.300000', b'0', NULL, NULL, NULL, '水电费');
INSERT INTO `li_custom_words` VALUES (1450074350040735745, 'admin', '2021-10-18 20:20:40.786000', b'0', 'admin', '2021-10-26 15:04:18.755000', NULL, '华为手机');
INSERT INTO `li_custom_words` VALUES (1455466891321851906, 'admin', '2021-11-02 17:28:42.815000', b'0', NULL, NULL, NULL, '郭宇');
INSERT INTO `li_custom_words` VALUES (1456132721256742913, 'admin', '2021-11-04 13:34:29.043000', b'0', NULL, NULL, NULL, '电脑');
INSERT INTO `li_custom_words` VALUES (1456132765783474177, 'admin', '2021-11-04 13:34:39.658000', b'0', 'admin', '2021-11-08 18:25:02.045000', NULL, '电脑1');
INSERT INTO `li_custom_words` VALUES (1457252170026274817, 'admin', '2021-11-07 15:42:46.427000', b'0', NULL, NULL, NULL, '妮维雅');
INSERT INTO `li_custom_words` VALUES (1463729201022849026, 'admin', '2021-11-25 12:40:10.955000', b'0', NULL, NULL, NULL, '耐克');

-- ----------------------------
-- Table structure for li_department
-- ----------------------------
DROP TABLE IF EXISTS `li_department`;
CREATE TABLE `li_department`  (
                                  `id` bigint NOT NULL COMMENT 'ID',
                                  `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
                                  `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                  `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                  `update_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者',
                                  `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                  `parent_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '父ID',
                                  `sort_order` decimal(10, 2) NULL DEFAULT NULL COMMENT '排序值',
                                  `title` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '部门名称',
                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_department
-- ----------------------------
INSERT INTO `li_department` VALUES (1363424504218386432, 'admin', '2021-02-21 09:45:07.000000', b'0', 'admin', '2022-01-12 23:31:44.852000', '0', 0.00, '财务部');
INSERT INTO `li_department` VALUES (1364415918628667392, 'admin', '2021-02-24 03:24:39.000000', b'0', 'admin', '2021-09-29 16:29:28.277000', '0', 0.00, '销售部');
INSERT INTO `li_department` VALUES (1371978420262535168, 'admin', '2021-03-16 19:15:20.000000', b'0', 'admin', '2021-10-29 17:34:30.546000', '1363424504218386432', 1.00, '财务一部');
INSERT INTO `li_department` VALUES (1371978462067163136, 'admin', '2021-03-16 19:15:30.000000', b'0', 'admin', '2021-11-24 19:24:16.418000', '1363424504218386432', 2.00, '财务二部');
INSERT INTO `li_department` VALUES (1423484408787369986, 'admin', '2021-08-06 11:21:44.000000', b'0', 'admin', '2021-08-06 11:23:57.790000', '0', 1.00, '运营部');
INSERT INTO `li_department` VALUES (1423484886954803202, 'admin', '2021-08-06 11:23:38.778000', b'0', NULL, NULL, '1423484408787369986', 0.00, '运营专员');
INSERT INTO `li_department` VALUES (1423484932957929473, 'admin', '2021-08-06 11:23:49.747000', b'0', NULL, NULL, '1423484408787369986', 0.00, '运营主管');
INSERT INTO `li_department` VALUES (1431176858761805825, 'admin', '2021-08-27 16:48:47.000000', b'0', 'admin', '2021-08-27 16:49:27.297000', '0', 0.00, 'cc');
INSERT INTO `li_department` VALUES (1455359452782047233, 'admin', '2021-11-02 10:21:47.471000', b'0', NULL, NULL, '0', 0.00, '电商部');
INSERT INTO `li_department` VALUES (1455373067090259970, 'admin', '2021-11-02 11:15:53.376000', b'0', NULL, NULL, '1455359452782047233', 0.00, '电商一部');
INSERT INTO `li_department` VALUES (1455818288894623746, 'admin', '2021-11-03 16:45:02.526000', b'0', NULL, NULL, '0', 0.00, '财务二部');

-- ----------------------------
-- Table structure for li_department_role
-- ----------------------------
DROP TABLE IF EXISTS `li_department_role`;
CREATE TABLE `li_department_role`  (
                                       `id` bigint NOT NULL COMMENT 'ID',
                                       `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
                                       `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                       `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                       `update_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者',
                                       `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                       `department_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '部门ID',
                                       `role_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '角色ID',
                                       PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_department_role
-- ----------------------------

-- ----------------------------
-- Table structure for li_distribution
-- ----------------------------
DROP TABLE IF EXISTS `li_distribution`;
CREATE TABLE `li_distribution`  (
                                    `id` bigint NOT NULL COMMENT 'ID',
                                    `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
                                    `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                    `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                    `update_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者',
                                    `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                    `can_rebate` decimal(10, 2) NULL DEFAULT NULL COMMENT '可提现金额',
                                    `commission_frozen` decimal(10, 2) NULL DEFAULT NULL COMMENT '冻结金额',
                                    `distribution_status` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '分销商状态',
                                    `member_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户ID',
                                    `member_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户名称',
                                    `rebate_total` decimal(10, 2) NULL DEFAULT NULL COMMENT '分销总额',
                                    `id_number` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '身份证号',
                                    `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '姓名',
                                    `distribution_order_count` int NULL DEFAULT NULL COMMENT '分销订单数',
                                    `settlement_bank_account_name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                    `settlement_bank_account_num` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                    `settlement_bank_branch_name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_distribution
-- ----------------------------

-- ----------------------------
-- Table structure for li_distribution_cash
-- ----------------------------
DROP TABLE IF EXISTS `li_distribution_cash`;
CREATE TABLE `li_distribution_cash`  (
                                         `id` bigint NOT NULL COMMENT 'ID',
                                         `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
                                         `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                         `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                         `update_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者',
                                         `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                         `distribution_cash_status` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '状态',
                                         `distribution_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '分销员ID',
                                         `distribution_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '分销员名称',
                                         `pay_time` datetime(6) NULL DEFAULT NULL COMMENT '支付时间',
                                         `price` decimal(10, 2) NULL DEFAULT NULL COMMENT '分销佣金',
                                         `sn` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '分销佣金编号',
                                         PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_distribution_cash
-- ----------------------------

-- ----------------------------
-- Table structure for li_distribution_goods
-- ----------------------------
DROP TABLE IF EXISTS `li_distribution_goods`;
CREATE TABLE `li_distribution_goods`  (
                                          `id` bigint NOT NULL COMMENT 'ID',
                                          `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
                                          `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                          `commission` decimal(10, 2) NOT NULL COMMENT '佣金金额',
                                          `goods_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '商品ID',
                                          `goods_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品名称',
                                          `price` decimal(10, 2) NULL DEFAULT NULL COMMENT '商品价格',
                                          `quantity` int NULL DEFAULT NULL COMMENT '库存',
                                          `store_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '店铺ID',
                                          `sku_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '规格ID',
                                          `specs` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '规格信息json',
                                          `thumbnail` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '缩略图路径',
                                          `store_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'storeName',
                                          PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_distribution_goods
-- ----------------------------

-- ----------------------------
-- Table structure for li_distribution_order
-- ----------------------------
DROP TABLE IF EXISTS `li_distribution_order`;
CREATE TABLE `li_distribution_order`  (
                                          `id` bigint NOT NULL,
                                          `create_time` datetime(6) NULL DEFAULT NULL,
                                          `distribution_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                          `distribution_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                          `distribution_order_status` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                          `goods_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                          `goods_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                          `image` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                          `member_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                          `member_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                          `num` int NULL DEFAULT NULL,
                                          `order_item_sn` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                          `order_sn` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                          `rebate` decimal(10, 2) NULL DEFAULT NULL,
                                          `sell_back_rebate` decimal(10, 2) NULL DEFAULT NULL,
                                          `settle_cycle` datetime(6) NULL DEFAULT NULL,
                                          `sku_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                          `specs` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
                                          `store_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                          `store_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                          PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_distribution_order
-- ----------------------------

-- ----------------------------
-- Table structure for li_distribution_selected_goods
-- ----------------------------
DROP TABLE IF EXISTS `li_distribution_selected_goods`;
CREATE TABLE `li_distribution_selected_goods`  (
                                                   `id` bigint NOT NULL COMMENT 'ID',
                                                   `distribution_goods_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '分销商品ID',
                                                   `distribution_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '分销商ID',
                                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_distribution_selected_goods
-- ----------------------------

-- ----------------------------
-- Table structure for li_draft_goods
-- ----------------------------
DROP TABLE IF EXISTS `li_draft_goods`;
CREATE TABLE `li_draft_goods`  (
                                   `id` bigint NOT NULL COMMENT 'ID',
                                   `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
                                   `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                   `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                   `update_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者',
                                   `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                   `brand_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '品牌ID',
                                   `buy_count` int NULL DEFAULT NULL COMMENT '购买数量',
                                   `category_name_json` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '分类名称json',
                                   `category_path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '分类path',
                                   `comment_num` int NULL DEFAULT NULL COMMENT '评论数量',
                                   `cost` decimal(10, 2) NULL DEFAULT NULL COMMENT '成本价格',
                                   `enable_quantity` int NULL DEFAULT NULL COMMENT '可用库存',
                                   `freight_payer` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '运费承担者',
                                   `goods_gallery_list_json` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '商品图片json',
                                   `goods_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品名称',
                                   `goods_params_list_json` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '商品参数json',
                                   `goods_unit` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '计量单位',
                                   `goods_video` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品视频',
                                   `grade` decimal(10, 2) NULL DEFAULT NULL COMMENT '商品好评率',
                                   `intro` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品详情',
                                   `mobile_intro` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品移动端详情',
                                   `original` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '原图路径',
                                   `price` decimal(10, 2) NULL DEFAULT NULL COMMENT '商品价格',
                                   `quantity` int NULL DEFAULT NULL COMMENT '库存',
                                   `recommend` bit(1) NULL DEFAULT NULL COMMENT '是否为推荐商品',
                                   `sales_model` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '销售模式',
                                   `save_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '草稿商品保存类型',
                                   `self_operated` bit(1) NULL DEFAULT NULL COMMENT '是否自营',
                                   `store_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '店铺ID',
                                   `store_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '店铺名称',
                                   `selling_point` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '卖点',
                                   `shop_category_path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '店铺分类',
                                   `sku_list_json` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '货品列表json',
                                   `small` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '小图路径',
                                   `sn` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品编号',
                                   `template_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '运费模板ID',
                                   `thumbnail` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '缩略图',
                                   `weight` decimal(10, 2) NULL DEFAULT NULL COMMENT '重量',
                                   `store_category_path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '店铺分类路径',
                                   `big` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '大图路径',
                                   `market_enable` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                   `goods_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for li_feedback
-- ----------------------------
DROP TABLE IF EXISTS `li_feedback`;
CREATE TABLE `li_feedback`  (
                                `id` bigint NOT NULL COMMENT 'ID',
                                `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                `context` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '反馈内容',
                                `images` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图片，多个图片使用：(，)分割',
                                `mobile` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
                                `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '类型',
                                `user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '会员名称',
                                PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for li_file
-- ----------------------------
DROP TABLE IF EXISTS `li_file`;
CREATE TABLE `li_file`  (
                            `id` bigint NOT NULL COMMENT 'ID',
                            `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
                            `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                            `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                            `update_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者',
                            `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                            `file_key` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '存储文件名',
                            `file_size` bigint NULL DEFAULT NULL COMMENT '大小',
                            `file_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件类型',
                            `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '原文件名',
                            `owner_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '拥有者ID',
                            `url` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '路径',
                            `user_enums` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户类型',
                            PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for li_foot_print
-- ----------------------------
DROP TABLE IF EXISTS `li_foot_print`;
CREATE TABLE `li_foot_print`  (
                                  `id` bigint NOT NULL COMMENT 'ID',
                                  `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
                                  `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                  `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                  `update_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者',
                                  `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                  `goods_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品ID',
                                  `member_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '会员ID',
                                  `sku_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '规格ID',
                                  `store_id` varchar(255) DEFAULT NULL COMMENT '店铺ID',
                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_foot_print
-- ----------------------------

-- ----------------------------
-- Table structure for li_freight_template
-- ----------------------------
DROP TABLE IF EXISTS `li_freight_template`;
CREATE TABLE `li_freight_template`  (
                                        `id` bigint NOT NULL COMMENT 'ID',
                                        `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
                                        `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                        `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                        `update_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者',
                                        `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                        `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '模板名称',
                                        `pricing_method` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '计价方式 按件、按重量',
                                        `store_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '店铺ID',
                                        PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_freight_template
-- ----------------------------
INSERT INTO `li_freight_template` VALUES (1376425599173656576, '15810610731', '2021-03-29 01:46:50.000000', b'0', '15810610731', '2021-07-19 11:52:20.386000', '默认运费', 'FREE', '1376364798236688384');
INSERT INTO `li_freight_template` VALUES (1376434171555086336, '13011111111', '2021-03-29 15:20:54.000000', b'0', '13011111111', '2021-08-06 11:13:13.687000', '默认模板', 'FREE', '1376417684140326912');
INSERT INTO `li_freight_template` VALUES (1402510797398441985, '13011111111', '2021-06-09 14:20:06.000000', b'0', '13011111111', '2021-07-27 16:02:54.450000', '测试模板', 'FREE', '1376417684140326912');

-- ----------------------------
-- Table structure for li_freight_template_child
-- ----------------------------
DROP TABLE IF EXISTS `li_freight_template_child`;
CREATE TABLE `li_freight_template_child`  (
                                              `id` bigint NOT NULL COMMENT 'ID',
                                              `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
                                              `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                              `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                              `update_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者',
                                              `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                              `area` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '地址',
                                              `area_id` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '地区ID',
                                              `continued_company` decimal(10, 2) NULL DEFAULT NULL COMMENT '续重/续件',
                                              `continued_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '续费',
                                              `first_company` decimal(10, 2) NULL DEFAULT NULL COMMENT '首重/首件',
                                              `first_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '运费',
                                              `freight_template_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '店铺模板ID',
                                              PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_freight_template_child
-- ----------------------------
INSERT INTO `li_freight_template_child` VALUES (1376425599370788864, '15810610731', '2021-07-19 11:52:20.466000', b'0', NULL, NULL, '广东省,辽宁省,新疆维吾尔自治区,河南省,内蒙古自治区,湖北省,黑龙江省,上海市,贵州省,重庆市,山东省,陕西省,西藏自治区,青海省,江苏省,安徽省,福建省,湖南省,海南省,宁夏回族自治区,广西壮族自治区,浙江省,河北省,甘肃省,四川省,天津市,云南省,北京市,江西省,吉林省,山西省,', '1401797451504943105,1401797451509137428,1401797451509137475,1401797451509137591,1401797451509137679,1401797451513331786,1401797451513331822,1401797451513331954,1401797451517526076,1401797451517526203,1401797451517526279,1401797451517526386,1401797451517526447,1401797451521720355,1401797451521720502,1401797451521720571,1401797451521720686,1401797451521720813,1401797451521720912,1401797451521720937,1401797451521720973,1401797451521721164,1401797451525914753,1401797451525914915,1401797451525915031,1401797451525915129,1401797451525915209,1401797451525915270,1401797451525915362,1401797451530109011,1401797451530109131,1401797451530109206,1401797451530109262,1401797451530109320,1401797451530109430,1401797451530109607,1401797451534303280,1401797451534303320,1401797451534303323,1401797451534303330,1401797451534303336,1401797451534303341,1401797451534303415,1401797451534303427,1401797451534303559,1401797451534303568,1401797451534303683,1401797451534303813,1401797451534303986,1401797451538497573,1401797451538497580,1401797451538497701,1401797451538497943,1401797451538497963,1401797451538497974,1401797451538498013,1401797451538498063,1401797451538498125,1401797451538498147,1401797451538498153,1401797451538498360,1401797451538498447,1401797451542691900,1401797451542692008,1401797451542692274,1401797451542692507,1401797451542692524,1401797451542692621,1401797451546886255,1401797451546886308,1401797451546886429,1401797451546886645,1401797451546886802,1401797451546886948,1401797451551080489,1401797451551080617,1401797451551080836,1401797451551081043,1401797451551081140,1401797451551081345,1401797451555274828,1401797451555274854,1401797451555274892,1401797451555275042,1401797451555275139,1401797451555275255,1401797451555275354,1401797451555275550,1401797451559469161,1401797451559469257,1401797451559469286,1401797451559469498,1401797451559469637,1401797451559469764,1401797451563663459,1401797451563663637,1401797451563663802,1401797451563663892,1401797451563663918,1401797451563664018,1401797451563664072,1401797451567857715,1401797451567857724,1401797451567857750,1401797451567857888,1401797451567857951,1401797451567857983,1401797451567858045,1401797451567858094,1401797451567858175,1401797451572052077,1401797451572052426,1401797451572052660,1401797451576246484,1401797451576246596,1401797451576246746,1401797451576246867,1401797451576246985,1401797451580440656,1401797451580440759,1401797451580441007,1401797451580441276,1401797451584634937,1401797451584635224,1401797451584635372,1401797451584635468,1401797451584635568,1401797451588829190,1401797451588829423,1401797451588829590,1401797451593023750,1401797451593024145,1401797451593024312,1401797451597217854,1401797451597218000,1401797451597218145,1401797451597218315,1401797451597218412,1401797451597218548,1401797451597218647,1401797451601412123,1401797451601412180,1401797451601412251,1401797451601412428,1401797451601412601,1401797451601412696,1401797451601412841,1401797451601413013,1401797451605606574,1401797451605606771,1401797451605606814,1401797451605606933,1401797451605607061,1401797451605607211,1401797451609800747,1401797451609800878,1401797451609801035,1401797451609801185,1401797451647549540,1401797451647549662,1401797451647549707,1401797451647549781,1401797451651743773,1401797451651743869,1401797451651744093,1401797451651744142,1401797451651744245,1401797451651744299,1401797451651744350,1401797451651744388,1401797451651744444,1401797451651744529,1401797451651744569,1401797451655938108,1401797451655938220,1401797451655938348,1401797451655938516,1401797451655938622,1401797451655938735,1401797451655938803,1401797451660132409,1401797451660132508,1401797451660132602,1401797451660132674,1401797451660132801,1401797451660132984,1401797451664326699,1401797451664326748,1401797451664326786,1401797451664326855,1401797451664326924,1401797451664327040,1401797451664327211,1401797451664327299,1401797451664327406,1401797451668521055,1401797451668521179,1401797451668521282,1401797451668521370,1401797451668521474,1401797451668521627,1401797451668521783,1401797451672715313,1401797451672715485,1401797451672715668,1401797451672715875,1401797451672715934,1401797451672716073,1401797451676909653,1401797451676909814,1401797451676909956,1401797451676910161,1401797451676910250,1401797451681103874,1401797451681104020,1401797451681104239,1401797451681104432,1401797451681104561,1401797451681104636,1401797451681104756,1401797451685298347,1401797451685298522,1401797451685298726,1401797451685298831,1401797451685298927,1401797451685298941,1401797451685298954,1401797451685298972,1401797451685298988,1401797451685299000,1401797451685299015,1401797451689492480,1401797451689492497,1401797451689492510,1401797451689492527,1401797451689492548,1401797451689492572,1401797451689492590,1401797451689492594,1401797451689492609,1401797451689492626,1401797451689492681,1401797451689492695,1401797451689492705,1401797451689492776,1401797451689492827,1401797451689492871,1401797451689492927,1401797451689492995,1401797451689493145,1401797451689493180,1401797451689493258,1401797451693686837,1401797451693686912,1401797451693687076,1401797451693687143,1401797451693687223,1401797451693687374,1401797451693687411,1401797451693687529,1401797451693687609,1401797451697881185,1401797451697881278,1401797451697881319,1401797451697881399,1401797451697881592,1401797451697881735,1401797451702075456,1401797451702075640,1401797451702075797,1401797451702075873,1401797451702075984,1401797451702076092,1401797451706269750,1401797451706270007,1401797451706270130,1401797451706270243,1401797451710464101,1401797451710464361,1401797451710464663,1401797451714658583,1401797451714658814,1401797451714659030,1401797451718852810,1401797451718852975,1401797451718853183,1401797451718853204,1401797451718853332,1401797451723046917,1401797451723047045,1401797451723047145,1401797451723047151,1401797451723047239,1401797451723047352,1401797451723047491,1401797451723047620,1401797451723047758,1401797451727241305,1401797451727241415,1401797451727241574,1401797451727241827,1401797451727241919,1401797451727242062,1401797451731435599,1401797451731435886,1401797451731435981,1401797451731436113,1401797451731436322,1401797451731436409,1401797451735629832,1401797451735629922,1401797451735630069,1401797451735630215,1401797451735630408,1401797451735630461,1401797451735630558,1401797451739824187,1401797451739824293,1401797451739824622,1401797451777573166,1401797451781767390,1401797451781767529,1401797451781767689,1401797451785961527,1401797451785961576,1401797451785961661,1401797451785961695,1401797451785961820,1401797451785961853,1401797451785961967,1401797451785962037,1401797451785962121,1401797451785962235,1401797451790155787,1401797451790155880,1401797451790155937,1401797451790156094,1401797451790156447,1401797451794350142,1401797451794350336,1401797451794350582,1401797451794350647,1401797451794350709,1401797451794350942,1401797451798544462,1401797451798544505,1401797451798544770,1401797451798544836,1401797451798545078,1401797451798545236,1401797451802738716,1401797451802738937,1401797451802739080,1401797451802739122,1401797451802739235,1401797451802739325,1401797451802739457,1401797451806932993,1401797451806933042,1401797451806933165,1401797451806933251,1401797451806933430,1401797451806933587,1401797451811127321,1401797451811127415,1401797451811127566,1401797451811127749,1401797451811127939,', 5.00, 1.00, 1.00, 1.00, '1376425599173656576');
INSERT INTO `li_freight_template_child` VALUES (1388033691770421248, '13011111111', '2021-08-06 11:13:13.742000', b'0', NULL, NULL, '广东省,新疆维吾尔自治区,贵州省,陕西省,林芝市,山南市,', '1401797451504943105,1401797451509137428,1401797451509137475,1401797451509137591,1401797451509137679,1401797451513331786,1401797451513331822,1401797451513331954,1401797451517526076,1401797451517526203,1401797451517526279,1401797451517526386,1401797451517526447,1401797451521720355,1401797451521720502,1401797451521720571,1401797451521720686,1401797451521720813,1401797451521720912,1401797451521720937,1401797451521720973,1401797451530109607,1401797451534303280,1401797451534303320,1401797451534303323,1401797451534303330,1401797451534303336,1401797451534303341,1401797451534303415,1401797451534303427,1401797451534303559,1401797451534303568,1401797451534303683,1401797451534303813,1401797451534303986,1401797451538497573,1401797451538497580,1401797451538497701,1401797451538497943,1401797451538497963,1401797451538497974,1401797451538498013,1401797451538498063,1401797451538498125,1401797451538498147,1401797451580441007,1401797451580441276,1401797451584634937,1401797451584635224,1401797451584635372,1401797451584635468,1401797451584635568,1401797451588829190,1401797451588829423,1401797451601413013,1401797451605606574,1401797451605606771,1401797451605606814,1401797451605606933,1401797451605607061,1401797451605607211,1401797451609800747,1401797451609800878,1401797451609801035,1401797451647549781,1401797451651743773,', 200.00, 1.00, 100.00, 1.00, '1376434171555086336');
INSERT INTO `li_freight_template_child` VALUES (1402510797566214146, '13011111111', '2021-07-27 16:02:54.502000', b'0', NULL, NULL, '新疆维吾尔自治区,内蒙古自治区,黑龙江省,西藏自治区,海南省,北京市,', '1401797451530109607,1401797451534303280,1401797451534303320,1401797451534303323,1401797451534303330,1401797451534303336,1401797451534303341,1401797451534303415,1401797451534303427,1401797451534303559,1401797451534303568,1401797451534303683,1401797451534303813,1401797451534303986,1401797451538497573,1401797451538497580,1401797451538497701,1401797451538497943,1401797451538497963,1401797451538497974,1401797451538498013,1401797451538498063,1401797451538498125,1401797451538498147,1401797451551081043,1401797451551081140,1401797451551081345,1401797451555274828,1401797451555274854,1401797451555274892,1401797451555275042,1401797451555275139,1401797451555275255,1401797451555275354,1401797451555275550,1401797451559469161,1401797451567857983,1401797451567858045,1401797451567858094,1401797451567858175,1401797451572052077,1401797451572052426,1401797451572052660,1401797451576246484,1401797451576246596,1401797451576246746,1401797451576246867,1401797451576246985,1401797451580440656,1401797451609801185,1401797451647549540,1401797451647549662,1401797451647549707,1401797451647549781,1401797451651743773,1401797451651743869,1401797451685298927,1401797451685298941,1401797451685298954,1401797451685298972,1401797451685298988,1401797451685299000,1401797451685299015,1401797451689492480,1401797451689492497,1401797451689492510,1401797451689492527,1401797451689492548,1401797451689492572,1401797451689492590,1401797451689492594,1401797451689492609,1401797451689492626,1401797451689492681,1401797451689492695,1401797451790156094,', 1.00, 10.00, 1.00, 10.00, '1402510797398441985');
INSERT INTO `li_freight_template_child` VALUES (1402510797658488834, '13011111111', '2021-07-27 16:02:54.522000', b'0', NULL, NULL, '河南省,', '1401797451538498153,1401797451538498360,1401797451538498447,1401797451542691900,1401797451542692008,1401797451542692274,1401797451542692507,1401797451542692524,1401797451542692621,1401797451546886255,1401797451546886308,1401797451546886429,1401797451546886645,1401797451546886802,1401797451546886948,1401797451551080489,1401797451551080617,1401797451551080836,', 1.00, 1.00, 1.00, 1.00, '1402510797398441985');

-- ----------------------------
-- Table structure for li_full_discount
-- ----------------------------
DROP TABLE IF EXISTS `li_full_discount`;
CREATE TABLE `li_full_discount`  (
                                     `id` bigint NOT NULL COMMENT 'ID',
                                     `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
                                     `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                     `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                     `update_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者',
                                     `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                     `end_time` datetime(6) NULL DEFAULT NULL COMMENT '活动结束时间',
                                     `promotion_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '活动名称',
                                     `store_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '店铺ID',
                                     `store_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '店铺名称',
                                     `start_time` datetime(6) NULL DEFAULT NULL COMMENT '活动开始时间',
                                     `coupon_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '优惠券ID',
                                     `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '活动说明',
                                     `full_minus` decimal(10, 2) NULL DEFAULT NULL COMMENT '减现金',
                                     `full_money` decimal(10, 2) NOT NULL COMMENT '优惠门槛金额',
                                     `full_rate` decimal(10, 2) NULL DEFAULT NULL COMMENT '打折',
                                     `gift_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '赠品ID',
                                     `coupon_flag` bit(1) NULL DEFAULT NULL COMMENT '是否赠优惠券',
                                     `free_freight_flag` bit(1) NULL DEFAULT NULL COMMENT '是否包邮',
                                     `full_minus_flag` bit(1) NULL DEFAULT NULL COMMENT '活动是否减现金',
                                     `full_rate_flag` bit(1) NULL DEFAULT NULL COMMENT '是否打折',
                                     `gift_flag` bit(1) NULL DEFAULT NULL COMMENT '是否有赠品',
                                     `point_flag` bit(1) NULL DEFAULT NULL COMMENT '是否赠送积分',
                                     `point` int NULL DEFAULT NULL COMMENT '赠送多少积分',
                                     `title` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '活动标题',
                                     `scope_id` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '范围关联的ID',
                                     `scope_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'PORTION_GOODS' COMMENT '关联范围类型',
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_full_discount
-- ----------------------------
INSERT INTO `li_full_discount` VALUES (1393830445262569472, '13011111111', '2021-05-16 15:27:28.000000', b'0', '13011111111', '2021-05-16 15:38:50.183000', '2021-05-23 00:00:00.000000', '试试', '1376433565247471616', '生活百货', '2021-05-16 16:00:00.000000', NULL, '100-10', 10.00, 100.00, NULL, NULL, NULL, b'1', b'1', NULL, NULL, NULL, NULL, '满100.0 减10.0', NULL, 'PORTION_GOODS');
INSERT INTO `li_full_discount` VALUES (1402945304551251969, '13011111111', '2021-06-10 19:06:40.000000', b'0', '13011111111', '2021-06-10 19:07:17.015000', '2021-06-12 00:00:00.000000', '123', '1376433565247471616', '生活百货', '2021-06-10 19:08:00.000000', NULL, '123', 12.00, 55.00, 8.50, '1377066518889627648', b'0', b'1', b'1', b'1', b'1', b'1', 11, '满55.0 减12.0', NULL, 'PORTION_GOODS');
INSERT INTO `li_full_discount` VALUES (1428511096822927361, '15810610731', '2021-08-20 08:16:00.594000', b'0', NULL, NULL, '2021-08-21 00:00:00.000000', '满额活动2021-8-20', '1376369067769724928', 'Lilishop自营店', '2021-08-20 08:20:00.000000', NULL, '活动描述', 10.00, 100.00, NULL, NULL, NULL, NULL, b'1', NULL, NULL, NULL, NULL, '满100.0 减10.0', NULL, 'PORTION_GOODS');
INSERT INTO `li_full_discount` VALUES (1438708787704037378, 'admin', '2021-09-17 11:37:59.563000', b'0', 'admin', '2021-09-26 15:15:49.314000', '2021-10-28 00:00:00.000000', '1111', '1413749887712206849', '君和', '2021-09-18 00:00:00.000000', NULL, '1111', 111.00, 1111.00, NULL, NULL, b'0', b'1', b'1', b'0', b'0', b'0', NULL, '满1111.0 减111.0', NULL, 'PORTION_GOODS');
INSERT INTO `li_full_discount` VALUES (1438770386032717826, 'admin', '2021-09-17 15:42:45.000000', b'0', 'admin', '2021-09-17 16:36:30.687000', '2021-10-31 00:00:00.000000', '0000', '1413749887712206849', '君和', '2021-10-30 00:00:00.000000', NULL, '1111111', 111.00, 111.00, NULL, NULL, b'0', b'1', b'1', b'0', b'0', b'0', NULL, '满111.0 减111.0', NULL, 'PORTION_GOODS');
INSERT INTO `li_full_discount` VALUES (1465936750249865217, '13011111111', '2021-12-01 14:52:11.705000', b'0', NULL, NULL, '2021-12-18 00:00:00.000000', '测试测试', '1376433565247471616', '家家乐', '2021-12-03 00:00:00.000000', NULL, 'test', 25.00, 100.00, NULL, NULL, b'0', b'1', b'1', b'0', b'0', b'0', NULL, '满100.0 减25.0', NULL, 'PORTION_GOODS');

-- ----------------------------
-- Table structure for li_goods
-- ----------------------------
DROP TABLE IF EXISTS `li_goods`;
CREATE TABLE `li_goods`  (
                             `id` bigint NOT NULL COMMENT 'ID',
                             `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
                             `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                             `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                             `update_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者',
                             `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                             `auth_message` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '审核信息',
                             `brand_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '品牌ID',
                             `buy_count` int NULL DEFAULT 0 COMMENT '购买数量',
                             `category_path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '分类路径',
                             `comment_num` int NULL DEFAULT NULL COMMENT '评论数量',
                             `cost` decimal(10, 2) NULL DEFAULT NULL COMMENT '成本价格',
                             `goods_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品名称',
                             `goods_unit` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '计量单位',
                             `goods_video` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品视频',
                             `grade` decimal(10, 2) NULL DEFAULT NULL COMMENT '商品好评率',
                             `intro` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '商品详情',
                             `auth_flag` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '审核状态',
                             `market_enable` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '上架状态',
                             `mobile_intro` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '商品移动端详情',
                             `original` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '原图路径',
                             `price` decimal(10, 2) NULL DEFAULT NULL COMMENT '商品价格',
                             `quantity` int NULL DEFAULT 0 COMMENT '库存',
                             `recommend` bit(1) NULL DEFAULT NULL COMMENT '是否为推荐商品',
                             `sales_model` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '销售模式',
                             `self_operated` bit(1) NULL DEFAULT NULL COMMENT '是否自营',
                             `store_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '店铺ID',
                             `store_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '店铺名称',
                             `selling_point` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '卖点',
                             `shop_category_path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '店铺分类',
                             `small` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '小图路径',
                             `sn` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品编号',
                             `template_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '运费模板ID',
                             `thumbnail` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '缩略图路径',
                             `under_message` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '下架原因',
                             `weight` decimal(10, 2) NULL DEFAULT NULL COMMENT '重量',
                             `store_category_path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '店铺分类路径',
                             `big` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '大图路径',
                             `params` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
                             `goods_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                             PRIMARY KEY (`id`) USING BTREE,
                             INDEX `store_id`(`store_id` ASC) USING BTREE COMMENT '店铺id索引'
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_goods
-- ----------------------------

-- ----------------------------
-- Table structure for li_goods_collection
-- ----------------------------
DROP TABLE IF EXISTS `li_goods_collection`;
CREATE TABLE `li_goods_collection`  (
                                        `id` bigint NOT NULL COMMENT 'ID',
                                        `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                        `member_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '会员id',
                                        `sku_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品id',
                                        PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_goods_collection
-- ----------------------------
INSERT INTO `li_goods_collection` VALUES (1387679288068669440, '2021-04-29 16:04:58.767000', '1376417684140326912', '1381792361985605632');
INSERT INTO `li_goods_collection` VALUES (1387936198541967360, '2021-04-30 09:05:50.997000', '1376364798236688384', '1386934441628139520');
INSERT INTO `li_goods_collection` VALUES (1387936246105374720, '2021-04-30 09:06:02.337000', '1376364798236688384', '1386934296454889472');
INSERT INTO `li_goods_collection` VALUES (1388049244748775424, '2021-04-30 16:35:03.313000', '1377906539959943168', '1387978669170884608');
INSERT INTO `li_goods_collection` VALUES (1388054314462740480, '2021-04-30 16:55:12.027000', '1377906539959943168', '1386933178354106368');
INSERT INTO `li_goods_collection` VALUES (1388064215104749568, '2021-04-30 17:34:32.524000', '1377906539959943168', '1381792103876526080');
INSERT INTO `li_goods_collection` VALUES (1390107018445979648, '2021-05-06 08:51:54.794000', '1384135843077160960', '1388032305993351168');
INSERT INTO `li_goods_collection` VALUES (1390107034388529152, '2021-05-06 08:51:58.595000', '1384135843077160960', '1388032306228232192');
INSERT INTO `li_goods_collection` VALUES (1392049933233684480, '2021-05-11 17:32:21.777000', '1377906539959943168', '1381792934499713024');
INSERT INTO `li_goods_collection` VALUES (1394212499577896960, '2021-05-17 16:45:37.772000', '1377906539959943168', '1387977574860193792');
INSERT INTO `li_goods_collection` VALUES (1399156362679681024, '2021-05-31 08:10:46.588000', '1395211997401841664', '1386931039305203712');
INSERT INTO `li_goods_collection` VALUES (1400625428065943552, '2021-06-04 09:28:19.070000', '1400625165313769472', '1386931039305203712');
INSERT INTO `li_goods_collection` VALUES (1401821190327480322, '2021-06-07 16:39:51.010000', '1401812661036908544', '1381789991541145600');
INSERT INTO `li_goods_collection` VALUES (1402197915712466946, '2021-06-08 17:36:49.341000', '1402195335947071490', '1381792263696285696');
INSERT INTO `li_goods_collection` VALUES (1402836008433938434, '2021-06-10 11:52:22.500000', '1402830585391804418', '1381795951156396032');
INSERT INTO `li_goods_collection` VALUES (1402923457134784513, '2021-06-10 17:39:51.896000', '1402195335947071490', '1381796080479371264');
INSERT INTO `li_goods_collection` VALUES (1402923982093873154, '2021-06-10 17:41:57.056000', '1402195335947071490', '1381792263700480000');
INSERT INTO `li_goods_collection` VALUES (1402942519504072706, '2021-06-10 18:55:36.719000', '1399986007926898688', '1387980372364492800');
INSERT INTO `li_goods_collection` VALUES (1402943253071065089, '2021-06-10 18:58:31.619000', '1399986007926898688', '1386932357549785088');
INSERT INTO `li_goods_collection` VALUES (1403002232929316865, '2021-06-10 22:52:53.508000', '1403001514054971393', '1387977732830265344');
INSERT INTO `li_goods_collection` VALUES (1404418123336265730, '2021-06-14 20:39:08.087000', '1403928259801133058', '1381796202034495488');
INSERT INTO `li_goods_collection` VALUES (1406192201667821570, '2021-06-19 18:08:41.323000', '1406192185838518273', '1381792103884914688');
INSERT INTO `li_goods_collection` VALUES (1407525468698427394, '2021-06-23 10:26:36.954000', '1405062996846546946', '1407237332172877826');
INSERT INTO `li_goods_collection` VALUES (1407525581139329025, '2021-06-23 10:27:03.762000', '1405062996846546946', '1381792263700480000');
INSERT INTO `li_goods_collection` VALUES (1407525614588903425, '2021-06-23 10:27:11.736000', '1405062996846546946', '1381795951156396032');
INSERT INTO `li_goods_collection` VALUES (1407525625561202689, '2021-06-23 10:27:14.353000', '1405062996846546946', '1388032474835058688');
INSERT INTO `li_goods_collection` VALUES (1407525640434204673, '2021-06-23 10:27:17.899000', '1405062996846546946', '1381792623026503680');
INSERT INTO `li_goods_collection` VALUES (1407525649825251330, '2021-06-23 10:27:20.138000', '1405062996846546946', '1381792623030697984');
INSERT INTO `li_goods_collection` VALUES (1407525693643145217, '2021-06-23 10:27:30.589000', '1405062996846546946', '1381792873405480960');
INSERT INTO `li_goods_collection` VALUES (1407525709145292801, '2021-06-23 10:27:34.281000', '1405062996846546946', '1381792873405480961');
INSERT INTO `li_goods_collection` VALUES (1407526103661527042, '2021-06-23 10:29:08.341000', '1405062996846546946', '1381796591895052288');
INSERT INTO `li_goods_collection` VALUES (1407526122506534914, '2021-06-23 10:29:12.835000', '1405062996846546946', '1381796537792724992');
INSERT INTO `li_goods_collection` VALUES (1407526529395965953, '2021-06-23 10:30:49.843000', '1405062996846546946', '1381796591899246592');
INSERT INTO `li_goods_collection` VALUES (1408274040226144257, '2021-06-25 12:01:10.313000', '1376417684140326912', '1387976572325068800');
INSERT INTO `li_goods_collection` VALUES (1408274268605997058, '2021-06-25 12:02:04.763000', '1376417684140326912', '1387977378034089984');
INSERT INTO `li_goods_collection` VALUES (1408685360012382210, '2021-06-26 15:15:36.593000', '1376364798236688384', '1387979014626344960');
INSERT INTO `li_goods_collection` VALUES (1408763155308666882, '2021-06-26 20:24:44.439000', '1399986007926898688', '1381792623030697984');
INSERT INTO `li_goods_collection` VALUES (1413313062312960002, '2021-07-09 09:44:26.801000', '1376364798236688384', '1381792187770994688');
INSERT INTO `li_goods_collection` VALUES (1414423317684002818, '2021-07-12 11:16:12.310000', '1414423107209633793', '1387977227303387136');
INSERT INTO `li_goods_collection` VALUES (1414978674005225473, '2021-07-14 00:02:59.573000', '1414976427284344834', '1381792934499713024');
INSERT INTO `li_goods_collection` VALUES (1415591160630849537, '2021-07-15 16:36:47.764000', '1415589981511331842', '1381792263700480000');
INSERT INTO `li_goods_collection` VALUES (1415606462492028929, '2021-07-15 17:37:36.012000', '1414597921299869698', '1388032999185973248');
INSERT INTO `li_goods_collection` VALUES (1416000675671027714, '2021-07-16 19:44:03.758000', '1416000218424782849', '1377132926088511488');
INSERT INTO `li_goods_collection` VALUES (1416232693013000193, '2021-07-17 11:06:01.003000', '1416231001982873602', '1381792934499713024');
INSERT INTO `li_goods_collection` VALUES (1416261983226146817, '2021-07-17 13:02:24.334000', '1416261963202539521', '1395291289733300224');
INSERT INTO `li_goods_collection` VALUES (1417035919371079682, '2021-07-19 16:17:45.089000', '1414738983339274241', '1386525851826257920');
INSERT INTO `li_goods_collection` VALUES (1417040344483434497, '2021-07-19 16:35:20.124000', '1416921576088383489', '1387977574864388096');
INSERT INTO `li_goods_collection` VALUES (1417335164435238913, '2021-07-20 12:06:50.672000', '1376417684140326912', '1381796144429924352');
INSERT INTO `li_goods_collection` VALUES (1417373558481686529, '2021-07-20 14:39:24.526000', '1416245946480963586', '1381792623026503680');
INSERT INTO `li_goods_collection` VALUES (1417387423114268673, '2021-07-20 15:34:30.113000', '1376417684140326912', '1415275858856628226');
INSERT INTO `li_goods_collection` VALUES (1417403503446659074, '2021-07-20 16:38:23.963000', '1376417684140326912', '1415275858860822529');
INSERT INTO `li_goods_collection` VALUES (1418416252356165633, '2021-07-23 11:42:42.119000', '1418388715903156225', '1381792873405480961');
INSERT INTO `li_goods_collection` VALUES (1418963890101059586, '2021-07-24 23:58:49.131000', '1418962618740408322', '1387977574864388096');
INSERT INTO `li_goods_collection` VALUES (1420277270237712385, '2021-07-28 14:57:43.358000', '1376417684140326912', '1387977878473277440');
INSERT INTO `li_goods_collection` VALUES (1420288136790183938, '2021-07-28 15:40:54.147000', '1376417684140326912', '1420202304892874756');
INSERT INTO `li_goods_collection` VALUES (1420662878122577921, '2021-07-29 16:29:59.441000', '1376417684140326912', '1387978410361356288');

-- ----------------------------
-- Table structure for li_goods_gallery
-- ----------------------------
DROP TABLE IF EXISTS `li_goods_gallery`;
CREATE TABLE `li_goods_gallery`  (
                                     `id` bigint NOT NULL COMMENT 'ID',
                                     `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
                                     `goods_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品ID',
                                     `is_default` int NULL DEFAULT NULL COMMENT '是否是默认图片',
                                     `original` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '原图路径',
                                     `small` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '小图路径',
                                     `sort` int NULL DEFAULT NULL COMMENT '排序',
                                     `thumbnail` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '缩略图路径',
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_goods_gallery
-- ----------------------------


-- ----------------------------
-- Table structure for li_goods_params
-- ----------------------------
DROP TABLE IF EXISTS `li_goods_params`;
CREATE TABLE `li_goods_params`  (
                                    `id` bigint NOT NULL COMMENT 'ID',
                                    `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
                                    `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                    `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                    `update_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者',
                                    `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                    `goods_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品ID',
                                    `param_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '参数ID',
                                    `param_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '参数名字',
                                    `param_value` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '参数值',
                                    `is_index` int NULL DEFAULT NULL,
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_goods_params
-- ----------------------------

-- ----------------------------
-- Table structure for li_goods_sku
-- ----------------------------
DROP TABLE IF EXISTS `li_goods_sku`;
CREATE TABLE `li_goods_sku`  (
                                 `id` bigint NOT NULL COMMENT 'ID',
                                 `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
                                 `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                 `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                 `update_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者',
                                 `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                 `auth_message` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '审核信息',
                                 `big` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '大图路径',
                                 `brand_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '品牌ID',
                                 `buy_count` int NULL DEFAULT NULL COMMENT '购买数量',
                                 `category_path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '分类路径',
                                 `comment_num` int NULL DEFAULT NULL COMMENT '评价数量',
                                 `cost` decimal(10, 2) NULL DEFAULT NULL COMMENT '成本价格',
                                 `freight_payer` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '运费承担者',
                                 `freight_template_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '配送模版ID',
                                 `goods_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品ID',
                                 `goods_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品名称',
                                 `goods_unit` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '计量单位',
                                 `goods_video` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品视频',
                                 `grade` decimal(10, 2) NULL DEFAULT NULL COMMENT '商品好评率',
                                 `intro` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '商品详情',
                                 `auth_flag` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '审核状态',
                                 `promotion_flag` bit(1) NULL DEFAULT NULL COMMENT '是否是促销商品',
                                 `market_enable` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '上架状态',
                                 `mobile_intro` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '移动端商品详情',
                                 `original` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '原图路径',
                                 `price` decimal(10, 2) NULL DEFAULT NULL COMMENT '商品价格',
                                 `promotion_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '促销价',
                                 `quantity` int NULL DEFAULT NULL COMMENT '库存',
                                 `recommend` bit(1) NOT NULL COMMENT '是否为推荐商品',
                                 `sales_model` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '销售模式',
                                 `self_operated` bit(1) NULL DEFAULT NULL COMMENT '是否自营',
                                 `store_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '店铺ID',
                                 `store_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '店铺名称',
                                 `selling_point` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '卖点',
                                 `small` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '小图路径',
                                 `sn` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品编号',
                                 `specs` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '规格信息json',
                                 `template_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '运费模板id',
                                 `thumbnail` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '缩略图路径',
                                 `under_message` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '下架原因',
                                 `view_count` int NULL DEFAULT NULL COMMENT '浏览数量',
                                 `weight` decimal(10, 2) NULL DEFAULT NULL COMMENT '重量',
                                 `simple_specs` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '规格信息',
                                 `store_category_path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '店铺分类路径',
                                 `goods_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_goods_sku
-- ----------------------------
-- ----------------------------
-- Table structure for li_goods_unit
-- ----------------------------
DROP TABLE IF EXISTS `li_goods_unit`;
CREATE TABLE `li_goods_unit`  (
                                  `id` bigint NOT NULL COMMENT 'ID',
                                  `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
                                  `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                  `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '计量单位名称',
                                  `update_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者',
                                  `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_goods_unit
-- ----------------------------
INSERT INTO `li_goods_unit` VALUES (1376371484724822016, 'admin', '2021-03-28 22:11:48.361000', b'0', 'admin', '2021-07-09 16:29:24.889000', '只');
INSERT INTO `li_goods_unit` VALUES (1376371500533153792, 'admin', '2021-03-28 22:11:52.130000', b'0', NULL, NULL, '件');
INSERT INTO `li_goods_unit` VALUES (1376371521315930112, 'admin', '2021-03-28 22:11:57.085000', b'0', NULL, NULL, '份');
INSERT INTO `li_goods_unit` VALUES (1376371538806177792, 'admin', '2021-03-28 22:12:01.255000', b'0', 'admin', '2021-09-22 15:18:18.907000', '克');

-- ----------------------------
-- Table structure for li_goods_words
-- ----------------------------
DROP TABLE IF EXISTS `li_goods_words`;
CREATE TABLE `li_goods_words`  (
                                   `id` bigint NOT NULL,
                                   `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                   `create_time` datetime(6) NULL DEFAULT NULL,
                                   `delete_flag` bit(1) NULL DEFAULT NULL,
                                   `update_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                   `update_time` datetime(6) NULL DEFAULT NULL,
                                   `abbreviate` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                   `sort` int NULL DEFAULT NULL,
                                   `type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                   `whole_spell` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                   `words` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for li_hot_words_history
-- ----------------------------
DROP TABLE IF EXISTS `li_hot_words_history`;
CREATE TABLE `li_hot_words_history`  (
                                         `id` bigint NOT NULL COMMENT 'ID',
                                         `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                         `keywords` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '热词',
                                         `score` int NULL DEFAULT NULL COMMENT '热词分数',
                                         PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '热词历史表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of li_hot_words_history
-- ----------------------------

-- ----------------------------
-- Table structure for li_kanjia_activity
-- ----------------------------
DROP TABLE IF EXISTS `li_kanjia_activity`;
CREATE TABLE `li_kanjia_activity`  (
                                       `id` bigint NOT NULL,
                                       `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                                       `create_time` datetime(6) NULL DEFAULT NULL,
                                       `delete_flag` bit(1) NULL DEFAULT NULL,
                                       `update_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                                       `update_time` datetime(6) NULL DEFAULT NULL,
                                       `goods_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                                       `kanjia_activity_goods_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                                       `member_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                                       `member_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                                       `purchase_price` decimal(10, 2) NULL DEFAULT NULL,
                                       `sku_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                                       `status` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                                       `surplus_price` decimal(10, 2) NULL DEFAULT NULL,
                                       `thumbnail` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                                       PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_kanjia_activity
-- ----------------------------

-- ----------------------------
-- Table structure for li_kanjia_activity_goods
-- ----------------------------
DROP TABLE IF EXISTS `li_kanjia_activity_goods`;
CREATE TABLE `li_kanjia_activity_goods`  (
                                             `id` bigint NOT NULL,
                                             `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                                             `create_time` datetime(6) NULL DEFAULT NULL,
                                             `delete_flag` bit(1) NULL DEFAULT NULL,
                                             `update_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                                             `update_time` datetime(6) NULL DEFAULT NULL,
                                             `end_time` datetime(6) NULL DEFAULT NULL,
                                             `promotion_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                                             `start_time` datetime(6) NULL DEFAULT NULL,
                                             `store_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                                             `store_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                                             `goods_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                                             `highest_price` decimal(10, 2) NULL DEFAULT NULL,
                                             `lowest_price` decimal(10, 2) NULL DEFAULT NULL,
                                             `original_price` decimal(10, 2) NULL DEFAULT NULL,
                                             `purchase_price` decimal(10, 2) NULL DEFAULT NULL,
                                             `settlement_price` decimal(10, 2) NULL DEFAULT NULL,
                                             `sku_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                                             `stock` int NULL DEFAULT NULL,
                                             `thumbnail` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                                             `scope_id` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '范围关联的ID',
                                             `scope_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'PORTION_GOODS' COMMENT '关联范围类型',
                                             `goods_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                                             PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_kanjia_activity_goods
-- ----------------------------

-- ----------------------------
-- Table structure for li_kanjia_activity_log
-- ----------------------------
DROP TABLE IF EXISTS `li_kanjia_activity_log`;
CREATE TABLE `li_kanjia_activity_log`  (
                                           `id` bigint NOT NULL,
                                           `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                                           `create_time` datetime(6) NULL DEFAULT NULL,
                                           `delete_flag` bit(1) NULL DEFAULT NULL,
                                           `update_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                                           `update_time` datetime(6) NULL DEFAULT NULL,
                                           `kanjia_activity_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                                           `kanjia_member_face` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                                           `kanjia_member_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                                           `kanjia_member_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                                           `kanjia_price` decimal(10, 2) NULL DEFAULT NULL,
                                           `surplus_price` decimal(10, 2) NULL DEFAULT NULL,
                                           PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_kanjia_activity_log
-- ----------------------------

-- ----------------------------
-- Table structure for li_logistics
-- ----------------------------
DROP TABLE IF EXISTS `li_logistics`;
CREATE TABLE `li_logistics`  (
                                 `id` bigint NOT NULL COMMENT 'ID',
                                 `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '创建者',
                                 `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                 `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                 `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '更新者',
                                 `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                 `code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '物流公司code',
                                 `disabled` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '禁用状态',
                                 `form_items` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '物流公司电子面单表单',
                                 `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '物流公司名称',
                                 `stand_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '支持电子面单',
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_logistics
-- ----------------------------
INSERT INTO `li_logistics` VALUES (1364825407932596224, 'admin', '2021-02-25 06:31:49.181000', b'0', 'admin', '2021-07-30 16:34:01.851000', 'SF', 'OPEN', 'SF', '顺丰速运', 'null');
INSERT INTO `li_logistics` VALUES (1364825461946843136, 'admin', '2021-02-25 06:32:02.017000', b'0', 'admin', '2021-07-26 15:29:42.125000', 'HTKY', 'OPEN', 'HTKY', '百世快递', 'null');
INSERT INTO `li_logistics` VALUES (1364825604276355072, 'admin', '2021-02-25 06:32:35.950000', b'0', 'admin', '2021-07-22 11:13:50.968000', 'YTO', 'OPEN', 'YTO', '圆通速递', 'true');
INSERT INTO `li_logistics` VALUES (1364825661964812288, 'admin', '2021-02-25 06:32:49.704000', b'0', 'admin', '2021-07-22 11:13:46.810000', 'YD', 'OPEN', 'YD', '韵达速递', 'null');
INSERT INTO `li_logistics` VALUES (1364825703807188992, 'admin', '2021-02-25 06:32:59.681000', b'0', 'admin', '2021-07-21 13:16:32.073000', 'STO', 'OPEN', 'STO', '申通快递', 'null');
INSERT INTO `li_logistics` VALUES (1364825753534857216, 'admin', '2021-02-25 06:33:11.537000', b'0', 'admin', '2021-07-21 13:16:29.948000', 'YZPY', 'OPEN', 'YZPY', '邮政快递包裹', 'null');
INSERT INTO `li_logistics` VALUES (1364825783545102336, 'admin', '2021-02-25 06:33:18.691000', b'0', 'admin', '2021-07-21 15:34:26.353000', 'EMS', 'OPEN', 'EMS', 'EMS', 'null');
INSERT INTO `li_logistics` VALUES (1364825828369629184, 'admin', '2021-02-25 06:33:29.379000', b'0', 'admin', '2021-07-20 12:06:31.990000', 'HHTT', 'OPEN', 'HHTT', '天天快递', 'null');
INSERT INTO `li_logistics` VALUES (1364825870564327424, 'admin', '2021-02-25 06:33:39.439000', b'0', 'admin', '2021-06-21 00:31:45.307000', 'JD', 'OPEN', 'JD', '京东快递', 'null');
INSERT INTO `li_logistics` VALUES (1364825911689478144, 'admin', '2021-02-25 06:33:49.243000', b'0', 'admin', '2021-03-07 19:32:19.660000', 'UC', 'OPEN', 'UC', '优速快递', 'null');
INSERT INTO `li_logistics` VALUES (1364825959177388032, 'admin', '2021-02-25 06:34:00.565000', b'0', 'admin', '2021-08-03 00:27:06.688000', 'DBL', 'OPEN', 'DBL', '德邦快递', 'false');
INSERT INTO `li_logistics` VALUES (1425472379076792321, 'admin', '2021-08-11 23:01:13.829000', b'0', NULL, NULL, 'ZTO', 'OPEN', NULL, '中通', NULL);

-- ----------------------------
-- Table structure for li_member
-- ----------------------------
DROP TABLE IF EXISTS `li_member`;
CREATE TABLE `li_member`  (
                              `id` bigint NOT NULL COMMENT 'ID',
                              `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
                              `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                              `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                              `update_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者',
                              `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                              `birthday` datetime(6) NULL DEFAULT NULL COMMENT '会员生日',
                              `disabled` bit(1) NULL DEFAULT NULL COMMENT '会员状态',
                              `face` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '会员头像',
                              `have_store` bit(1) NULL DEFAULT NULL COMMENT '是否开通店铺',
                              `mobile` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '手机号码',
                              `nick_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '会员昵称',
                              `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '会员密码',
                              `point` bigint NULL DEFAULT 0 COMMENT '积分数量',
                              `sex` int NOT NULL DEFAULT 0 COMMENT '会员性别',
                              `store_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '店铺ID',
                              `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '会员用户名',
                              `region` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '会员地址',
                              `region_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '会员地址ID',
                              `client_enum` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '客户端',
                              `last_login_date` datetime(6) NULL DEFAULT NULL COMMENT '最后一次登录时间',
                              `gradeId` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                              `experience` bigint NULL DEFAULT NULL,
                              `grade_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                              `total_point` bigint NULL DEFAULT 0,
                              PRIMARY KEY (`id`) USING BTREE,
                              UNIQUE INDEX `username`(`username` ASC) USING BTREE COMMENT 'username唯一索引'
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_member
-- ----------------------------
INSERT INTO `li_member` VALUES (1376364798236688384, NULL, '2021-03-28 21:45:14.178000', b'0', '15810610731', '2022-01-10 21:13:48.278000', '2021-03-28 00:00:00.000000', b'1', 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/00218693d2e64c9893b269d1aec77f94.jpeg', b'1', '15810610731', '星星眨眨眼,月儿画问号', '$2a$10$FhOh0wqan7PCwFh96WXyqOcdxh.FQkUuuKBh745LyHR4Tkg0khiDO', 41601, 1, '1376369067769724928', '15810610731', '上海市 , 上海城区 , 浦东新区 , 高东镇', '1371783040048562731,1371783040048562732,1371783040048562733,1371783040048562734', 'PC', '2022-01-10 21:13:48.232000', NULL, NULL, NULL, 44717);
INSERT INTO `li_member` VALUES (1376417684140326912, NULL, '2021-03-29 01:15:23.159000', b'0', '13011111111', '2021-12-22 16:07:27.926000', '2021-11-28 00:00:00.000000', b'1', 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/cec8b43b6bad41c69f393e93c55c528f.jpg', b'1', '13011111111', '张三', '$2a$10$s9xnEGxRDdXRzvK/n2SNKu0aVz2Tq6Y8Sw04tyLzaOs3077jQlLMi', 55790972, 1, '1376433565247471616', '13011111111', '新疆维吾尔自治区,铁门关市,双丰镇', '', 'PC', '2021-12-22 16:07:27.915000', NULL, NULL, NULL, 56334259);

-- ----------------------------
-- Table structure for li_member_address
-- ----------------------------
DROP TABLE IF EXISTS `li_member_address`;
CREATE TABLE `li_member_address`  (
                                      `id` bigint NOT NULL COMMENT 'ID',
                                      `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
                                      `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                      `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                      `update_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者',
                                      `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                      `alias` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '地址别名',
                                      `consignee_address_id_path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '地址ID',
                                      `consignee_address_path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '地址名称',
                                      `detail` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '详细地址',
                                      `is_default` bit(1) NULL DEFAULT NULL COMMENT '是否为默认收货地址',
                                      `lat` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '纬度',
                                      `lon` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '经度',
                                      `member_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '会员ID',
                                      `mobile` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '手机号码',
                                      `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '收货人姓名',
                                      PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_member_address
-- ----------------------------
INSERT INTO `li_member_address` VALUES (1464108195383668738, '13011111111', '2021-11-26 13:46:10.000000', b'0', '13011111111', '2021-11-27 04:08:15.042000', 'null', '1401797451504943104,1401797451509137679,1401797451513331719,1401797451513331723', '广东省,深圳市,福田区,华强北街道', '振兴路341号上步工业区', b'0', '22.547', '114.085947', '1376417684140326912', '13333333333', '1231');
INSERT INTO `li_member_address` VALUES (1464305901209186305, '13011111111', '2021-11-27 02:51:46.000000', b'0', '13011111111', '2021-11-27 02:51:51.378000', '谁打上', '1401797451504943104,1401797451504943105,1401797451504943106,1401797451504943107', '广东省,汕头市,潮阳区,和平镇', '大幅度发的', b'0', '116.519', '23.2664', '1376417684140326912', '13011111111', '分担分担');
INSERT INTO `li_member_address` VALUES (1466246586711756802, '13011111111', '2021-12-02 11:23:22.474000', b'0', NULL, NULL, NULL, '1401797451790156446,1401797451798544836,1401797451798545015,1401797451798545026', '江西省,上饶市,鄱阳县,田畈街镇', '庙下', b'1', '29.319165', '116.889374', '1376417684140326912', '18162305819', '王八蛋');

-- ----------------------------
-- Table structure for li_member_coupon
-- ----------------------------
DROP TABLE IF EXISTS `li_member_coupon`;
CREATE TABLE `li_member_coupon`  (
                                     `id` bigint NOT NULL COMMENT 'ID',
                                     `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
                                     `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                     `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                     `update_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者',
                                     `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                     `consume_threshold` decimal(10, 2) NULL DEFAULT NULL COMMENT '消费门槛',
                                     `consumption_time` datetime(6) NULL DEFAULT NULL COMMENT '核销时间',
                                     `coupon_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '优惠券ID',
                                     `coupon_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '活动类型',
                                     `discount` decimal(10, 2) NULL DEFAULT NULL COMMENT '折扣',
                                     `end_time` datetime(6) NULL DEFAULT NULL COMMENT '使用截止时间',
                                     `get_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '优惠券类型',
                                     `platform_flag` bit(1) NULL DEFAULT NULL COMMENT '是否是平台优惠券',
                                     `member_coupon_status` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '会员优惠券状态',
                                     `member_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '会员ID',
                                     `member_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '会员名称',
                                     `price` decimal(10, 2) NULL DEFAULT NULL COMMENT '面额',
                                     `scope_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '范围关联的ID',
                                     `scope_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '关联范围类型',
                                     `store_commission` decimal(10, 2) NULL DEFAULT NULL COMMENT '店铺承担比例',
                                     `store_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '店铺ID',
                                     `start_time` datetime(6) NULL DEFAULT NULL COMMENT '使用起始时间',
                                     `store_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '店铺名称',
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_member_coupon
-- ----------------------------

-- ----------------------------
-- Table structure for li_member_coupon_sign
-- ----------------------------
DROP TABLE IF EXISTS `li_member_coupon_sign`;
CREATE TABLE `li_member_coupon_sign` (
                                         `id` bigint NOT NULL,
                                         `coupon_activity_Id` bigint DEFAULT NULL COMMENT '优惠券活动id',
                                         `member_id` bigint DEFAULT NULL COMMENT '会员id',
                                         `invalid_time` datetime DEFAULT NULL COMMENT '过期时间',
                                         `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                         PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of li_member_coupon_sign
-- ----------------------------

-- ----------------------------
-- Table structure for li_member_evaluation
-- ----------------------------
DROP TABLE IF EXISTS `li_member_evaluation`;
CREATE TABLE `li_member_evaluation`  (
                                         `id` bigint NOT NULL COMMENT 'ID',
                                         `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
                                         `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                         `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                         `update_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者',
                                         `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                         `content` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '评价内容',
                                         `goods_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品ID',
                                         `goods_image` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品图片',
                                         `goods_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品名称',
                                         `grade` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '好中差评',
                                         `have_image` bit(1) NULL DEFAULT NULL COMMENT '评价是否有图片',
                                         `have_reply_image` bit(1) NULL DEFAULT NULL COMMENT '回复是否有图片',
                                         `images` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '评价图片',
                                         `member_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '会员ID',
                                         `member_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '会员名称',
                                         `member_profile` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '会员头像',
                                         `order_no` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '订单号',
                                         `reply` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '评价回复',
                                         `reply_image` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '评价回复图片',
                                         `store_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '店铺ID',
                                         `sku_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '货品ID',
                                         `status` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '状态',
                                         `reply_status` bit(1) NULL DEFAULT NULL COMMENT '回复状态',
                                         `delivery_score` int NULL DEFAULT NULL COMMENT '物流评分',
                                         `description_score` int NULL DEFAULT NULL COMMENT '描述评分',
                                         `service_score` int NULL DEFAULT NULL COMMENT '服务评分',
                                         `store_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '店铺名称',
                                         PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for li_member_grade
-- ----------------------------
DROP TABLE IF EXISTS `li_member_grade`;
CREATE TABLE `li_member_grade`  (
                                    `id` bigint NOT NULL,
                                    `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                                    `create_time` datetime(6) NULL DEFAULT NULL,
                                    `delete_flag` bit(1) NULL DEFAULT NULL,
                                    `update_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                                    `update_time` datetime(6) NULL DEFAULT NULL,
                                    `experience_value` int NOT NULL,
                                    `grade_image` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                    `grade_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                    `is_default` bit(1) NULL DEFAULT NULL,
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_member_grade
-- ----------------------------

-- ----------------------------
-- Table structure for li_member_message
-- ----------------------------
DROP TABLE IF EXISTS `li_member_message`;
CREATE TABLE `li_member_message`  (
                                      `id` bigint NOT NULL COMMENT 'ID',
                                      `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建者',
                                      `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                      `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志',
                                      `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新者',
                                      `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                                      `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '消息内容',
                                      `member_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '会员ID',
                                      `member_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '会员名称',
                                      `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '状态',
                                      `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '消息标题',
                                      `message_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                                      PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_member_message
-- ----------------------------

-- ----------------------------
-- Table structure for li_member_notice
-- ----------------------------
DROP TABLE IF EXISTS `li_member_notice`;
CREATE TABLE `li_member_notice`  (
                                     `id` bigint NOT NULL COMMENT 'ID',
                                     `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
                                     `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                     `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                     `update_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者',
                                     `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                     `content` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '站内信内容',
                                     `is_read` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '是否已读',
                                     `member_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '会员ID',
                                     `receive_time` bigint NULL DEFAULT NULL COMMENT '阅读时间',
                                     `title` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '标题',
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_member_notice
-- ----------------------------

-- ----------------------------
-- Table structure for li_member_notice_log
-- ----------------------------
DROP TABLE IF EXISTS `li_member_notice_log`;
CREATE TABLE `li_member_notice_log`  (
                                         `id` bigint NOT NULL COMMENT 'ID',
                                         `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
                                         `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                         `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                         `update_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者',
                                         `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                         `admin_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '管理员ID',
                                         `admin_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '管理员名称',
                                         `content` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '消息内容',
                                         `member_ids` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '会员ID',
                                         `send_time` datetime(6) NULL DEFAULT NULL COMMENT '发送时间',
                                         `send_type` int NULL DEFAULT NULL COMMENT '发送类型',
                                         `title` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '标题',
                                         PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_member_notice_log
-- ----------------------------

-- ----------------------------
-- Table structure for li_member_notice_senter
-- ----------------------------
DROP TABLE IF EXISTS `li_member_notice_senter`;
CREATE TABLE `li_member_notice_senter`  (
                                            `id` bigint NOT NULL COMMENT 'ID',
                                            `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
                                            `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                            `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                            `update_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者',
                                            `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                            `content` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '消息内容',
                                            `member_ids` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '会员ID',
                                            `send_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '发送类型',
                                            `title` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '标题',
                                            PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_member_notice_senter
-- ----------------------------

-- ----------------------------
-- Table structure for li_member_points_history
-- ----------------------------
DROP TABLE IF EXISTS `li_member_points_history`;
CREATE TABLE `li_member_points_history`  (
                                             `id` bigint NOT NULL COMMENT 'ID',
                                             `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
                                             `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                             `before_point` bigint NULL DEFAULT NULL COMMENT '消费之前积分',
                                             `content` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '内容',
                                             `member_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '会员ID',
                                             `member_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '会员名称',
                                             `point` bigint NULL DEFAULT NULL COMMENT '当前积分',
                                             `point_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '消费积分类型',
                                             `variable_point` bigint NULL DEFAULT NULL COMMENT '消费积分',
                                             PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for li_member_receipt
-- ----------------------------
DROP TABLE IF EXISTS `li_member_receipt`;
CREATE TABLE `li_member_receipt`  (
                                      `id` bigint NOT NULL COMMENT 'ID',
                                      `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                      `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                      `is_default` int NULL DEFAULT NULL COMMENT '是否为默认选项',
                                      `member_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '会员ID',
                                      `member_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '会员名称',
                                      `receipt_content` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '发票内容',
                                      `receipt_title` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '发票抬头',
                                      `receipt_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '发票类型',
                                      `taxpayer_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '纳税人识别号',
                                      PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_member_receipt
-- ----------------------------

-- ----------------------------
-- Table structure for li_member_sign
-- ----------------------------
DROP TABLE IF EXISTS `li_member_sign`;
CREATE TABLE `li_member_sign` (
                                  `id` bigint NOT NULL COMMENT 'ID',
                                  `create_time` datetime(6) DEFAULT NULL COMMENT '创建时间',
                                  `member_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '会员用户ID',
                                  `member_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '会员用户名',
                                  `sign_day` int DEFAULT NULL COMMENT '连续签到天数',
                                  `day` int DEFAULT NULL COMMENT '签到日',
                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of li_member_sign
-- ----------------------------

-- ----------------------------
-- Table structure for li_member_statistics_data
-- ----------------------------
DROP TABLE IF EXISTS `li_member_statistics_data`;
CREATE TABLE `li_member_statistics_data`  (
                                              `id` bigint NOT NULL COMMENT 'ID',
                                              `active_quantity` int NULL DEFAULT NULL COMMENT '当日活跃数量',
                                              `create_date` datetime(6) NULL DEFAULT NULL COMMENT '统计日',
                                              `member_count` int NULL DEFAULT NULL COMMENT '当前会员数量',
                                              `newly_added` int NULL DEFAULT NULL COMMENT '新增会员数量',
                                              PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_member_statistics_data
-- ----------------------------

-- ----------------------------
-- Table structure for li_member_wallet
-- ----------------------------
DROP TABLE IF EXISTS `li_member_wallet`;
CREATE TABLE `li_member_wallet`  (
                                     `id` bigint NOT NULL COMMENT 'ID',
                                     `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '创建者',
                                     `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                     `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志',
                                     `update_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '更新者',
                                     `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                     `member_frozen_wallet` decimal(10, 2) NULL DEFAULT NULL COMMENT '会员预存款冻结金额',
                                     `member_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '会员ID',
                                     `member_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '会员用户名',
                                     `member_wallet` decimal(10, 2) NULL DEFAULT NULL COMMENT '会员预存款',
                                     `wallet_password` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '预存款密码',
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_member_wallet
-- ----------------------------
INSERT INTO `li_member_wallet` VALUES (1376417817896681472, '13011111111', '2021-03-29 01:15:55.050000', b'0', '13011111111', '2022-10-21 11:17:47.704000', 1012150.1, '1376417684140326912', '13011111111', 155593.46, NULL);

-- ----------------------------
-- Table structure for li_member_withdraw_apply
-- ----------------------------
DROP TABLE IF EXISTS `li_member_withdraw_apply`;
CREATE TABLE `li_member_withdraw_apply`  (
                                             `id` bigint NOT NULL COMMENT 'ID',
                                             `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
                                             `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                             `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                             `update_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者',
                                             `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                             `apply_money` decimal(10, 2) NULL DEFAULT NULL COMMENT '申请提现金额',
                                             `apply_status` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '提现状态',
                                             `inspect_remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '审核备注',
                                             `inspect_time` datetime(6) NULL DEFAULT NULL COMMENT '审核时间',
                                             `member_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '会员ID',
                                             `member_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '会员名称',
                                             `sn` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '编号',
                                             `real_name` varchar(255) DEFAULT NULL COMMENT '真实姓名',
                                             `connect_number` varchar(255) DEFAULT NULL COMMENT '联系方式',
                                             `error_message`  varchar(255) DEFAULT NULL COMMENT '错误信息',
                                             PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_member_withdraw_apply
-- ----------------------------

-- ----------------------------
-- Table structure for li_menu
-- ----------------------------
DROP TABLE IF EXISTS `li_menu`;
CREATE TABLE `li_menu`  (
                            `id` bigint NOT NULL COMMENT 'ID',
                            `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
                            `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                            `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                            `update_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者',
                            `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                            `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '说明备注',
                            `front_route` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '前端路由',
                            `icon` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '图标',
                            `level` int NULL DEFAULT NULL COMMENT '层级',
                            `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '菜单/权限名称',
                            `parent_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '父id',
                            `path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '赋权API地址,正则表达式',
                            `sort_order` decimal(10, 2) NULL DEFAULT NULL COMMENT '排序值',
                            `title` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '菜单标题',
                            `front_component` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件地址',
                            `permission` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '权限url',
                            PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_menu
-- ----------------------------
INSERT INTO `li_menu` VALUES (1348810750596767744, 'admin', '2021-01-12 09:55:17', b'0', 'admin', '2021-01-15 09:42:50', 'null', '1', 'ios-american-football', 0, 'settings', '0', '1', 2.00, '设置', 'null', NULL);
INSERT INTO `li_menu` VALUES (1348810864748945408, 'admin', '2021-01-12 09:55:45', b'0', 'admin', '2021-03-15 20:57:12', 'null', 'null', 'ios-american-football', 0, 'log', '0', 'null', 3.00, '日志', 'null', '/manager/setting/log*');
INSERT INTO `li_menu` VALUES (1349237129847005184, 'admin', '2021-01-13 14:09:34', b'0', 'admin', '2021-01-15 09:43:16', 'null', 'Main', 'ios-american-football', 1, 'sys', '1348810750596767744', '/sys', 1.00, '系统设置', 'null', NULL);
INSERT INTO `li_menu` VALUES (1349237207378714624, 'admin', '2021-01-13 14:09:53', b'0', 'admin', '2021-07-27 16:07:49', 'null', 'Main', 'ios-american-football', 1, 'member', '1348810750596767744', '/member', 0.00, '用户管理', 'null', '/manager/permission/department*,/manager/passport/user*,/manager/permission/role*,/manager/permission/menu*,/manager/passport/user/admin/edit*');
INSERT INTO `li_menu` VALUES (1349237928434098176, 'admin', '2021-01-13 14:13:03', b'0', 'admin', '2021-07-27 16:09:11', 'null', 'Main', 'ios-american-football', 1, 'log', '1348810864748945408', '/log', 1.00, '系统监控', 'null', '/manager/log*');
INSERT INTO `li_menu` VALUES (1349246048900243456, 'admin', '2021-01-13 14:45:00', b'0', 'admin', '2021-07-27 16:07:57', 'null', 'sys/setting-manage/settingManage', 'ios-american-football', 2, 'setting', '1349237129847005184', 'setting', 1.00, '系统设置', 'null', '/manager/system/setting/get*,/manager/system/setting/put*,/manager/setting/setting*');
INSERT INTO `li_menu` VALUES (1349246347597602816, 'admin', '2021-01-13 14:46:12', b'0', 'admin', '2021-07-27 16:08:03', 'null', 'sys/oss-manage/ossManage', 'ios-american-football', 2, 'oss-manage', '1349237129847005184', 'oss-manage', 3.00, 'OSS资源', '', '/manager/common/file*');
INSERT INTO `li_menu` VALUES (1349246468775239680, 'admin', '2021-01-13 14:46:41', b'0', 'admin', '2021-07-27 16:08:14', 'null', 'region/index', 'ios-american-football', 2, 'region', '1349237129847005184', 'region', 4.00, '行政地区', 'null', '/manager/region*');
INSERT INTO `li_menu` VALUES (1349246671158796288, 'admin', '2021-01-13 14:47:29', b'0', 'admin', '2021-07-27 16:08:09', 'null', 'logistics/index', 'ios-american-football', 2, 'logistics', '1349237129847005184', 'logistics', 5.00, '物流公司', 'null', '/manager/other/logistics*');
INSERT INTO `li_menu` VALUES (1349246896661356544, 'admin', '2021-01-13 14:48:23', b'0', 'admin', '2021-07-27 16:08:23', 'null', 'sys/setting-manage/settingManage', 'ios-american-football', 2, 'authLogin', '1349237129847005184', 'authLogin', 6.00, '信任登录', 'null', '/manager/system/setting/get*,/manager/system/setting/put*,/manager/setting/setting*\r\n');
INSERT INTO `li_menu` VALUES (1349247081504333824, 'admin', '2021-01-13 14:49:07', b'0', 'admin', '2021-07-27 16:08:45', 'null', 'sys/setting-manage/settingManage', 'ios-american-football', 2, 'pay', '1349237129847005184', 'pay', 7.00, '支付设置', 'null', '/manager/system/setting/get*,/manager/system/setting/put*,/manager/system/setting*');
INSERT INTO `li_menu` VALUES (1349247640584085504, 'admin', '2021-01-13 14:51:20', b'0', 'admin', '2021-07-27 16:08:56', 'null', 'sensitive-words/index', 'ios-american-football', 2, 'sensitive-words', '1349237129847005184', 'sensitive-words', 8.00, '敏感词', 'null', '/manager/other/sensitiveWords*');
INSERT INTO `li_menu` VALUES (1349254815809298432, 'admin', '2021-01-13 15:19:51', b'0', 'admin', '2021-01-15 11:15:40', 'null', 'sys/user-manage/userManage', 'ios-american-football', 2, 'user-manage', '1349237207378714624', 'user-manage', 1.00, '用户管理', 'null', NULL);
INSERT INTO `li_menu` VALUES (1349255214977015808, 'admin', '2021-01-13 15:21:26', b'0', 'admin', '2021-01-15 11:16:21', 'null', 'sys/department-manage/departmentManage', 'ios-american-football', 2, 'department-manage', '1349237207378714624', 'department-manage', 3.00, '部门管理', 'null', '/manager/permission/department*,/manager/permission/departmentRole*,');
INSERT INTO `li_menu` VALUES (1349255404425338880, 'admin', '2021-01-13 15:22:11', b'0', 'admin', '2021-02-24 09:22:21', 'null', 'sys/role-manage/roleManage', 'ios-american-football', 2, 'role-manage', '1349237207378714624', 'role-manage', 4.00, '角色权限', 'null', '/manager/permission/role*,/manager/permission/roleMenu*');
INSERT INTO `li_menu` VALUES (1349256082979840000, 'admin', '2021-01-13 15:24:53', b'0', 'admin', '2021-01-15 11:18:14', 'null', 'sys/log-manage/logManage', 'ios-american-football', 2, 'log-manage', '1349237928434098176', 'log-manage', 2.00, '日志管理', 'null', NULL);
INSERT INTO `li_menu` VALUES (1357584224760102912, 'admin', '2021-02-05 06:57:57', b'0', 'admin', '2021-07-27 16:09:02', 'null', 'sys/app-version/appVersion', 'ios-american-football', 2, 'appVersion', '1349237129847005184', 'appVersion', 9.00, 'APP版本', 'null', '/manager/other/appVersion*');
INSERT INTO `li_menu` VALUES (1357873097859923969, 'admin', '2021-02-24 09:53:02', b'0', 'admin', '2021-02-24 09:53:12', NULL, 'sys/menu-manage/menuManage', 'ios-american-football', 2, 'menuManage', '1349237207378714624', 'menu-manage', 2.00, '菜单管理', NULL, NULL);
INSERT INTO `li_menu` VALUES (1367038467288072192, 'admin', '2021-03-03 09:05:44', b'0', 'admin', '2021-03-03 09:09:27', 'null', 'null', 'ios-person-add', 0, 'member', '0', 'null', 0.00, '会员', 'null', NULL);
INSERT INTO `li_menu` VALUES (1367039534616805376, 'admin', '2021-03-03 09:09:58', b'0', 'admin', '2021-05-18 10:51:12', 'null', 'null', 'md-reorder', 0, 'order', '0', 'null', 0.00, '订单', 'null', NULL);
INSERT INTO `li_menu` VALUES (1367039950368800768, 'admin', '2021-03-03 09:11:37', b'0', NULL, NULL, NULL, NULL, 'ios-share', 0, 'goods', '0', NULL, 0.20, '商品', NULL, NULL);
INSERT INTO `li_menu` VALUES (1367040067201138688, 'admin', '2021-03-03 09:12:05', b'0', 'admin', '2021-12-02 19:45:22', NULL, 'null', 'ios-hammer', 0, 'promotions', '0', 'null', 0.30, '促销', NULL, 'null');
INSERT INTO `li_menu` VALUES (1367040599596728320, 'admin', '2021-03-03 09:14:12', b'0', 'admin', '2021-03-03 09:52:13', 'null', 'null', 'ios-color-palette', 0, 'operate', '0', 'null', 0.50, '运营', 'null', NULL);
INSERT INTO `li_menu` VALUES (1367040819248234496, 'admin', '2021-03-03 09:15:04', b'0', 'lili_ftyy', '2022-03-01 15:13:04', NULL, 'null', 'ios-stats', 0, 'statistics', '0', 'null', 0.70, '统计', NULL, 'null');
INSERT INTO `li_menu` VALUES (1367041332861730816, 'admin', '2021-03-03 09:17:07', b'0', NULL, NULL, NULL, 'Main', 'ios-aperture', 1, '/', '1367038467288072192', '/', 0.00, '会员管理', NULL, NULL);
INSERT INTO `li_menu` VALUES (1367041461194850304, 'admin', '2021-03-03 09:17:37', b'0', 'admin', '2021-07-27 16:02:17', NULL, 'member/list/index', 'ios-aperture', 2, 'memberList', '1367041332861730816', 'memberList', 0.00, '会员列表', NULL, '/manager/common/file*,/manager/passport/member*');
INSERT INTO `li_menu` VALUES (1367041575619657728, 'admin', '2021-03-03 09:18:05', b'0', 'admin', '2021-07-27 15:59:50', NULL, 'member/list/memberRecycle', 'ios-aperture', 2, 'memberRecycle', '1367041332861730816', 'memberRecycle', 1.00, '回收站', NULL, '/manager/member*');
INSERT INTO `li_menu` VALUES (1367042490443497472, 'admin', '2021-03-03 09:21:43', b'0', NULL, NULL, NULL, 'Main', 'ios-aperture', 1, '/', '1367038467288072192', '/', 1.00, '预存款', NULL, NULL);
INSERT INTO `li_menu` VALUES (1367042664410644480, 'admin', '2021-03-03 09:22:24', b'0', 'admin', '2021-07-27 16:02:44', 'null', 'member/advance/walletLog', 'ios-aperture', 2, 'walletLog', '1367042490443497472', 'walletLog', 0.00, '会员资金', 'null', '/manager/wallet/log*');
INSERT INTO `li_menu` VALUES (1367042804944994304, 'admin', '2021-03-03 09:22:58', b'0', 'admin', '2021-07-27 16:02:48', NULL, 'member/advance/recharge', 'ios-alert', 2, 'recharge', '1367042490443497472', 'recharge', 1.00, '充值记录', NULL, '/manager/wallet/recharge*');
INSERT INTO `li_menu` VALUES (1367042804944994305, 'admin', '2021-03-03 09:22:58', b'0', 'admin', '2021-07-27 16:02:52', NULL, 'member/advance/withdrawApply', 'ios-alert', 2, 'withdrawApply', '1367042490443497472', 'withdrawApply', 1.00, '提现申请', NULL, '/manager/wallet/withdrawApply*');
INSERT INTO `li_menu` VALUES (1367042917113266176, 'admin', '2021-03-03 09:23:25', b'0', NULL, NULL, NULL, 'Main', 'ios-aperture', 1, 'commont', '1367038467288072192', '/', 0.00, '评价', NULL, NULL);
INSERT INTO `li_menu` VALUES (1367043020976816128, 'admin', '2021-03-03 09:23:49', b'0', 'admin', '2021-07-27 16:02:35', NULL, 'goods/goods-review/index', 'md-aperture', 2, 'goodsReview', '1367042917113266176', 'goodsReview', 0.00, '会员评价', NULL, '/manager/memberEvaluation*');
INSERT INTO `li_menu` VALUES (1367043443917848576, 'admin', '2021-03-03 09:25:30', b'0', 'admin', '2021-07-27 16:03:00', NULL, 'Main', 'md-aperture', 1, 'order', '1367039534616805376', '/', 0.00, '订单', NULL, '/manager/orders*');
INSERT INTO `li_menu` VALUES (1367043505771249664, 'admin', '2021-03-03 09:25:45', b'0', NULL, NULL, NULL, 'Main', 'md-aperture', 1, 'aftersale', '1367039534616805376', '/', 0.00, '售后', NULL, NULL);
INSERT INTO `li_menu` VALUES (1367043642379730944, 'admin', '2021-03-03 09:26:17', b'0', NULL, NULL, NULL, 'order/order/orderList', 'ios-aperture', 2, 'orderList', '1367043443917848576', 'orderList', 0.00, '商品订单', NULL, '/manager/order/order*');
INSERT INTO `li_menu` VALUES (1367043791105556480, 'admin', '2021-03-03 09:26:53', b'0', NULL, NULL, NULL, 'order/order/fictitiousOrderList', 'ios-aperture', 2, 'fictitiousOrderList', '1367043443917848576', 'fictitiousOrderList', 1.00, '虚拟订单', NULL, '/manager/order/order*');
INSERT INTO `li_menu` VALUES (1367043980407078912, 'admin', '2021-03-03 09:27:38', b'0', 'admin', '2021-07-27 16:03:43', NULL, 'order/after-order/afterSaleOrder', 'md-alert', 2, 'afterSaleOrder', '1367043505771249664', 'afterSaleOrder', 0.00, '售后管理', NULL, '/manager/order/afterSale*');
INSERT INTO `li_menu` VALUES (1367044121163726848, 'admin', '2021-03-03 09:28:12', b'0', 'admin', '2021-07-27 16:03:48', NULL, 'order/after-order/orderComplaint', 'md-alert', 2, 'orderComplaint', '1367043505771249664', 'orderComplaint', 2.00, '交易投诉', NULL, '/manager/order/complain*');
INSERT INTO `li_menu` VALUES (1367044247978508288, 'admin', '2021-03-03 09:28:42', b'0', 'admin', '2021-07-27 16:03:52', NULL, 'order/after-order/afterSale', 'md-aperture', 2, 'afterSaleReason', '1367043505771249664', 'afterSaleReason', 3.00, '售后原因', NULL, '/manager/order/afterSaleReason*');
INSERT INTO `li_menu` VALUES (1367044376391319552, 'admin', '2021-03-03 09:29:12', b'0', 'admin', '2021-07-27 16:04:08', NULL, 'Main', 'md-aperture', 1, 'goodsManager', '1367039950368800768', '/', 0.00, '商品管理', NULL, '/manager/goods*');
INSERT INTO `li_menu` VALUES (1367044657296441344, 'admin', '2021-03-03 09:30:19', b'0', NULL, NULL, NULL, 'Main', 'ios-aperture', 1, 'association', '1367039950368800768', '/', 1.00, '关联管理', NULL, NULL);
INSERT INTO `li_menu` VALUES (1367045529720061952, 'admin', '2021-03-03 09:33:47', b'0', 'admin', '2021-07-27 15:38:46', NULL, 'goods/goods-info/goods', 'md-aperture', 2, 'managerGoods', '1367044376391319552', 'managerGoods', 0.00, '平台商品', NULL, 'null');
INSERT INTO `li_menu` VALUES (1367045630710513664, 'admin', '2021-03-03 09:34:11', b'0', 'admin', '2021-07-27 15:38:56', NULL, 'goods/goods-info/goodsApply', 'ios-alert', 2, 'applyGoods', '1367044376391319552', 'applyGoods', 1.00, '商品审核', NULL, 'null');
INSERT INTO `li_menu` VALUES (1367045794284175360, 'admin', '2021-03-03 09:34:50', b'0', 'admin', '2021-07-27 16:04:18', NULL, 'goods/goods-manage/category', 'md-alert', 2, 'goodsCategory', '1367044657296441344', 'goodsCategory', 0.00, '商品分类', NULL, '/manager/goods/category*,/manager/goods/brand*,/manager/goods/spec*,/manager/goods/parameters*');
INSERT INTO `li_menu` VALUES (1367045921434501120, 'admin', '2021-03-03 09:35:21', b'0', 'admin', '2021-07-27 16:04:23', NULL, 'goods/goods-manage/brand', 'md-alert', 2, 'goodsBrand', '1367044657296441344', 'goodsBrand', 1.00, '品牌列表', NULL, '/manager/goods/brand*');
INSERT INTO `li_menu` VALUES (1367046068369358848, 'admin', '2021-03-03 09:35:56', b'0', 'admin', '2021-07-27 16:04:27', NULL, 'goods/goods-manage/spec', 'md-aperture', 2, 'goodsSpec', '1367044657296441344', 'goodsSpec', 2.00, '规格列表', NULL, '/manager/goods/spec*');
INSERT INTO `li_menu` VALUES (1367046266214678528, 'admin', '2021-03-03 09:36:43', b'0', 'admin', '2021-07-27 16:04:32', NULL, 'goods-unit/index', 'md-alert', 2, 'goodsUnit', '1367044657296441344', 'goodsUnit', 4.00, '计量单位', NULL, '/manager/goods/goodsUnit*');
INSERT INTO `li_menu` VALUES (1367048084701315072, 'admin', '2021-03-03 09:43:57', b'0', 'admin', '2021-03-03 09:52:17', 'null', 'null', 'ios-pricetags', 0, 'shop', '0', 'null', 0.40, '店铺', 'null', NULL);
INSERT INTO `li_menu` VALUES (1367048684339986432, 'admin', '2021-03-03 09:46:20', b'0', NULL, NULL, NULL, 'Main', 'md-aperture', 1, 'shopManager', '1367048084701315072', '/', 0.00, '店铺管理', NULL, NULL);
INSERT INTO `li_menu` VALUES (1367048754229673984, 'admin', '2021-03-03 09:46:36', b'0', NULL, NULL, NULL, 'Main', 'md-aperture', 1, 'bill', '1367048084701315072', '/', 0.00, ' 店铺结算', NULL, NULL);
INSERT INTO `li_menu` VALUES (1367048832210173952, 'admin', '2021-03-03 09:46:55', b'0', 'admin', '2021-07-27 16:05:30', NULL, 'seller/shop/shopList', 'md-aperture', 2, 'shopList', '1367048684339986432', 'shopList', 0.00, '店铺列表', NULL, '/manager/order/order*,/manager/store*');
INSERT INTO `li_menu` VALUES (1367048967635861504, 'admin', '2021-03-03 09:47:27', b'0', 'admin', '2021-07-27 16:05:32', NULL, 'seller/shop/shopAuditList', 'md-alert', 2, 'shopAuth', '1367048684339986432', 'shopAuth', 1.00, '店铺审核', NULL, '/manager/store*');
INSERT INTO `li_menu` VALUES (1367049068122996736, 'admin', '2021-03-03 09:47:51', b'0', 'admin', '2021-07-27 16:05:36', NULL, 'seller/bill/bill', 'md-alert', 2, 'billList', '1367048754229673984', 'billList', 0.00, '店铺结算', NULL, '/manager/order/bill*');
INSERT INTO `li_menu` VALUES (1367049214198022144, 'admin', '2021-03-03 09:48:26', b'0', 'admin', '2021-12-02 19:45:28', NULL, 'Main', 'md-aperture', 1, 'promotionsManager', '1367040067201138688', '/', 0.00, '促销管理', NULL, 'null');
INSERT INTO `li_menu` VALUES (1367049384792948736, 'admin', '2021-03-03 09:49:07', b'0', 'admin', '2021-12-02 19:54:12', NULL, 'promotions/coupon/coupon', 'md-alert', 2, 'promotions/coupon', '1367049214198022144', 'promotions/coupon', 0.00, '优惠券', NULL, '/manager/promotion/coupon*');
INSERT INTO `li_menu` VALUES (1367049500782231552, 'admin', '2021-03-03 09:49:34', b'0', 'admin', '2021-12-02 19:41:37', 'null', 'promotions/full-discount/full-discount', 'md-alert', 2, 'promotions/full-discount', '1367049214198022144', 'promotions/full-discount', 1.00, '满额活动', 'null', '/manager/promotion/fullDiscount*');
INSERT INTO `li_menu` VALUES (1367049611578966016, 'admin', '2021-03-03 09:50:01', b'0', 'admin', '2021-12-02 20:16:10', 'null', 'promotions/seckill/seckill', 'md-alert', 2, 'promotions/seckill', '1367049214198022144', 'promotions/seckill', 2.00, '秒杀活动', 'null', '/manager/promotion/seckill*');
INSERT INTO `li_menu` VALUES (1367049712657498112, 'admin', '2021-03-03 09:50:25', b'0', 'admin', '2021-12-02 20:22:04', 'null', 'promotions/pintuan/pintuan', 'md-alert', 2, 'promotions/pintuan', '1367049214198022144', 'promotions/pintuan', 3.00, '拼团活动', 'null', '/manager/promotion/pintuan*');
INSERT INTO `li_menu` VALUES (1367050250249830400, 'admin', '2021-03-03 09:52:33', b'0', 'admin', '2021-03-22 20:38:14', 'null', 'Main', 'md-aperture', 1, 'document', '1367040599596728320', '/', 2.00, '文章管理', 'null', NULL);
INSERT INTO `li_menu` VALUES (1367050320584114176, 'admin', '2021-03-03 09:52:50', b'0', 'admin', '2021-07-27 16:05:49', NULL, 'Main', 'md-aperture', 1, 'floor', '1367040599596728320', '/', 0.00, '楼层装修', NULL, '/manager/pageData*,/manager/file*,/manager/article-category*,/manager/article*,/manager/promotion*,/manager/goods*,/manager/store*');
INSERT INTO `li_menu` VALUES (1367050530030878720, 'admin', '2021-03-03 09:53:40', b'0', 'admin', '2022-10-21 11:12:24', 'null', 'page-decoration/floorList', 'md-alert', 2, 'pcFloor', '1367050320584114176', 'page-decoration/pc', 0.00, 'PC端', 'null', '/manager/other/pageData*');
INSERT INTO `li_menu` VALUES (1367050673312497664, 'admin', '2021-03-03 09:54:14', b'0', 'admin', '2022-10-21 11:13:17', 'null', 'page-decoration/wap/wapList', 'md-aperture', 2, 'wapList', '1367050320584114176', 'page-decoration/wap', 1.00, '移动端', 'null', '/manager/other/pageData*');
INSERT INTO `li_menu` VALUES (1367050829697122304, 'admin', '2021-03-03 09:54:51', b'0', 'admin', '2021-07-27 16:06:32', 'null', 'page/article-manage/hotWords', 'md-aperture', 2, 'hotKeyWord', '1367050250249830400', 'hotKeyWord', 0.00, '搜索热词', 'null', '/manager/hotwords*');
INSERT INTO `li_menu` VALUES (1367050939084570624, 'admin', '2021-03-03 09:55:17', b'0', 'admin', '2021-07-27 16:06:38', NULL, 'page/article-manage/ArticleCategory', 'md-aperture', 2, 'article-category', '1367050250249830400', 'article-category', 1.00, '文章分类', NULL, '/manager/other/articleCategory*');
INSERT INTO `li_menu` VALUES (1367051048232943616, 'admin', '2021-03-03 09:55:43', b'0', 'admin', '2021-07-27 16:06:42', NULL, 'page/article-manage/articleList', 'md-alert', 2, 'articleList', '1367050250249830400', 'articleList', 3.00, '文章管理', NULL, '/manager/other/article*,/manager/other/articleCategory*');
INSERT INTO `li_menu` VALUES (1367052616634204160, 'admin', '2021-03-03 10:01:57', b'0', 'admin', '2021-07-27 16:07:38', NULL, 'Main', 'md-aperture', 1, 'statistics', '1367040819248234496', '/', 0.00, '统计', NULL, '/manager/store*,/manager/member*');
INSERT INTO `li_menu` VALUES (1367052705725415424, 'admin', '2021-03-03 10:02:18', b'0', 'admin', '2021-03-11 22:11:05', 'null', 'statistics/member', 'md-alert', 2, 'memberStatistics', '1367052616634204160', 'memberStatistics', 0.00, '会员统计', 'null', NULL);
INSERT INTO `li_menu` VALUES (1367052805503713280, 'admin', '2021-03-03 10:02:42', b'0', 'admin', '2021-03-11 22:11:14', 'null', 'statistics/order', 'md-alert', 2, 'orderStatistics', '1367052616634204160', 'orderStatistics', 1.00, '订单统计', 'null', NULL);
INSERT INTO `li_menu` VALUES (1367052915314786304, 'admin', '2021-03-03 10:03:08', b'0', 'admin', '2021-03-11 22:11:23', 'null', 'statistics/goods', 'md-alert', 2, 'goodsStatistics', '1367052616634204160', 'goodsStatistics', 2.00, '商品统计', 'null', NULL);
INSERT INTO `li_menu` VALUES (1367053087121866752, 'admin', '2021-03-03 10:03:49', b'0', 'admin', '2021-03-11 22:11:34', 'null', 'statistics/traffic', 'md-alert', 2, 'trafficStatistics', '1367052616634204160', 'trafficStatistics', 4.00, '流量统计', 'null', NULL);
INSERT INTO `li_menu` VALUES (1372807928452481024, 'admin', '2021-03-19 02:11:30', b'0', NULL, NULL, NULL, 'Main', 'ios-aperture', 1, 'flow', '1367039534616805376', '/', 3.00, '流水', NULL, NULL);
INSERT INTO `li_menu` VALUES (1372808148565360640, 'admin', '2021-03-19 02:12:23', b'0', 'admin', '2021-07-27 16:03:57', NULL, 'order/flow/paymentLog', 'md-alert', 2, 'paymentLog', '1372807928452481024', 'paymentLog', 1.00, '收款记录', NULL, '/manager/order/paymentLog*');
INSERT INTO `li_menu` VALUES (1372808352295288832, 'admin', '2021-03-19 02:13:11', b'0', 'admin', '2021-07-27 16:04:01', NULL, 'order/flow/refundLog', 'ios-aperture', 2, 'refundLog', '1372807928452481024', 'refundLog', 2.00, '退款流水', NULL, '/manager/order/refundLog*');
INSERT INTO `li_menu` VALUES (1373166892465782784, 'admin', '2021-03-20 01:57:54', b'0', 'admin', '2021-03-22 20:13:48', 'null', 'Main', 'ios-aperture', 1, '/', '1367038467288072192', '/', 0.00, '积分', 'null', NULL);
INSERT INTO `li_menu` VALUES (1373167227385151488, 'admin', '2021-03-20 01:59:14', b'0', 'admin', '2021-07-27 16:02:40', 'null', 'member/point/point', 'ios-aperture', 2, 'point', '1373166892465782784', 'point', 0.00, '积分历史', 'null', '/manager/member/memberPointsHistory*');
INSERT INTO `li_menu` VALUES (1373791578371391488, 'admin', '2021-03-21 19:20:11', b'0', 'admin', '2021-07-27 16:05:38', NULL, 'seller/bill/accountStatementBill', 'md-alert', 2, 'accountStatementBill', '1367048754229673984', 'accountStatementBill', 0.00, '商家对账', NULL, '/manager/order/bill*');
INSERT INTO `li_menu` VALUES (1374154349697040384, 'admin', '2021-03-22 19:21:42', b'0', 'admin', '2021-07-27 16:06:55', 'null', 'Main', 'md-aperture', 1, 'feedback', '1367040599596728320', '/', 3.00, '意见反馈', 'null', '/manager/other/feedback*');
INSERT INTO `li_menu` VALUES (1374155741123837952, 'admin', '2021-03-22 19:27:14', b'0', 'admin', '2021-07-27 15:41:40', 'null', 'page/feedback/feedback', 'md-aperture', 2, 'feedback', '1374154349697040384', 'feedback', 0.00, '意见反馈', 'null', 'null');
INSERT INTO `li_menu` VALUES (1374173575405109248, 'admin', '2021-03-22 20:38:06', b'0', 'admin', '2021-03-22 20:52:58', 'null', 'Main', 'ios-analytics', 1, 'distributionManager', '1367040599596728320', '/', 1.00, '分销管理', 'null', NULL);
INSERT INTO `li_menu` VALUES (1374177618072436736, 'admin', '2021-03-22 20:54:10', b'0', 'admin', '2021-07-27 16:05:58', 'null', 'distribution/distributionSetting', 'ios-basketball', 2, 'distributionSetting', '1374173575405109248', 'distributionSetting', 0.00, '分销设置', 'null', '/manager/system/setting/put/DISTRIBUTION_SETTING*,/manager/system/setting/get/DISTRIBUTION_SETTING*');
INSERT INTO `li_menu` VALUES (1374177789581721600, 'admin', '2021-03-22 20:54:51', b'0', 'admin', '2021-07-27 16:06:15', 'null', 'distribution/distributionGoods', 'ios-chatbubbles', 2, 'distributionGoods', '1374173575405109248', 'distributionGoods', 3.00, '分销商品', 'null', '/manager/distribution/goods*');
INSERT INTO `li_menu` VALUES (1374177910411231232, 'admin', '2021-03-22 20:55:19', b'0', 'admin', '2021-07-27 16:06:20', 'null', 'distribution/distributionOrder', 'ios-cloudy', 2, 'distributionOrder', '1374173575405109248', 'distributionOrder', 4.00, '分销订单', 'null', '/manager/distribution/order*,/manager/store*');
INSERT INTO `li_menu` VALUES (1374178079181635584, 'admin', '2021-03-22 20:56:00', b'0', 'admin', '2021-07-27 16:06:05', 'null', 'distribution/distributionApply', 'md-egg', 2, 'distributionApply', '1374173575405109248', 'distributionApply', 1.00, '分销申请', 'null', '/manager/distribution*');
INSERT INTO `li_menu` VALUES (1374178303975358464, 'admin', '2021-03-22 20:56:53', b'0', 'admin', '2021-07-27 16:06:08', 'null', 'distribution/distribution', 'md-person', 2, 'distribution', '1374173575405109248', 'distribution', 2.00, '分销员', 'null', '/manager/distribution*');
INSERT INTO `li_menu` VALUES (1374916594269945856, 'admin', '2021-03-24 21:50:35', b'0', 'admin', '2021-07-27 16:08:51', NULL, 'sys/slider/slider', 'ios-aperture', 2, 'slider', '1349237129847005184', 'slider', 7.00, '验证码', NULL, '/manager/other/verificationSource*');
INSERT INTO `li_menu` VALUES (1376450531517530112, 'admin', '2021-03-29 03:25:55', b'0', NULL, NULL, NULL, 'Main', 'md-basketball', 1, 'notice', '1367040599596728320', '/', 5.00, '站内信', NULL, NULL);
INSERT INTO `li_menu` VALUES (1376450662098796544, 'admin', '2021-03-29 03:26:26', b'0', 'admin', '2021-07-27 16:07:23', NULL, 'sys/message/noticeMessageTemplate', 'ios-american-football', 2, 'noticeMessageTemplate', '1376450531517530112', 'noticeMessageTemplate', 1.00, '站内信', NULL, '/manager/other/message*');
INSERT INTO `li_menu` VALUES (1376450766817984512, 'admin', '2021-03-29 03:26:51', b'0', 'admin', '2021-03-29 03:27:25', 'null', 'Main', 'md-checkmark', 1, 'sms', '1367040599596728320', '/', 6.00, '短信管理', 'null', NULL);
INSERT INTO `li_menu` VALUES (1376450876423536640, 'admin', '2021-03-29 03:27:17', b'0', 'admin', '2021-07-27 16:07:29', NULL, 'sys/message/sms', 'ios-timer', 2, 'sms', '1376450766817984512', 'sms', 1.00, '短信', NULL, '/manager/sms/sms*,/manager/passport/member*');
INSERT INTO `li_menu` VALUES (1384035281702748160, 'admin', '2021-04-19 14:45:00', b'0', 'admin', '2021-07-27 16:08:18', 'null', 'member/message-manage/weChatMessageManager', 'md-aperture', 2, 'message-manage', '1349237129847005184', 'message-manage', 5.00, '微信消息', 'null', '/manager/wechat/wechatMessage*');
INSERT INTO `li_menu` VALUES (1403988156444962818, 'admin', '2021-06-13 16:10:36', b'0', 'admin', '2021-12-02 19:54:37', 'null', 'promotions/coupon-activity/coupon', '', 2, 'promotions/coupon-activity', '1367049214198022144', 'promotions/coupon-activity', 0.00, '券活动', 'null', '/manager/promotion/couponActivity*');
INSERT INTO `li_menu` VALUES (1407601962899230721, 'admin', '2021-06-23 15:30:35', b'0', 'admin', '2021-07-27 16:05:08', NULL, 'Main', '', 1, 'liveManage', '1367040067201138688', '/', 2.00, '直播管理', NULL, '/manager/broadcast*');
INSERT INTO `li_menu` VALUES (1407602049759072258, 'admin', '2021-06-23 15:30:55', b'0', 'admin', '2021-12-07 10:54:54', NULL, 'promotions/live/live', '', 2, 'promotions/live', '1407601962899230721', 'promotions/live', 1.00, '直播管理', NULL, 'null');
INSERT INTO `li_menu` VALUES (1407602441964244994, 'admin', '2021-06-23 15:32:29', b'0', NULL, NULL, NULL, 'Main', '', 1, 'pointManage', '1367040067201138688', '/', 3.00, '积分活动', NULL, NULL);
INSERT INTO `li_menu` VALUES (1407602516912263170, 'admin', '2021-06-23 15:32:47', b'0', 'admin', '2021-12-03 19:18:30', NULL, 'promotions/points-goods/points-goods', '', 2, 'promotions/points-goods', '1407602441964244994', 'promotions/points-goods', 1.00, '积分商品', NULL, '/manager/promotion/pointsGoods*,/manager/goods*');
INSERT INTO `li_menu` VALUES (1407602673334636546, 'admin', '2021-06-23 15:33:24', b'0', 'admin', '2021-12-03 19:19:23', NULL, 'promotions/points-goods-category/points-goods-category', '', 2, 'promotions/points-goods-category', '1407602441964244994', 'promotions/points-goods-category', 2.00, '积分分类', NULL, '/manager/promotion/pointsGoodsCategory*');
INSERT INTO `li_menu` VALUES (1410862675914764290, 'admin', '2021-07-02 15:27:29', b'0', 'admin', '2021-07-27 16:06:26', 'null', 'distribution/distributionCash', '', 2, 'distributionCash', '1374173575405109248', 'distributionCash', 5.00, '分销提现', 'null', '/manager/distribution/cash*');
INSERT INTO `li_menu` VALUES (1419926569920536578, 'admin', '2021-07-27 15:44:10', b'0', 'admin', '2022-10-21 11:03:41', NULL, 'custom-words/index', NULL, 2, 'custom-words', '1367050250249830400', 'custom-words', 4.00, 'ES分词', NULL, '/manager/manager/custom-words*');
INSERT INTO `li_menu` VALUES (1430799171593535490, 'admin', '2021-08-26 15:48:00', b'0', 'admin', '2021-12-02 20:21:34', NULL, 'promotions/kanjia/kanjia-activity-goods', NULL, 2, 'promotions/kanjia', '1367049214198022144', 'promotions/kanjia', 6.00, '砍价活动', NULL, '/manager/promotion/kanJiaGoods*');

-- ----------------------------
-- Table structure for li_message
-- ----------------------------
DROP TABLE IF EXISTS `li_message`;
CREATE TABLE `li_message`  (
                               `id` bigint NOT NULL COMMENT 'ID',
                               `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建者',
                               `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                               `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                               `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新者',
                               `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                               `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '内容',
                               `message_range` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '发送范围',
                               `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标题',
                               `create_send` bit(1) NULL DEFAULT NULL,
                               `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                               `message_client` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '发送客户端',
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_message
-- ----------------------------

-- ----------------------------
-- Table structure for li_notice_message
-- ----------------------------
DROP TABLE IF EXISTS `li_notice_message`;
CREATE TABLE `li_notice_message`  (
                                      `id` bigint NOT NULL,
                                      `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                      `create_time` datetime(6) NULL DEFAULT NULL,
                                      `delete_flag` bit(1) NULL DEFAULT NULL,
                                      `update_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                      `update_time` datetime(6) NULL DEFAULT NULL,
                                      `notice_content` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                      `notice_node` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                      `notice_status` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                      `notice_title` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                      `variable` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                      PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_notice_message
-- ----------------------------

-- ----------------------------
-- Table structure for li_notice_message_template
-- ----------------------------
DROP TABLE IF EXISTS `li_notice_message_template`;
CREATE TABLE `li_notice_message_template`  (
                                               `id` bigint NOT NULL COMMENT 'ID',
                                               `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建者',
                                               `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                               `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                               `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新者',
                                               `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                                               `message_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '消息CODE',
                                               `notice_content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '站内信内容',
                                               `notice_state` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '站内信是否开启',
                                               `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '类型',
                                               `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '模板名称',
                                               PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_notice_message_template
-- ----------------------------

-- ----------------------------
-- Table structure for li_order
-- ----------------------------
DROP TABLE IF EXISTS `li_order`;
CREATE TABLE `li_order`  (
                             `id` bigint NOT NULL COMMENT 'ID',
                             `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '创建者',
                             `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                             `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                             `update_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者',
                             `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                             `can_return` bit(1) NULL DEFAULT NULL COMMENT '订单是否支持原路退回',
                             `cancel_reason` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '订单取消原因',
                             `client_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '订单来源',
                             `complete_time` datetime(6) NULL DEFAULT NULL COMMENT '完成时间',
                             `consignee_address_id_path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '地址id \',\'分割',
                             `consignee_address_path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '地址名称  \',\'分割\"',
                             `consignee_detail` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '详细地址',
                             `consignee_mobile` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '收件人手机',
                             `consignee_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '收件人姓名',
                             `deliver_status` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '货运状态',
                             `discount_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '优惠的金额',
                             `flow_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '总价格',
                             `freight_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '运费',
                             `goods_num` int NULL DEFAULT NULL COMMENT '商品数量',
                             `goods_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '商品价格',
                             `logistics_code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '物流公司CODE',
                             `logistics_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '物流公司名称',
                             `logistics_no` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '发货单号',
                             `logistics_time` datetime(6) NULL DEFAULT NULL COMMENT '送货时间',
                             `member_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '会员ID',
                             `member_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户名',
                             `need_receipt` bit(1) NULL DEFAULT NULL COMMENT '是否需要发票',
                             `order_status` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '订单状态',
                             `order_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '订单类型',
                             `parent_order_sn` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '是否为其他订单下的订单，如果是则为依赖订单的sn，否则为空',
                             `pay_order_no` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '支付方式返回的交易号',
                             `pay_status` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '付款状态',
                             `payment_method` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '支付方式',
                             `payment_time` datetime(6) NULL DEFAULT NULL COMMENT '支付时间',
                             `price_detail` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '价格详情',
                             `promotion_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '是否为某订单类型的订单，如果是则为订单类型的id，否则为空',
                             `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '买家订单备注',
                             `store_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '店铺ID',
                             `store_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '店铺名称',
                             `sn` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '订单编号',
                             `trade_sn` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '交易编号 关联Trade',
                             `weight` decimal(10, 2) NULL DEFAULT NULL COMMENT '订单商品总重量',
                             `qr_code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '提货码',
                             `update_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '修改价格',
                             `distribution_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '分销员ID',
                             `delivery_method` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '配送方式',
                             `use_platform_member_coupon_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '使用的平台会员优惠券id',
                             `use_store_member_coupon_ids` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '使用的店铺会员优惠券id(,区分)',
                             `receivable_no` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                             `order_promotion_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                             `verification_code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                             `store_address_path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
                             `store_address_center` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
                             `store_address_mobile` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
                             PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for li_order_complaint
-- ----------------------------
DROP TABLE IF EXISTS `li_order_complaint`;
CREATE TABLE `li_order_complaint`  (
                                       `id` bigint NOT NULL COMMENT 'ID',
                                       `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建者',
                                       `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                       `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                       `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新者',
                                       `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                                       `appeal_content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '申诉商家内容',
                                       `appeal_images` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '申诉商家上传的图片',
                                       `appeal_time` datetime NULL DEFAULT NULL COMMENT '申诉商家时间',
                                       `arbitration_result` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '仲裁结果',
                                       `complain_status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '交易投诉状态',
                                       `complain_topic` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '投诉主题',
                                       `consignee_address_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '收货地址',
                                       `consignee_mobile` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '收货人手机',
                                       `consignee_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '收货人',
                                       `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '投诉内容',
                                       `freight_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '运费',
                                       `goods_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商品ID',
                                       `goods_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商品图片',
                                       `goods_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商品名称',
                                       `goods_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '商品价格',
                                       `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '投诉凭证图片',
                                       `logistics_no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '物流单号',
                                       `member_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '会员ID',
                                       `member_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '会员名称',
                                       `num` int NULL DEFAULT NULL COMMENT '购买的商品数量',
                                       `order_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '订单金额',
                                       `order_sn` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '订单编号',
                                       `order_time` datetime NULL DEFAULT NULL COMMENT '下单时间',
                                       `store_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '店铺ID',
                                       `store_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '店铺名称',
                                       `sku_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '货品ID',
                                       PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_order_complaint
-- ----------------------------

-- ----------------------------
-- Table structure for li_order_complaint_communication
-- ----------------------------
DROP TABLE IF EXISTS `li_order_complaint_communication`;
CREATE TABLE `li_order_complaint_communication`  (
                                                     `id` bigint NOT NULL COMMENT 'ID',
                                                     `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
                                                     `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                                     `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                                     `update_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者',
                                                     `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                                     `complain_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '投诉ID',
                                                     `content` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '对话内容',
                                                     `owner` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '所属，买家/卖家',
                                                     `owner_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '对话所属ID,卖家ID/买家ID',
                                                     `owner_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '对话所属名称',
                                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_order_complaint_communication
-- ----------------------------

-- ----------------------------
-- Table structure for li_order_item
-- ----------------------------
DROP TABLE IF EXISTS `li_order_item`;
CREATE TABLE `li_order_item`  (
                                  `id` bigint NOT NULL COMMENT 'ID',
                                  `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
                                  `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                  `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                  `update_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者',
                                  `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                  `after_sale_status` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '售后状态',
                                  `category_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '分类ID',
                                  `comment_status` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '评论状态',
                                  `complain_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '交易投诉ID',
                                  `complain_status` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '投诉状态',
                                  `flow_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '实际金额',
                                  `goods_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '销售金额',
                                  `goods_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品ID',
                                  `image` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '图片',
                                  `goods_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品名称',
                                  `num` int NULL DEFAULT NULL COMMENT '销售量',
                                  `order_sn` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '订单编号',
                                  `price_detail` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '价格详情',
                                  `promotion_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '促销ID',
                                  `promotion_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '促销类型',
                                  `sku_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '货品ID',
                                  `sn` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '子订单编号',
                                  `snapshot_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '快照ID',
                                  `specs` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '规格json',
                                  `trade_sn` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '交易编号',
                                  `price_fluctuation_ratio` decimal(10, 2) NULL DEFAULT NULL COMMENT '浮动价格比例',
                                  `unit_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '单价',
                                  `sub_total` decimal(10, 2) NULL DEFAULT NULL COMMENT '小记',
                                  `return_goods_number` int NULL DEFAULT 0 COMMENT '已退货数量 ',
                                  `is_goods_number` int NULL DEFAULT 0 COMMENT '正在进行售后的商品数量 ',
                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for li_order_log
-- ----------------------------
DROP TABLE IF EXISTS `li_order_log`;
CREATE TABLE `li_order_log`  (
                                 `id` bigint NOT NULL COMMENT 'ID',
                                 `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
                                 `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                 `message` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '日志信息',
                                 `operator_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作者ID',
                                 `operator_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作者名称',
                                 `operator_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作者类型',
                                 `order_sn` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '订单编号',
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for li_page_data
-- ----------------------------
DROP TABLE IF EXISTS `li_page_data`;
CREATE TABLE `li_page_data`  (
                                 `id` bigint NOT NULL COMMENT 'ID',
                                 `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
                                 `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                 `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                 `update_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者',
                                 `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                 `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '页面名称',
                                 `num` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '值',
                                 `page_client_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '客户端类型',
                                 `page_data` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '页面数据',
                                 `page_show` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '页面开关状态',
                                 `page_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '页面类型',
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_page_data
-- ----------------------------

-- ----------------------------
-- Table structure for li_parameters
-- ----------------------------
DROP TABLE IF EXISTS `li_parameters`;
CREATE TABLE `li_parameters`  (
                                  `id` bigint NOT NULL COMMENT 'ID',
                                  `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
                                  `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                  `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                  `update_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者',
                                  `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                  `category_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '分类ID',
                                  `group_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '参数分组ID',
                                  `is_index` int NULL DEFAULT NULL COMMENT '是否可索引必选',
                                  `options` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '选择值',
                                  `param_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '参数名称',
                                  `param_type` int NULL DEFAULT NULL COMMENT '参数类型',
                                  `required` int NULL DEFAULT NULL COMMENT '是否必填',
                                  `sort` int NULL DEFAULT NULL COMMENT '排序',
                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_parameters
-- ----------------------------
INSERT INTO `li_parameters` VALUES (1379616833291681792, 'admin', '2021-04-06 21:07:39.755000', b'0', 'admin', '2021-05-11 11:07:11.060000', '1348576427264204941', '1379616786822987776', 0, '180天', '保质期', 2, 0, NULL);
INSERT INTO `li_parameters` VALUES (1379616885544321024, 'admin', '2021-04-06 21:07:52.213000', b'0', 'admin', '2021-05-11 11:06:13.544000', '1348576427264204941', '1379616786822987776', 0, '极好', '产品质量', 2, 0, NULL);
INSERT INTO `li_parameters` VALUES (1384437321981493248, 'admin', '2021-04-20 17:22:33.819000', b'0', NULL, NULL, '1348576427268399367', '1384437198903836672', 1, '原味,麻辣,番茄', '口味', 2, 0, NULL);
INSERT INTO `li_parameters` VALUES (1384439159900340224, 'admin', '2021-04-20 17:29:52.013000', b'0', NULL, NULL, '1348576427264204982', '1384439041373503488', 1, '黑色,白色,花色,黄色', '颜色', 2, 0, NULL);
INSERT INTO `li_parameters` VALUES (1384439255744380928, 'admin', '2021-04-20 17:30:14.864000', b'0', NULL, NULL, '1348576427264204982', '1384439041373503488', 1, '8G,16G,32G,64G,128G', '内存', 2, 0, NULL);
INSERT INTO `li_parameters` VALUES (1384439433687728128, 'admin', '2021-04-20 17:30:57.289000', b'0', NULL, NULL, '1348576427264204982', '1384439041373503488', 1, '128G,256G,512G,1T,2T,4T', '容量', 2, 0, NULL);
INSERT INTO `li_parameters` VALUES (1384439631541436416, 'admin', '2021-04-20 17:31:44.461000', b'0', NULL, NULL, '1348576427264204982', '1384439041373503488', 1, '机械硬盘,固态硬盘', '硬盘', 2, 0, NULL);
INSERT INTO `li_parameters` VALUES (1387975061817786368, 'admin', '2021-04-30 11:40:16.714000', b'0', NULL, NULL, '1348576427264204947', '1387974875825569792', 1, '其他,SD卡', '存储介质', 2, 0, NULL);
INSERT INTO `li_parameters` VALUES (1387975197688070144, 'admin', '2021-04-30 11:40:49.118000', b'0', NULL, NULL, '1348576427264204947', '1387974875825569792', 1, '32G,18G,54G,128G', '存储容量', 2, 1, NULL);
INSERT INTO `li_parameters` VALUES (1387975559488733184, 'admin', '2021-04-30 11:42:15.378000', b'0', NULL, NULL, '1348576427264204947', '1387974875825569792', 1, '1100mAh,2000mAh', '电池容量', 2, 0, NULL);
INSERT INTO `li_parameters` VALUES (1387975685796003840, 'admin', '2021-04-30 11:42:45.492000', b'0', NULL, NULL, '1348576427264204947', '1387974875825569792', 1, '55mm,60mm', '滤镜直径', 2, 0, NULL);
INSERT INTO `li_parameters` VALUES (1399163675733917696, 'admin', '2021-05-31 08:39:50.156000', b'0', 'admin', '2021-06-16 19:07:09.634000', '1348576427264204944', '1399163632926851072', 1, NULL, 'asd', NULL, 0, NULL);
INSERT INTO `li_parameters` VALUES (1399163724392038400, 'admin', '2021-05-31 08:40:01.757000', b'0', NULL, NULL, '1348576427264204944', '1399163632926851072', 0, NULL, '123', NULL, 1, NULL);
INSERT INTO `li_parameters` VALUES (1402933168479457282, 'admin', '2021-06-10 18:18:27.263000', b'0', NULL, NULL, '1348576427264204943', '1402933121687801857', 1, '1,2,3,4', '尺寸', NULL, 0, 1);
INSERT INTO `li_parameters` VALUES (1407255206270717954, 'admin', '2021-06-22 16:32:41.372000', b'0', NULL, NULL, '1348576427264204802', '1407255092831571970', 1, '1,2,3,4,5', '大小', NULL, 0, NULL);
INSERT INTO `li_parameters` VALUES (1407255274247802881, 'admin', '2021-06-22 16:32:57.581000', b'0', NULL, NULL, '1348576427264204802', '1407255092831571970', 1, '红,黄,蓝', '颜色', NULL, 1, NULL);
INSERT INTO `li_parameters` VALUES (1414881138368155649, NULL, NULL, NULL, NULL, NULL, '1348576427264204943', '1411185884645330945', 1, '1,4,5,6', '测试', NULL, 0, 10);
INSERT INTO `li_parameters` VALUES (1415225440843698177, NULL, NULL, NULL, NULL, NULL, '1348576427264204947', '1387974875825569792', 1, '其他,SD卡', '其他', NULL, 0, NULL);
INSERT INTO `li_parameters` VALUES (1415226085168484354, NULL, NULL, NULL, NULL, NULL, '1348576427264204947', '1415218780783874049', 0, '对对对,坎坎坷坷,顶顶顶顶,顶顶顶顶 ', '信息', NULL, 0, NULL);
INSERT INTO `li_parameters` VALUES (1415911705582866433, NULL, NULL, NULL, NULL, NULL, '1348576427264204943', '1411185884645330945', 1, '4', '123', NULL, 0, 20);
INSERT INTO `li_parameters` VALUES (1417015729749073921, NULL, NULL, NULL, NULL, NULL, '1348576427264204949', '1417015651999260674', 1, 'haha ,hdhdhdh ', 'dd', NULL, 0, NULL);
INSERT INTO `li_parameters` VALUES (1417016832309960706, NULL, NULL, NULL, NULL, NULL, '1348576427264204943', '1402933121687801857', 1, '4,5,6,1', 'eeee', NULL, 1, 0);
INSERT INTO `li_parameters` VALUES (1418400802180239361, NULL, NULL, NULL, NULL, NULL, '1410121538200788994', '1418400774359420930', 0, '1,2', '11', NULL, 0, NULL);
INSERT INTO `li_parameters` VALUES (1422065540005208066, NULL, NULL, NULL, NULL, NULL, '1348576427264204943', '1402933121687801857', 1, '4,5', 'test', NULL, 1, 2);
INSERT INTO `li_parameters` VALUES (1432251085225050113, NULL, NULL, NULL, NULL, NULL, '1348576427268399683', '1432250905452986369', 1, 'R1,R2,R3,R1 PRO', '兰博基尼', NULL, 0, 201);
INSERT INTO `li_parameters` VALUES (1432252543693602817, NULL, NULL, NULL, NULL, NULL, '1348576427268399683', '1432250905452986369', 1, 'HY,HY1,HY2,HY PRO', '劳斯莱斯', NULL, 0, 30);
INSERT INTO `li_parameters` VALUES (1432252734576377857, NULL, NULL, NULL, NULL, NULL, '1348576427268399683', '1432250905452986369', 1, 'FLL,FLL1,FLL2,FLL PRO', '法拉利', NULL, 0, 40);
INSERT INTO `li_parameters` VALUES (1432253514427514882, NULL, NULL, NULL, NULL, NULL, '1348576427268399683', '1432252790805217282', 1, 'England,Scotland,Wales', 'Britain', NULL, 0, 55);
INSERT INTO `li_parameters` VALUES (1432253764395450370, NULL, NULL, NULL, NULL, NULL, '1348576427268399683', '1432252790805217282', 1, 'East,West', 'Germany', NULL, 0, 100);
INSERT INTO `li_parameters` VALUES (1432254033808179202, NULL, NULL, NULL, NULL, NULL, '1348576427268399683', '1432252790805217282', 1, 'Old,New', 'France', NULL, 0, 90);
INSERT INTO `li_parameters` VALUES (1440577561582112769, NULL, NULL, NULL, NULL, NULL, '1348576427264204943', '1440576600633511937', 0, '1,2,3', 'cc', NULL, 0, 0);
INSERT INTO `li_parameters` VALUES (1440577603638398978, NULL, NULL, NULL, NULL, NULL, '1348576427264204943', '1440576600633511937', 1, '2,3,4,5', 'cc2', NULL, 1, -5);

-- ----------------------------
-- Table structure for li_pintuan
-- ----------------------------
DROP TABLE IF EXISTS `li_pintuan`;
CREATE TABLE `li_pintuan`  (
                               `id` bigint NOT NULL COMMENT 'ID',
                               `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
                               `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                               `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                               `update_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者',
                               `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                               `end_time` datetime(6) NULL DEFAULT NULL COMMENT '活动结束时间',
                               `promotion_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '活动名称',
                               `store_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '店铺ID',
                               `store_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '店铺名称',
                               `start_time` datetime(6) NULL DEFAULT NULL COMMENT '活动开始时间',
                               `fictitious` bit(1) NULL DEFAULT NULL COMMENT '虚拟成团',
                               `limit_num` int NULL DEFAULT NULL COMMENT '限购数量',
                               `pintuan_rule` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '拼团规则',
                               `required_num` int NULL DEFAULT NULL COMMENT '成团人数',
                               `scope_id` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '范围关联的ID',
                               `scope_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'PORTION_GOODS' COMMENT '关联范围类型',
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_pintuan
-- ----------------------------

-- ----------------------------
-- Table structure for li_points_goods
-- ----------------------------
DROP TABLE IF EXISTS `li_points_goods`;
CREATE TABLE `li_points_goods`  (
                                    `id` bigint NOT NULL COMMENT 'ID',
                                    `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建者',
                                    `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                    `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                    `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新者',
                                    `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                                    `end_time` datetime NULL DEFAULT NULL COMMENT '活动结束时间',
                                    `promotion_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '活动名称',
                                    `store_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '店铺ID',
                                    `store_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '店铺名称',
                                    `start_time` datetime NULL DEFAULT NULL COMMENT '活动开始时间',
                                    `active_stock` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '活动库存数量',
                                    `points` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '兑换积分',
                                    `points_goods_category_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '积分商品分类编号',
                                    `points_goods_category_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '分类名称',
                                    `settlement_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '结算价格',
                                    `sku_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商品编号',
                                    `scope_id` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '范围关联的ID',
                                    `scope_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'PORTION_GOODS' COMMENT '关联范围类型',
                                    `original_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '原价',
                                    `thumbnail` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '缩略图',
                                    `goods_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '商品编号',
                                    `goods_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '货品名称',
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_points_goods
-- ----------------------------

-- ----------------------------
-- Table structure for li_points_goods_category
-- ----------------------------
DROP TABLE IF EXISTS `li_points_goods_category`;
CREATE TABLE `li_points_goods_category`  (
                                             `id` bigint NOT NULL COMMENT 'ID',
                                             `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建者',
                                             `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                             `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                             `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新者',
                                             `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                                             `level` int NULL DEFAULT NULL COMMENT '层级',
                                             `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '分类名称',
                                             `parent_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '父ID',
                                             `sort_order` decimal(19, 2) NULL DEFAULT NULL COMMENT '排序值',
                                             PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_points_goods_category
-- ----------------------------

-- ----------------------------
-- Table structure for li_promotion_goods
-- ----------------------------
DROP TABLE IF EXISTS `li_promotion_goods`;
CREATE TABLE `li_promotion_goods`  (
                                       `id` bigint NOT NULL COMMENT 'ID',
                                       `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
                                       `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                       `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                       `update_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者',
                                       `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                       `end_time` datetime(6) NULL DEFAULT NULL COMMENT '活动结束时间',
                                       `goods_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '货品名称',
                                       `limit_num` int NULL DEFAULT NULL COMMENT '限购数量',
                                       `num` int NULL DEFAULT NULL COMMENT '卖出的商品数量',
                                       `price` decimal(10, 2) NULL DEFAULT NULL COMMENT '促销价格',
                                       `promotion_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '活动ID',
                                       `quantity` int NULL DEFAULT NULL COMMENT '促销库存',
                                       `promotion_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '促销工具类型',
                                       `store_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '店铺ID',
                                       `store_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '店铺名称',
                                       `sku_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '货品ID',
                                       `start_time` datetime(6) NULL DEFAULT NULL COMMENT '活动开始时间',
                                       `thumbnail` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '缩略图',
                                       `title` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '活动标题',
                                       `category_path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '分类路径',
                                       `goods_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                       `scope_id` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '范围关联的ID',
                                       `scope_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'PORTION_GOODS' COMMENT '关联范围类型',
                                       `original_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '原价',
                                       `points` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '兑换积分',
                                       `goods_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '商品编号',
                                       PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_promotion_goods
-- ----------------------------

-- ----------------------------
-- Table structure for li_purchase_order
-- ----------------------------
DROP TABLE IF EXISTS `li_purchase_order`;
CREATE TABLE `li_purchase_order`  (
                                      `id` bigint NOT NULL COMMENT 'ID',
                                      `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
                                      `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                      `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                      `update_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者',
                                      `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                      `category_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '分类ID',
                                      `category_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '分类名称',
                                      `consignee_address_id_path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '地址ID',
                                      `consignee_address_path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '地址名称',
                                      `contact_number` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '联系电话',
                                      `contact_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '联系类型',
                                      `contacts` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '联系人',
                                      `deadline` datetime(6) NULL DEFAULT NULL COMMENT '截止时间',
                                      `member_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '供求人',
                                      `need_receipt` bit(1) NULL DEFAULT NULL COMMENT '是否需要发票',
                                      `price_method` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '价格类型',
                                      `receipt_time` datetime(6) NULL DEFAULT NULL COMMENT '收货时间',
                                      `status` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '状态',
                                      `supplement` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '补充说明',
                                      `title` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '标题',
                                      PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_purchase_order
-- ----------------------------

-- ----------------------------
-- Table structure for li_purchase_order_item
-- ----------------------------
DROP TABLE IF EXISTS `li_purchase_order_item`;
CREATE TABLE `li_purchase_order_item`  (
                                           `id` bigint NOT NULL COMMENT 'ID',
                                           `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                           `goods_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品名称',
                                           `goods_unit` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '数量单位',
                                           `images` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '图片',
                                           `num` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '数量',
                                           `price` decimal(10, 2) NULL DEFAULT NULL COMMENT '价格',
                                           `purchase_order_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '采购ID',
                                           `specs` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '规格',
                                           PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_purchase_order_item
-- ----------------------------

-- ----------------------------
-- Table structure for li_purchase_quoted
-- ----------------------------
DROP TABLE IF EXISTS `li_purchase_quoted`;
CREATE TABLE `li_purchase_quoted`  (
                                       `id` bigint NOT NULL COMMENT 'ID',
                                       `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                       `annex` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '附件',
                                       `company_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '公司名称',
                                       `contact_number` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '联系电话',
                                       `contacts` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '联系人',
                                       `context` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '报价说明',
                                       `member_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '报价人',
                                       `purchase_order_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '采购单ID',
                                       `title` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '标题',
                                       PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_purchase_quoted
-- ----------------------------

-- ----------------------------
-- Table structure for li_purchase_quoted_item
-- ----------------------------
DROP TABLE IF EXISTS `li_purchase_quoted_item`;
CREATE TABLE `li_purchase_quoted_item`  (
                                            `id` bigint NOT NULL COMMENT 'ID',
                                            `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                            `purchase_quoted_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '报价单ID',
                                            `goods_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品名称',
                                            `goods_unit` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '数量单位',
                                            `num` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '数量',
                                            `price` decimal(10, 2) NULL DEFAULT NULL COMMENT '价格',
                                            `specs` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '规格',
                                            PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_purchase_quoted_item
-- ----------------------------

-- ----------------------------
-- Table structure for li_receipt
-- ----------------------------
DROP TABLE IF EXISTS `li_receipt`;
CREATE TABLE `li_receipt`  (
                               `id` bigint NOT NULL COMMENT 'ID',
                               `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '创建者',
                               `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                               `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                               `update_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '更新者',
                               `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                               `member_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '会员ID',
                               `member_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '会员名称',
                               `order_sn` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '订单编号',
                               `receipt_content` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '发票内容',
                               `receipt_detail` text CHARACTER SET utf8 COLLATE utf8_bin NULL COMMENT '发票详情',
                               `receipt_price` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '发票金额',
                               `receipt_status` int NULL DEFAULT NULL COMMENT '发票状态',
                               `receipt_title` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '发票抬头',
                               `store_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '店铺ID',
                               `store_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '店铺名称',
                               `taxpayer_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '纳税人识别号',
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_receipt
-- ----------------------------
-- ----------------------------
-- Table structure for li_recharge
-- ----------------------------
DROP TABLE IF EXISTS `li_recharge`;
CREATE TABLE `li_recharge`  (
                                `id` bigint NOT NULL COMMENT 'ID',
                                `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
                                `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                `member_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '会员id',
                                `member_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '会员名称',
                                `pay_status` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '支付状态',
                                `payment_plugin_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '支付插件id',
                                `recharge_money` decimal(10, 2) NULL DEFAULT NULL COMMENT '充值金额',
                                `recharge_sn` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '充值订单编号',
                                `recharge_way` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '充值方式',
                                `pay_time` datetime(6) NULL DEFAULT NULL COMMENT '支付时间',
                                `receivable_no` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_recharge
-- ----------------------------
INSERT INTO `li_recharge` VALUES (1, NULL, '2021-02-27 14:06:05.000000', '123', '天上最闪烁的小星星', 'PAID', NULL, 111, 'Y1365545356611289088', '支付宝', '2021-02-27 14:06:14.000000', NULL);
INSERT INTO `li_recharge` VALUES (1402924240626577410, '15139650614', '2021-06-10 17:42:58.695000', '1402195335947071490', '15139650614', 'CANCEL', NULL, 1, 'Y1402924240625205248', NULL, NULL, NULL);
INSERT INTO `li_recharge` VALUES (1402924241419300865, '15139650614', '2021-06-10 17:42:58.885000', '1402195335947071490', '15139650614', 'CANCEL', NULL, 1, 'Y1402924241426317312', NULL, NULL, NULL);
INSERT INTO `li_recharge` VALUES (1402924248893550594, '15139650614', '2021-06-10 17:43:00.667000', '1402195335947071490', '15139650614', 'CANCEL', NULL, 1, 'Y1402924248900567040', NULL, NULL, NULL);
INSERT INTO `li_recharge` VALUES (1403273644915044354, '2fe433a8aae59f978eb867186bc228a6', '2021-06-11 16:51:23.176000', '1403248387177070594', '2fe433a8aae59f978eb867186bc228a6', 'CANCEL', NULL, 9999, 'Y1403273644943081472', NULL, NULL, NULL);
INSERT INTO `li_recharge` VALUES (1403273689005568001, '2fe433a8aae59f978eb867186bc228a6', '2021-06-11 16:51:33.682000', '1403248387177070594', '2fe433a8aae59f978eb867186bc228a6', 'CANCEL', NULL, 1, 'Y1403273689012633600', NULL, NULL, NULL);
INSERT INTO `li_recharge` VALUES (1403273708328726530, '2fe433a8aae59f978eb867186bc228a6', '2021-06-11 16:51:38.287000', '1403248387177070594', '2fe433a8aae59f978eb867186bc228a6', 'CANCEL', NULL, 1, 'Y1403273708327403520', NULL, NULL, NULL);
INSERT INTO `li_recharge` VALUES (1404994609156001793, '5b7260bc632696dec11b180e7247c428', '2021-06-16 10:49:53.023000', '1404979273362903042', '5b7260bc632696dec11b180e7247c428', 'CANCEL', NULL, 1, 'Y1404994609175592960', NULL, NULL, NULL);
INSERT INTO `li_recharge` VALUES (1404994629829726210, '5b7260bc632696dec11b180e7247c428', '2021-06-16 10:49:57.946000', '1404979273362903042', '5b7260bc632696dec11b180e7247c428', 'CANCEL', NULL, 100, 'Y1404994629828345856', NULL, NULL, NULL);
INSERT INTO `li_recharge` VALUES (1405005955587182593, '13011111111', '2021-06-16 11:34:58.219000', '1376417684140326912', '13011111111', 'CANCEL', NULL, 1000, 'Y1405005955594190848', NULL, NULL, NULL);
INSERT INTO `li_recharge` VALUES (1405006031835435010, '13011111111', '2021-06-16 11:35:16.397000', '1376417684140326912', '13011111111', 'CANCEL', NULL, 100, 'Y1405006031834054656', NULL, NULL, NULL);
INSERT INTO `li_recharge` VALUES (1407271117245509634, '13011111111', '2021-06-22 17:35:54.842000', '1376417684140326912', '13011111111', 'CANCEL', NULL, 1, 'Y1407271117239877632', NULL, NULL, NULL);
INSERT INTO `li_recharge` VALUES (1407271133800427522, '13011111111', '2021-06-22 17:35:58.789000', '1376417684140326912', '13011111111', 'CANCEL', NULL, 1, 'Y1407271133803184128', NULL, NULL, NULL);
INSERT INTO `li_recharge` VALUES (1407271147322863618, '13011111111', '2021-06-22 17:36:02.012000', '1376417684140326912', '13011111111', 'CANCEL', NULL, 1, 'Y1407271147321425920', NULL, NULL, NULL);
INSERT INTO `li_recharge` VALUES (1407271194668167170, '13011111111', '2021-06-22 17:36:13.302000', '1376417684140326912', '13011111111', 'CANCEL', NULL, 1, 'Y1407271194675118080', NULL, NULL, NULL);
INSERT INTO `li_recharge` VALUES (1408267170417549313, '15320614170', '2021-06-25 11:33:52.429000', '1395211997401841664', '15320614170', 'CANCEL', NULL, 1, 'Y1408267170432942080', NULL, NULL, NULL);
INSERT INTO `li_recharge` VALUES (1408267191305183233, '15320614170', '2021-06-25 11:33:57.405000', '1395211997401841664', '15320614170', 'CANCEL', NULL, 2222, 'Y1408267191312187392', NULL, NULL, NULL);
INSERT INTO `li_recharge` VALUES (1408344128396550145, '13011111111', '2021-06-25 16:39:40.637000', '1376417684140326912', '13011111111', 'CANCEL', NULL, 1, 'Y1408344128399343616', NULL, NULL, NULL);
INSERT INTO `li_recharge` VALUES (1408344146394308610, '13011111111', '2021-06-25 16:39:44.929000', '1376417684140326912', '13011111111', 'CANCEL', NULL, 1, 'Y1408344146405490688', NULL, NULL, NULL);
INSERT INTO `li_recharge` VALUES (1408344515618889730, '13011111111', '2021-06-25 16:41:12.958000', '1376417684140326912', '13011111111', 'CANCEL', NULL, 1, 'Y1408344515625877504', NULL, NULL, NULL);
INSERT INTO `li_recharge` VALUES (1409329015496712193, '2fe433a8aae59f978eb867186bc228a6', '2021-06-28 09:53:16.019000', '1403248387177070594', '2fe433a8aae59f978eb867186bc228a6', 'PAID', NULL, 1, 'Y1409329015491133440', 'ALIPAY', '2021-06-28 09:53:35.334000', '2021062822001457321425323874');
INSERT INTO `li_recharge` VALUES (1409330849904926721, '2fe433a8aae59f978eb867186bc228a6', '2021-06-28 10:00:33.384000', '1403248387177070594', '2fe433a8aae59f978eb867186bc228a6', 'PAID', NULL, 1, 'Y1409330849920319488', 'WECHAT', '2021-06-28 10:00:46.962000', '4200001209202106285939058999');
INSERT INTO `li_recharge` VALUES (1409397393737383938, '13011111111', '2021-06-28 14:24:58.667000', '1376417684140326912', '13011111111', 'PAID', NULL, 1, 'Y1409397393757044736', 'WECHAT', '2021-06-28 14:25:37.978000', '4200001224202106284040945433');
INSERT INTO `li_recharge` VALUES (1409400882811109378, '13011111111', '2021-06-28 14:38:50.521000', '1376417684140326912', '13011111111', 'CANCEL', NULL, 1, 'Y1409400882805604352', NULL, NULL, NULL);
INSERT INTO `li_recharge` VALUES (1409401996818251778, '13011111111', '2021-06-28 14:43:16.123000', '1376417684140326912', '13011111111', 'PAID', NULL, 1, 'Y1409401996821135360', 'WECHAT', '2021-06-28 14:43:32.230000', '4200001234202106281140312758');
INSERT INTO `li_recharge` VALUES (1409410406896893954, '13011111111', '2021-06-28 15:16:41.240000', '1376417684140326912', '13011111111', 'CANCEL', NULL, 1, 'Y1409410406895583232', NULL, NULL, NULL);
INSERT INTO `li_recharge` VALUES (1409414117203492866, '13011111111', '2021-06-28 15:31:25.850000', '1376417684140326912', '13011111111', 'CANCEL', NULL, 1, 'Y1409414117210456064', NULL, NULL, NULL);
INSERT INTO `li_recharge` VALUES (1409415369538727938, '13011111111', '2021-06-28 15:36:24.427000', '1376417684140326912', '13011111111', 'CANCEL', NULL, 1, 'Y1409415369520578560', NULL, NULL, NULL);
INSERT INTO `li_recharge` VALUES (1409419177404661762, '13011111111', '2021-06-28 15:51:32.294000', '1376417684140326912', '13011111111', 'CANCEL', NULL, 1, 'Y1409419177390768128', NULL, NULL, NULL);
INSERT INTO `li_recharge` VALUES (1409419410763153409, '13011111111', '2021-06-28 15:52:27.930000', '1376417684140326912', '13011111111', 'CANCEL', NULL, 0.1, 'Y1409419410761842688', NULL, NULL, NULL);
INSERT INTO `li_recharge` VALUES (1409419604888125442, '13011111111', '2021-06-28 15:53:14.212000', '1376417684140326912', '13011111111', 'CANCEL', NULL, 0.1, 'Y1409419604886814720', NULL, NULL, NULL);

-- ----------------------------
-- Table structure for li_refund_log
-- ----------------------------
DROP TABLE IF EXISTS `li_refund_log`;
CREATE TABLE `li_refund_log`  (
                                  `id` bigint NOT NULL COMMENT 'ID',
                                  `after_sale_no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '退单编号',
                                  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                  `error_message` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '退款失败原因',
                                  `is_refund` bit(1) NULL DEFAULT NULL COMMENT '是否已退款',
                                  `pay_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '交易支付金额',
                                  `payment_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '退款方式',
                                  `payment_out_order_no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '支付第三方申请流水号',
                                  `payment_receivable_no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '支付第三方返回流水',
                                  `receivable_no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '第三方返回退款流水号',
                                  `refund_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '退款理由',
                                  `total_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '金额',
                                  `member_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '会员ID',
                                  `order_sn` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '订单编号',
                                  `out_order_no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '退款申请号码',
                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_refund_log
-- ----------------------------
INSERT INTO `li_refund_log` VALUES (1386488004473782272, '订单取消', '2021-04-26 09:11:15', '{\"code\":\"NOT_ENOUGH\",\"message\":\"基本账户余额不足，请充值后重新发起\"}', b'0', 3.00, 'WECHAT', '1386151099693531136', '4200001023202104258384156899', NULL, '订单取消', 3.00, '1377906539959943168', 'O202104251386151095826382849', NULL);
INSERT INTO `li_refund_log` VALUES (1386581215322046464, '订单取消', '2021-04-26 15:21:38', NULL, b'1', 4000.00, 'WALLET', '1385423012433494016', NULL, NULL, '订单取消', 4000.00, '1384135843077160960', 'O202104231385422758841679873', NULL);
INSERT INTO `li_refund_log` VALUES (1386581284997824512, '订单取消', '2021-04-26 15:21:54', NULL, b'1', 3000.00, 'WALLET', '1386580103277838336', NULL, NULL, '订单取消', 3000.00, '1384135843077160960', 'O202104261386579932200566785', NULL);
INSERT INTO `li_refund_log` VALUES (1386581415214186496, '订单取消', '2021-04-26 15:22:25', NULL, b'1', 3000.00, 'WALLET', '1386579453827612672', NULL, NULL, '订单取消', 3000.00, '1384135843077160960', 'O202104261386579409409933313', NULL);
INSERT INTO `li_refund_log` VALUES (1386586606521024512, '订单取消', '2021-04-26 15:43:03', NULL, b'0', 3.00, 'WECHAT', '1386586542050377728', '4200001005202104263246510825', NULL, '订单取消', 3.00, '1384135843077160960', 'O202104261386586535050084353', NULL);
INSERT INTO `li_refund_log` VALUES (1386592473643483136, '订单取消', '2021-04-26 16:06:22', NULL, b'0', 3.00, 'WECHAT', '1386592365111672832', '4200001023202104265699868000', NULL, '订单取消', 3.00, '1384135843077160960', 'O202104261386592358165905409', NULL);
INSERT INTO `li_refund_log` VALUES (1386593015220404224, '订单取消', '2021-04-26 16:08:31', NULL, b'0', 3.00, 'WECHAT', '1386592921066668032', '4200001029202104262624639768', NULL, '订单取消', 3.00, '1384135843077160960', 'O202104261386592919758045184', NULL);
INSERT INTO `li_refund_log` VALUES (1386615191310958592, '订单取消', '2021-04-26 17:36:38', NULL, b'1', 3.00, 'WECHAT', '1386592365111672832', '4200001023202104265699868000', NULL, '订单取消', 3.00, '1384135843077160960', 'O202104261386592358165905409', NULL);
INSERT INTO `li_refund_log` VALUES (1386615348089847808, '订单取消', '2021-04-26 17:37:16', NULL, b'1', 3799.00, 'WALLET', '1386493200847339520', NULL, NULL, '订单取消', 3799.00, '1376417684140326912', 'O202104261386493166466629633', NULL);
INSERT INTO `li_refund_log` VALUES (1386615827414908928, '订单取消', '2021-04-26 17:39:10', NULL, b'1', 3.00, 'WECHAT', '1386592921066668032', '4200001029202104262624639768', NULL, '订单取消', 3.00, '1384135843077160960', 'O202104261386592919758045184', NULL);
INSERT INTO `li_refund_log` VALUES (1386616982362652672, '订单取消', '2021-04-26 17:43:45', NULL, b'1', 3.00, 'WECHAT', '1386586542050377728', '4200001005202104263246510825', NULL, '订单取消', 3.00, '1384135843077160960', 'O202104261386586535050084353', NULL);
INSERT INTO `li_refund_log` VALUES (1386621094030475264, '订单取消', '2021-04-26 18:00:06', NULL, b'1', 3799.00, 'WALLET', '1386492456542928896', NULL, NULL, '订单取消', 3799.00, '1376417684140326912', 'O202104261386492419054239745', NULL);
INSERT INTO `li_refund_log` VALUES (1386855951050473472, '订单取消', '2021-04-27 09:33:20', '{\"code\":\"NOT_ENOUGH\",\"message\":\"基本账户余额不足，请充值后重新发起\"}', b'0', 2.00, 'WECHAT', '1386465007255945216', '4200001003202104265295432535', NULL, '订单取消', 2.00, '1376364798236688384', 'O202104261386465001287450624', NULL);
INSERT INTO `li_refund_log` VALUES (1386948674201845760, 'A202104271386948102102974464', '2021-04-27 15:41:47', NULL, b'0', 16.00, 'WECHAT', '1386947739257929728', '4200001017202104273933963677', NULL, '1357583466371219456', 16.00, '1376417684140326912', 'O202104271386947732861616129', NULL);
INSERT INTO `li_refund_log` VALUES (1386948713296953344, 'A202104271386948102102974464', '2021-04-27 15:41:56', NULL, b'1', 16.00, 'WECHAT', '1386947739257929728', '4200001017202104273933963677', NULL, '1357583466371219456', 16.00, '1376417684140326912', 'O202104271386947732861616129', NULL);
INSERT INTO `li_refund_log` VALUES (1386961406984716288, '订单取消', '2021-04-27 16:32:23', NULL, b'1', 170.00, 'ALIPAY', '1386961231088189440', '2021042722001436931402046925', NULL, '订单取消', 170.00, '1376417684140326912', 'O202104271386961176205721601', NULL);
INSERT INTO `li_refund_log` VALUES (1387216659923599360, '订单取消', '2021-04-28 09:26:40', NULL, b'0', 1.00, 'WECHAT', '1387215482582466560', '4200001010202104283961532490', NULL, '订单取消', 1.00, '1376364798236688384', 'O202104281387214477874692097', NULL);
INSERT INTO `li_refund_log` VALUES (1387216700843229184, '订单取消', '2021-04-28 09:26:49', NULL, b'1', 1.00, 'WECHAT', '1387215482582466560', '4200001010202104283961532490', NULL, '订单取消', 1.00, '1376364798236688384', 'O202104281387214477874692097', NULL);
INSERT INTO `li_refund_log` VALUES (1387252013569409024, '订单取消', '2021-04-28 11:47:09', NULL, b'0', 3.00, 'WECHAT', NULL, '4200001011202104285582566409', NULL, '订单取消', 3.00, '1384135843077160960', 'O202104281387251957692891137', NULL);
INSERT INTO `li_refund_log` VALUES (1387252052794540032, '订单取消', '2021-04-28 11:47:18', NULL, b'1', 3.00, 'WECHAT', NULL, '4200001011202104285582566409', NULL, '订单取消', 3.00, '1384135843077160960', 'O202104281387251957692891137', NULL);
INSERT INTO `li_refund_log` VALUES (1387254902312402944, '订单取消', '2021-04-28 11:58:37', '{\"code\":\"PARAM_ERROR\",\"message\":\"微信订单号非法\"}', b'0', 3.00, 'WECHAT', NULL, '2021042822001457321444326679', NULL, '订单取消', 3.00, '1385494981749243904', 'O202104281387254804811612161', NULL);
INSERT INTO `li_refund_log` VALUES (1387297180414377984, '订单取消', '2021-04-28 14:46:37', NULL, b'1', 3.00, 'ALIPAY', NULL, '2021042822001457321443609959', NULL, '订单取消', 3.00, '1385494981749243904', 'O202104281387293217954725889', NULL);
INSERT INTO `li_refund_log` VALUES (1387324563712376832, '订单取消', '2021-04-28 16:35:26', NULL, b'0', 3.00, 'WECHAT', NULL, '4200001001202104285231004651', NULL, '订单取消', 3.00, '1376364798236688384', 'O202104281387320620890456065', NULL);
INSERT INTO `li_refund_log` VALUES (1387577947530985472, '订单取消', '2021-04-29 09:22:17', NULL, b'1', 3.00, 'ALIPAY', NULL, '2021042922001457321444311707', NULL, '订单取消', 3.00, '1385494981749243904', 'O202104291387577848532828161', NULL);
INSERT INTO `li_refund_log` VALUES (1387578138325680128, '订单取消', '2021-04-29 09:23:03', NULL, b'0', 3.00, 'WECHAT', NULL, '4200001011202104293892507547', NULL, '订单取消', 3.00, '1385494981749243904', 'O202104291387578061255344129', NULL);
INSERT INTO `li_refund_log` VALUES (1387589246830772224, '订单取消', '2021-04-29 10:07:11', NULL, b'1', 3.00, 'WECHAT', NULL, '4200001001202104285231004651', '50300908172021042808334819993', '订单取消', 3.00, '1376364798236688384', 'O202104281387320620890456065', NULL);
INSERT INTO `li_refund_log` VALUES (1387589497188777984, '订单取消', '2021-04-29 10:08:11', NULL, b'0', 3.00, 'WECHAT', NULL, '4200001009202104297508531692', NULL, '订单取消', 3.00, '1385494981749243904', 'O202104291387589318452707329', NULL);
INSERT INTO `li_refund_log` VALUES (1387589506537881600, '订单取消', '2021-04-29 10:08:13', NULL, b'1', 3.00, 'ALIPAY', NULL, '2021042922001457321444743227', '2021042922001457321444743227', '订单取消', 3.00, '1385494981749243904', 'O202104291387589318029082625', NULL);
INSERT INTO `li_refund_log` VALUES (1387589537315684352, '订单取消', '2021-04-29 10:08:21', NULL, b'1', 3.00, 'WECHAT', NULL, '4200001009202104297508531692', '50301108172021042908356934887', '订单取消', 3.00, '1385494981749243904', 'O202104291387589318452707329', NULL);
INSERT INTO `li_refund_log` VALUES (1387589554537496576, '订单取消', '2021-04-29 10:08:25', NULL, b'1', 3.00, 'WECHAT', NULL, '4200001011202104293892507547', '50301508022021042908361627624', '订单取消', 3.00, '1385494981749243904', 'O202104291387578061255344129', NULL);
INSERT INTO `li_refund_log` VALUES (1387600785096835072, '订单取消', '2021-04-29 10:53:02', NULL, b'0', 3.00, 'WECHAT', NULL, '4200001000202104290917727506', NULL, '订单取消', 3.00, '1385494981749243904', 'O202104291387600605068918785', NULL);
INSERT INTO `li_refund_log` VALUES (1387600800875806720, '订单取消', '2021-04-29 10:53:06', NULL, b'1', 3.00, 'ALIPAY', NULL, '2021042922001457321444344331', '2021042922001457321444344331', '订单取消', 3.00, '1385494981749243904', 'O202104291387600604611739649', NULL);
INSERT INTO `li_refund_log` VALUES (1387600823369859072, '订单取消', '2021-04-29 10:53:11', NULL, b'1', 3.00, 'WECHAT', NULL, '4200001000202104290917727506', '50300908142021042908355791717', '订单取消', 3.00, '1385494981749243904', 'O202104291387600605068918785', NULL);
INSERT INTO `li_refund_log` VALUES (1387605994841833472, '订单取消', '2021-04-29 11:13:44', NULL, b'1', 3.00, 'ALIPAY', NULL, '2021042922001457321444708196', 'AF1387605986868461569', '订单取消', 3.00, '1385494981749243904', 'O202104291387600599930896385', 'AF1387605986868461569');
INSERT INTO `li_refund_log` VALUES (1387606004128022528, '订单取消', '2021-04-29 11:13:47', NULL, b'0', 3.00, 'WECHAT', NULL, '4200001010202104293719636599', NULL, '订单取消', 3.00, '1385494981749243904', 'O202104291387600599368859649', 'AF1387605999958884353');
INSERT INTO `li_refund_log` VALUES (1387606044401729536, '订单取消', '2021-04-29 11:13:56', NULL, b'1', 3.00, 'WECHAT', NULL, '4200001010202104293719636599', '50301408112021042908355812742', '订单取消', 3.00, '1385494981749243904', 'O202104291387600599368859649', 'AF1387605999958884353');
INSERT INTO `li_refund_log` VALUES (1387608850680512512, 'A202104291387608106606788608', '2021-04-29 11:25:05', NULL, b'0', 3.00, 'WECHAT', NULL, '4200001027202104298055615171', NULL, '不按时发货', 3.00, '1384135843077160960', 'O202104291387607090087854081', 'AF1387608845802536960');
INSERT INTO `li_refund_log` VALUES (1387608890413154304, 'A202104291387608106606788608', '2021-04-29 11:25:15', NULL, b'1', 3.00, 'WECHAT', NULL, '4200001027202104298055615171', '50301308132021042908359398691', '不按时发货', 3.00, '1384135843077160960', 'O202104291387607090087854081', 'AF1387608845802536960');
INSERT INTO `li_refund_log` VALUES (1387611290817527808, 'A202104291387611235486269440', '2021-04-29 11:34:47', NULL, b'0', 3.00, 'WECHAT', NULL, '4200001025202104292228443300', NULL, '地址或商品选择错误', 3.00, '1384135843077160960', 'O202104291387610983786086401', 'AF1387611287369809920');
INSERT INTO `li_refund_log` VALUES (1387611331808460800, 'A202104291387611235486269440', '2021-04-29 11:34:57', NULL, b'1', 3.00, 'WECHAT', NULL, '4200001025202104292228443300', '50301108102021042908360591605', '地址或商品选择错误', 3.00, '1384135843077160960', 'O202104291387610983786086401', 'AF1387611287369809920');
INSERT INTO `li_refund_log` VALUES (1390106631500464128, '订单取消', '2021-05-06 08:50:23', NULL, b'0', 3.00, 'WECHAT', NULL, '4200001015202105067150915456', NULL, '订单取消', 3.00, '1384135843077160960', 'O202105061390106496418709504', 'AF1390106627306160129');
INSERT INTO `li_refund_log` VALUES (1390106670150975488, '订单取消', '2021-05-06 08:50:32', NULL, b'1', 3.00, 'WECHAT', NULL, '4200001015202105067150915456', '50301308252021050608562029290', '订单取消', 3.00, '1384135843077160960', 'O202105061390106496418709504', 'AF1390106627306160129');
INSERT INTO `li_refund_log` VALUES (1390481007903244288, '订单取消', '2021-05-07 09:38:01', NULL, b'0', 3.00, 'WECHAT', NULL, '4200001024202105076730856050', NULL, '订单取消', 3.00, '1384135843077160960', 'O202105071390480929452982273', 'AF1390481003784437761');
INSERT INTO `li_refund_log` VALUES (1390481046310486016, '订单取消', '2021-05-07 09:38:10', NULL, b'1', 3.00, 'WECHAT', NULL, '4200001024202105076730856050', '50301108052021050708610820690', '订单取消', 3.00, '1384135843077160960', 'O202105071390480929452982273', 'AF1390481003784437761');
INSERT INTO `li_refund_log` VALUES (1390481752677416960, 'A202105071390481609500655616', '2021-05-07 09:40:58', NULL, b'0', 3.00, 'WECHAT', NULL, '4200001021202105071411946203', NULL, '不按时发货', 3.00, '1384135843077160960', 'O202105071390481256625471489', 'AF1390481748986429440');
INSERT INTO `li_refund_log` VALUES (1390481794335244288, 'A202105071390481609500655616', '2021-05-07 09:41:08', NULL, b'1', 3.00, 'WECHAT', NULL, '4200001021202105071411946203', '50301108282021050708603001528', '不按时发货', 3.00, '1384135843077160960', 'O202105071390481256625471489', 'AF1390481748986429440');
INSERT INTO `li_refund_log` VALUES (1390492238391279616, 'A202105071390482961383555072', '2021-05-07 10:22:38', '{\"code\":\"INVALID_REQUEST\",\"message\":\"订单金额或退款金额与之前请求不一致，请核实后再试\"}', b'0', 2.00, 'WECHAT', NULL, '4200001005202105077801546387', NULL, '不按时发货', 2.00, '1384135843077160960', 'O202105071390482669317390337', 'AF1390492236839387136');
INSERT INTO `li_refund_log` VALUES (1390493675225612288, 'A202105071390482359786143744', '2021-05-07 10:28:21', NULL, b'0', 3.00, 'WECHAT', NULL, '4200001013202105079382328010', NULL, '不合适', 3.00, '1384135843077160960', 'O202105071390481922135687169', 'AF1390493671899529216');
INSERT INTO `li_refund_log` VALUES (1390493696499122176, 'A202105071390493092192190464', '2021-05-07 10:28:26', '{\"code\":\"INVALID_REQUEST\",\"message\":\"订单金额或退款金额与之前请求不一致，请核实后再试\"}', b'0', 2.00, 'WECHAT', NULL, '4200001019202105070974717240', NULL, '不合适', 2.00, '1384135843077160960', 'O202105071390492855209820161', 'AF1390493695253413888');
INSERT INTO `li_refund_log` VALUES (1390493714870173696, 'A202105071390482359786143744', '2021-05-07 10:28:30', NULL, b'1', 3.00, 'WECHAT', NULL, '4200001013202105079382328010', '50301608012021050708606665271', '不合适', 3.00, '1384135843077160960', 'O202105071390481922135687169', 'AF1390493671899529216');
INSERT INTO `li_refund_log` VALUES (1395287431632125952, 'A202104271386892648505147392', '2021-05-20 15:57:02', NULL, b'1', 2999.00, 'BANK_TRANSFER', NULL, NULL, NULL, '1357583466371219456', 2999.00, '1376417684140326912', 'O202104271386889620351877122', 'AF1395287431581794304');
INSERT INTO `li_refund_log` VALUES (1395653490608439296, '订单取消', '2021-05-21 16:11:37', NULL, b'0', 39.00, 'WECHAT', NULL, '4200001023202105214324760435', NULL, '订单取消', 39.00, '1376364798236688384', 'O202105211395557137412784129', 'AF1395653485503971329');
INSERT INTO `li_refund_log` VALUES (1395653531964276736, '订单取消', '2021-05-21 16:11:47', NULL, b'1', 39.00, 'WECHAT', NULL, '4200001023202105214324760435', '50301408112021052108978385756', '订单取消', 39.00, '1376364798236688384', 'O202105211395557137412784129', 'AF1395653485503971329');
INSERT INTO `li_refund_log` VALUES (1395657568709246976, 'A202105211395657461746106368', '2021-05-21 16:27:49', NULL, b'0', 37.00, 'WECHAT', NULL, '4200001026202105217591450791', NULL, '不按时发货', 37.00, '1376364798236688384', 'O202105211395654669073645569', 'AF1395657563852242944');
INSERT INTO `li_refund_log` VALUES (1395657605384241152, 'A202105211395657504809025536', '2021-05-21 16:27:58', NULL, b'0', 37.00, 'WECHAT', NULL, '4200001003202105218313713982', NULL, '不按时发货', 37.00, '1376364798236688384', 'O202105211395653910416326657', 'AF1395657601986854912');
INSERT INTO `li_refund_log` VALUES (1395657613701545984, 'A202105211395657461746106368', '2021-05-21 16:28:00', NULL, b'1', 37.00, 'WECHAT', NULL, '4200001026202105217591450791', '50301508232021052108977795087', '不按时发货', 37.00, '1376364798236688384', 'O202105211395654669073645569', 'AF1395657563852242944');
INSERT INTO `li_refund_log` VALUES (1395657635109273600, 'A202105211395657375104368640', '2021-05-21 16:28:05', NULL, b'0', 29.90, 'WECHAT', NULL, '4200001013202105214248915244', NULL, '不按时发货', 29.90, '1376364798236688384', 'O202105211395655277335805953', 'AF1395657631657361408');
INSERT INTO `li_refund_log` VALUES (1395657648992419840, 'A202105211395657504809025536', '2021-05-21 16:28:08', NULL, b'1', 37.00, 'WECHAT', NULL, '4200001003202105218313713982', '50301208062021052108995285709', '不按时发货', 37.00, '1376364798236688384', 'O202105211395653910416326657', 'AF1395657601986854912');
INSERT INTO `li_refund_log` VALUES (1395657678662926336, 'A202105211395657375104368640', '2021-05-21 16:28:15', NULL, b'1', 29.90, 'WECHAT', NULL, '4200001013202105214248915244', '50301008042021052108989966208', '不按时发货', 29.90, '1376364798236688384', 'O202105211395655277335805953', 'AF1395657631657361408');
INSERT INTO `li_refund_log` VALUES (1395661300863860736, 'A202105211395661242416234496', '2021-05-21 16:42:39', NULL, b'0', 29.90, 'WECHAT', NULL, '4200001026202105211729604122', NULL, '不按时发货', 29.90, '1376364798236688384', 'O202105211395658056464859137', 'AF1395661296967352320');
INSERT INTO `li_refund_log` VALUES (1395661345621278720, 'A202105211395661242416234496', '2021-05-21 16:42:50', NULL, b'1', 29.90, 'WECHAT', NULL, '4200001026202105211729604122', '50301008282021052108997636716', '不按时发货', 29.90, '1376364798236688384', 'O202105211395658056464859137', 'AF1395661296967352320');
INSERT INTO `li_refund_log` VALUES (1400024326471680000, '订单取消', '2021-06-02 17:39:45', NULL, b'0', 21.00, 'WECHAT', NULL, '4200001029202106024317410324', NULL, '订单取消', 21.00, '1376364798236688384', 'O202106021400024226542387201', 'AF1400024322331901953');
INSERT INTO `li_refund_log` VALUES (1400024388396384256, '订单取消', '2021-06-02 17:40:00', NULL, b'1', 21.00, 'WECHAT', NULL, '4200001029202106024317410324', '50301408402021060209362064227', '订单取消', 21.00, '1376364798236688384', 'O202106021400024226542387201', 'AF1400024322331901953');
INSERT INTO `li_refund_log` VALUES (1400024696203771904, '订单取消', '2021-06-02 17:41:13', NULL, b'0', 21.00, 'WECHAT', NULL, '4200001017202106023180174792', NULL, '订单取消', 21.00, '1376364798236688384', 'O202106021400024452879613953', 'AF1400024692462452737');
INSERT INTO `li_refund_log` VALUES (1400024758115893248, '订单取消', '2021-06-02 17:41:28', NULL, b'1', 21.00, 'WECHAT', NULL, '4200001017202106023180174792', '50300908392021060209359694141', '订单取消', 21.00, '1376364798236688384', 'O202106021400024452879613953', 'AF1400024692462452737');
INSERT INTO `li_refund_log` VALUES (1400250853801066496, '订单取消', '2021-06-03 08:39:54', NULL, b'0', 15.00, 'WECHAT', NULL, '4200001004202106035882854931', NULL, '订单取消', 15.00, '1376364798236688384', 'O202106031400250827628609537', 'AF1400250849824866305');
INSERT INTO `li_refund_log` VALUES (1400251054922137600, '订单取消', '2021-06-03 08:40:42', NULL, b'1', 15.00, 'WECHAT', NULL, '4200001004202106035882854931', '50301208532021060309373312581', '订单取消', 15.00, '1376364798236688384', 'O202106031400250827628609537', 'AF1400250849824866305');
INSERT INTO `li_refund_log` VALUES (1400251094608642048, '订单取消', '2021-06-03 08:40:51', NULL, b'0', 36.00, 'WECHAT', NULL, '4200001028202106031075277920', NULL, '订单取消', 36.00, '1376364798236688384', 'O202106031400250521595412481', 'AF1400251090724716545');
INSERT INTO `li_refund_log` VALUES (1400251120126787584, '订单取消', '2021-06-03 08:40:57', NULL, b'0', 15.00, 'WECHAT', NULL, '4200001019202106032769994809', NULL, '订单取消', 15.00, '1376364798236688384', 'O202106031400250405270585345', 'AF1400251116737789953');
INSERT INTO `li_refund_log` VALUES (1400251159020568576, '订单取消', '2021-06-03 08:41:06', NULL, b'1', 36.00, 'WECHAT', NULL, '4200001028202106031075277920', '50300808482021060309372103552', '订单取消', 36.00, '1376364798236688384', 'O202106031400250521595412481', 'AF1400251090724716545');
INSERT INTO `li_refund_log` VALUES (1400251182353481728, '订单取消', '2021-06-03 08:41:12', NULL, b'1', 15.00, 'WECHAT', NULL, '4200001019202106032769994809', '50301308562021060309374481652', '订单取消', 15.00, '1376364798236688384', 'O202106031400250405270585345', 'AF1400251116737789953');
INSERT INTO `li_refund_log` VALUES (1400252132996677632, 'A202106031400252071189413888', '2021-06-03 08:44:59', NULL, b'0', 299.00, 'WECHAT', NULL, '4200001010202106033323134706', NULL, '不按时发货', 299.00, '1376364798236688384', 'O202106031400251338159292417', 'AF1400252127565053952');
INSERT INTO `li_refund_log` VALUES (1400252190135681024, 'A202106031400252071189413888', '2021-06-03 08:45:12', NULL, b'1', 299.00, 'WECHAT', NULL, '4200001010202106033323134706', '50300908622021060309370331605', '不按时发货', 299.00, '1376364798236688384', 'O202106031400251338159292417', 'AF1400252127565053952');
INSERT INTO `li_refund_log` VALUES (1401724504199135232, '订单取消', '2021-06-07 10:15:39', NULL, b'0', 25.00, 'WECHAT', NULL, '4200001023202106074156154516', NULL, '订单取消', 25.00, '1376364798236688384', 'O202106071401724395113676801', 'AF1401724500264878081');
INSERT INTO `li_refund_log` VALUES (1401724565704409088, '订单取消', '2021-06-07 10:15:54', NULL, b'1', 25.00, 'WECHAT', NULL, '4200001023202106074156154516', '50301008592021060709466762906', '订单取消', 25.00, '1376364798236688384', 'O202106071401724395113676801', 'AF1401724500264878081');
INSERT INTO `li_refund_log` VALUES (1402434888519716866, '订单取消', '2021-06-09 09:18:28', NULL, b'0', 15.00, 'WECHAT', NULL, '4200001025202106095687436344', NULL, '订单取消', 15.00, '1376364798236688384', 'O202106091402434790421954561', 'AF1402434883950739457');
INSERT INTO `li_refund_log` VALUES (1402450059359252482, '订单取消', '2021-06-09 10:18:45', NULL, b'1', 15.00, 'WALLET', NULL, NULL, NULL, '订单取消', 15.00, '1384135843077160960', 'O202106091402427268868866049', 'AF1402450058011475969');
INSERT INTO `li_refund_log` VALUES (1403250591669706754, 'A202104261386516836345970688', '2021-06-11 15:19:47', '{\"code\":\"PARAM_ERROR\",\"message\":\"transaction_id and out_trade_no both empty\"}', b'0', 8.00, 'WECHAT', NULL, NULL, NULL, '不按时发货', 8.00, '1376364798236688384', 'O202104261386513842112036865', 'AF1403250588954656768');
INSERT INTO `li_refund_log` VALUES (1403250595960479745, 'A202104261386516836345970688', '2021-06-11 15:19:48', '{\"code\":\"PARAM_ERROR\",\"message\":\"transaction_id and out_trade_no both empty\"}', b'0', 8.00, 'WECHAT', NULL, NULL, NULL, '不按时发货', 8.00, '1376364798236688384', 'O202104261386513842112036865', 'AF1403250595090923520');
INSERT INTO `li_refund_log` VALUES (1404994888077221889, '订单取消', '2021-06-16 10:51:00', NULL, b'0', 20.00, 'WECHAT', NULL, '4200001222202106161500701209', NULL, '订单取消', 20.00, '1384135843077160960', 'O202106161404994817045299201', 'AF1404994883793453057');
INSERT INTO `li_refund_log` VALUES (1405447699491573762, '订单取消', '2021-06-17 16:50:18', NULL, b'1', 25.00, 'ALIPAY', NULL, '2021061722001457321417636126', 'AF1405447692527599617', '订单取消', 25.00, '1384135843077160960', 'O202106171405447477087174657', 'AF1405447692527599617');
INSERT INTO `li_refund_log` VALUES (1406786576567357442, '订单取消', '2021-06-21 09:30:31', NULL, b'1', 37.00, 'ALIPAY', NULL, '2021062122001457321419936331', 'AF1406786573076267009', '订单取消', 37.00, '1385494981749243904', 'O202106211406786438460080129', 'AF1406786573076267009');
INSERT INTO `li_refund_log` VALUES (1408270864915722242, '订单取消', '2021-06-25 11:48:33', NULL, b'1', 1.00, 'WECHAT', NULL, '4200001139202106250928461209', '50301308722021062510096270251', '订单取消', 1.00, '1376417684140326912', 'O202106251408267610356711425', 'AF1408270859512053761');
INSERT INTO `li_refund_log` VALUES (1409694829882171393, '订单取消', '2021-06-29 10:06:53', NULL, b'1', 1772.00, 'WALLET', NULL, NULL, NULL, '订单取消', 1772.00, '1376417684140326912', 'O202106291409694687438045185', 'AF1409694829419429889');
INSERT INTO `li_refund_log` VALUES (1410097883097862146, '订单取消', '2021-06-30 12:48:28', NULL, b'1', 14.00, 'WECHAT', NULL, '4200001117202106307976888291', '50301508932021063010227494299', '订单取消', 14.00, '1409816096790626305', 'O202106301410019899295662081', 'AF1410097877790752769');
INSERT INTO `li_refund_log` VALUES (1410881610463887362, '订单取消', '2021-07-02 16:42:44', NULL, b'1', 12.00, 'WECHAT', NULL, '4200001129202107028493058519', '50301108912021070210305663913', '订单取消', 12.00, '1409816096790626305', 'O202107021410880586846830593', 'AF1410881606024953857');
INSERT INTO `li_refund_log` VALUES (1411164913590910977, '订单取消', '2021-07-03 11:28:28', NULL, b'1', 3000.00, 'WALLET', NULL, NULL, NULL, '订单取消', 3000.00, '1376417684140326912', 'O202107031411144844838961153', 'AF1411164913203675137');
INSERT INTO `li_refund_log` VALUES (1411935158828072961, 'A202107051411934979356622848', '2021-07-05 14:29:09', NULL, b'1', 2.00, 'WALLET', NULL, NULL, NULL, '1357582072222318592', 2.00, '1376417684140326912', 'O202107041411532347098005504', 'AF1411935158503735296');
INSERT INTO `li_refund_log` VALUES (1412614604555886594, '订单取消', '2021-07-07 11:29:01', NULL, b'1', 1.00, 'WECHAT', NULL, '4200001130202107071332611546', '50301408662021070710420060245', '订单取消', 1.00, '1376364798236688384', 'O202107071412605671580368897', 'AF1412614599143849985');
INSERT INTO `li_refund_log` VALUES (1412701766395719681, '订单取消', '2021-07-07 17:15:23', NULL, b'1', 36.00, 'WECHAT', NULL, '4200001223202107072678703317', '50300808912021070710440697744', '订单取消', 36.00, '1376364798236688384', 'O202107071412701576656584705', 'AF1412701762111930369');
INSERT INTO `li_refund_log` VALUES (1412950325141565442, 'A202107071412719349466136576', '2021-07-08 09:43:04', NULL, b'1', 37.00, 'WECHAT', NULL, '4200001123202107076730546168', '50301008882021070810457507735', '不按时发货', 37.00, '1376364798236688384', 'O202107071412701873290346497', 'AF1412950320648028160');
INSERT INTO `li_refund_log` VALUES (1413404460567609345, 'A202107071412719209405743104', '2021-07-09 15:47:38', NULL, b'1', 37.00, 'WECHAT', NULL, '4200001208202107079167304833', '50300808832021070910494151600', '不按时发货', 37.00, '1376364798236688384', 'O202107071412705174660579329', 'AF1413404454648020992');
INSERT INTO `li_refund_log` VALUES (1414508434888704001, '订单取消', '2021-07-12 16:54:26', NULL, b'1', 10229.00, 'WALLET', NULL, NULL, NULL, '订单取消', 10229.00, '1376417684140326912', 'O202107121414508194838872065', 'AF1414508434354601985');
INSERT INTO `li_refund_log` VALUES (1414570606478069761, 'A202107121414570108591800320', '2021-07-12 21:01:29', NULL, b'1', 100.00, 'WALLET', NULL, NULL, NULL, '1357583466371219456', 100.00, '1376417684140326912', 'O202107121414569576359788545', 'AF1414570606245969920');
INSERT INTO `li_refund_log` VALUES (1414570840352460801, 'A202107121414542572461752320', '2021-07-12 21:02:24', NULL, b'1', 100.00, 'WALLET', NULL, NULL, NULL, '1357582072222318592', 100.00, '1376417684140326912', 'O202107121414541833840623617', 'AF1414570840334270464');
INSERT INTO `li_refund_log` VALUES (1416228569043288065, '订单取消', '2021-07-17 10:49:38', NULL, b'1', 6001.00, 'WALLET', NULL, NULL, NULL, '订单取消', 6001.00, '1376417684140326912', 'O202107161415949355591925761', 'AF1416228568865701889');
INSERT INTO `li_refund_log` VALUES (1416790953357066242, '订单取消', '2021-07-19 00:04:21', NULL, b'1', 2999.00, 'WALLET', NULL, NULL, NULL, '订单取消', 2999.00, '1376417684140326912', 'O202107181416789223851163650', 'AF1416790953150119937');
INSERT INTO `li_refund_log` VALUES (1416796662094204929, 'A202107191416796586385604608', '2021-07-19 00:27:02', NULL, b'1', 8.00, 'WALLET', NULL, NULL, NULL, '1357582072222318592', 8.00, '1376417684140326912', 'O202107161415928433187225601', 'AF1416796661878882304');
INSERT INTO `li_refund_log` VALUES (1417288182014484482, '订单取消', '2021-07-20 09:00:09', NULL, b'1', 25.00, 'WALLET', NULL, NULL, NULL, '订单取消', 25.00, '1376417684140326912', 'O202107191416969208830361601', 'AF1417288181648195585');
INSERT INTO `li_refund_log` VALUES (1417421306077716481, '订单取消', '2021-07-20 17:49:08', NULL, b'1', 105.68, 'WALLET', NULL, NULL, NULL, '订单取消', 105.68, '1417410661773021185', 'O202107201417421229257654273', 'AF1417421306009223169');
INSERT INTO `li_refund_log` VALUES (1417772210970693634, 'A202107211417743481266241536', '2021-07-21 17:03:31', NULL, b'1', 28.00, 'WALLET', NULL, NULL, NULL, '其他原因', 28.00, '1376417684140326912', 'O202107201417416238811840513', 'AF1417772210197626880');
INSERT INTO `li_refund_log` VALUES (1417819937825886209, 'A202107191417051238641434624', '2021-07-21 20:13:10', NULL, b'1', 3999.00, 'WALLET', NULL, NULL, NULL, '1357583611645132800', 3999.00, '1376417684140326912', 'O202107191416969160553922561', 'AF1417819937640022016');
INSERT INTO `li_refund_log` VALUES (1418024028904660993, 'A202107221418022579788054528', '2021-07-22 09:44:09', NULL, b'1', 4.00, 'WALLET', NULL, NULL, NULL, '1357583504233201664', 4.00, '1376417684140326912', 'O202107211417775790212251649', 'AF1418024026067959808');
INSERT INTO `li_refund_log` VALUES (1418024041349160962, 'A202107221418022579788054528', '2021-07-22 09:44:12', NULL, b'1', 4.00, 'WALLET', NULL, NULL, NULL, '1357583504233201664', 4.00, '1376417684140326912', 'O202107211417775790212251649', 'AF1418024031306645504');
INSERT INTO `li_refund_log` VALUES (1418024042221576193, 'A202107221418022579788054528', '2021-07-22 09:44:12', NULL, b'1', 4.00, 'WALLET', NULL, NULL, NULL, '1357583504233201664', 4.00, '1376417684140326912', 'O202107211417775790212251649', 'AF1418024032682377216');
INSERT INTO `li_refund_log` VALUES (1418024043706359809, 'A202107221418022579788054528', '2021-07-22 09:44:12', NULL, b'1', 4.00, 'WALLET', NULL, NULL, NULL, '1357583504233201664', 4.00, '1376417684140326912', 'O202107211417775790212251649', 'AF1418024033844199424');
INSERT INTO `li_refund_log` VALUES (1418024044637495298, 'A202107221418022579788054528', '2021-07-22 09:44:13', NULL, b'1', 4.00, 'WALLET', NULL, NULL, NULL, '1357583504233201664', 4.00, '1376417684140326912', 'O202107211417775790212251649', 'AF1418024034620145664');
INSERT INTO `li_refund_log` VALUES (1418024046571069441, 'A202107221418022579788054528', '2021-07-22 09:44:13', NULL, b'1', 4.00, 'WALLET', NULL, NULL, NULL, '1357583504233201664', 4.00, '1376417684140326912', 'O202107211417775790212251649', 'AF1418024037606490112');
INSERT INTO `li_refund_log` VALUES (1418029274179608578, 'A202107221418028450387591168', '2021-07-22 10:04:59', NULL, b'1', 35.00, 'WALLET', NULL, NULL, NULL, '1357583649075101696', 35.00, '1376417684140326912', 'O202107221418026320612294657', 'AF1418029274153091072');
INSERT INTO `li_refund_log` VALUES (1418039576602386433, 'A202107221418039526529433600', '2021-07-22 10:45:56', NULL, b'1', 99.00, 'WALLET', NULL, NULL, NULL, '不按时发货', 99.00, '1376417684140326912', 'O202107221418031041091731457', 'AF1418039576567480320');
INSERT INTO `li_refund_log` VALUES (1418042481497673730, 'A202107221418041424514908160', '2021-07-22 10:57:28', NULL, b'1', 22.00, 'BANK_TRANSFER', NULL, NULL, NULL, '不按时发货', 22.00, '1376417684140326912', 'O202107221418040719288827905', 'AF1418042481479516160');
INSERT INTO `li_refund_log` VALUES (1418046254135709697, 'A202107221418044435446366208', '2021-07-22 11:12:28', NULL, b'1', 0.01, 'WALLET', NULL, NULL, NULL, '1357583504233201664', 0.01, '1376417684140326912', 'O202107211417750086821085185', 'AF1418046253832339456');
INSERT INTO `li_refund_log` VALUES (1418110905552769026, '订单取消', '2021-07-22 15:29:22', NULL, b'1', 4542.00, 'WALLET', NULL, NULL, NULL, '订单取消', 4542.00, '1376417684140326912', 'O202107221418110432379404289', 'AF1418110904997773313');
INSERT INTO `li_refund_log` VALUES (1418476182404636673, 'A202107231418475931839758336', '2021-07-23 15:40:51', NULL, b'1', 0.04, 'WALLET', NULL, NULL, NULL, '1357582072222318592', 0.04, '1417410661773021185', 'O202107231418475575734960129', 'AF1418476182147432448');
INSERT INTO `li_refund_log` VALUES (1418479282708353025, 'A202107231418477845465792512', '2021-07-23 15:53:10', NULL, b'1', 0.05, 'WALLET', NULL, NULL, NULL, '1357582072222318592', 0.05, '1376364798236688384', 'O202107231418477715463340033', 'AF1418479282514034688');
INSERT INTO `li_refund_log` VALUES (1418481041388376065, '订单取消', '2021-07-23 16:00:09', NULL, b'1', 173.00, 'WALLET', NULL, NULL, NULL, '订单取消', 173.00, '1417410661773021185', 'O202107231418480404037369857', 'AF1418481041177313281');
INSERT INTO `li_refund_log` VALUES (1418482403828342785, 'A202107231418481790732992512', '2021-07-23 16:05:34', NULL, b'1', 74.00, 'WALLET', NULL, NULL, NULL, '1357582072222318592', 74.00, '1417410661773021185', 'O202107231418481317342871553', 'AF1418482403625664512');
INSERT INTO `li_refund_log` VALUES (1418485608624726018, 'A202107231418485026458173440', '2021-07-23 16:18:18', NULL, b'1', 25.00, 'WALLET', NULL, NULL, NULL, '1357583611645132800', 25.00, '1417410661773021185', 'O202107231418484538396377089', 'AF1418485608384299008');
INSERT INTO `li_refund_log` VALUES (1419862628852080641, '订单取消', '2021-07-27 11:30:05', NULL, b'1', 400.00, 'WECHAT', NULL, '4200001134202107272572957030', '50301509192021072711007742739', '订单取消', 400.00, '1376364798236688384', 'O202107271419862526492934145', 'AF1419862624891305985');
INSERT INTO `li_refund_log` VALUES (1419863014807740418, '订单取消', '2021-07-27 11:31:37', NULL, b'1', 20.00, 'WECHAT', NULL, '4200001143202107278404200357', '50300808992021072711008857159', '订单取消', 20.00, '1376364798236688384', 'O202107271419862948163092481', 'AF1419863011165732865');
INSERT INTO `li_refund_log` VALUES (1419863461807300610, '订单取消', '2021-07-27 11:33:24', NULL, b'1', 20.00, 'WECHAT', NULL, '4200001130202107278455560540', '50300809182021072711001948883', '订单取消', 20.00, '1419863215450656769', 'O202107271419863388569206785', 'AF1419863458253373441');
INSERT INTO `li_refund_log` VALUES (1420183181429354498, 'A202107281420180325551570944', '2021-07-28 08:43:51', NULL, b'1', 37.00, 'WALLET', NULL, NULL, NULL, '1357583611645132800', 37.00, '1417410661773021185', 'O202107211417768313273974785', 'AF1420183181142786048');
INSERT INTO `li_refund_log` VALUES (1420223892535156737, '订单取消', '2021-07-28 11:25:37', NULL, b'1', 3999.00, 'WALLET', NULL, NULL, NULL, '订单取消', 3999.00, '1376417684140326912', 'O202107281420223307713609729', 'AF1420223892286341121');
INSERT INTO `li_refund_log` VALUES (1420678592455827457, 'A202107231418484018264932352', '2021-07-29 17:32:26', NULL, b'1', 72.00, 'WALLET', NULL, NULL, NULL, '1357582072222318592', 72.00, '1417410661773021185', 'O202107231418483244378095617', 'AF1420678591628181504');
INSERT INTO `li_refund_log` VALUES (1420679954212118530, 'A202107291420649563278016512', '2021-07-29 17:37:51', '{\"code\":\"INVALID_REQUEST\",\"message\":\"订单已全额退款\"}', b'0', 1.00, 'WECHAT', NULL, '4200001128202106255473720416', NULL, '地址或商品选择错误', 1.00, '1384135843077160960', 'O202106251408332303037366273', 'AF1420679952310730752');
INSERT INTO `li_refund_log` VALUES (1422463693200044033, '订单取消', '2021-08-03 15:45:47', NULL, b'1', 2999.00, 'WALLET', NULL, NULL, NULL, '订单取消', 2999.00, '1376417684140326912', 'O202108031422463513611206657', 'AF1422463692712181761');
INSERT INTO `li_refund_log` VALUES (1422481437261836289, '订单取消', '2021-08-03 16:56:18', NULL, b'1', 222.00, 'WALLET', NULL, NULL, NULL, '订单取消', 222.00, '1376417684140326912', 'O202108031422481315780886529', 'AF1422481437231153153');
INSERT INTO `li_refund_log` VALUES (1422885825917550593, 'A202108041422885327135047680', '2021-08-04 19:43:11', NULL, b'1', 11.00, 'BANK_TRANSFER', NULL, NULL, NULL, '1357582072222318592', 11.00, '1376364798236688384', 'O202108041422884479327797249', 'AF1422885825904902144');
INSERT INTO `li_refund_log` VALUES (1422889120111329282, 'A202108041422889043984646144', '2021-08-04 19:56:17', NULL, b'1', 11.00, 'BANK_TRANSFER', NULL, NULL, NULL, '1357583466371219456', 11.00, '1376364798236688384', 'O202108041422887693158711297', 'AF1422889120111263744');
INSERT INTO `li_refund_log` VALUES (1422889973278904321, 'A202108041422889805787693056', '2021-08-04 19:59:40', NULL, b'1', 11.00, 'BANK_TRANSFER', NULL, NULL, NULL, '1357582072222318592', 11.00, '1376364798236688384', 'O202108041422888520711667713', 'AF1422889973257863168');
INSERT INTO `li_refund_log` VALUES (1423073272781611010, 'A202108051423073194272620544', '2021-08-05 08:08:02', NULL, b'1', 11.00, 'BANK_TRANSFER', NULL, NULL, NULL, '1357583466371219456', 11.00, '1376364798236688384', 'O202108051423072831410798593', 'AF1423073272764825600');
INSERT INTO `li_refund_log` VALUES (1423074096756826113, 'A202108051423073997523779584', '2021-08-05 08:11:19', NULL, b'1', 11.00, 'BANK_TRANSFER', NULL, NULL, NULL, '1357582072222318592', 11.00, '1376364798236688384', 'O202108051423071875822518273', 'AF1423074096756817920');
INSERT INTO `li_refund_log` VALUES (1423075015795941377, 'A202108051423074927052849152', '2021-08-05 08:14:58', NULL, b'1', 11.00, 'BANK_TRANSFER', NULL, NULL, NULL, '1357582072222318592', 11.00, '1376364798236688384', 'O202108051423074256861790209', 'AF1423075015795933184');
INSERT INTO `li_refund_log` VALUES (1423214064020013058, 'A202108051423213888031162368', '2021-08-05 17:27:30', NULL, b'1', 11.00, 'BANK_TRANSFER', NULL, NULL, NULL, '1357582072222318592', 11.00, '1376364798236688384', 'O202108051423213103734063105', 'AF1423214064007380992');
INSERT INTO `li_refund_log` VALUES (1425804148053405697, 'A202108121425804030306680832', '2021-08-12 20:59:34', NULL, b'1', 4999.00, 'WALLET', NULL, NULL, NULL, '1357583466371219456', 4999.00, '1376364798236688384', 'O202108121425803593843212289', 'AF1425804146996412416');
INSERT INTO `li_refund_log` VALUES (1425804156467179522, 'A202108121425804030306680832', '2021-08-12 20:59:36', NULL, b'1', 4999.00, 'WALLET', NULL, NULL, NULL, '1357583466371219456', 4999.00, '1376364798236688384', 'O202108121425803593843212289', 'AF1425804154630045696');
INSERT INTO `li_refund_log` VALUES (1425804157012439041, 'A202108121425804030306680832', '2021-08-12 20:59:36', NULL, b'1', 4999.00, 'WALLET', NULL, NULL, NULL, '1357583466371219456', 4999.00, '1376364798236688384', 'O202108121425803593843212289', 'AF1425804155385020416');
INSERT INTO `li_refund_log` VALUES (1425804157863882754, 'A202108121425804030306680832', '2021-08-12 20:59:36', NULL, b'1', 4999.00, 'WALLET', NULL, NULL, NULL, '1357583466371219456', 4999.00, '1376364798236688384', 'O202108121425803593843212289', 'AF1425804156228075520');
INSERT INTO `li_refund_log` VALUES (1432889744930066433, '订单取消', '2021-09-01 10:15:12', NULL, b'1', 222.00, 'WALLET', NULL, NULL, NULL, '订单取消', 222.00, '1376417684140326912', 'O202108311432638630068748288', 'AF1432889744395599873');
INSERT INTO `li_refund_log` VALUES (1433003975447265281, '订单取消', '2021-09-01 17:49:06', NULL, b'1', 3232.00, 'WALLET', NULL, NULL, NULL, '订单取消', 3232.00, '1376417684140326912', 'O202109011433003944719679488', 'AF1433003975119994880');
INSERT INTO `li_refund_log` VALUES (1433005128717172737, '订单取消', '2021-09-01 17:53:41', NULL, b'1', 3232.00, 'WALLET', NULL, NULL, NULL, '订单取消', 3232.00, '1376417684140326912', 'O202109011433005088988725249', 'AF1433005128348073985');
INSERT INTO `li_refund_log` VALUES (1433005806130823169, '订单取消', '2021-09-01 17:56:23', NULL, b'1', 3232.00, 'WALLET', NULL, NULL, NULL, '订单取消', 3232.00, '1376417684140326912', 'O202109011433005532674785281', 'AF1433005806038548481');

-- ----------------------------
-- Table structure for li_region
-- ----------------------------
DROP TABLE IF EXISTS `li_region`;
CREATE TABLE `li_region`  (
                              `id` bigint NOT NULL COMMENT 'ID',
                              `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
                              `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                              `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                              `update_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者',
                              `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                              `ad_code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '区域编码',
                              `center` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '区域中心点经纬度',
                              `city_code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '城市代码',
                              `level` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '行政区划级别',
                              `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '名称',
                              `order_num` int NULL DEFAULT NULL COMMENT '排序',
                              `parent_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '父ID',
                              `path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '行政地区路径',
                              PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for li_role
-- ----------------------------
DROP TABLE IF EXISTS `li_role`;
CREATE TABLE `li_role`  (
                            `id` bigint NOT NULL COMMENT 'ID',
                            `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
                            `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                            `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                            `update_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者',
                            `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                            `default_role` bit(1) NULL DEFAULT NULL COMMENT '是否为注册默认角色',
                            `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
                            `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '角色名',
                            PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_role
-- ----------------------------

-- ----------------------------
-- Table structure for li_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `li_role_menu`;
CREATE TABLE `li_role_menu`  (
                                 `id` bigint NOT NULL COMMENT 'ID',
                                 `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
                                 `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                 `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                 `update_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者',
                                 `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                 `is_super` bit(1) NULL DEFAULT NULL COMMENT '是否拥有操作数据权限',
                                 `menu_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '菜单',
                                 `role_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '角色ID',
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_role_menu
-- ----------------------------

-- ----------------------------
-- Table structure for li_s_platform_view_data
-- ----------------------------
DROP TABLE IF EXISTS `li_s_platform_view_data`;
CREATE TABLE `li_s_platform_view_data`  (
                                            `id` bigint NOT NULL COMMENT 'ID',
                                            `date` datetime(6) NULL DEFAULT NULL COMMENT '统计日',
                                            `pv_num` bigint NULL DEFAULT NULL COMMENT 'PV数量',
                                            `store_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '店铺ID',
                                            `uv_num` bigint NULL DEFAULT NULL COMMENT 'UV数量',
                                            PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_s_platform_view_data
-- ----------------------------

-- ----------------------------
-- Table structure for li_seckill
-- ----------------------------
DROP TABLE IF EXISTS `li_seckill`;
CREATE TABLE `li_seckill`  (
                               `id` bigint NOT NULL COMMENT 'ID',
                               `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
                               `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                               `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                               `update_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者',
                               `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                               `end_time` datetime(6) NULL DEFAULT NULL COMMENT '活动结束时间',
                               `promotion_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '活动名称',
                               `store_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '店铺ID',
                               `store_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '店铺名称',
                               `start_time` datetime(6) NULL DEFAULT NULL COMMENT '活动开始时间',
                               `apply_end_time` datetime(6) NOT NULL COMMENT '报名截至时间',
                               `hours` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '开启几点场',
                               `seckill_rule` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '申请规则',
                               `store_ids` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '店铺ID集合以逗号分隔',
                               `goods_num` int NULL DEFAULT NULL,
                               `scope_id` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '范围关联的ID',
                               `scope_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'PORTION_GOODS' COMMENT '关联范围类型',
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_seckill
-- ----------------------------

-- ----------------------------
-- Table structure for li_seckill_apply
-- ----------------------------
DROP TABLE IF EXISTS `li_seckill_apply`;
CREATE TABLE `li_seckill_apply`  (
                                     `id` bigint NOT NULL COMMENT 'ID',
                                     `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
                                     `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                     `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                     `update_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者',
                                     `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                     `fail_reason` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '驳回原因',
                                     `goods_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品名称',
                                     `original_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '商品原始价格',
                                     `price` decimal(10, 2) NOT NULL COMMENT '价格',
                                     `promotion_apply_status` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '促销活动申请状态',
                                     `quantity` int NOT NULL COMMENT '促销数量',
                                     `sales_num` int NULL DEFAULT NULL COMMENT '已售数量',
                                     `seckill_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '活动ID',
                                     `store_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '店铺ID',
                                     `store_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '店铺名称',
                                     `sku_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '货品ID',
                                     `time_line` int NOT NULL COMMENT '时刻',
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_seckill_apply
-- ----------------------------

-- ----------------------------
-- Table structure for li_sensitive_words
-- ----------------------------
DROP TABLE IF EXISTS `li_sensitive_words`;
CREATE TABLE `li_sensitive_words`  (
                                       `id` bigint NOT NULL COMMENT 'ID',
                                       `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
                                       `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                       `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                       `update_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者',
                                       `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                       `sensitive_word` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '敏感词名称',
                                       PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_sensitive_words
-- ----------------------------
INSERT INTO `li_sensitive_words` VALUES (1455437623816667138, 'admin', '2021-11-02 15:32:24.903000', b'0', 'admin', '2021-11-02 15:32:47.091000', '11111啊');
INSERT INTO `li_sensitive_words` VALUES (1455437664195231745, 'admin', '2021-11-02 15:32:34.526000', b'0', 'admin', '2021-11-02 15:32:40.759000', '啊啊啊啊啊1');
INSERT INTO `li_sensitive_words` VALUES (1455437848660721666, 'admin', '2021-11-02 15:33:18.506000', b'0', NULL, NULL, 'sdd');
INSERT INTO `li_sensitive_words` VALUES (1455437891442622466, 'admin', '2021-11-02 15:33:28.706000', b'0', NULL, NULL, 'sss');
INSERT INTO `li_sensitive_words` VALUES (1455437907951403010, 'admin', '2021-11-02 15:33:32.642000', b'0', NULL, NULL, 'dsscdsdx');
INSERT INTO `li_sensitive_words` VALUES (1455437923965255681, 'admin', '2021-11-02 15:33:36.459000', b'0', NULL, NULL, 'sddcxasd');
INSERT INTO `li_sensitive_words` VALUES (1455437938469158914, 'admin', '2021-11-02 15:33:39.918000', b'0', 'admin', '2021-11-18 15:00:11.270000', 'dcasawad');
INSERT INTO `li_sensitive_words` VALUES (1455437955477061633, 'admin', '2021-11-02 15:33:43.972000', b'0', NULL, NULL, 'sacsac');
INSERT INTO `li_sensitive_words` VALUES (1455437972774371329, 'admin', '2021-11-02 15:33:48.096000', b'0', 'admin', '2021-11-18 15:00:04.501000', 'scddsx');
INSERT INTO `li_sensitive_words` VALUES (1464602719843307521, 'admin', '2021-11-27 22:31:14.070000', b'0', NULL, NULL, 'jj');

-- ----------------------------
-- Table structure for li_service_notice
-- ----------------------------
DROP TABLE IF EXISTS `li_service_notice`;
CREATE TABLE `li_service_notice`  (
                                      `id` bigint NOT NULL COMMENT 'ID',
                                      `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
                                      `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                      `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                      `update_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者',
                                      `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                      `banner_image` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'banner图',
                                      `content` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '站内信内容',
                                      `store_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '店铺ID',
                                      `sub_title` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '副标题',
                                      `title` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '标题',
                                      `to_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '点击跳转',
                                      PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_service_notice
-- ----------------------------

-- ----------------------------
-- Table structure for li_setting
-- ----------------------------
DROP TABLE IF EXISTS `li_setting`;
CREATE TABLE `li_setting`  (
                               `id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'ID',
                               `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '创建者',
                               `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                               `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                               `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '更新者',
                               `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                               `setting_value` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '配置值value',
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_setting
-- ----------------------------
INSERT INTO `li_setting` VALUES ('ALIPAY_PAYMENT', 'admin', '2021-02-27 07:20:11.914000', b'0', 'admin', '2021-09-15 11:57:38.683000', '{\"privateKey\":\"privateKey\",\"alipayPublicCertPath\":\"/home/crt/lili-alipay/alipayCertPublicKey_RSA2.crt\",\"certPath\":\"/home/crt/lili-alipay/appCertPublicKey_2021002107649773.crt\",\"appId\":\"appId\",\"rootCertPath\":\"/home/crt/lili-alipay/alipayRootCert.crt\"}');
INSERT INTO `li_setting` VALUES ('BASE_SETTING', 'admin', '2021-01-21 13:46:35.348000', b'0', 'admin', '2022-10-12 19:10:04.355000', '{\"staticPageAddress\":\"https://pickmall.cn/1\",\"storeSideLogo\":\"https://cdn.pickmall.cn/cdn/logo/logo2.png\",\"icp\":\"icp\",\"domainLogo\":\"https://cdn.pickmall.cn/cdn/logo/logo2.png\",\"siteName\":\"lilishop\",\"buyerSideLogo\":\"https://cdn.pickmall.cn/cdn/logo/logo2.png\",\"staticPageWapAddress\":\"https://m.pickmall.cn/\"}');
INSERT INTO `li_setting` VALUES ('DISTRIBUTION_SETTING', 'admin', '2021-01-22 02:56:44.099000', b'0', 'admin', '2021-09-24 15:39:46.245000', '{\"distributionDay\":94,\"isOpen\":true,\"cashDay\":1}');
INSERT INTO `li_setting` VALUES ('ES_SIGN', 'admin', '2021-01-22 02:56:44.099000', b'0', 'admin', '2021-01-22 02:56:44.099000', '{\"secretKey\":\"111\"}');
INSERT INTO `li_setting` VALUES ('GOODS_SETTING', 'admin', '2020-12-14 10:48:04.000000', b'0', 'admin', '2021-03-25 20:44:38.514000', '{\"goodsCheck\":\"true\",\"smallPictureWidth\":\"200\",\"smallPictureHeight\":\"200\",\"abbreviationPictureWidth\":\"400\",\"abbreviationPictureHeight\":\"400\",\"originalPictureWidth\":\"800\",\"originalPictureHeight\":\"800\"}');
INSERT INTO `li_setting` VALUES ('IM_SETTING', 'admin', '2021-09-17 12:00:41.085000', b'0', 'admin', '2022-11-18 10:25:16.804000', '{\"httpUrl\":\"https://www.baidu.com\"}');
INSERT INTO `li_setting` VALUES ('KUAIDI_SETTING', 'admin', '2021-01-18 03:30:53.430000', b'0', 'admin', '2021-09-15 11:57:07.495000', '{\"ebusinessID\":\"test\",\"appKey\":\"test\",\"reqURL\":\"test\"}');
INSERT INTO `li_setting` VALUES ('ORDER_SETTING', 'admin', '2021-02-03 08:16:54.942000', b'0', 'admin', '2021-09-16 16:32:18.050000', '{\"closeComplaint\":\"12\",\"autoCancelAfterSale\":\"7\",\"autoReceive\":\"5\",\"autoAfterSaleComplete\":\"0\",\"closeAfterSale\":\"12\",\"autoAfterSaleReview\":\"0\",\"autoEvaluation\":\"7\",\"autoCancel\":\"35\"}');
INSERT INTO `li_setting` VALUES ('OSS_SETTING', 'admin', '2020-12-12 08:44:27.376000', b'0', 'admin', '2022-10-12 19:11:49.244000', '{\"accessKeyId\":\"LTAI5tAw2cz7chK8GEjUuFCs\",\"endPoint\":\"oss-cn-beijing.aliyuncs.com\",\"bucketName\":\"lilishop-oss\",\"picLocation\":\"/template\",\"m_secretKey\":\"wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY\",\"accessKeySecret\":\"MsnF1RH4TFLIMd0i19DtnOYbAHQWLM\",\"m_bucketName\":\"lilishop\",\"m_endpoint\":\"http://pg2481.com:9000\",\"m_accessKey\":\"AKIAIOSFODNN7EXAMPLE\",\"type\":\"ALI_OSS\",\"m_frontUrl\":\"http://pg2481.com:9000\"}');
INSERT INTO `li_setting` VALUES ('PAYMENT_SUPPORT', 'admin', '2021-03-01 10:07:49.831000', b'0', 'admin', '2021-04-14 03:58:30.726000', '{\"paymentSupportItems\":[{\"client\":\"H5\",\"supports\":[\"WALLET\",\"ALIPAY\",\"WECHAT\"]},{\"client\":\"PC\",\"supports\":[\"WECHAT\",\"ALIPAY\",\"WALLET\"]},{\"client\":\"WECHAT_MP\",\"supports\":[\"WECHAT\",\"WALLET\"]},{\"client\":\"APP\",\"supports\":[\"WECHAT\",\"ALIPAY\",\"WALLET\"]}]}');
INSERT INTO `li_setting` VALUES ('POINT_SETTING', 'admin', '2021-02-26 07:57:39.629000', b'0', 'admin', '2021-09-26 16:03:56.043000', '{\"money\":1,\"signIn\":2,\"comment\":24,\"pointSettingItems\":[],\"register\":10}');
INSERT INTO `li_setting` VALUES ('QQ_CONNECT', 'admin', '2021-03-02 09:16:44.000000', b'0', 'admin', '2021-03-05 02:25:51.615000', '{\"qqConnectSettingItemList\":[{\"clientType\":\"PC\",\"appId\":\"\",\"appKey\":\"\"},{\"clientType\":\"H5\",\"appId\":\"\",\"appKey\":\"\"}]}');
INSERT INTO `li_setting` VALUES ('SECKILL_SETTING', 'admin', '2021-09-27 10:33:38.000000', NULL, 'admin', '2021-09-27 10:30:42.917000', '{\"hours\":\"1,2\",\"seckillRule\":\"秒杀规则\"}');
INSERT INTO `li_setting` VALUES ('SMS_SETTING', 'admin', '2021-01-23 02:18:03.299000', b'0', 'admin', '2021-09-15 11:57:11.962000', '{\"accessKeyId\":\"test\",\"signName\":\"lili\",\"accessSecret\":\"test\",\"regionId\":\"test\"}');
INSERT INTO `li_setting` VALUES ('WECHAT_CONNECT', 'admin', '2021-03-02 09:17:10.000000', b'0', 'admin', '2021-03-05 02:26:43.348000', '{\"wechatConnectSettingItems\":[{\"clientType\":\"PC\",\"appId\":\"\",\"appSecret\":\"\"},{\"clientType\":\"H5\",\"appId\":\"\",\"appSecret\":\"\"},{\"clientType\":\"WECHAT_MP\",\"appId\":\"\",\"appSecret\":\"\"}]}');
INSERT INTO `li_setting` VALUES ('WECHAT_PAYMENT', 'admin', '2021-02-27 07:18:13.767000', b'0', 'admin', '2021-04-27 16:09:19.724000', '{\"appId\":\"\",\"mchId\":\"\",\"apiclient_key\":\"\",\"apiclient_cert_pem\":\"\",\"apiclient_cert_p12\":\"\",\"serialNumber\":\"\",\"apiKey3\":\"\"}');
INSERT INTO `li_setting` VALUES ('WITHDRAWAL_SETTING', 'admin', '2021-02-21 09:34:41.153000', b'0', 'admin', '2021-02-26 08:54:01.267000', '{\"apply\":true}');

-- ----------------------------
-- Table structure for li_short_link
-- ----------------------------
DROP TABLE IF EXISTS `li_short_link`;
CREATE TABLE `li_short_link`  (
                                  `id` bigint NOT NULL,
                                  `original_params` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_short_link
-- ----------------------------
INSERT INTO `li_short_link` VALUES (1409715818003222529, '1381796591895052288,1409709806114369537,1382586852829036544');
INSERT INTO `li_short_link` VALUES (1409718745925230594, '1381796591895052288,1377145942322446336,1382586852829036544');
INSERT INTO `li_short_link` VALUES (1409805260080627714, '1386931039305203712,1409805176324579330,1381773066530455552');
INSERT INTO `li_short_link` VALUES (1409806134576234498, '1381796591895052288,1377145942322446336,1381790692237377536');
INSERT INTO `li_short_link` VALUES (1409806183959969794, '1386931039305203712,1376841229546815488,1381773066530455552');
INSERT INTO `li_short_link` VALUES (1409815929974767617, '1409794871225802754,1409794871070613505,1381773066530455552');
INSERT INTO `li_short_link` VALUES (1410131017373843458, '1381793060458856448,1377085464581767168,1382586852829036544');
INSERT INTO `li_short_link` VALUES (1410431207200784385, '1409794871225802754,1409794871070613505,1410431006687887361');
INSERT INTO `li_short_link` VALUES (1410431315002785794, '1381793060458856448,1377085464581767168,1410431006687887361');
INSERT INTO `li_short_link` VALUES (1410431701600174082, '1381796591895052288,1377145942322446336,1410431006687887361');
INSERT INTO `li_short_link` VALUES (1415228199290646529, '1377132926088511488,1377132926042374144,1415227969929326593');
INSERT INTO `li_short_link` VALUES (1415276421623164929, '1415275858856628226,1415275858776936450,1415227969929326593');
INSERT INTO `li_short_link` VALUES (1419943286316240897, '1377132926088511488,1377132926042374144,1410431006687887361');
INSERT INTO `li_short_link` VALUES (1420272969624883202, '1377132926088511488,1377132926042374144,1399556395723915264');
INSERT INTO `li_short_link` VALUES (1420288048927903746, '1377132926088511488,1377132926042374144,1382586852829036544');
INSERT INTO `li_short_link` VALUES (1420288066959216642, '1381792263700480000,1377064344218501120,1382586852829036544');

-- ----------------------------
-- Table structure for li_sms_reach
-- ----------------------------
DROP TABLE IF EXISTS `li_sms_reach`;
CREATE TABLE `li_sms_reach`  (
                                 `id` bigint NOT NULL COMMENT 'ID',
                                 `context` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '消息内容',
                                 `message_code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '消息CODE',
                                 `num` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '预计发送条数',
                                 `sign_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '签名名称',
                                 `sms_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '模板名称',
                                 `sms_range` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '接收人',
                                 `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_sms_reach
-- ----------------------------

-- ----------------------------
-- Table structure for li_sms_sign
-- ----------------------------
DROP TABLE IF EXISTS `li_sms_sign`;
CREATE TABLE `li_sms_sign`  (
                                `business_license` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '营业执照',
                                `license` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '授权委托书',
                                `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '短信签名申请说明',
                                `sign_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '签名名称',
                                `sign_source` int NULL DEFAULT NULL COMMENT '签名来源',
                                `sign_status` int NULL DEFAULT NULL COMMENT '签名审核状态',
                                `reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审核备注',
                                `id` bigint NOT NULL,
                                PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_sms_sign
-- ----------------------------
INSERT INTO `li_sms_sign` VALUES ('https://lilishop-oss.oss-cn-beijing.aliyuncs.com/a2d345f393934b78b8d92e9bb9c54a83.png', 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/59a8bc583cd24bcd85e6b9160e04817c.png', 'lilishop演示站点', 'lilishop演示站点', 4, 2, '请参考下述修改建议:\n1. 未核实到该店铺，请提供电商平台店铺地址链接；', 1463708669619556354);

-- ----------------------------
-- Table structure for li_sms_template
-- ----------------------------
DROP TABLE IF EXISTS `li_sms_template`;
CREATE TABLE `li_sms_template`  (
                                    `id` bigint NOT NULL COMMENT 'ID',
                                    `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '短信模板申请说明',
                                    `template_content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '模板内容',
                                    `template_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '模板名称',
                                    `template_status` int NULL DEFAULT NULL COMMENT '模板审核状态',
                                    `template_type` int NULL DEFAULT NULL COMMENT '短信类型',
                                    `reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审核备注',
                                    `template_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '短信模板CODE',
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_sms_template
-- ----------------------------
INSERT INTO `li_sms_template` VALUES (1411313932375998465, '用于通知会员注册', '恭喜您已经成为香道网的新会员，首次登陆可以到https://pc-b2b2c.pickmall.cn/领取新人优惠券', '会员注册通知', 2, 1, '*短信通知模板不支持发送营销内容，建议删除相关推广部分：首次登陆可以到https://pc-b2b2c.pickmall.cn/领取新人优惠券', 'SMS_218905489');

-- ----------------------------
-- Table structure for li_spec_values
-- ----------------------------
DROP TABLE IF EXISTS `li_spec_values`;
CREATE TABLE `li_spec_values`  (
                                   `id` bigint NOT NULL COMMENT 'ID',
                                   `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
                                   `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                   `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                   `update_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者',
                                   `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                   `spec_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '规格项ID',
                                   `spec_value` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '规格值名字',
                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_spec_values
-- ----------------------------


-- ----------------------------
-- Table structure for li_special
-- ----------------------------
DROP TABLE IF EXISTS `li_special`;
CREATE TABLE `li_special`  (
                               `id` bigint NOT NULL COMMENT 'ID',
                               `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
                               `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                               `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                               `update_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者',
                               `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                               `client_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '楼层对应连接端类型',
                               `page_data_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '页面ID',
                               `special_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '专题活动名称',
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_special
-- ----------------------------

-- ----------------------------
-- Table structure for li_specification
-- ----------------------------
DROP TABLE IF EXISTS `li_specification`;
CREATE TABLE `li_specification`  (
                                     `id` bigint NOT NULL COMMENT 'ID',
                                     `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
                                     `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                     `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                     `update_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者',
                                     `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                     `spec_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '规格名称',
                                     `store_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '所属店铺',
                                     `spec_value` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_specification
-- ----------------------------


-- ----------------------------
-- Table structure for li_store
-- ----------------------------
DROP TABLE IF EXISTS `li_store`;
CREATE TABLE `li_store`  (
                             `id` bigint NOT NULL COMMENT 'ID',
                             `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
                             `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                             `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                             `update_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者',
                             `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                             `member_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '会员Id',
                             `member_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '会员名称',
                             `self_operated` bit(1) NOT NULL COMMENT '是否自营',
                             `store_disable` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '店铺状态',
                             `store_end_time` datetime(6) NULL DEFAULT NULL COMMENT '店铺关闭时间',
                             `store_logo` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '店铺logo',
                             `store_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '店铺名称',
                             `store_address_detail` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '详细地址',
                             `store_address_id_path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '地址id',
                             `store_address_path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '地址名称',
                             `store_center` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '经纬度',
                             `store_desc` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '店铺简介',
                             `delivery_score` decimal(10, 2) NULL DEFAULT NULL COMMENT '物流评分',
                             `description_score` decimal(10, 2) NULL DEFAULT NULL COMMENT '描述评分',
                             `service_score` decimal(10, 2) NULL DEFAULT NULL COMMENT '服务评分',
                             `goods_num` int NULL DEFAULT NULL COMMENT '商品数量',
                             `collection_num` int NULL DEFAULT NULL COMMENT '收藏数量',
                             `yzf_mp_sign` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                             `yzf_sign` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                             `merchant_euid` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                             `page_show` bit(1) NULL DEFAULT NULL COMMENT '默认页面是否开启',
                             `self_pick_flag` bit(1) DEFAULT NULL COMMENT '是否开启自提',
                             PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_store
-- ----------------------------
INSERT INTO `li_store` VALUES (1376369067769724928, 'admin', '2021-03-28 22:02:12.109000', b'0', 'admin', '2021-10-18 14:47:22.227000', '1376364798236688384', '15810610731', b'1', 'OPEN', NULL, 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/e3e1c6ef28a844f98a0c907bd74a4085.png', '自营店', '北京五道口', '1371783043718578711,1371783043718578712,1371783043722772487,1371783043722772492', '北京市 北京城区 海淀区 学院路街道', '39.992552,116.338611', 'Lilishop自营店简介', 4.00, 4.00, 4.00, 54, 13, '4a971f4f4ff9637cd9286a0197b0573bf2f65de052c21791f90d4235fca41456e1cc145075567f5f47b8e13c895f3fc8cacec5789f9afee8df670f7cbe5c0f82', '37ef9b97807d03c6741298ed4eb5b536d2d238e08a3c00fb01fe48f03a569974c99ad767e72c04b3165ef29aca2c488b505fe4ca', NULL, NULL, NULL);
INSERT INTO `li_store` VALUES (1376433565247471616, 'admin', '2021-03-29 02:18:29.507000', b'0', 'admin', '2021-12-02 10:25:56.165000', '1376417684140326912', '13011111111', b'1', 'OPEN', NULL, 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/158bff831cff499a8d45a226a6e020c2.png', '家家乐', '家家乐超市', '1401797451790156093,1401797451790156094,1401797451790156416,1401797451790156442', '北京市 北京城区 海淀区 西三旗街道', '116.378877,40.063637', '店223232', 4.41, 4.47, 4.58, 189, 69, '32b8ff6f8d1c240be8d7fe51bdd6d44a6776ea86930afbe5c3c342825e942c914fc6126b6be1f003ab04aee1af9f442d2c33e1427529300671588866edaa4b12', '37ef9b97807d03c6741298ed4eb5b536d2d238e08a3c00fb01fe48f03a569974c99ad767e72c04b3165ef29aca2c488b505fe4ca', NULL, NULL, NULL);

-- ----------------------------
-- Table structure for li_store_address
-- ----------------------------
DROP TABLE IF EXISTS `li_store_address`;
CREATE TABLE `li_store_address`  (
                                     `id` bigint NOT NULL COMMENT 'ID',
                                     `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建者',
                                     `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                     `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                     `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新者',
                                     `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                                     `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '地址',
                                     `address_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '自提点名称',
                                     `center` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '经纬度',
                                     `mobile` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '电话',
                                     `store_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '店铺ID',
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_store_address
-- ----------------------------
INSERT INTO `li_store_address` VALUES (1416973824579416066, '15810610731', '2021-07-19 12:11:01', b'0', '15810610731', '2021-07-19 12:12:16', '北京市丰台区太平桥街道北京西站', '猜猜猜', '39.89491,116.322056', '13213131312', '1376369067769724928');
INSERT INTO `li_store_address` VALUES (1438328560679190529, 'admin', '2021-09-16 10:27:06', b'0', NULL, NULL, '陕西省西安市新城区西一路街道中国电信(小灵通专营店)新时代广场', 'ss', '34.263161,108.948024', '13565432143', '1413749887712206849');
INSERT INTO `li_store_address` VALUES (1451514949080027137, '13011111111', '2021-10-22 19:45:06', b'0', '13011111111', '2021-10-22 20:08:17', '广东省广州市黄埔区萝岗街道中核数字产业园萝岗至泰商业中心', '超牛逼牌', '23.173921,113.484778', '13115510735', '1376433565247471616');
INSERT INTO `li_store_address` VALUES (1459760189876310018, 'Jay997', '2021-11-14 13:48:45', b'0', NULL, NULL, '北京市东城区东华门街道王府井172号公寓丹耀大厦', 'qqqq', '39.912319,116.411694', '13145678661', '1459758076710449154');
INSERT INTO `li_store_address` VALUES (1460165010232004610, '13011111111', '2021-11-15 16:37:22', b'0', NULL, NULL, '四川省成都市武侯区桂溪街道长虹科技大厦(天府四街)', '嗨呀', '30.541223,104.063418', '18980474848', '1376433565247471616');
INSERT INTO `li_store_address` VALUES (1460641052583366657, '13011111111', '2021-11-17 00:08:59', b'0', NULL, NULL, '陕西省西安市莲湖区枣园街道立信路万科·金色悦城', '帝国时代', '34.285556,108.857558', '15826652365', '1376433565247471616');

-- ----------------------------
-- Table structure for li_store_collection
-- ----------------------------
DROP TABLE IF EXISTS `li_store_collection`;
CREATE TABLE `li_store_collection`  (
                                        `id` bigint NOT NULL COMMENT 'ID',
                                        `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
                                        `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                        `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                        `update_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者',
                                        `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                        `member_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '会员ID',
                                        `store_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '店铺ID',
                                        PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_store_collection
-- ----------------------------

-- ----------------------------
-- Table structure for li_store_department
-- ----------------------------
DROP TABLE IF EXISTS `li_store_department`;
CREATE TABLE `li_store_department`  (
                                        `id` bigint NOT NULL COMMENT 'ID',
                                        `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '创建者',
                                        `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                        `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                        `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '更新者',
                                        `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                        `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '部门名称',
                                        `store_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '店铺id',
                                        `parent_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '父id',
                                        `sort_order` decimal(20, 2) NULL DEFAULT NULL COMMENT '排序值',
                                        PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_store_department
-- ----------------------------

-- ----------------------------
-- Table structure for li_store_department_role
-- ----------------------------
DROP TABLE IF EXISTS `li_store_department_role`;
CREATE TABLE `li_store_department_role`  (
                                             `id` bigint NOT NULL COMMENT 'ID',
                                             `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '创建者',
                                             `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                             `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                             `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '更新者',
                                             `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                             `role_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '角色id',
                                             `department_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '部门id',
                                             PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_store_department_role
-- ----------------------------

-- ----------------------------
-- Table structure for li_store_detail
-- ----------------------------
DROP TABLE IF EXISTS `li_store_detail`;
CREATE TABLE `li_store_detail`  (
                                    `id` bigint NOT NULL COMMENT 'ID',
                                    `company_address` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '公司地址',
                                    `company_address_id_path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '公司地址地区ID',
                                    `company_address_path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '公司地址地区',
                                    `company_email` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '电子邮箱',
                                    `company_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '公司名称',
                                    `company_phone` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '公司电话',
                                    `dd_code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '同城配送达达店铺编码',
                                    `employee_num` int NULL DEFAULT NULL COMMENT '员工总数',
                                    `goods_management_category` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '店铺经营类目',
                                    `legal_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '法人身份证',
                                    `legal_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '法人姓名',
                                    `legal_photo` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '法人身份证照片',
                                    `licence_photo` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '营业执照电子版',
                                    `license_num` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '营业执照号',
                                    `link_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '联系人姓名',
                                    `link_phone` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '联系人电话',
                                    `registered_capital` decimal(10, 2) NULL DEFAULT NULL COMMENT '注册资金',
                                    `scope` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '法定经营范围',
                                    `settlement_bank_account_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '结算银行开户行名称',
                                    `settlement_bank_account_num` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '结算银行开户账号',
                                    `settlement_bank_branch_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '结算银行开户支行名称',
                                    `settlement_bank_joint_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '结算银行支行联行号',
                                    `store_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '店铺ID',
                                    `store_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '店铺名称',
                                    `stock_warning` int NULL DEFAULT NULL COMMENT '库存预警数量',
                                    `settlement_cycle` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '结算周期',
                                    `sales_consignee_address_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '退货地址Id',
                                    `sales_consignee_address_path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '退货地址名称',
                                    `sales_consignee_detail` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '退货详细地址',
                                    `sales_consignee_mobile` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '退货收件人手机',
                                    `sales_consignee_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '退货收货人姓名',
                                    `settlement_day` datetime(6) NULL DEFAULT NULL COMMENT '店铺上次结算日',
                                    `sales_consignor_address_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '发货地址id',
                                    `sales_consignor_address_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '发货地址名称',
                                    `sales_consignor_detail` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '发货详细地址',
                                    `sales_consignor_mobile` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '发货人手机',
                                    `sales_consignor_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '发货人姓名',
                                    `app_secret_key` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                    `merchant_number` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                    `app_merchant_key` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_store_detail
-- ----------------------------


-- ----------------------------
-- Table structure for li_store_flow
-- ----------------------------
DROP TABLE IF EXISTS `li_store_flow`;
CREATE TABLE `li_store_flow`  (
                                  `id` bigint NOT NULL COMMENT 'ID',
                                  `bill_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '最终结算金额',
                                  `category_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '分类ID',
                                  `commission_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '平台收取交易佣金',
                                  `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                  `distribution_rebate` decimal(10, 2) NULL DEFAULT NULL COMMENT '单品分销返现支出',
                                  `final_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '流水金额',
                                  `flow_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '流水类型',
                                  `goods_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '商品ID',
                                  `goods_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '商品名称',
                                  `image` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '图片',
                                  `member_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '会员ID',
                                  `member_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '会员名称',
                                  `num` int NULL DEFAULT NULL COMMENT '销售量',
                                  `order_item_sn` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '子订单编号',
                                  `payment_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '支付方式名称',
                                  `refund_sn` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '售后SN',
                                  `site_coupon_commission` decimal(10, 2) NULL DEFAULT NULL COMMENT '站点优惠券佣金',
                                  `site_coupon_point` decimal(10, 2) NULL DEFAULT NULL COMMENT '站点优惠券佣金比例',
                                  `site_coupon_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '平台优惠券使用金额',
                                  `sku_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '货品ID',
                                  `sn` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '编号',
                                  `specs` text CHARACTER SET utf8 COLLATE utf8_bin NULL COMMENT '规格',
                                  `store_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '店铺ID',
                                  `store_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '店铺名称',
                                  `transaction_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '第三方交易流水号',
                                  `order_sn` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                                  `point_settlement_price` decimal(10, 2) NULL DEFAULT 0 COMMENT '积分商品结算金额',
                                  `kanjia_settlement_price` decimal(10, 2) NULL DEFAULT 0 COMMENT '砍价商品结算金额',
                                  `order_promotion_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for li_store_goods_label
-- ----------------------------
DROP TABLE IF EXISTS `li_store_goods_label`;
CREATE TABLE `li_store_goods_label`  (
                                         `id` bigint NOT NULL COMMENT 'ID',
                                         `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
                                         `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                         `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                         `update_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者',
                                         `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                         `label_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '店铺商品分类名称',
                                         `level` int NULL DEFAULT NULL COMMENT '层级',
                                         `parent_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '父ID',
                                         `store_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '店铺ID',
                                         `sort_order` decimal(19, 2) NULL DEFAULT NULL COMMENT '店铺商品分类排序',
                                         PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_store_goods_label
-- ----------------------------


-- ----------------------------
-- Table structure for li_store_logistics
-- ----------------------------
DROP TABLE IF EXISTS `li_store_logistics`;
CREATE TABLE `li_store_logistics`  (
                                       `id` bigint NOT NULL COMMENT 'ID',
                                       `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
                                       `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                       `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志',
                                       `update_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者',
                                       `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                       `logistics_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '物流公司ID',
                                       `store_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '店铺ID',
                                       `customer_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '客户代码',
                                       `customer_pwd` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '客户密码',
                                       `month_code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '月结号/密钥',
                                       `send_site` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '归属网点',
                                       `send_staff` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '收件快递员',
                                       `face_sheet_flag` bit(1) NULL DEFAULT NULL COMMENT '是否使用电子面单',
                                       `pay_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '支付方式',
                                       `exp_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '快递类型',
                                       PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_store_logistics
-- ----------------------------
INSERT INTO `li_store_logistics` VALUES (1377051504787062784, '15810610731', '2021-03-30 19:13:57.778000', b'0', NULL, NULL, '1364825604276355072', '1376369067769724928', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `li_store_logistics` VALUES (1377051514584956928, '15810610731', '2021-03-30 19:14:00.114000', b'0', NULL, NULL, '1364825510772736000', '1376369067769724928', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `li_store_logistics` VALUES (1377051673758793728, '15810610731', '2021-03-30 19:14:38.064000', b'0', NULL, NULL, '1364825661964812288', '1376369067769724928', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `li_store_logistics` VALUES (1395296361108209664, '13011111111', '2021-05-20 16:32:30.496000', b'0', NULL, NULL, '1364825870564327424', '1376433565247471616', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `li_store_logistics` VALUES (1414854266305499138, '13011111111', '2021-07-13 15:48:38.465000', b'0', NULL, NULL, 'getChecked', '1376433565247471616', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `li_store_logistics` VALUES (1416973712306286594, '15810610731', '2021-07-19 12:10:33.772000', b'0', NULL, NULL, '1364825870564327424', '1376369067769724928', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `li_store_logistics` VALUES (1416973719675678721, '15810610731', '2021-07-19 12:10:35.527000', b'0', NULL, NULL, '1364825911689478144', '1376369067769724928', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `li_store_logistics` VALUES (1437578665634652162, '13011111111', '2021-09-14 08:47:17.464000', b'0', NULL, NULL, '1364825604276355072', '1376433565247471616', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `li_store_logistics` VALUES (1439150637015441410, '13011111111', '2021-09-18 16:53:44.647000', b'0', NULL, NULL, '1364825959177388032', '1376433565247471616', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `li_store_logistics` VALUES (1439150644204478465, '13011111111', '2021-09-18 16:53:46.362000', b'0', NULL, NULL, '1364825911689478144', '1376433565247471616', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `li_store_logistics` VALUES (1446383076662140930, '13011111111', '2021-10-08 15:52:52.632000', b'0', NULL, NULL, '1364825783545102336', '1376433565247471616', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `li_store_logistics` VALUES (1450761796751564802, '13011111111', '2021-10-20 17:52:20.854000', b'0', NULL, NULL, '1444568095427731457', '1376433565247471616', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `li_store_logistics` VALUES (1455392928579289089, '13011111111', '2021-11-02 12:34:48.723000', b'0', NULL, NULL, '1425471238789480450', '1376433565247471616', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `li_store_logistics` VALUES (1457526565349199874, '13011111111', '2021-11-08 09:53:07.371000', b'0', NULL, NULL, '1434793639171891201', '1376433565247471616', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `li_store_logistics` VALUES (1457878658006224897, '13011111111', '2021-11-09 09:12:12.802000', b'0', NULL, NULL, '1364825828369629184', '1376433565247471616', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `li_store_logistics` VALUES (1457907810545537025, '13011111111', '2021-11-09 11:08:03.309000', b'0', NULL, NULL, '1455424431237414914', '1376433565247471616', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `li_store_logistics` VALUES (1460165720222814210, '13011111111', '2021-11-15 16:40:10.926000', b'0', NULL, NULL, '1434004179756818434', '1376433565247471616', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `li_store_logistics` VALUES (1464121697213157377, '13011111111', '2021-11-26 14:39:49.340000', b'0', NULL, NULL, '1463775405668651010', '1376433565247471616', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `li_store_logistics` VALUES (1565246934112530433, '13011111111', '2022-09-01 15:55:24.642000', b'0', NULL, NULL, '1364825661964812288', '1376433565247471616', 'null', 'null', 'null', 'null', 'null', b'0', '1', '1');
INSERT INTO `li_store_logistics` VALUES (1565248087923617794, '13011111111', '2022-09-01 15:59:59.732000', b'0', NULL, NULL, '1364825703807188992', '1376433565247471616', '123', '123', '123', '123', '123', b'1', '1', '1');

-- ----------------------------
-- Table structure for li_store_menu
-- ----------------------------
DROP TABLE IF EXISTS `li_store_menu`;
CREATE TABLE `li_store_menu`  (
                                  `id` bigint NOT NULL COMMENT 'ID',
                                  `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '创建者',
                                  `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                  `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                  `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '更新者',
                                  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                                  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '说明备注',
                                  `front_route` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '前端路由',
                                  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '图标',
                                  `level` int NULL DEFAULT NULL COMMENT '层级',
                                  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '菜单/权限名称',
                                  `parent_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '父id',
                                  `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '赋权API地址,正则表达式',
                                  `sort_order` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '排序值',
                                  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '菜单标题',
                                  `permission` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '权限url',
                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_store_menu
-- ----------------------------
INSERT INTO `li_store_menu` VALUES (1348810750596767744, NULL, '2022-01-11 22:35:33.000000', b'0', NULL, '2022-01-11 22:36:52', NULL, 'Main', 'ios-american-football', 0, 'goods', '0', '/', '1', '商品', NULL);
INSERT INTO `li_store_menu` VALUES (1348810864748945408, NULL, '2022-01-11 22:35:37.000000', b'0', NULL, '2022-01-11 22:36:55', NULL, 'Main', 'ios-american-football', 1, 'Main', '1348810750596767744', '/', '1.1', '商品管理', '');
INSERT INTO `li_store_menu` VALUES (1349237129847005184, NULL, '2022-01-11 22:35:40.000000', b'0', NULL, '2022-01-11 22:36:59', NULL, 'goods-operation', 'ios-american-football', 2, 'goods-operation', '1348810864748945408', '/goods-operation', '1.11', '商品发布', '/store/goods/category*,/store/goods/categorySpec*,/store/goods/categoryParameters*,/store/goods/draftGoods*,/store/goods/label*,/store/goods/goods*,/store/goods/goodsUnit*,/store/goods/spec*');
INSERT INTO `li_store_menu` VALUES (1349237207378714624, NULL, '2022-01-11 22:35:42.000000', b'0', NULL, '2022-01-11 22:37:02', NULL, 'goods/goods-seller/goods', 'ios-american-football', 2, 'goods', '1348810864748945408', 'goods', '1.12', '商品列表', NULL);
INSERT INTO `li_store_menu` VALUES (1349237928434098176, NULL, '2022-01-11 22:35:45.000000', b'0', NULL, '2022-01-11 22:37:05', NULL, 'goods/goods-seller/draftGoods', 'ios-american-football', 2, 'template-goods', '1348810864748945408', 'template-goods', '1.13', '商品模板', NULL);
INSERT INTO `li_store_menu` VALUES (1349246048900243456, NULL, '2022-01-11 22:35:48.000000', b'0', NULL, '2022-01-11 22:37:08', NULL, 'goods/goods-manage/category', 'ios-american-football', 2, 'category', '1348810864748945408', 'category', '1.14', '店铺分类', NULL);
INSERT INTO `li_store_menu` VALUES (1349246347597602816, NULL, '2022-01-11 22:35:51.000000', b'0', NULL, '2022-01-11 22:37:11', NULL, 'Main', 'ios-american-football', 0, 'order', '0', '/', '2', '订单', NULL);
INSERT INTO `li_store_menu` VALUES (1349246468775239680, NULL, '2022-01-11 22:38:28.000000', b'0', NULL, '2022-01-11 22:38:31', NULL, 'Main', 'ios-american-football', 1, 'Main', '1349246347597602816', '/', '2.1', '订单管理', NULL);
INSERT INTO `li_store_menu` VALUES (1349246671158796288, NULL, '2022-01-11 22:38:42.000000', b'0', NULL, '2022-01-11 22:38:35', NULL, 'order/order/orderList', 'ios-american-football', 2, 'orderList', '1349246468775239680', 'orderList', '2.11', '商品订单', '/store/order/order*');
INSERT INTO `li_store_menu` VALUES (1349246896661356544, NULL, '2022-01-11 22:40:27.000000', b'0', NULL, '2022-01-11 22:40:34', NULL, 'order/order/virtualOrderList', 'ios-american-football', 2, 'virtualOrderList', '1349246468775239680', 'virtualOrderList', '2.12', '虚拟订单', '/store/order/order*');
INSERT INTO `li_store_menu` VALUES (1349247081504333824, NULL, '2022-01-11 22:41:47.000000', b'0', NULL, '2022-01-11 22:41:50', NULL, 'Main', 'ios-american-football', 1, 'Main', '1349246347597602816', '/', '2.2', '评价管理', NULL);
INSERT INTO `li_store_menu` VALUES (1349247640584085504, NULL, '2022-01-11 22:43:29.000000', b'0', NULL, '2022-01-11 22:45:47', NULL, 'member/memberComment', 'ios-american-football', 2, 'memberComment', '1349247081504333824', 'memberComment', '2.21', '评价管理', '/store/member/evaluation*');
INSERT INTO `li_store_menu` VALUES (1349254815809298432, NULL, '2022-01-11 22:45:35.000000', b'0', NULL, '2022-01-11 22:45:50', NULL, 'Main', 'ios-american-football', 1, 'Main', '1349246347597602816', '/', '2.3', '售后管理', NULL);
INSERT INTO `li_store_menu` VALUES (1349255214977015808, NULL, '2022-01-11 22:49:22.000000', b'0', NULL, '2022-01-11 22:49:27', NULL, 'order/after-order/returnGoodsOrder', 'ios-american-football', 2, 'returnGoodsOrder', '1349254815809298432', 'returnGoodsOrder', '2.31', '退货管理', '/store/order/afterSale*');
INSERT INTO `li_store_menu` VALUES (1349255404425338880, NULL, '2022-01-11 22:51:20.000000', b'0', NULL, '2022-01-11 22:51:22', NULL, 'order/after-order/returnMoneyOrder', 'ios-american-football', 2, 'returnMoneyOrder', '1349254815809298432', 'returnMoneyOrder', '2.32', '退款管理', '/store/order/afterSale*');
INSERT INTO `li_store_menu` VALUES (1349256082979840000, NULL, '2022-01-11 22:52:50.000000', b'0', NULL, '2022-01-11 22:52:55', NULL, 'order/after-order/orderComplaint', 'ios-american-football', 2, 'orderComplaint', '1349254815809298432', 'orderComplaint', '2.33', '投诉管理', '/store/order/complain*');
INSERT INTO `li_store_menu` VALUES (1357584224760102912, NULL, '2022-01-11 23:02:20.000000', b'0', NULL, '2022-01-11 23:02:25', NULL, 'Main', 'ios-american-football', 0, 'bill', '0', '/', '3', '财务', NULL);
INSERT INTO `li_store_menu` VALUES (1357873097859923969, NULL, '2022-01-11 23:04:13.000000', b'0', NULL, '2022-01-11 23:04:17', NULL, 'Main', 'ios-american-football', 1, 'Main', '1357584224760102912', '/', '3.1', '财务管理', '');
INSERT INTO `li_store_menu` VALUES (1367038467288072192, NULL, '2022-01-11 23:06:11.000000', b'0', NULL, '2022-01-11 23:06:16', NULL, 'shop/bill/accountStatementBill', 'ios-american-football', 2, 'accountStatementBill', '1357873097859923969', 'accountStatementBill', '3.11', '财务对账', '/store/order/bill*');
INSERT INTO `li_store_menu` VALUES (1367039534616805376, NULL, '2022-01-11 23:07:54.000000', b'0', NULL, '2022-01-11 23:07:57', NULL, 'shop/bill/storeBill', 'ios-american-football', 2, 'storeBill', '1357873097859923969', 'storeBill', '3.12', '店铺结算', '/store/order/bill*');
INSERT INTO `li_store_menu` VALUES (1367039950368800768, NULL, '2022-01-11 23:09:26.000000', b'0', NULL, '2022-01-11 23:09:29', NULL, 'Main', 'ios-american-football', 1, 'Main', '1357584224760102912', '/', '3.2', '发票管理', NULL);
INSERT INTO `li_store_menu` VALUES (1367040067201138688, NULL, '2022-01-11 23:11:14.000000', b'0', NULL, '2022-01-11 23:11:18', NULL, 'order/receiptManager/receipt', 'ios-american-football', 2, 'receipt', '1367039950368800768', 'receipt', '3.21', '发票管理', '/store/trade/receipt*');
INSERT INTO `li_store_menu` VALUES (1367040599596728320, NULL, '2022-01-11 23:12:49.000000', b'0', NULL, '2022-01-11 23:12:52', NULL, 'Main', 'ios-american-football', 0, 'promotion', '0', '/', '4', '营销', NULL);
INSERT INTO `li_store_menu` VALUES (1367040819248234496, NULL, '2022-01-11 23:14:35.000000', b'0', NULL, '2022-01-11 23:14:38', NULL, 'Main', 'ios-american-football', 1, 'Main', '1367040599596728320', '/', '4.1', '平台活动', NULL);
INSERT INTO `li_store_menu` VALUES (1367041332861730816, NULL, '2022-01-11 23:15:42.000000', b'0', NULL, '2022-01-11 23:15:48', NULL, 'promotion/pintuan/pintuan', 'ios-american-football', 2, 'pintuan', '1367040819248234496', 'pintuan', '4.11', '拼团活动', '/store/promotion/pintuan*');
INSERT INTO `li_store_menu` VALUES (1367041461194850304, NULL, '2022-01-11 23:17:32.000000', b'0', NULL, '2022-01-11 23:17:44', NULL, 'promotion/seckill/seckill', 'ios-american-football', 2, 'seckill', '1367040819248234496', 'seckill', '4.12', '秒杀活动', '/store/promotion/seckill*');
INSERT INTO `li_store_menu` VALUES (1367041575619657728, NULL, '2022-01-11 23:20:19.000000', b'0', NULL, '2022-01-11 23:20:24', NULL, 'Main', 'ios-american-football', 1, 'Main', '1367040599596728320', '/', '4.2', '直播活动', '');
INSERT INTO `li_store_menu` VALUES (1367042490443497472, NULL, '2022-01-11 23:21:13.000000', b'0', NULL, '2022-01-11 23:21:22', NULL, 'promotion/live/live', 'ios-american-football', 2, 'live', '1367041575619657728', 'live', '4.21', '直播管理', '/store/broadcast/studio*');
INSERT INTO `li_store_menu` VALUES (1367042664410644480, NULL, '2022-01-11 23:22:42.000000', b'0', NULL, '2022-01-11 23:22:59', NULL, 'promotion/live/liveGoods', 'ios-american-football', 2, 'liveGoods', '1367041575619657728', 'liveGoods', '4.22', '直播商品', '/store/broadcast/commodity*');
INSERT INTO `li_store_menu` VALUES (1367042804944994304, NULL, '2022-01-11 23:24:24.000000', b'0', NULL, '2022-01-11 23:24:38', NULL, 'Main', 'ios-american-football', 1, 'Main', '1367040599596728320', '/', '4.3', '商家活动', NULL);
INSERT INTO `li_store_menu` VALUES (1367042804944994305, NULL, '2022-01-11 23:24:29.000000', b'0', NULL, '2022-01-11 23:24:42', NULL, 'promotion/full-discount/full-discount', 'ios-american-football', 2, 'full-cut', '1367042804944994304', 'full-discount', '4.31', '满额活动', '/store/promotion/fullDiscount*');
INSERT INTO `li_store_menu` VALUES (1367042917113266176, NULL, '2022-01-11 23:26:45.000000', b'0', NULL, '2022-01-11 23:26:50', NULL, 'promotion/coupon/coupon', 'ios-american-football', 2, 'coupon', '1367042804944994304', 'coupon', '4.32', '优惠券', '/store/promotion/coupon*');
INSERT INTO `li_store_menu` VALUES (1367043020976816128, NULL, '2022-01-11 23:28:50.000000', b'0', NULL, '2022-01-11 23:29:02', NULL, 'Main', 'ios-american-football', 1, 'Main', '1367040599596728320', '/', '4.4', '分销管理', NULL);
INSERT INTO `li_store_menu` VALUES (1367043443917848576, NULL, '2022-01-11 23:28:53.000000', b'0', NULL, '2022-01-11 23:29:04', NULL, 'distribution/distributionGoods', 'ios-american-football', 2, 'distributionGoods', '1367043020976816128', 'distributionGoods', '4.41', '分销商品', '/store/distribution/goods*');
INSERT INTO `li_store_menu` VALUES (1367043505771249664, NULL, '2022-01-11 23:28:56.000000', b'0', NULL, '2022-01-11 23:29:07', NULL, 'distribution/distributionOrder', 'ios-american-football', 2, 'distributionOrder', '1367043020976816128', 'distributionOrderdistributionOrder', '4.42', '分销订单', '/store/distribution/order*');
INSERT INTO `li_store_menu` VALUES (1367044121163726848, NULL, '2022-01-12 21:47:03.000000', b'0', NULL, '2022-01-12 21:47:25', NULL, 'Main', 'ios-american-football', 0, 'statistics', '0', '/', '5', '统计', NULL);
INSERT INTO `li_store_menu` VALUES (1367044247978508288, NULL, '2022-01-12 21:47:19.000000', b'0', NULL, '2022-01-12 21:47:28', NULL, 'Main', 'ios-american-football', 1, 'Main', '1367044121163726848', '/', '5.1', '统计管理', NULL);
INSERT INTO `li_store_menu` VALUES (1367044376391319552, NULL, '2022-01-12 21:49:45.000000', b'0', NULL, '2022-01-12 21:50:01', NULL, 'statistics/goods', 'ios-american-football', 2, 'goodsStatistics', '1367044247978508288', 'goodsStatistics', '5.11', '商品统计', '/store/statistics/goods*');
INSERT INTO `li_store_menu` VALUES (1367044657296441344, NULL, '2022-01-12 21:49:48.000000', b'0', NULL, '2022-01-12 21:49:58', NULL, 'statistics/order', 'ios-american-football', 2, 'orderStatistics', '1367044247978508288', 'orderStatistics', '5.12', '订单统计', '/store/statistics/order*,/store/statistics/order*,/store/statistics/order*,/store/statistics/order*');
INSERT INTO `li_store_menu` VALUES (1367045529720061952, NULL, '2022-01-12 21:49:51.000000', b'0', NULL, '2022-01-12 21:50:03', NULL, 'statistics/traffic', 'ios-american-football', 2, 'trafficStatistics', '1367044247978508288', 'trafficStatistics', '5.13', '流量统计', '/store/statistics/view*');
INSERT INTO `li_store_menu` VALUES (1367045630710513664, NULL, '2022-01-12 21:52:59.000000', b'0', NULL, '2022-01-12 21:53:09', NULL, 'Main', 'ios-american-football', 0, 'settings', '0', '/', '6', '设置', NULL);
INSERT INTO `li_store_menu` VALUES (1367045794284175360, NULL, '2022-01-12 21:53:03.000000', b'0', NULL, '2022-01-12 21:53:12', NULL, 'Main', 'ios-american-football', 1, 'Main', '1367045630710513664', '/', '6.1', '配送设置', NULL);
INSERT INTO `li_store_menu` VALUES (1367045921434501120, NULL, '2022-01-12 21:55:49.000000', b'0', NULL, '2022-01-12 21:55:52', NULL, 'shop/ship/shipTemplate', 'ios-american-football', 2, 'shipTemplate', '1367045794284175360', 'shipTemplate', '6.11', '配送模板', '/store/setting/freightTemplate*');
INSERT INTO `li_store_menu` VALUES (1367046068369358848, NULL, '2022-01-12 21:58:05.000000', b'0', NULL, '2022-01-12 21:58:13', NULL, 'shop/ship/logistics', 'ios-american-football', 2, 'logistics', '1367045794284175360', 'logistics', '6.12', '物流公司', '/store/other/logistics*');
INSERT INTO `li_store_menu` VALUES (1367046266214678528, NULL, '2022-01-12 21:59:07.000000', b'0', NULL, '2022-01-12 21:59:43', NULL, 'Main', 'ios-american-football', 1, 'Main', '1367045630710513664', '/', '6.2', '店铺管理', NULL);
INSERT INTO `li_store_menu` VALUES (1367048084701315072, NULL, '2022-01-12 21:59:32.000000', b'0', NULL, '2022-01-12 21:59:48', NULL, 'shop/shopSetting', 'ios-american-football', 2, 'shopSetting', '1367046266214678528', 'shopSetting', '6.21', '店铺设置', NULL);
INSERT INTO `li_store_menu` VALUES (1367048684339986432, NULL, '2022-01-12 21:59:36.000000', b'0', NULL, '2022-01-12 21:59:51', NULL, 'shop/shopAddress', 'ios-american-football', 2, 'shopAddress', '1367046266214678528', 'shopAddress', '6.22', '自提管理', '/store/member/storeAddress*');
INSERT INTO `li_store_menu` VALUES (1367048754229673984, NULL, '2022-01-12 22:02:11.000000', b'0', NULL, '2022-01-12 22:04:36', NULL, 'Main', 'ios-american-football', 0, 'Main', '0', '/', '7', '消息', NULL);
INSERT INTO `li_store_menu` VALUES (1367048832210173952, NULL, '2022-01-12 22:02:49.000000', b'0', NULL, '2022-01-12 22:04:39', NULL, 'Main', 'ios-american-football', 1, 'Main', '1367048754229673984', '/', '7.1', '系统消息', NULL);
INSERT INTO `li_store_menu` VALUES (1367048967635861503, NULL, '2022-02-18 16:08:30.000000', b'0', NULL, '2022-02-18 16:08:36', NULL, 'Main', 'ios-american-football', 1, 'Main', '1367045630710513664', '/', '6.3', '店员设置', '');
INSERT INTO `li_store_menu` VALUES (1367048967635861504, NULL, '2022-01-12 22:02:51.000000', b'0', NULL, '2022-01-12 22:04:45', NULL, 'message', 'ios-american-football', 2, 'message_index', '1367048832210173952', 'message', '7.11', '系统消息', '/store/message/storeMessage*');
INSERT INTO `li_store_menu` VALUES (1367048967635861505, NULL, '2022-02-18 16:12:18.000000', b'0', NULL, '2022-02-18 16:12:21', NULL, 'shop/system/clerk/clerkManage', 'ios-american-football', 2, '\nclerkManage', '1367048967635861503', '\nclerkManage', '6.31', '店员管理', '/store/department*,/store/clerk*,/store/role*,/store/department*');
INSERT INTO `li_store_menu` VALUES (1367048967635861506, NULL, '2022-02-18 16:25:27.000000', b'0', NULL, '2022-02-18 16:25:31', NULL, 'shop/system/department/storeDepartmentManage', 'ios-american-football', 2, 'storeDepartmentManage', '1367048967635861503', 'storeDepartmentManage', '6.32', '部门管理', '/store/department*');
INSERT INTO `li_store_menu` VALUES (1367048967635861507, NULL, '2022-02-18 16:27:28.000000', b'0', NULL, '2022-02-18 16:27:30', NULL, 'shop/system/role/storeRoleManage', 'ios-american-football', 2, 'storeRoleManage', '1367048967635861503', 'storeRoleManage', '6.33', '角色权限', '/store/role*');
INSERT INTO `li_store_menu` VALUES (1367048967635861510, NULL, '2022-03-07 14:45:10.000000', b'0', NULL, '2022-03-07 14:45:13', NULL, 'shop/floorList', 'ios-american-football', 2, 'floorList', '1367046266214678528', 'floorList', '6.23', 'PC端', '/store/other/pageData*');
INSERT INTO `li_store_menu` VALUES (1367048967635861511, NULL, '2022-03-07 15:13:52.000000', b'0', NULL, '2022-03-07 15:13:55', NULL, 'shop/wap/wapList', 'ios-american-football', 2, 'wapList', '1367046266214678528', 'wapList', '6.24', '移动端', '/store/other/pageData*');

-- ----------------------------
-- Table structure for li_store_menu_role
-- ----------------------------
DROP TABLE IF EXISTS `li_store_menu_role`;
CREATE TABLE `li_store_menu_role`  (
                                       `id` bigint NOT NULL COMMENT 'ID',
                                       `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '创建者',
                                       `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                       `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                       `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '更新者',
                                       `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                       `role_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '角色id',
                                       `menu_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '菜单',
                                       `store_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '店铺id',
                                       `is_super` bit(1) NULL DEFAULT NULL COMMENT '是否拥有操作数据权限，为否则只有查看权限',
                                       PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_store_menu_role
-- ----------------------------


-- ----------------------------
-- Table structure for li_store_message
-- ----------------------------
DROP TABLE IF EXISTS `li_store_message`;
CREATE TABLE `li_store_message`  (
                                     `id` bigint NOT NULL COMMENT 'ID',
                                     `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建者',
                                     `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                     `message_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '关联消息ID',
                                     `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '状态',
                                     `store_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '关联店铺ID',
                                     `store_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '关联店铺名称',
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_store_message
-- ----------------------------

-- ----------------------------
-- Table structure for li_store_role
-- ----------------------------
DROP TABLE IF EXISTS `li_store_role`;
CREATE TABLE `li_store_role`  (
                                  `id` bigint NOT NULL COMMENT 'ID',
                                  `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '创建者',
                                  `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                  `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                  `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '更新者',
                                  `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '角色名称',
                                  `store_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '店铺id',
                                  `default_role` bit(1) NULL DEFAULT NULL COMMENT '是否为注册默认角色',
                                  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_store_role
-- ----------------------------
INSERT INTO `li_store_role` VALUES (1481639692985126913, '15810610731', '2022-01-13 22:50:05.000000', b'0', '15810610731', '2022-01-13 23:01:50.177000', '财务', '1376369067769724928', b'0', '1');
INSERT INTO `li_store_role` VALUES (1582976795065167874, '13011111111', '2022-10-20 14:07:32.894000', b'0', NULL, NULL, '营销', '1376433565247471616', b'0', 'jack');

-- ----------------------------
-- Table structure for li_studio
-- ----------------------------
DROP TABLE IF EXISTS `li_studio`;
CREATE TABLE `li_studio`  (
                              `id` bigint NOT NULL,
                              `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                              `create_time` datetime(6) NULL DEFAULT NULL,
                              `delete_flag` bit(1) NULL DEFAULT NULL,
                              `update_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                              `update_time` datetime(6) NULL DEFAULT NULL,
                              `anchor_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                              `anchor_wechat` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                              `cover_img` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                              `end_time` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                              `feeds_img` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                              `media_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                              `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                              `qr_code_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                              `recommend` bit(1) NOT NULL,
                              `room_goods_list` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                              `room_goods_num` int NULL DEFAULT 0,
                              `room_id` int NULL DEFAULT NULL,
                              `share_img` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                              `start_time` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                              `status` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                              `store_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
                              PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_studio
-- ----------------------------

-- ----------------------------
-- Table structure for li_studio_commodity
-- ----------------------------
DROP TABLE IF EXISTS `li_studio_commodity`;
CREATE TABLE `li_studio_commodity`  (
                                        `id` bigint NOT NULL,
                                        `goods_id` int NULL DEFAULT NULL,
                                        `room_id` int NULL DEFAULT NULL,
                                        PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_studio_commodity
-- ----------------------------
INSERT INTO `li_studio_commodity` VALUES (1412252164404080641, 50, 30);

-- ----------------------------
-- Table structure for li_trade
-- ----------------------------
DROP TABLE IF EXISTS `li_trade`;
CREATE TABLE `li_trade`  (
                             `id` bigint NOT NULL COMMENT 'ID',
                             `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
                             `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                             `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志',
                             `update_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者',
                             `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                             `consignee_address_id_path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '地址id',
                             `consignee_address_path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '地址名称',
                             `consignee_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '收货人姓名',
                             `discount_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '优惠的金额',
                             `flow_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '总价格',
                             `freight_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '运费',
                             `goods_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '原价',
                             `member_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '买家id',
                             `member_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '买家用户名',
                             `payment_method` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '支付方式',
                             `sn` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '交易编号',
                             `delivery_method` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '配送方式',
                             `consignee_mobile` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '收件人手机',
                             `pay_status` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                             PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for li_user_role
-- ----------------------------
DROP TABLE IF EXISTS `li_user_role`;
CREATE TABLE `li_user_role`  (
                                 `id` bigint NOT NULL COMMENT 'ID',
                                 `role_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '角色唯一id',
                                 `user_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户唯一id',
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_user_role
-- ---------------------------
-- ----------------------------
-- Table structure for li_verification_source
-- ----------------------------
DROP TABLE IF EXISTS `li_verification_source`;
CREATE TABLE `li_verification_source`  (
                                           `id` bigint NOT NULL COMMENT 'ID',
                                           `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '创建者',
                                           `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                           `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                           `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '更新者',
                                           `update_time` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                           `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '名称',
                                           `resource` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '资源地址',
                                           `type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '验证码资源类型',
                                           PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_verification_source
-- ----------------------------

-- ----------------------------
-- Table structure for li_wallet_log
-- ----------------------------
DROP TABLE IF EXISTS `li_wallet_log`;
CREATE TABLE `li_wallet_log`  (
                                  `id` bigint NOT NULL COMMENT 'ID',
                                  `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '创建者',
                                  `create_time` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                  `detail` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '日志明细',
                                  `member_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '会员id',
                                  `member_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '会员名称',
                                  `money` decimal(10, 2) NULL DEFAULT NULL COMMENT '金额',
                                  `service_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '业务类型',
                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for li_wechat_message
-- ----------------------------
DROP TABLE IF EXISTS `li_wechat_message`;
CREATE TABLE `li_wechat_message`  (
                                      `id` bigint NOT NULL,
                                      `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                      `create_time` datetime(6) NULL DEFAULT NULL,
                                      `delete_flag` bit(1) NULL DEFAULT NULL,
                                      `update_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                      `update_time` datetime(6) NULL DEFAULT NULL,
                                      `code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                      `enable` bit(1) NULL DEFAULT NULL,
                                      `first` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                      `keywords` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                      `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                      `order_status` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                      `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                      PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_wechat_message
-- ----------------------------

-- ----------------------------
-- Table structure for li_wechat_mp_message
-- ----------------------------
DROP TABLE IF EXISTS `li_wechat_mp_message`;
CREATE TABLE `li_wechat_mp_message`  (
                                         `id` bigint NOT NULL,
                                         `create_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                         `create_time` datetime(6) NULL DEFAULT NULL,
                                         `delete_flag` bit(1) NULL DEFAULT NULL,
                                         `update_by` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                         `update_time` datetime(6) NULL DEFAULT NULL,
                                         `code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                         `enable` bit(1) NULL DEFAULT NULL,
                                         `keywords` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                         `keywords_text` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                         `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                         `order_status` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                         `template_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
                                         PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_wechat_mp_message
-- ----------------------------
INSERT INTO `li_wechat_mp_message` VALUES (1466026565142130690, 'admin', '2021-12-01 20:49:05.243000', b'0', NULL, NULL, 'LL33E9vpSiFw-rgM6Sh_dtvwZ-Smt345lLwhgJrD-Z8', b'1', '[\"ORDER_SN\",\"MEMBER_NAME\",\"PRICE\",\"GOODS_INFO\"]', '[\"character_string9\",\"thing23\",\"amount12\",\"thing24\"]', '订单支付成功,准备发货', 'UNDELIVERED', '487');
INSERT INTO `li_wechat_mp_message` VALUES (1466026569038639106, 'admin', '2021-12-01 20:49:06.173000', b'0', NULL, NULL, '8UqLGc3kBUDN3md_czOLARv-_SQuuNPevYIAnf0yf4s', b'1', '[\"ORDER_SN\",\"GOODS_INFO\",\"LOGISTICS_NAME\",\"LOGISTICS_NO\"]', '[\"character_string15\",\"thing23\",\"thing14\",\"character_string13\"]', '订单发货成功', 'DELIVERED', '374');
INSERT INTO `li_wechat_mp_message` VALUES (1466026574944219137, 'admin', '2021-12-01 20:49:07.581000', b'0', NULL, NULL, 'knckkHDSRC7eYuX_4sOJaaVg2owWgiB9v07KbkHae18', b'1', '[\"SHOP_NAME\",\"GOODS_INFO\"]', '[\"thing1\",\"thing3\"]', '订单完成', 'COMPLETED', '3606');

-- ----------------------------
-- Table structure for li_wholesale
-- ----------------------------
DROP TABLE IF EXISTS `li_wholesale`;
CREATE TABLE `li_wholesale`  (
                                 `id` bigint NOT NULL,
                                 `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                                 `create_time` datetime(6) NULL DEFAULT NULL,
                                 `delete_flag` bit(1) NULL DEFAULT NULL,
                                 `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                                 `update_time` datetime(6) NULL DEFAULT NULL,
                                 `price` decimal(10, 2) NULL DEFAULT NULL COMMENT '价格',
                                 `goods_id` bigint NULL DEFAULT NULL COMMENT '商品id',
                                 `sku_id` bigint NULL DEFAULT NULL COMMENT '商品skuId',
                                 `num` int NULL DEFAULT NULL COMMENT '起购量',
                                 `template_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '批发规则表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of li_wholesale
-- ----------------------------

-- ----------------------------
-- Table structure for payment_log
-- ----------------------------
DROP TABLE IF EXISTS `payment_log`;
CREATE TABLE `payment_log`  (
                                `id` bigint NOT NULL,
                                `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                                `create_time` datetime(6) NULL DEFAULT NULL,
                                `delete_flag` bit(1) NULL DEFAULT NULL,
                                `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                                `update_time` datetime(6) NULL DEFAULT NULL,
                                `client_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                                `flow_price` decimal(10, 2) NULL DEFAULT NULL,
                                `member_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                                `member_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                                `order_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                                `pay_order_no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                                `pay_status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                                `payment_method` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                                `payment_time` datetime(6) NULL DEFAULT NULL,
                                `receivable_no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                                `sn` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                                `store_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                                `store_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                                `trade_sn` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
                                PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of payment_log
-- ----------------------------


DROP TABLE IF EXISTS `li_im_talk`;
CREATE TABLE `li_im_talk` (
                              `id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                              `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建者',
                              `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                              `delete_flag` bit(1) DEFAULT NULL COMMENT '删除标志',
                              `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '修改者',
                              `update_time` datetime(6) DEFAULT NULL COMMENT '修改时间',
                              `user_id1` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户1Id',
                              `user_id2` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户2Id',
                              `name1` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户1名称',
                              `name2` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户2名称',
                              `face1` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户1头像',
                              `face2` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户2头像',
                              `top1` bit(1) DEFAULT NULL COMMENT '用户1置顶标识',
                              `top2` bit(1) DEFAULT NULL COMMENT '用户2置顶标识',
                              `disable1` bit(1) DEFAULT NULL COMMENT '用户1禁用标识',
                              `disable2` bit(1) DEFAULT NULL COMMENT '用户2禁用标识',
                              `store_flag1` bit(1) DEFAULT NULL COMMENT '用户1店铺标识',
                              `store_flag2` bit(1) DEFAULT NULL COMMENT '用户2店铺标识',
                              `last_talk_time` datetime DEFAULT NULL COMMENT '最后聊天时间',
                              `last_talk_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '最后的消息',
                              `last_message_type` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '最后消息类型',
                              `talk_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '聊天Id',
                              `tenant_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '坐席Id',
                              `tenant_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '坐席名称',
                              PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


/**
  IM消息
 */
DROP TABLE IF EXISTS `li_im_message`;
CREATE TABLE `li_im_message` (
                                 `id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                 `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建者',
                                 `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                 `delete_flag` bit(1) DEFAULT NULL COMMENT '删除标识',
                                 `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '修改者',
                                 `update_time` datetime(6) DEFAULT NULL COMMENT '修改标识',
                                 `from_user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '发送用户Id',
                                 `to_user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '接收用户Id',
                                 `is_read` bit(1) DEFAULT NULL COMMENT '已读标识',
                                 `message_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '聊天类型',
                                 `text` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '聊天内容',
                                 `talk_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '聊天Id',
                                 PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for xxl_job_group
-- ----------------------------
DROP TABLE IF EXISTS `xxl_job_group`;
CREATE TABLE `xxl_job_group`  (
                                  `id` int NOT NULL AUTO_INCREMENT,
                                  `app_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '执行器AppName',
                                  `title` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '执行器名称',
                                  `address_type` tinyint NOT NULL DEFAULT 0 COMMENT '执行器地址类型：0=自动注册、1=手动录入',
                                  `address_list` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '执行器地址列表，多地址逗号分隔',
                                  `update_time` datetime NULL DEFAULT NULL,
                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of xxl_job_group
-- ----------------------------
INSERT INTO `xxl_job_group` VALUES (1, 'xxl-job-exec-sample', '示例执行器', 0, NULL, '2022-01-09 20:31:39');
INSERT INTO `xxl_job_group` VALUES (2, 'xxl-job-exec-lilishop', 'lilishop', 0, NULL, '2022-01-09 20:31:39');

-- ----------------------------
-- Table structure for xxl_job_info
-- ----------------------------
DROP TABLE IF EXISTS `xxl_job_info`;
CREATE TABLE `xxl_job_info`  (
                                 `id` int NOT NULL AUTO_INCREMENT,
                                 `job_group` int NOT NULL COMMENT '执行器主键ID',
                                 `job_desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                                 `add_time` datetime NULL DEFAULT NULL,
                                 `update_time` datetime NULL DEFAULT NULL,
                                 `author` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '作者',
                                 `alarm_email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '报警邮件',
                                 `schedule_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'NONE' COMMENT '调度类型',
                                 `schedule_conf` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '调度配置，值含义取决于调度类型',
                                 `misfire_strategy` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'DO_NOTHING' COMMENT '调度过期策略',
                                 `executor_route_strategy` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '执行器路由策略',
                                 `executor_handler` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '执行器任务handler',
                                 `executor_param` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '执行器任务参数',
                                 `executor_block_strategy` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '阻塞处理策略',
                                 `executor_timeout` int NOT NULL DEFAULT 0 COMMENT '任务执行超时时间，单位秒',
                                 `executor_fail_retry_count` int NOT NULL DEFAULT 0 COMMENT '失败重试次数',
                                 `glue_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'GLUE类型',
                                 `glue_source` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'GLUE源代码',
                                 `glue_remark` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'GLUE备注',
                                 `glue_updatetime` datetime NULL DEFAULT NULL COMMENT 'GLUE更新时间',
                                 `child_jobid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '子任务ID，多个逗号分隔',
                                 `trigger_status` tinyint NOT NULL DEFAULT 0 COMMENT '调度状态：0-停止，1-运行',
                                 `trigger_last_time` bigint NOT NULL DEFAULT 0 COMMENT '上次调度时间',
                                 `trigger_next_time` bigint NOT NULL DEFAULT 0 COMMENT '下次调度时间',
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of xxl_job_info
-- ----------------------------
INSERT INTO `xxl_job_info` VALUES (1, 1, '测试任务1', '2018-11-03 22:21:31', '2018-11-03 22:21:31', 'XXL', '', 'CRON', '0 0 0 * * ? *', 'DO_NOTHING', 'FIRST', 'demoJobHandler', '', 'SERIAL_EXECUTION', 0, 0, 'BEAN', '', 'GLUE代码初始化', '2018-11-03 22:21:31', '', 0, 0, 0);
INSERT INTO `xxl_job_info` VALUES (2, 2, '每小时执行任务', '2020-12-24 11:01:24', '2020-12-24 15:03:03', 'admin', '', 'CRON', '0 0 0/1 * * ?', 'DO_NOTHING', 'ROUND', 'everyHourExecuteJobHandler', '', 'SERIAL_EXECUTION', 0, 0, 'BEAN', '', 'GLUE代码初始化', '2020-12-24 11:01:24', '', 1, 1641729600000, 1641733200000);
INSERT INTO `xxl_job_info` VALUES (3, 2, '每分钟执行', '2020-12-24 11:02:58', '2020-12-24 15:02:49', 'admin', '', 'CRON', '0 0/1 * * * ?', 'DO_NOTHING', 'ROUND', 'everyMinuteExecute', '', 'SERIAL_EXECUTION', 0, 0, 'BEAN', '', 'GLUE代码初始化', '2020-12-24 11:02:58', '', 1, 1641731460000, 1641731520000);
INSERT INTO `xxl_job_info` VALUES (4, 2, '每天执行', '2020-12-24 11:03:41', '2020-12-24 15:02:28', 'admin', '', 'CRON', '0 5 2 1/1 * ?', 'DO_NOTHING', 'ROUND', 'everyDayExecuteJobHandler', '', 'SERIAL_EXECUTION', 0, 0, 'BEAN', '', 'GLUE代码初始化', '2020-12-24 11:03:41', '', 1, 1641665100000, 1641751500000);

-- ----------------------------
-- Table structure for xxl_job_lock
-- ----------------------------
DROP TABLE IF EXISTS `xxl_job_lock`;
CREATE TABLE `xxl_job_lock`  (
                                 `lock_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '锁名称',
                                 PRIMARY KEY (`lock_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of xxl_job_lock
-- ----------------------------
INSERT INTO `xxl_job_lock` VALUES ('schedule_lock');

-- ----------------------------
-- Table structure for xxl_job_log
-- ----------------------------
DROP TABLE IF EXISTS `xxl_job_log`;
CREATE TABLE `xxl_job_log`  (
                                `id` bigint NOT NULL AUTO_INCREMENT,
                                `job_group` int NOT NULL COMMENT '执行器主键ID',
                                `job_id` int NOT NULL COMMENT '任务，主键ID',
                                `executor_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '执行器地址，本次执行的地址',
                                `executor_handler` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '执行器任务handler',
                                `executor_param` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '执行器任务参数',
                                `executor_sharding_param` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '执行器任务分片参数，格式如 1/2',
                                `executor_fail_retry_count` int NOT NULL DEFAULT 0 COMMENT '失败重试次数',
                                `trigger_time` datetime NULL DEFAULT NULL COMMENT '调度-时间',
                                `trigger_code` int NOT NULL COMMENT '调度-结果',
                                `trigger_msg` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '调度-日志',
                                `handle_time` datetime NULL DEFAULT NULL COMMENT '执行-时间',
                                `handle_code` int NOT NULL COMMENT '执行-状态',
                                `handle_msg` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '执行-日志',
                                `alarm_status` tinyint NOT NULL DEFAULT 0 COMMENT '告警状态：0-默认、1-无需告警、2-告警成功、3-告警失败',
                                PRIMARY KEY (`id`) USING BTREE,
                                INDEX `I_trigger_time`(`trigger_time` ASC) USING BTREE,
                                INDEX `I_handle_code`(`handle_code` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of xxl_job_log
-- ----------------------------

-- ----------------------------
-- Table structure for xxl_job_log_report
-- ----------------------------
DROP TABLE IF EXISTS `xxl_job_log_report`;
CREATE TABLE `xxl_job_log_report`  (
                                       `id` int NOT NULL AUTO_INCREMENT,
                                       `trigger_day` datetime NULL DEFAULT NULL COMMENT '调度-时间',
                                       `running_count` int NOT NULL DEFAULT 0 COMMENT '运行中-日志数量',
                                       `suc_count` int NOT NULL DEFAULT 0 COMMENT '执行成功-日志数量',
                                       `fail_count` int NOT NULL DEFAULT 0 COMMENT '执行失败-日志数量',
                                       `update_time` datetime NULL DEFAULT NULL,
                                       PRIMARY KEY (`id`) USING BTREE,
                                       UNIQUE INDEX `i_trigger_day`(`trigger_day` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 189 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for xxl_job_logglue
-- ----------------------------
DROP TABLE IF EXISTS `xxl_job_logglue`;
CREATE TABLE `xxl_job_logglue`  (
                                    `id` int NOT NULL AUTO_INCREMENT,
                                    `job_id` int NOT NULL COMMENT '任务，主键ID',
                                    `glue_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'GLUE类型',
                                    `glue_source` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'GLUE源代码',
                                    `glue_remark` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'GLUE备注',
                                    `add_time` datetime NULL DEFAULT NULL,
                                    `update_time` datetime NULL DEFAULT NULL,
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of xxl_job_logglue
-- ----------------------------

-- ----------------------------
-- Table structure for xxl_job_registry
-- ----------------------------
DROP TABLE IF EXISTS `xxl_job_registry`;
CREATE TABLE `xxl_job_registry`  (
                                     `id` int NOT NULL AUTO_INCREMENT,
                                     `registry_group` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                                     `registry_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                                     `registry_value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                                     `update_time` datetime NULL DEFAULT NULL,
                                     PRIMARY KEY (`id`) USING BTREE,
                                     INDEX `i_g_k_v`(`registry_group` ASC, `registry_key` ASC, `registry_value` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of xxl_job_registry
-- ----------------------------

-- ----------------------------
-- Table structure for xxl_job_user
-- ----------------------------
DROP TABLE IF EXISTS `xxl_job_user`;
CREATE TABLE `xxl_job_user`  (
                                 `id` int NOT NULL AUTO_INCREMENT,
                                 `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '账号',
                                 `password` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
                                 `role` tinyint NOT NULL COMMENT '角色：0-普通用户、1-管理员',
                                 `permission` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '权限：执行器ID列表，多个逗号分割',
                                 PRIMARY KEY (`id`) USING BTREE,
                                 UNIQUE INDEX `i_username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of xxl_job_user
-- ----------------------------
INSERT INTO `xxl_job_user` VALUES (1, 'admin', '96e79218965eb72c92a549dd5a330112', 1, NULL);

SET FOREIGN_KEY_CHECKS = 1;

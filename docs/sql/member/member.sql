-- ----------------------------
-- Table structure for member
-- ----------------------------
DROP TABLE IF EXISTS `member`;
CREATE TABLE `member`  (
                              `id` bigint NOT NULL COMMENT 'ID',
                              `create_by` varchar(255)COMMENT '创建者',
                              `create_at` datetime(6) COMMENT '创建时间',
                              `delete_flag` bit(1) COMMENT '删除标志 true/false 删除/未删除',
                              `update_by` varchar(255)COMMENT '更新者',
                              `update_at` datetime(6) COMMENT '更新时间',
                              `birthday` datetime(6) COMMENT '会员生日',
                              `disabled` bit(1) COMMENT '会员状态',
                              `face` varchar(255)COMMENT '会员头像',
                              `have_store` bit(1) COMMENT '是否开通店铺',
                              `mobile` varchar(255)COMMENT '手机号码',
                              `nick_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '会员昵称',
                              `password` varchar(255)COMMENT '会员密码',
                              `point` bigint NULL DEFAULT 0 COMMENT '积分数量',
                              `sex` int NOT NULL DEFAULT 0 COMMENT '会员性别',
                              `store_id` bigint COMMENT '店铺ID',
                              `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '会员用户名',
                              `region` varchar(255)COMMENT '会员地址',
                              `region_id` bigint COMMENT '会员地址ID',
                              `client_enum` varchar(255)COMMENT '客户端',
                              `last_login_date` datetime(6) COMMENT '最后一次登录时间',
                              `gradeId` varchar(32)NULL DEFAULT NULL,
                              `experience` bigint NULL DEFAULT NULL,
                              `grade_id` varchar(255)NULL DEFAULT NULL,
                              `total_point` bigint NULL DEFAULT 0,
                              PRIMARY KEY (`id`) USING BTREE ,
                              UNIQUE INDEX `username`(`username` ASC) USING BTREE COMMENT 'username唯一索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of member
-- ----------------------------
INSERT INTO `member` VALUES (1376364798236688384, NULL, '2021-03-28 21:45:14.178000', b'0', '15810610731', '2022-01-10 21:13:48.278000', '2021-03-28 00:00:00.000000', b'1', 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/00218693d2e64c9893b269d1aec77f94.jpeg', b'1', '15810610731', '星星眨眨眼,月儿画问号', '$2a$10$FhOh0wqan7PCwFh96WXyqOcdxh.FQkUuuKBh745LyHR4Tkg0khiDO', 41601, 1, '1376369067769724928', '15810610731', '上海市 , 上海城区 , 浦东新区 , 高东镇', '1371783040048562731,1371783040048562732,1371783040048562733,1371783040048562734', 'PC', '2022-01-10 21:13:48.232000', NULL, NULL, NULL, 44717);
INSERT INTO `member` VALUES (1376417684140326912, NULL, '2021-03-29 01:15:23.159000', b'0', '13011111111', '2021-12-22 16:07:27.926000', '2021-11-28 00:00:00.000000', b'1', 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/cec8b43b6bad41c69f393e93c55c528f.jpg', b'1', '13011111111', '张三', '$2a$10$s9xnEGxRDdXRzvK/n2SNKu0aVz2Tq6Y8Sw04tyLzaOs3077jQlLMi', 55790972, 1, '1376433565247471616', '13011111111', '新疆维吾尔自治区,铁门关市,双丰镇', '', 'PC', '2021-12-22 16:07:27.915000', NULL, NULL, NULL, 56334259);

-- ----------------------------
-- Table structure for member_address
-- ----------------------------
DROP TABLE IF EXISTS `member_address`;
CREATE TABLE `member_address`  (
                                      `id` bigint NOT NULL COMMENT 'ID',
                                      `create_by` varchar(255)COMMENT '创建者',
                                      `create_at` datetime(6) COMMENT '创建时间',
                                      `delete_flag` bit(1) COMMENT '删除标志 true/false 删除/未删除',
                                      `update_by` varchar(255)COMMENT '更新者',
                                      `update_at` datetime(6) COMMENT '更新时间',
                                      `alias` varchar(255)COMMENT '地址别名',
                                      `consignee_address_id_path` varchar(255)COMMENT '地址ID',
                                      `consignee_address_path` varchar(255)COMMENT '地址名称',
                                      `detail` varchar(255)COMMENT '详细地址',
                                      `is_default` bit(1) COMMENT '是否为默认收货地址',
                                      `lat` varchar(255)COMMENT '纬度',
                                      `lon` varchar(255)COMMENT '经度',
                                      `member_id` varchar(255)COMMENT '会员ID',
                                      `mobile` varchar(255)COMMENT '手机号码',
                                      `name` varchar(255)COMMENT '收货人姓名',
                                      PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of member_address
-- ----------------------------
INSERT INTO `member_address` VALUES (1464108195383668738, '13011111111', '2021-11-26 13:46:10.000000', b'0', '13011111111', '2021-11-27 04:08:15.042000', 'null', '1401797451504943104,1401797451509137679,1401797451513331719,1401797451513331723', '广东省,深圳市,福田区,华强北街道', '振兴路341号上步工业区', b'0', '22.547', '114.085947', '1376417684140326912', '13333333333', '1231');
INSERT INTO `member_address` VALUES (1464305901209186305, '13011111111', '2021-11-27 02:51:46.000000', b'0', '13011111111', '2021-11-27 02:51:51.378000', '谁打上', '1401797451504943104,1401797451504943105,1401797451504943106,1401797451504943107', '广东省,汕头市,潮阳区,和平镇', '大幅度发的', b'0', '116.519', '23.2664', '1376417684140326912', '13011111111', '分担分担');
INSERT INTO `member_address` VALUES (1466246586711756802, '13011111111', '2021-12-02 11:23:22.474000', b'0', NULL, NULL, NULL, '1401797451790156446,1401797451798544836,1401797451798545015,1401797451798545026', '江西省,上饶市,鄱阳县,田畈街镇', '庙下', b'1', '29.319165', '116.889374', '1376417684140326912', '18162305819', '王八蛋');

-- ----------------------------
-- Table structure for member_coupon
-- ----------------------------
DROP TABLE IF EXISTS `member_coupon`;
CREATE TABLE `member_coupon`  (
                                     `id` bigint NOT NULL COMMENT 'ID',
                                     `create_by` varchar(255)COMMENT '创建者',
                                     `create_at` datetime(6) COMMENT '创建时间',
                                     `delete_flag` bit(1) COMMENT '删除标志 true/false 删除/未删除',
                                     `update_by` varchar(255)COMMENT '更新者',
                                     `update_at` datetime(6) COMMENT '更新时间',
                                     `consume_threshold` decimal(10, 2) COMMENT '消费门槛',
                                     `consumption_time` datetime(6) COMMENT '核销时间',
                                     `coupon_id` varchar(255)COMMENT '优惠券ID',
                                     `coupon_type` varchar(255)COMMENT '活动类型',
                                     `discount` decimal(10, 2) COMMENT '折扣',
                                     `end_time` datetime(6) COMMENT '使用截止时间',
                                     `get_type` varchar(255)COMMENT '优惠券类型',
                                     `platform_flag` bit(1) COMMENT '是否是平台优惠券',
                                     `member_coupon_status` varchar(255)COMMENT '会员优惠券状态',
                                     `member_id` varchar(255)COMMENT '会员ID',
                                     `member_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '会员名称',
                                     `price` decimal(10, 2) COMMENT '面额',
                                     `scope_id` varchar(255)COMMENT '范围关联的ID',
                                     `scope_type` varchar(255)COMMENT '关联范围类型',
                                     `store_commission` decimal(10, 2) COMMENT '店铺承担比例',
                                     `store_id` varchar(255)COMMENT '店铺ID',
                                     `start_time` datetime(6) COMMENT '使用起始时间',
                                     `store_name` varchar(255)COMMENT '店铺名称',
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of member_coupon
-- ----------------------------

-- ----------------------------
-- Table structure for member_coupon_sign
-- ----------------------------
DROP TABLE IF EXISTS `member_coupon_sign`;
CREATE TABLE `member_coupon_sign`  (
                                          `id` bigint NOT NULL,
                                          `coupon_activity_Id` bigint COMMENT '优惠券活动id',
                                          `member_id` bigint COMMENT '会员id',
                                          `invalid_time` datetime COMMENT '过期时间',
                                          `create_at` datetime COMMENT '创建时间',
                                          PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of member_coupon_sign
-- ----------------------------

-- ----------------------------
-- Table structure for member_evaluation
-- ----------------------------
DROP TABLE IF EXISTS `member_evaluation`;
CREATE TABLE `member_evaluation`  (
                                         `id` bigint NOT NULL COMMENT 'ID',
                                         `create_by` varchar(255)COMMENT '创建者',
                                         `create_at` datetime(6) COMMENT '创建时间',
                                         `delete_flag` bit(1) COMMENT '删除标志 true/false 删除/未删除',
                                         `update_by` varchar(255)COMMENT '更新者',
                                         `update_at` datetime(6) COMMENT '更新时间',
                                         `content` varchar(500)COMMENT '评价内容',
                                         `goods_id` bigint COMMENT '商品ID',
                                         `goods_image` varchar(255)COMMENT '商品图片',
                                         `goods_name` varchar(255)COMMENT '商品名称',
                                         `grade` varchar(255)COMMENT '好中差评',
                                         `have_image` bit(1) COMMENT '评价是否有图片',
                                         `have_reply_image` bit(1) COMMENT '回复是否有图片',
                                         `images` textNULL COMMENT '评价图片',
                                         `member_id` varchar(255)COMMENT '会员ID',
                                         `member_name` varchar(255)COMMENT '会员名称',
                                         `member_profile` varchar(255)COMMENT '会员头像',
                                         `order_no` varchar(255)COMMENT '订单号',
                                         `reply` varchar(255)COMMENT '评价回复',
                                         `reply_image` textNULL COMMENT '评价回复图片',
                                         `store_id` varchar(255)COMMENT '店铺ID',
                                         `sku_id` varchar(255)COMMENT '货品ID',
                                         `status` varchar(255)COMMENT '状态',
                                         `reply_status` bit(1) COMMENT '回复状态',
                                         `delivery_score` int COMMENT '物流评分',
                                         `description_score` int COMMENT '描述评分',
                                         `service_score` int COMMENT '服务评分',
                                         `store_name` varchar(255)COMMENT '店铺名称',
                                         PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of member_evaluation
-- ----------------------------

-- ----------------------------
-- Table structure for member_grade
-- ----------------------------
DROP TABLE IF EXISTS `member_grade`;
CREATE TABLE `member_grade`  (
                                    `id` bigint NOT NULL,
                                    `create_by` varchar(255) ,
                                    `create_at` datetime(6) NULL DEFAULT NULL,
                                    `delete_flag` bit(1) NULL DEFAULT NULL,
                                    `update_by` varchar(255) ,
                                    `update_at` datetime(6) NULL DEFAULT NULL,
                                    `experience_value` int NOT NULL,
                                    `grade_image` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                                    `grade_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                                    `is_default` bit(1) NULL DEFAULT NULL,
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of member_grade
-- ----------------------------

-- ----------------------------
-- Table structure for member_message
-- ----------------------------
DROP TABLE IF EXISTS `member_message`;
CREATE TABLE `member_message`  (
                                      `id` bigint NOT NULL COMMENT 'ID',
                                      `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '创建者',
                                      `create_at` datetime COMMENT '创建时间',
                                      `delete_flag` bit(1) COMMENT '删除标志',
                                      `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '更新者',
                                      `update_at` datetime COMMENT '更新时间',
                                      `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '消息内容',
                                      `member_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '会员ID',
                                      `member_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '会员名称',
                                      `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '状态',
                                      `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '消息标题',
                                      `message_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                                      PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of member_message
-- ----------------------------

-- ----------------------------
-- Table structure for member_notice
-- ----------------------------
DROP TABLE IF EXISTS `member_notice`;
CREATE TABLE `member_notice`  (
                                     `id` bigint NOT NULL COMMENT 'ID',
                                     `create_by` varchar(255)COMMENT '创建者',
                                     `create_at` datetime(6) COMMENT '创建时间',
                                     `delete_flag` bit(1) COMMENT '删除标志 true/false 删除/未删除',
                                     `update_by` varchar(255)COMMENT '更新者',
                                     `update_at` datetime(6) COMMENT '更新时间',
                                     `content` varchar(255)COMMENT '站内信内容',
                                     `is_read` varchar(255)COMMENT '是否已读',
                                     `member_id` varchar(255)COMMENT '会员ID',
                                     `receive_time` bigint COMMENT '阅读时间',
                                     `title` varchar(255)COMMENT '标题',
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of member_notice
-- ----------------------------

-- ----------------------------
-- Table structure for member_notice_log
-- ----------------------------
DROP TABLE IF EXISTS `member_notice_log`;
CREATE TABLE `member_notice_log`  (
                                         `id` bigint NOT NULL COMMENT 'ID',
                                         `create_by` varchar(255)COMMENT '创建者',
                                         `create_at` datetime(6) COMMENT '创建时间',
                                         `delete_flag` bit(1) COMMENT '删除标志 true/false 删除/未删除',
                                         `update_by` varchar(255)COMMENT '更新者',
                                         `update_at` datetime(6) COMMENT '更新时间',
                                         `admin_id` varchar(255)COMMENT '管理员ID',
                                         `admin_name` varchar(255)COMMENT '管理员名称',
                                         `content` varchar(255)COMMENT '消息内容',
                                         `member_ids` varchar(255)COMMENT '会员ID',
                                         `send_time` datetime(6) COMMENT '发送时间',
                                         `send_type` int COMMENT '发送类型',
                                         `title` varchar(255)COMMENT '标题',
                                         PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of member_notice_log
-- ----------------------------

-- ----------------------------
-- Table structure for member_notice_senter
-- ----------------------------
DROP TABLE IF EXISTS `member_notice_senter`;
CREATE TABLE `member_notice_senter`  (
                                            `id` bigint NOT NULL COMMENT 'ID',
                                            `create_by` varchar(255)COMMENT '创建者',
                                            `create_at` datetime(6) COMMENT '创建时间',
                                            `delete_flag` bit(1) COMMENT '删除标志 true/false 删除/未删除',
                                            `update_by` varchar(255)COMMENT '更新者',
                                            `update_at` datetime(6) COMMENT '更新时间',
                                            `content` varchar(255)COMMENT '消息内容',
                                            `member_ids` varchar(255)COMMENT '会员ID',
                                            `send_type` varchar(255)COMMENT '发送类型',
                                            `title` varchar(255)COMMENT '标题',
                                            PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of member_notice_senter
-- ----------------------------

-- ----------------------------
-- Table structure for member_points_history
-- ----------------------------
DROP TABLE IF EXISTS `member_points_history`;
CREATE TABLE `member_points_history`  (
                                             `id` bigint NOT NULL COMMENT 'ID',
                                             `create_by` varchar(255)COMMENT '创建者',
                                             `create_at` datetime(6) COMMENT '创建时间',
                                             `before_point` bigint COMMENT '消费之前积分',
                                             `content` varchar(255)COMMENT '内容',
                                             `member_id` varchar(255)COMMENT '会员ID',
                                             `member_name` varchar(255)COMMENT '会员名称',
                                             `point` bigint COMMENT '当前积分',
                                             `point_type` varchar(255)COMMENT '消费积分类型',
                                             `variable_point` bigint COMMENT '消费积分',
                                             PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of member_points_history
-- ----------------------------

-- ----------------------------
-- Table structure for member_receipt
-- ----------------------------
DROP TABLE IF EXISTS `member_receipt`;
CREATE TABLE `member_receipt`  (
                                      `id` bigint NOT NULL COMMENT 'ID',
                                      `create_at` datetime(6) COMMENT '创建时间',
                                      `delete_flag` bit(1) COMMENT '删除标志 true/false 删除/未删除',
                                      `is_default` int COMMENT '是否为默认选项',
                                      `member_id` varchar(255)COMMENT '会员ID',
                                      `member_name` varchar(255)COMMENT '会员名称',
                                      `receipt_content` varchar(255)COMMENT '发票内容',
                                      `receipt_title` varchar(255)COMMENT '发票抬头',
                                      `receipt_type` varchar(255)COMMENT '发票类型',
                                      `taxpayer_id` varchar(255)COMMENT '纳税人识别号',
                                      PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of member_receipt
-- ----------------------------

-- ----------------------------
-- Table structure for member_sign
-- ----------------------------
DROP TABLE IF EXISTS `member_sign`;
CREATE TABLE `member_sign`  (
                                   `id` bigint NOT NULL COMMENT 'ID',
                                   `create_at` datetime(6) COMMENT '创建时间',
                                   `member_id` varchar(255)COMMENT '会员用户ID',
                                   `member_name` varchar(255)COMMENT '会员用户名',
                                   `sign_day` int COMMENT '连续签到天数',
                                   `day` int COMMENT '签到日',
                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of member_sign
-- ----------------------------

-- ----------------------------
-- Table structure for member_statistics_data
-- ----------------------------
DROP TABLE IF EXISTS `member_statistics_data`;
CREATE TABLE `member_statistics_data`  (
                                              `id` bigint NOT NULL COMMENT 'ID',
                                              `active_quantity` int COMMENT '当日活跃数量',
                                              `create_date` datetime(6) COMMENT '统计日',
                                              `member_count` int COMMENT '当前会员数量',
                                              `newly_added` int COMMENT '新增会员数量',
                                              PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of member_statistics_data
-- ----------------------------

-- ----------------------------
-- Table structure for member_wallet
-- ----------------------------
DROP TABLE IF EXISTS `member_wallet`;
CREATE TABLE `member_wallet`  (
                                     `id` bigint NOT NULL COMMENT 'ID',
                                     `create_by` varchar(255)  COMMENT '创建者',
                                     `create_at` datetime(6) COMMENT '创建时间',
                                     `delete_flag` bit(1) COMMENT '删除标志',
                                     `update_by` varchar(255)  COMMENT '更新者',
                                     `update_at` datetime(6) COMMENT '更新时间',
                                     `member_frozen_wallet` decimal(10, 2) COMMENT '会员预存款冻结金额',
                                     `member_id` varchar(255)  COMMENT '会员ID',
                                     `member_name` varchar(255)  COMMENT '会员用户名',
                                     `member_wallet` decimal(10, 2) COMMENT '会员预存款',
                                     `wallet_password` varchar(255)  COMMENT '预存款密码',
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of member_wallet
-- ----------------------------
INSERT INTO `member_wallet` VALUES (1376417817896681472, '13011111111', '2021-03-29 01:15:55.050000', b'0', '13011111111', '2022-10-21 11:17:47.704000', 1012150.10, '1376417684140326912', '13011111111', 155593.46, NULL);

-- ----------------------------
-- Table structure for member_withdraw_apply
-- ----------------------------
DROP TABLE IF EXISTS `member_withdraw_apply`;
CREATE TABLE `member_withdraw_apply`  (
                                             `id` bigint NOT NULL COMMENT 'ID',
                                             `create_by` varchar(255)COMMENT '创建者',
                                             `create_at` datetime(6) COMMENT '创建时间',
                                             `delete_flag` bit(1) COMMENT '删除标志 true/false 删除/未删除',
                                             `update_by` varchar(255)COMMENT '更新者',
                                             `update_at` datetime(6) COMMENT '更新时间',
                                             `apply_money` decimal(10, 2) COMMENT '申请提现金额',
                                             `apply_status` varchar(255)COMMENT '提现状态',
                                             `inspect_remark` varchar(255)COMMENT '审核备注',
                                             `inspect_time` datetime(6) COMMENT '审核时间',
                                             `member_id` varchar(255)COMMENT '会员ID',
                                             `member_name` varchar(255)COMMENT '会员名称',
                                             `sn` varchar(255)COMMENT '编号',
                                             `real_name` varchar(255)COMMENT '真实姓名',
                                             `connect_number` varchar(255)COMMENT '联系方式',
                                             `error_message` varchar(255)COMMENT '错误信息',
                                             PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;
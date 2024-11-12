DROP TABLE IF EXISTS `payment_log`;
CREATE TABLE `payment_log`  (
                                `id` bigint NOT NULL,
                                `create_by` varchar(255) ,
                                `create_at` datetime(6) NULL DEFAULT NULL,
                                `delete_flag` bit(1) NULL DEFAULT NULL,
                                `update_by` varchar(255) ,
                                `update_at` datetime(6) NULL DEFAULT NULL,
                                `client_type` varchar(255) ,
                                `flow_price` decimal(10, 2) NULL DEFAULT NULL,
                                `member_id` varchar(255) ,
                                `member_name` varchar(255) ,
                                `order_type` varchar(255) ,
                                `pay_order_no` varchar(255) ,
                                `pay_status` varchar(255) ,
                                `payment_method` varchar(255) ,
                                `payment_time` datetime(6) NULL DEFAULT NULL,
                                `receivable_no` varchar(255) ,
                                `sn` varchar(255) ,
                                `store_id` varchar(255) ,
                                `store_name` varchar(255) ,
                                `trade_sn` varchar(255) ,
                                PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;
-- ----------------------------
-- Table structure for order
-- ----------------------------
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order`  (
                             `id` bigint NOT NULL COMMENT 'ID',
                             `create_by` varchar(255)NOT NULL COMMENT '创建者',
                             `create_at` datetime(6) COMMENT '创建时间',
                             `delete_flag` bit(1) COMMENT '删除标志 true/false 删除/未删除',
                             `update_by` varchar(255)COMMENT '更新者',
                             `update_at` datetime(6) COMMENT '更新时间',
                             `can_return` bit(1) COMMENT '订单是否支持原路退回',
                             `cancel_reason` varchar(255)COMMENT '订单取消原因',
                             `client_type` varchar(255)COMMENT '订单来源',
                             `complete_time` datetime(6) COMMENT '完成时间',
                             `consignee_address_id_path` varchar(255)COMMENT '地址id \',\'分割',
                             `consignee_address_path` varchar(255)COMMENT '地址名称  \',\'分割\"',
                             `consignee_detail` varchar(255)COMMENT '详细地址',
                             `consignee_mobile` varchar(255)COMMENT '收件人手机',
                             `consignee_name` varchar(255)COMMENT '收件人姓名',
                             `deliver_status` varchar(255)COMMENT '货运状态',
                             `discount_price` decimal(10, 2) COMMENT '优惠的金额',
                             `flow_price` decimal(10, 2) COMMENT '总价格',
                             `freight_price` decimal(10, 2) COMMENT '运费',
                             `goods_num` int COMMENT '商品数量',
                             `goods_price` decimal(10, 2) COMMENT '商品价格',
                             `logistics_code` varchar(255)COMMENT '物流公司CODE',
                             `logistics_name` varchar(255)COMMENT '物流公司名称',
                             `logistics_no` varchar(255)COMMENT '发货单号',
                             `logistics_time` datetime(6) COMMENT '送货时间',
                             `member_id` varchar(255)COMMENT '会员ID',
                             `member_name` varchar(255)COMMENT '用户名',
                             `need_receipt` bit(1) COMMENT '是否需要发票',
                             `order_status` varchar(255)COMMENT '订单状态',
                             `order_type` varchar(255)COMMENT '订单类型',
                             `parent_order_sn` varchar(255)COMMENT '是否为其他订单下的订单，如果是则为依赖订单的sn，否则为空',
                             `pay_order_no` varchar(255)COMMENT '支付方式返回的交易号',
                             `pay_status` varchar(255)COMMENT '付款状态',
                             `payment_method` varchar(255)COMMENT '支付方式',
                             `payment_time` datetime(6) COMMENT '支付时间',
                             `price_detail` textNULL COMMENT '价格详情',
                             `promotion_id` varchar(255)COMMENT '是否为某订单类型的订单，如果是则为订单类型的id，否则为空',
                             `remark` varchar(255)COMMENT '买家订单备注',
                             `seller_remark` varchar(255)COMMENT '商家订单备注',
                             `store_id` varchar(255)COMMENT '店铺ID',
                             `store_name` varchar(255)COMMENT '店铺名称',
                             `sn` varchar(255)COMMENT '订单编号',
                             `trade_sn` varchar(255)COMMENT '交易编号 关联Trade',
                             `weight` decimal(10, 2) COMMENT '订单商品总重量',
                             `qr_code` varchar(255)COMMENT '提货码',
                             `update_price` decimal(10, 2) COMMENT '修改价格',
                             `distribution_id` varchar(255)COMMENT '分销员ID',
                             `delivery_method` varchar(255)COMMENT '配送方式',
                             `use_platform_member_coupon_id` varchar(255)COMMENT '使用的平台会员优惠券id',
                             `use_store_member_coupon_ids` varchar(255)COMMENT '使用的店铺会员优惠券id(,区分)',
                             `receivable_no` varchar(255)NULL DEFAULT NULL,
                             `order_promotion_type` varchar(255)NULL DEFAULT NULL,
                             `verification_code` varchar(255)NULL DEFAULT NULL,
                             `store_address_path` varchar(255)NULL DEFAULT NULL,
                             `store_address_center` varchar(255)NULL DEFAULT NULL,
                             `store_address_mobile` varchar(255)NULL DEFAULT NULL,
                             PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;


DROP TABLE IF EXISTS `order_complaint`;
CREATE TABLE `order_complaint`  (
                                       `id` bigint NOT NULL COMMENT 'ID',
                                       `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '创建者',
                                       `create_at` datetime COMMENT '创建时间',
                                       `delete_flag` bit(1) COMMENT '删除标志 true/false 删除/未删除',
                                       `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '更新者',
                                       `update_at` datetime COMMENT '更新时间',
                                       `appeal_content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '申诉商家内容',
                                       `appeal_images` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '申诉商家上传的图片',
                                       `appeal_time` datetime COMMENT '申诉商家时间',
                                       `arbitration_result` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '仲裁结果',
                                       `complain_status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '交易投诉状态',
                                       `complain_topic` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '投诉主题',
                                       `consignee_address_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '收货地址',
                                       `consignee_mobile` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '收货人手机',
                                       `consignee_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '收货人',
                                       `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '投诉内容',
                                       `freight_price` decimal(10, 2) COMMENT '运费',
                                       `goods_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '商品ID',
                                       `goods_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '商品图片',
                                       `goods_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '商品名称',
                                       `goods_price` decimal(10, 2) COMMENT '商品价格',
                                       `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '投诉凭证图片',
                                       `logistics_no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '物流单号',
                                       `member_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '会员ID',
                                       `member_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '会员名称',
                                       `num` int COMMENT '购买的商品数量',
                                       `order_price` decimal(10, 2) COMMENT '订单金额',
                                       `order_sn` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '订单编号',
                                       `order_time` datetime COMMENT '下单时间',
                                       `store_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '店铺ID',
                                       `store_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '店铺名称',
                                       `sku_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '货品ID',
                                       PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for order_complaint_communication
-- ----------------------------
DROP TABLE IF EXISTS `order_complaint_communication`;
CREATE TABLE `order_complaint_communication`  (
                                                     `id` bigint NOT NULL COMMENT 'ID',
                                                     `create_by` varchar(255)COMMENT '创建者',
                                                     `create_at` datetime(6) COMMENT '创建时间',
                                                     `delete_flag` bit(1) COMMENT '删除标志 true/false 删除/未删除',
                                                     `update_by` varchar(255)COMMENT '更新者',
                                                     `update_at` datetime(6) COMMENT '更新时间',
                                                     `complain_id` varchar(255)COMMENT '投诉ID',
                                                     `content` varchar(255)COMMENT '对话内容',
                                                     `owner` varchar(255)COMMENT '所属，买家/卖家',
                                                     `owner_id` varchar(255)COMMENT '对话所属ID,卖家ID/买家ID',
                                                     `owner_name` varchar(255)COMMENT '对话所属名称',
                                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of order_complaint_communication
-- ----------------------------

-- ----------------------------
-- Table structure for order_item
-- ----------------------------
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item`  (
                                  `id` bigint NOT NULL COMMENT 'ID',
                                  `create_by` varchar(255)COMMENT '创建者',
                                  `create_at` datetime(6) COMMENT '创建时间',
                                  `delete_flag` bit(1) COMMENT '删除标志 true/false 删除/未删除',
                                  `update_by` varchar(255)COMMENT '更新者',
                                  `update_at` datetime(6) COMMENT '更新时间',
                                  `after_sale_status` varchar(255)COMMENT '售后状态',
                                  `category_id` varchar(255)COMMENT '分类ID',
                                  `comment_status` varchar(255)COMMENT '评论状态',
                                  `complain_id` varchar(255)COMMENT '交易投诉ID',
                                  `complain_status` varchar(255)COMMENT '投诉状态',
                                  `flow_price` decimal(10, 2) COMMENT '实际金额',
                                  `goods_price` decimal(10, 2) COMMENT '销售金额',
                                  `goods_id` bigint COMMENT '商品ID',
                                  `image` varchar(255)COMMENT '图片',
                                  `goods_name` varchar(255)COMMENT '商品名称',
                                  `num` int COMMENT '销售量',
                                  `order_sn` varchar(255)COMMENT '订单编号',
                                  `price_detail` textNULL COMMENT '价格详情',
                                  `promotion_id` varchar(255)COMMENT '促销ID',
                                  `promotion_type` varchar(255)COMMENT '促销类型',
                                  `sku_id` varchar(255)COMMENT '货品ID',
                                  `sn` varchar(255)COMMENT '子订单编号',
                                  `snapshot_id` varchar(255)COMMENT '快照ID',
                                  `specs` textNULL COMMENT '规格json',
                                  `trade_sn` varchar(255)COMMENT '交易编号',
                                  `price_fluctuation_ratio` decimal(10, 2) COMMENT '浮动价格比例',
                                  `unit_price` decimal(10, 2) COMMENT '单价',
                                  `sub_total` decimal(10, 2) COMMENT '小记',
                                  `return_goods_number` int NULL DEFAULT 0 COMMENT '已退货数量 ',
                                  `is_goods_number` int NULL DEFAULT 0 COMMENT '正在进行售后的商品数量 ',
                                  `deliver_number` int COMMENT '发货数量',
                                  `is_refund` varchar(255)COMMENT '是否退款',
                                  `refund_price` decimal(10, 2) COMMENT '退款金额',
                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of order_item
-- ----------------------------

-- ----------------------------
-- Table structure for order_log
-- ----------------------------
DROP TABLE IF EXISTS `order_log`;
CREATE TABLE `order_log`  (
                                 `id` bigint NOT NULL COMMENT 'ID',
                                 `create_by` varchar(255)COMMENT '创建者',
                                 `create_at` datetime(6) COMMENT '创建时间',
                                 `message` varchar(255)COMMENT '日志信息',
                                 `operator_id` varchar(255)COMMENT '操作者ID',
                                 `operator_name` varchar(255)COMMENT '操作者名称',
                                 `operator_type` varchar(255)COMMENT '操作者类型',
                                 `order_sn` varchar(255)COMMENT '订单编号',
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of order_log
-- ----------------------------

-- ----------------------------
-- Table structure for order_package
-- ----------------------------
DROP TABLE IF EXISTS `order_package`;
CREATE TABLE `order_package`  (
                                     `id` bigint NOT NULL COMMENT 'ID',
                                     `create_by` varchar(255)  COMMENT '创建者',
                                     `create_at` datetime(6) COMMENT '创建时间',
                                     `delete_flag` bit(1) COMMENT '是否删除',
                                     `update_by` varchar(255)  COMMENT '修改者',
                                     `update_at` datetime(6) COMMENT '修改时间',
                                     `package_no` varchar(255)  COMMENT '包裹单号',
                                     `order_sn` varchar(255)  COMMENT '订单编号',
                                     `logistics_no` varchar(255)  COMMENT '发货单号',
                                     `logistics_code` varchar(255)  COMMENT '物流公司CODE',
                                     `logistics_name` varchar(255)  COMMENT '物流公司名称',
                                     `consignee_mobile` varchar(255)  COMMENT '收件人手机',
                                     `status` varchar(255)  COMMENT '状态',
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of order_package
-- ----------------------------

-- ----------------------------
-- Table structure for order_package_item
-- ----------------------------
DROP TABLE IF EXISTS `order_package_item`;
CREATE TABLE `order_package_item`  (
                                          `id` bigint NOT NULL COMMENT 'ID',
                                          `create_by` varchar(255)  COMMENT '创建者',
                                          `create_at` datetime(6) COMMENT '创建时间',
                                          `delete_flag` bit(1) COMMENT '是否删除',
                                          `update_by` varchar(255)  COMMENT '修改者',
                                          `update_at` datetime(6) COMMENT '修改时间',
                                          `package_no` varchar(255)  COMMENT '包裹单号',
                                          `order_sn` varchar(255)  COMMENT '订单编号',
                                          `order_item_sn` varchar(255)  COMMENT '子订单编号',
                                          `deliver_number` int COMMENT '发货数量',
                                          `logistics_time` datetime(6) COMMENT '发货时间',
                                          `goods_name` varchar(255) ,
                                          `thumbnail` varchar(255) ,
                                          PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of order_package_item
-- ----------------------------

-- ----------------------------
-- Table structure for page_data
-- ----------------------------
DROP TABLE IF EXISTS `page_data`;
CREATE TABLE `page_data`  (
                                 `id` bigint NOT NULL COMMENT 'ID',
                                 `create_by` varchar(255)COMMENT '创建者',
                                 `create_at` datetime(6) COMMENT '创建时间',
                                 `delete_flag` bit(1) COMMENT '删除标志 true/false 删除/未删除',
                                 `update_by` varchar(255)COMMENT '更新者',
                                 `update_at` datetime(6) COMMENT '更新时间',
                                 `name` varchar(255)COMMENT '页面名称',
                                 `num` varchar(255)COMMENT '值',
                                 `page_client_type` varchar(255)COMMENT '客户端类型',
                                 `page_data` longtextNULL COMMENT '页面数据',
                                 `page_show` varchar(255)COMMENT '页面开关状态',
                                 `page_type` varchar(255)COMMENT '页面类型',
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;
DROP TABLE IF EXISTS `region`;
CREATE TABLE `region`  (
                              `id` bigint NOT NULL COMMENT 'ID',
                              `create_by` varchar(64)  COMMENT '创建者',
                              `create_at` datetime(6) COMMENT '创建时间',
                              `delete_flag` bit(1) COMMENT '删除标志 true/false 删除/未删除',
                              `update_by` varchar(64)  COMMENT '更新者',
                              `update_at` datetime(6) COMMENT '更新时间',
                              `ad_code` varchar(255)  COMMENT '区域编码',
                              `center` varchar(255)  COMMENT '区域中心点经纬度',
                              `city_code` varchar(255)  COMMENT '城市代码',
                              `level` varchar(255)  COMMENT '行政区划级别',
                              `name` varchar(255)  COMMENT '名称',
                              `order_num` int COMMENT '排序',
                              `parent_id` bigint COMMENT '父ID',
                              `path` varchar(255)  COMMENT '行政地区路径',
                              PRIMARY KEY (`id`) USING BTREE,
                              INDEX `parent_id`(`parent_id` ASC) USING BTREE COMMENT '父id'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;

DROP TABLE IF EXISTS `refund_log`;
CREATE TABLE `refund_log`  (
                                  `id` bigint NOT NULL COMMENT 'ID',
                                  `after_sale_no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '退单编号',
                                  `create_at` datetime COMMENT '创建时间',
                                  `error_message` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '退款失败原因',
                                  `is_refund` bit(1) COMMENT '是否已退款',
                                  `pay_price` decimal(10, 2) COMMENT '交易支付金额',
                                  `payment_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '退款方式',
                                  `payment_out_order_no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '支付第三方申请流水号',
                                  `payment_receivable_no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '支付第三方返回流水',
                                  `receivable_no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '第三方返回退款流水号',
                                  `refund_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '退款理由',
                                  `total_amount` decimal(10, 2) COMMENT '金额',
                                  `member_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '会员ID',
                                  `order_sn` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '订单编号',
                                  `out_order_no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '退款申请号码',
                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;
DROP TABLE IF EXISTS `recharge`;
CREATE TABLE `recharge`  (
                                `id` bigint NOT NULL COMMENT 'ID',
                                `create_by` varchar(255)COMMENT '创建者',
                                `create_at` datetime(6) COMMENT '创建时间',
                                `member_id` varchar(255)COMMENT '会员id',
                                `member_name` varchar(255)COMMENT '会员名称',
                                `pay_status` varchar(255)COMMENT '支付状态',
                                `payment_plugin_id` varchar(255)COMMENT '支付插件id',
                                `recharge_money` decimal(10, 2) COMMENT '充值金额',
                                `recharge_sn` varchar(255)COMMENT '充值订单编号',
                                `recharge_way` varchar(255)COMMENT '充值方式',
                                `pay_time` datetime(6) COMMENT '支付时间',
                                `receivable_no` varchar(255)NULL DEFAULT NULL,
                                PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;
-- ----------------------------
-- Table structure for receipt
-- ----------------------------
DROP TABLE IF EXISTS `receipt`;
CREATE TABLE `receipt`  (
                               `id` bigint NOT NULL COMMENT 'ID',
                               `create_by` varchar(255)  COMMENT '创建者',
                               `create_at` datetime(6) COMMENT '创建时间',
                               `delete_flag` bit(1) COMMENT '删除标志 true/false 删除/未删除',
                               `update_by` varchar(255)  COMMENT '更新者',
                               `update_at` datetime(6) COMMENT '更新时间',
                               `member_id` varchar(255)  COMMENT '会员ID',
                               `member_name` varchar(255)  COMMENT '会员名称',
                               `order_sn` varchar(255)  COMMENT '订单编号',
                               `receipt_content` varchar(255)  COMMENT '发票内容',
                               `receipt_detail` text CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL COMMENT '发票详情',
                               `receipt_price` varchar(255)  COMMENT '发票金额',
                               `receipt_status` int COMMENT '发票状态',
                               `receipt_title` varchar(255)  COMMENT '发票抬头',
                               `store_id` varchar(255)  COMMENT '店铺ID',
                               `store_name` varchar(255)  COMMENT '店铺名称',
                               `taxpayer_id` varchar(255)  COMMENT '纳税人识别号',
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin ROW_FORMAT = DYNAMIC;
-- Table structure for pintuan
-- ----------------------------
DROP TABLE IF EXISTS `pintuan`;
CREATE TABLE `pintuan`  (
                               `id` bigint NOT NULL COMMENT 'ID',
                               `create_by` varchar(255)COMMENT '创建者',
                               `create_at` datetime(6) COMMENT '创建时间',
                               `delete_flag` bit(1) COMMENT '删除标志 true/false 删除/未删除',
                               `update_by` varchar(255)COMMENT '更新者',
                               `update_at` datetime(6) COMMENT '更新时间',
                               `end_time` datetime(6) COMMENT '活动结束时间',
                               `promotion_name` varchar(255)COMMENT '活动名称',
                               `store_id` varchar(255)COMMENT '店铺ID',
                               `store_name` varchar(255)COMMENT '店铺名称',
                               `start_time` datetime(6) COMMENT '活动开始时间',
                               `fictitious` bit(1) COMMENT '虚拟成团',
                               `limit_num` int COMMENT '限购数量',
                               `pintuan_rule` varchar(255)COMMENT '拼团规则',
                               `required_num` int COMMENT '成团人数',
                               `scope_id` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '范围关联的ID',
                               `scope_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'PORTION_GOODS' COMMENT '关联范围类型',
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of pintuan
-- ----------------------------

-- ----------------------------
-- Table structure for purchase_order
-- ----------------------------
DROP TABLE IF EXISTS `purchase_order`;
CREATE TABLE `purchase_order`  (
                                      `id` bigint NOT NULL COMMENT 'ID',
                                      `create_by` varchar(255)COMMENT '创建者',
                                      `create_at` datetime(6) COMMENT '创建时间',
                                      `delete_flag` bit(1) COMMENT '删除标志 true/false 删除/未删除',
                                      `update_by` varchar(255)COMMENT '更新者',
                                      `update_at` datetime(6) COMMENT '更新时间',
                                      `category_id` varchar(255)COMMENT '分类ID',
                                      `category_name` varchar(255)COMMENT '分类名称',
                                      `consignee_address_id_path` varchar(255)COMMENT '地址ID',
                                      `consignee_address_path` varchar(255)COMMENT '地址名称',
                                      `contact_number` varchar(255)COMMENT '联系电话',
                                      `contact_type` varchar(255)COMMENT '联系类型',
                                      `contacts` varchar(255)COMMENT '联系人',
                                      `deadline` datetime(6) COMMENT '截止时间',
                                      `member_id` varchar(255)COMMENT '供求人',
                                      `need_receipt` bit(1) COMMENT '是否需要发票',
                                      `price_method` varchar(255)COMMENT '价格类型',
                                      `receipt_time` datetime(6) COMMENT '收货时间',
                                      `status` varchar(255)COMMENT '状态',
                                      `supplement` varchar(255)COMMENT '补充说明',
                                      `title` varchar(255)COMMENT '标题',
                                      PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of purchase_order
-- ----------------------------

-- ----------------------------
-- Table structure for purchase_order_item
-- ----------------------------
DROP TABLE IF EXISTS `purchase_order_item`;
CREATE TABLE `purchase_order_item`  (
                                           `id` bigint NOT NULL COMMENT 'ID',
                                           `create_at` datetime(6) COMMENT '创建时间',
                                           `goods_name` varchar(255)COMMENT '商品名称',
                                           `goods_unit` varchar(255)COMMENT '数量单位',
                                           `images` varchar(255)COMMENT '图片',
                                           `num` varchar(255)COMMENT '数量',
                                           `price` decimal(10, 2) COMMENT '价格',
                                           `purchase_order_id` varchar(255)COMMENT '采购ID',
                                           `specs` varchar(255)COMMENT '规格',
                                           PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of purchase_order_item
-- ----------------------------

-- ----------------------------
-- Table structure for purchase_quoted
-- ----------------------------
DROP TABLE IF EXISTS `purchase_quoted`;
CREATE TABLE `purchase_quoted`  (
                                       `id` bigint NOT NULL COMMENT 'ID',
                                       `create_at` datetime(6) COMMENT '创建时间',
                                       `annex` varchar(255)COMMENT '附件',
                                       `company_name` varchar(255)COMMENT '公司名称',
                                       `contact_number` varchar(255)COMMENT '联系电话',
                                       `contacts` varchar(255)COMMENT '联系人',
                                       `context` varchar(255)COMMENT '报价说明',
                                       `member_id` varchar(255)COMMENT '报价人',
                                       `purchase_order_id` varchar(255)COMMENT '采购单ID',
                                       `title` varchar(255)COMMENT '标题',
                                       PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of purchase_quoted
-- ----------------------------

-- ----------------------------
-- Table structure for purchase_quoted_item
-- ----------------------------
DROP TABLE IF EXISTS `purchase_quoted_item`;
CREATE TABLE `purchase_quoted_item`  (
                                            `id` bigint NOT NULL COMMENT 'ID',
                                            `create_at` datetime(6) COMMENT '创建时间',
                                            `purchase_quoted_id` varchar(255)COMMENT '报价单ID',
                                            `goods_name` varchar(255)COMMENT '商品名称',
                                            `goods_unit` varchar(255)COMMENT '数量单位',
                                            `num` varchar(255)COMMENT '数量',
                                            `price` decimal(10, 2) COMMENT '价格',
                                            `specs` varchar(255)COMMENT '规格',
                                            PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;
-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role`  (
                            `id` bigint NOT NULL COMMENT 'ID',
                            `create_by` varchar(255)COMMENT '创建者',
                            `create_at` datetime(6) COMMENT '创建时间',
                            `delete_flag` bit(1) COMMENT '删除标志 true/false 删除/未删除',
                            `update_by` varchar(255)COMMENT '更新者',
                            `update_at` datetime(6) COMMENT '更新时间',
                            `default_role` bit(1) COMMENT '是否为注册默认角色',
                            `description` varchar(255)COMMENT '备注',
                            `name` varchar(255)COMMENT '角色名',
                            PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of role
-- ----------------------------

-- ----------------------------
-- Table structure for role_menu
-- ----------------------------
DROP TABLE IF EXISTS `role_menu`;
CREATE TABLE `role_menu`  (
                                 `id` bigint NOT NULL COMMENT 'ID',
                                 `create_by` varchar(255)COMMENT '创建者',
                                 `create_at` datetime(6) COMMENT '创建时间',
                                 `delete_flag` bit(1) COMMENT '删除标志 true/false 删除/未删除',
                                 `update_by` varchar(255)COMMENT '更新者',
                                 `update_at` datetime(6) COMMENT '更新时间',
                                 `is_super` bit(1) COMMENT '是否拥有操作数据权限',
                                 `menu_id` varchar(255)COMMENT '菜单',
                                 `role_id` varchar(255)COMMENT '角色ID',
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of role_menu
-- ----------------------------

-- ----------------------------
-- Table structure for s_platform_view_data
-- ----------------------------
DROP TABLE IF EXISTS `s_platform_view_data`;
CREATE TABLE `s_platform_view_data`  (
                                            `id` bigint NOT NULL COMMENT 'ID',
                                            `date` datetime(6) COMMENT '统计日',
                                            `pv_num` bigint COMMENT 'PV数量',
                                            `store_id` varchar(255)  COMMENT '店铺ID',
                                            `uv_num` bigint COMMENT 'UV数量',
                                            PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin ROW_FORMAT = DYNAMIC;
-- ----------------------------
-- Table structure for seckill
-- ----------------------------
DROP TABLE IF EXISTS `seckill`;
CREATE TABLE `seckill`  (
                               `id` bigint NOT NULL COMMENT 'ID',
                               `create_by` varchar(255)COMMENT '创建者',
                               `create_at` datetime(6) COMMENT '创建时间',
                               `delete_flag` bit(1) COMMENT '删除标志 true/false 删除/未删除',
                               `update_by` varchar(255)COMMENT '更新者',
                               `update_at` datetime(6) COMMENT '更新时间',
                               `end_time` datetime(6) COMMENT '活动结束时间',
                               `promotion_name` varchar(255)COMMENT '活动名称',
                               `store_id` varchar(255)COMMENT '店铺ID',
                               `store_name` varchar(255)COMMENT '店铺名称',
                               `start_time` datetime(6) COMMENT '活动开始时间',
                               `apply_end_time` datetime(6) NOT NULL COMMENT '报名截至时间',
                               `hours` varchar(255)NOT NULL COMMENT '开启几点场',
                               `seckill_rule` varchar(255)COMMENT '申请规则',
                               `store_ids` varchar(255)COMMENT '店铺ID集合以逗号分隔',
                               `goods_num` int NULL DEFAULT NULL,
                               `scope_id` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '范围关联的ID',
                               `scope_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'PORTION_GOODS' COMMENT '关联范围类型',
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of seckill
-- ----------------------------

-- ----------------------------
-- Table structure for seckill_apply
-- ----------------------------
DROP TABLE IF EXISTS `seckill_apply`;
CREATE TABLE `seckill_apply`  (
                                     `id` bigint NOT NULL COMMENT 'ID',
                                     `create_by` varchar(255)COMMENT '创建者',
                                     `create_at` datetime(6) COMMENT '创建时间',
                                     `delete_flag` bit(1) COMMENT '删除标志 true/false 删除/未删除',
                                     `update_by` varchar(255)COMMENT '更新者',
                                     `update_at` datetime(6) COMMENT '更新时间',
                                     `fail_reason` varchar(255)COMMENT '驳回原因',
                                     `goods_name` varchar(255)COMMENT '商品名称',
                                     `original_price` decimal(10, 2) COMMENT '商品原始价格',
                                     `price` decimal(10, 2) NOT NULL COMMENT '价格',
                                     `promotion_apply_status` varchar(255)COMMENT '促销活动申请状态',
                                     `quantity` int NOT NULL COMMENT '促销数量',
                                     `sales_num` int COMMENT '已售数量',
                                     `seckill_id` varchar(255)NOT NULL COMMENT '活动ID',
                                     `store_id` varchar(255)COMMENT '店铺ID',
                                     `store_name` varchar(255)COMMENT '店铺名称',
                                     `sku_id` varchar(255)NOT NULL COMMENT '货品ID',
                                     `time_line` int NOT NULL COMMENT '时刻',
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of seckill_apply
-- ----------------------------

-- ----------------------------
-- Table structure for sensitive_words
-- ----------------------------
DROP TABLE IF EXISTS `sensitive_words`;
CREATE TABLE `sensitive_words`  (
                                       `id` bigint NOT NULL COMMENT 'ID',
                                       `create_by` varchar(255)COMMENT '创建者',
                                       `create_at` datetime(6) COMMENT '创建时间',
                                       `delete_flag` bit(1) COMMENT '删除标志 true/false 删除/未删除',
                                       `update_by` varchar(255)COMMENT '更新者',
                                       `update_at` datetime(6) COMMENT '更新时间',
                                       `sensitive_word` varchar(20)COMMENT '敏感词名称',
                                       PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sensitive_words
-- ----------------------------
INSERT INTO `sensitive_words` VALUES (1455437623816667138, 'admin', '2021-11-02 15:32:24.903000', b'0', 'admin', '2021-11-02 15:32:47.091000', '11111啊');
INSERT INTO `sensitive_words` VALUES (1455437664195231745, 'admin', '2021-11-02 15:32:34.526000', b'0', 'admin', '2021-11-02 15:32:40.759000', '啊啊啊啊啊1');
INSERT INTO `sensitive_words` VALUES (1455437848660721666, 'admin', '2021-11-02 15:33:18.506000', b'0', NULL, NULL, 'sdd');
INSERT INTO `sensitive_words` VALUES (1455437891442622466, 'admin', '2021-11-02 15:33:28.706000', b'0', NULL, NULL, 'sss');
INSERT INTO `sensitive_words` VALUES (1455437907951403010, 'admin', '2021-11-02 15:33:32.642000', b'0', NULL, NULL, 'dsscdsdx');
INSERT INTO `sensitive_words` VALUES (1455437923965255681, 'admin', '2021-11-02 15:33:36.459000', b'0', NULL, NULL, 'sddcxasd');
INSERT INTO `sensitive_words` VALUES (1455437938469158914, 'admin', '2021-11-02 15:33:39.918000', b'0', 'admin', '2021-11-18 15:00:11.270000', 'dcasawad');
INSERT INTO `sensitive_words` VALUES (1455437955477061633, 'admin', '2021-11-02 15:33:43.972000', b'0', NULL, NULL, 'sacsac');
INSERT INTO `sensitive_words` VALUES (1455437972774371329, 'admin', '2021-11-02 15:33:48.096000', b'0', 'admin', '2021-11-18 15:00:04.501000', 'scddsx');
INSERT INTO `sensitive_words` VALUES (1464602719843307521, 'admin', '2021-11-27 22:31:14.070000', b'0', NULL, NULL, 'jj');

-- ----------------------------
-- Table structure for service_notice
-- ----------------------------
DROP TABLE IF EXISTS `service_notice`;
CREATE TABLE `service_notice`  (
                                      `id` bigint NOT NULL COMMENT 'ID',
                                      `create_by` varchar(255)COMMENT '创建者',
                                      `create_at` datetime(6) COMMENT '创建时间',
                                      `delete_flag` bit(1) COMMENT '删除标志 true/false 删除/未删除',
                                      `update_by` varchar(255)COMMENT '更新者',
                                      `update_at` datetime(6) COMMENT '更新时间',
                                      `banner_image` varchar(255)COMMENT 'banner图',
                                      `content` varchar(255)COMMENT '站内信内容',
                                      `store_id` varchar(255)COMMENT '店铺ID',
                                      `sub_title` varchar(255)COMMENT '副标题',
                                      `title` varchar(255)COMMENT '标题',
                                      `to_url` varchar(255)COMMENT '点击跳转',
                                      PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of service_notice
-- ----------------------------

-- ----------------------------
-- Table structure for setting
-- ----------------------------
DROP TABLE IF EXISTS `setting`;
CREATE TABLE `setting`  (
                               `id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'ID',
                               `create_by` varchar(64)  COMMENT '创建者',
                               `create_at` datetime(6) COMMENT '创建时间',
                               `delete_flag` bit(1) COMMENT '删除标志 true/false 删除/未删除',
                               `update_by` varchar(64)  COMMENT '更新者',
                               `update_at` datetime(6) COMMENT '更新时间',
                               `setting_value` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '配置值value',
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of setting
-- ----------------------------
INSERT INTO `setting` VALUES ('ALIPAY_PAYMENT', 'admin', '2021-02-27 07:20:11.914000', b'0', 'admin', '2024-07-07 13:57:52.882000', '{\"privateKey\":\"privateKey\",\"alipayPublicCertPath\":\"/home/crt/lili-alipay/alipayCertPublicKey_RSA2.crt\",\"callbackUrl\":\"https://buyer-api.pickmall.cn\",\"certPath\":\"/home/crt/lili-alipay/appCertPublicKey_2021002107649773.crt\",\"appId\":\"appId\",\"rootCertPath\":\"/home/crt/lili-alipay/alipayRootCert.crt\"}');
INSERT INTO `setting` VALUES ('BASE_SETTING', 'admin', '2021-01-21 13:46:35.348000', b'0', 'admin', '2022-10-12 19:10:04.355000', '{\"staticPageAddress\":\"https://pickmall.cn/1\",\"storeSideLogo\":\"https://cdn.pickmall.cn/cdn/logo/logo2.png\",\"icp\":\"icp\",\"domainLogo\":\"https://cdn.pickmall.cn/cdn/logo/logo2.png\",\"siteName\":\"lilishop\",\"buyerSideLogo\":\"https://cdn.pickmall.cn/cdn/logo/logo2.png\",\"staticPageWapAddress\":\"https://m.pickmall.cn/\"}');
INSERT INTO `setting` VALUES ('CONNECT_SETTING', 'admin', '2024-07-07 13:55:38.686000', b'0', NULL, NULL, '{\"callbackUrl\":\"https://buyer-api.pickmall.cn\",\"pc\":\"https://pc-b2b2c.pickmall.cn\",\"wap\":\"https://m-b2b2c.pickmall.cn\"}');
INSERT INTO `setting` VALUES ('DISTRIBUTION_SETTING', 'admin', '2021-01-22 02:56:44.099000', b'0', 'admin', '2021-09-24 15:39:46.245000', '{\"distributionDay\":94,\"isOpen\":true,\"cashDay\":1}');
INSERT INTO `setting` VALUES ('ES_SIGN', 'admin', '2021-01-22 02:56:44.099000', b'0', 'admin', '2021-01-22 02:56:44.099000', '{\"secretKey\":\"111\"}');
INSERT INTO `setting` VALUES ('GOODS_SETTING', 'admin', '2020-12-14 10:48:04.000000', b'0', 'admin', '2021-03-25 20:44:38.514000', '{\"goodsCheck\":\"true\",\"smallPictureWidth\":\"200\",\"smallPictureHeight\":\"200\",\"abbreviationPictureWidth\":\"400\",\"abbreviationPictureHeight\":\"400\",\"originalPictureWidth\":\"800\",\"originalPictureHeight\":\"800\"}');
INSERT INTO `setting` VALUES ('IM_SETTING', 'admin', '2021-09-17 12:00:41.085000', b'0', 'admin', '2022-11-18 10:25:16.804000', '{\"httpUrl\":\"https://www.baidu.com\"}');
INSERT INTO `setting` VALUES ('KUAIDI_SETTING', 'admin', '2021-01-18 03:30:53.430000', b'0', 'admin', '2021-09-15 11:57:07.495000', '{\"ebusinessID\":\"test\",\"appKey\":\"test\",\"reqURL\":\"test\"}');
INSERT INTO `setting` VALUES ('ORDER_SETTING', 'admin', '2021-02-03 08:16:54.942000', b'0', 'admin', '2021-09-16 16:32:18.050000', '{\"closeComplaint\":\"12\",\"autoCancelAfterSale\":\"7\",\"autoReceive\":\"5\",\"autoAfterSaleComplete\":\"0\",\"closeAfterSale\":\"12\",\"autoAfterSaleReview\":\"0\",\"autoEvaluation\":\"7\",\"autoCancel\":\"35\"}');
INSERT INTO `setting` VALUES ('OSS_SETTING', 'admin', '2020-12-12 08:44:27.376000', b'0', 'admin', '2022-10-12 19:11:49.244000', '{\"accessKeyId\":\"LTAI5tAw2cz7chK8GEjUuFCs\",\"endPoint\":\"oss-cn-beijing.aliyuncs.com\",\"bucketName\":\"lilishop-oss\",\"picLocation\":\"/template\",\"m_secretKey\":\"wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY\",\"accessKeySecret\":\"MsnF1RH4TFLIMd0i19DtnOYbAHQWLM\",\"m_bucketName\":\"lilishop\",\"m_endpoint\":\"http://pg2481.com:9000\",\"m_accessKey\":\"AKIAIOSFODNN7EXAMPLE\",\"category\":\"ALI_OSS\",\"m_frontUrl\":\"http://pg2481.com:9000\"}');
INSERT INTO `setting` VALUES ('PAYMENT_SUPPORT', 'admin', '2021-03-01 10:07:49.831000', b'0', 'admin', '2021-04-14 03:58:30.726000', '{\"paymentSupportItems\":[{\"client\":\"H5\",\"supports\":[\"WALLET\",\"ALIPAY\",\"WECHAT\"]},{\"client\":\"PC\",\"supports\":[\"WECHAT\",\"ALIPAY\",\"WALLET\"]},{\"client\":\"WECHAT_MP\",\"supports\":[\"WECHAT\",\"WALLET\"]},{\"client\":\"APP\",\"supports\":[\"WECHAT\",\"ALIPAY\",\"WALLET\"]}]}');
INSERT INTO `setting` VALUES ('POINT_SETTING', 'admin', '2021-02-26 07:57:39.629000', b'0', 'admin', '2021-09-26 16:03:56.043000', '{\"money\":1,\"signIn\":2,\"comment\":24,\"pointSettingItems\":[],\"register\":10}');
INSERT INTO `setting` VALUES ('QQ_CONNECT', 'admin', '2021-03-02 09:16:44.000000', b'0', 'admin', '2021-03-05 02:25:51.615000', '{\"qqConnectSettingItemList\":[{\"clientType\":\"PC\",\"appId\":\"\",\"appKey\":\"\"},{\"clientType\":\"H5\",\"appId\":\"\",\"appKey\":\"\"}]}');
INSERT INTO `setting` VALUES ('SECKILL_SETTING', 'admin', '2021-09-27 10:33:38.000000', NULL, 'admin', '2021-09-27 10:30:42.917000', '{\"hours\":\"1,2\",\"seckillRule\":\"秒杀规则\"}');
INSERT INTO `setting` VALUES ('SMS_SETTING', 'admin', '2021-01-23 02:18:03.299000', b'0', 'admin', '2024-07-07 13:53:44.732000', '{\"accessKeyId\":\"test\",\"tencentSdkAppId\":\"null\",\"registerTemplateCode\":\"SMS_205755298\",\"huaweiSender\":\"null\",\"signName\":\"lili\",\"tencentSecretId\":\"null\",\"huaweiAppKey\":\"null\",\"isTestModel\":\"true\",\"tencentSecretKey\":\"null\",\"category\":\"ALI\",\"accessSecret\":\"test\",\"tencentSignName\":\"null\",\"huaweiSignature\":\"null\",\"payPasswordTemplateCode\":\"SMS_205755301\",\"walletPasswordTemplateCode\":\"SMS_205755297\",\"findPasswordTemplateCode\":\"SMS_205755301\",\"huaweiAppSecret\":\"null\",\"loginTemplateCode\":\"SMS_205755300\"}');
INSERT INTO `setting` VALUES ('WECHAT_CONNECT', 'admin', '2021-03-02 09:17:10.000000', b'0', 'admin', '2021-03-05 02:26:43.348000', '{\"wechatConnectSettingItems\":[{\"clientType\":\"PC\",\"appId\":\"\",\"appSecret\":\"\"},{\"clientType\":\"H5\",\"appId\":\"\",\"appSecret\":\"\"},{\"clientType\":\"WECHAT_MP\",\"appId\":\"\",\"appSecret\":\"\"}]}');
INSERT INTO `setting` VALUES ('WECHAT_PAYMENT', 'admin', '2021-02-27 07:18:13.767000', b'0', 'admin', '2021-04-27 16:09:19.724000', '{\"nativeAppId\":\"\",\"jsapiAppId\":\"\",\"mchId\":\"\",\"apiclient_key\":\"\",\"serialNumber\":\"\",\"mpAppId\":\"\",\"appAppId\":\"\",\"apiKey3\":\"\",\"apiclient_cert_pem\":\"\",\"apiclient_cert_p12\":\"\",\"h5AppId\":\"\"}');
INSERT INTO `setting` VALUES ('WITHDRAWAL_SETTING', 'admin', '2021-02-21 09:34:41.153000', b'0', 'admin', '2021-02-26 08:54:01.267000', '{\"apply\":true}');

-- ----------------------------
-- Table structure for short_link
-- ----------------------------
DROP TABLE IF EXISTS `short_link`;
CREATE TABLE `short_link`  (
                                  `id` bigint NOT NULL,
                                  `original_params` varchar(255)NULL DEFAULT NULL,
                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of short_link
-- ----------------------------
INSERT INTO `short_link` VALUES (1409715818003222529, '1381796591895052288,1409709806114369537,1382586852829036544');
INSERT INTO `short_link` VALUES (1409718745925230594, '1381796591895052288,1377145942322446336,1382586852829036544');
INSERT INTO `short_link` VALUES (1409805260080627714, '1386931039305203712,1409805176324579330,1381773066530455552');
INSERT INTO `short_link` VALUES (1409806134576234498, '1381796591895052288,1377145942322446336,1381790692237377536');
INSERT INTO `short_link` VALUES (1409806183959969794, '1386931039305203712,1376841229546815488,1381773066530455552');
INSERT INTO `short_link` VALUES (1409815929974767617, '1409794871225802754,1409794871070613505,1381773066530455552');
INSERT INTO `short_link` VALUES (1410131017373843458, '1381793060458856448,1377085464581767168,1382586852829036544');
INSERT INTO `short_link` VALUES (1410431207200784385, '1409794871225802754,1409794871070613505,1410431006687887361');
INSERT INTO `short_link` VALUES (1410431315002785794, '1381793060458856448,1377085464581767168,1410431006687887361');
INSERT INTO `short_link` VALUES (1410431701600174082, '1381796591895052288,1377145942322446336,1410431006687887361');
INSERT INTO `short_link` VALUES (1415228199290646529, '1377132926088511488,1377132926042374144,1415227969929326593');
INSERT INTO `short_link` VALUES (1415276421623164929, '1415275858856628226,1415275858776936450,1415227969929326593');
INSERT INTO `short_link` VALUES (1419943286316240897, '1377132926088511488,1377132926042374144,1410431006687887361');
INSERT INTO `short_link` VALUES (1420272969624883202, '1377132926088511488,1377132926042374144,1399556395723915264');
INSERT INTO `short_link` VALUES (1420288048927903746, '1377132926088511488,1377132926042374144,1382586852829036544');
INSERT INTO `short_link` VALUES (1420288066959216642, '1381792263700480000,1377064344218501120,1382586852829036544');

-- ----------------------------
-- Table structure for sms_reach
-- ----------------------------
DROP TABLE IF EXISTS `sms_reach`;
CREATE TABLE `sms_reach`  (
                                 `id` bigint NOT NULL COMMENT 'ID',
                                 `context` varchar(255)COMMENT '消息内容',
                                 `message_code` varchar(255)COMMENT '消息CODE',
                                 `num` varchar(255)COMMENT '预计发送条数',
                                 `sign_name` varchar(255)COMMENT '签名名称',
                                 `sms_name` varchar(255)COMMENT '模板名称',
                                 `sms_range` varchar(255)COMMENT '接收人',
                                 `create_at` datetime(6) COMMENT '创建时间',
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sms_reach
-- ----------------------------

-- ----------------------------
-- Table structure for sms_sign
-- ----------------------------
DROP TABLE IF EXISTS `sms_sign`;
CREATE TABLE `sms_sign`  (
                                `business_license` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '营业执照',
                                `license` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '授权委托书',
                                `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '短信签名申请说明',
                                `sign_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '签名名称',
                                `sign_source` int COMMENT '签名来源',
                                `sign_status` int COMMENT '签名审核状态',
                                `reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '审核备注',
                                `id` bigint NOT NULL,
                                PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sms_sign
-- ----------------------------
INSERT INTO `sms_sign` VALUES ('https://lilishop-oss.oss-cn-beijing.aliyuncs.com/a2d345f393934b78b8d92e9bb9c54a83.png', 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/59a8bc583cd24bcd85e6b9160e04817c.png', 'lilishop演示站点', 'lilishop演示站点', 4, 2, '请参考下述修改建议:\n1. 未核实到该店铺，请提供电商平台店铺地址链接；', 1463708669619556354);

-- ----------------------------
-- Table structure for sms_template
-- ----------------------------
DROP TABLE IF EXISTS `sms_template`;
CREATE TABLE `sms_template`  (
                                    `id` bigint NOT NULL COMMENT 'ID',
                                    `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '短信模板申请说明',
                                    `template_content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '模板内容',
                                    `template_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '模板名称',
                                    `template_status` int COMMENT '模板审核状态',
                                    `template_type` int COMMENT '短信类型',
                                    `reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '审核备注',
                                    `template_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '短信模板CODE',
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sms_template
-- ----------------------------
INSERT INTO `sms_template` VALUES (1411313932375998465, '用于通知会员注册', '恭喜您已经成为香道网的新会员，首次登陆可以到https://pc-b2b2c.pickmall.cn/领取新人优惠券', '会员注册通知', 2, 1, '*短信通知模板不支持发送营销内容，建议删除相关推广部分：首次登陆可以到https://pc-b2b2c.pickmall.cn/领取新人优惠券', 'SMS_218905489');

-- ----------------------------
-- Table structure for spec_values
-- ----------------------------
DROP TABLE IF EXISTS `spec_values`;
CREATE TABLE `spec_values`  (
                                   `id` bigint NOT NULL COMMENT 'ID',
                                   `create_by` varchar(255)COMMENT '创建者',
                                   `create_at` datetime(6) COMMENT '创建时间',
                                   `delete_flag` bit(1) COMMENT '删除标志 true/false 删除/未删除',
                                   `update_by` varchar(255)COMMENT '更新者',
                                   `update_at` datetime(6) COMMENT '更新时间',
                                   `spec_id` varchar(255)COMMENT '规格项ID',
                                   `spec_value` varchar(255)COMMENT '规格值名字',
                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for special
-- ----------------------------
DROP TABLE IF EXISTS `special`;
CREATE TABLE `special`  (
                               `id` bigint NOT NULL COMMENT 'ID',
                               `create_by` varchar(255)COMMENT '创建者',
                               `create_at` datetime(6) COMMENT '创建时间',
                               `delete_flag` bit(1) COMMENT '删除标志 true/false 删除/未删除',
                               `update_by` varchar(255)COMMENT '更新者',
                               `update_at` datetime(6) COMMENT '更新时间',
                               `client_type` varchar(255)COMMENT '楼层对应连接端类型',
                               `page_data_id` varchar(255)COMMENT '页面ID',
                               `special_name` varchar(255)COMMENT '专题活动名称',
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of special
-- ----------------------------

-- ----------------------------
-- Table structure for specification
-- ----------------------------
DROP TABLE IF EXISTS `specification`;
CREATE TABLE `specification`  (
                                     `id` bigint NOT NULL COMMENT 'ID',
                                     `create_by` varchar(255)COMMENT '创建者',
                                     `create_at` datetime(6) COMMENT '创建时间',
                                     `delete_flag` bit(1) COMMENT '删除标志 true/false 删除/未删除',
                                     `update_by` varchar(255)COMMENT '更新者',
                                     `update_at` datetime(6) COMMENT '更新时间',
                                     `spec_name` varchar(255)COMMENT '规格名称',
                                     `store_id` varchar(255)COMMENT '所属店铺',
                                     `spec_value` textNULL,
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for studio
-- ----------------------------
DROP TABLE IF EXISTS `studio`;
CREATE TABLE `studio`  (
                              `id` bigint NOT NULL,
                              `create_by` varchar(255) ,
                              `create_at` datetime(6) NULL DEFAULT NULL,
                              `delete_flag` bit(1) NULL DEFAULT NULL,
                              `update_by` varchar(255) ,
                              `update_at` datetime(6) NULL DEFAULT NULL,
                              `anchor_name` varchar(255) ,
                              `anchor_wechat` varchar(255) ,
                              `cover_img` varchar(255) ,
                              `end_time` varchar(255) ,
                              `feeds_img` varchar(255) ,
                              `media_url` varchar(255) ,
                              `name` varchar(255) ,
                              `qr_code_url` varchar(255) ,
                              `recommend` bit(1) NOT NULL,
                              `room_goods_list` varchar(255) ,
                              `room_goods_num` int NULL DEFAULT 0,
                              `room_id` int NULL DEFAULT NULL,
                              `share_img` varchar(255) ,
                              `start_time` varchar(255) ,
                              `status` varchar(255) ,
                              `store_id` varchar(255) ,
                              PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of studio
-- ----------------------------
INSERT INTO `studio` VALUES (1411884420198367234, '13011111111', '2021-07-05 11:07:31.977000', b'0', NULL, NULL, '流音寻帆', 'hqx1018841732', 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/facd37390c364f569bf4826af0905afd.png', '1625461200', 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/94039e11ffae4921876c5eb3426bd4b3.png', NULL, '测试直播', NULL, b'0', NULL, 0, 28, 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/74703aab6c754104b67b0ba91c678699.png', '1625455800', 'END', '1376433565247471616');
INSERT INTO `studio` VALUES (1411888772438884353, '13011111111', '2021-07-05 11:24:49.631000', b'0', NULL, NULL, '皮皮华', 'v2sstt', 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/a2694920a06c485381ea3417d179d33d.png', '1625500800', 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/85df35ae6d3c434c8abe4f2e0322eb4e.png', NULL, '皮皮华测试', NULL, b'0', NULL, 0, 29, 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/34dba17227664cfdae94c81a8a5f343c.png', '1625456400', 'END', '1376433565247471616');
INSERT INTO `studio` VALUES (1412252127372570626, '15810610731', '2021-07-06 11:28:40.200000', b'0', '15810610731', '2021-07-06 11:28:49.102000', '测试昵称', 'lifenlonga', 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/1ce5c05ab6c043b89bfdcd63bf0586d9.jpeg', '1625554800', 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/6e4803cb5d844412a4aee8b5df730585.jpeg', NULL, '测试标题', NULL, b'0', '[\"https://lilishop-oss.oss-cn-beijing.aliyuncs.com/0c32095777704e2db7a5f858c32403e9.jpg?x-oss-process=style/200X200\"]', 1, 30, 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/2d7c767cbc4b4e00abb8b92800a9b569.jpeg', '1625543400', 'END', '1376369067769724928');

-- ----------------------------
-- Table structure for studio_commodity
-- ----------------------------
DROP TABLE IF EXISTS `studio_commodity`;
CREATE TABLE `studio_commodity`  (
                                        `id` bigint NOT NULL,
                                        `goods_id` int NULL DEFAULT NULL,
                                        `room_id` int NULL DEFAULT NULL,
                                        PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of studio_commodity
-- ----------------------------
INSERT INTO `studio_commodity` VALUES (1412252164404080641, 50, 30);

-- ----------------------------
-- Table structure for trade
-- ----------------------------
DROP TABLE IF EXISTS `trade`;
CREATE TABLE `trade`  (
                             `id` bigint NOT NULL COMMENT 'ID',
                             `create_by` varchar(255)COMMENT '创建者',
                             `create_at` datetime(6) COMMENT '创建时间',
                             `delete_flag` bit(1) COMMENT '删除标志',
                             `update_by` varchar(255)COMMENT '更新者',
                             `update_at` datetime(6) COMMENT '更新时间',
                             `consignee_address_id_path` varchar(255)COMMENT '地址id',
                             `consignee_address_path` varchar(255)COMMENT '地址名称',
                             `consignee_name` varchar(255)COMMENT '收货人姓名',
                             `discount_price` decimal(10, 2) COMMENT '优惠的金额',
                             `flow_price` decimal(10, 2) COMMENT '总价格',
                             `freight_price` decimal(10, 2) COMMENT '运费',
                             `goods_price` decimal(10, 2) COMMENT '原价',
                             `member_id` varchar(255)COMMENT '买家id',
                             `member_name` varchar(255)COMMENT '买家用户名',
                             `payment_method` varchar(255)COMMENT '支付方式',
                             `sn` varchar(255)COMMENT '交易编号',
                             `delivery_method` varchar(255)COMMENT '配送方式',
                             `consignee_mobile` varchar(255)COMMENT '收件人手机',
                             `pay_status` varchar(255)NULL DEFAULT NULL,
                             PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of trade
-- ----------------------------

-- ----------------------------
-- Table structure for user_role
-- ----------------------------
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role`  (
                                 `id` bigint NOT NULL COMMENT 'ID',
                                 `role_id` varchar(255)COMMENT '角色唯一id',
                                 `user_id` varchar(255)COMMENT '用户唯一id',
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3;

-- ----------------------------
-- Table structure for verification_source
-- ----------------------------
DROP TABLE IF EXISTS `verification_source`;
CREATE TABLE `verification_source`  (
                                           `id` bigint NOT NULL COMMENT 'ID',
                                           `create_by` varchar(255)  COMMENT '创建者',
                                           `create_at` datetime(6) COMMENT '创建时间',
                                           `delete_flag` bit(1) COMMENT '删除标志 true/false 删除/未删除',
                                           `update_by` varchar(255)  COMMENT '更新者',
                                           `update_at` datetime(6) COMMENT '更新时间',
                                           `name` varchar(255)  COMMENT '名称',
                                           `resource` varchar(255)  COMMENT '资源地址',
                                           `category` varchar(30)  COMMENT '验证码资源类型',
                                           PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of verification_source
-- ----------------------------
INSERT INTO `verification_source` VALUES (1374923382696443904, 'admin', '2021-03-24 22:17:33.929000', b'0', NULL, NULL, 'aaaa', '', 'SOURCE');
INSERT INTO `verification_source` VALUES (1374924056813371392, 'admin', '2021-03-24 22:20:14.655000', b'0', NULL, NULL, 'aaaa', '', 'SOURCE');
INSERT INTO `verification_source` VALUES (1374925624577097728, 'admin', '2021-03-24 22:26:28.439000', b'0', 'admin', '2021-03-25 04:37:15.833000', 'pic1', 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/9bda22fa8bb34033922faeca931f53b9.jpg', 'RESOURCE');
INSERT INTO `verification_source` VALUES (1374928342632890368, 'admin', '2021-03-24 22:37:16.474000', b'0', 'admin', '2021-03-25 04:40:15.272000', 'slider1', 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/571cc482c3f54d378de12a07f5262934.png', 'SLIDER');
INSERT INTO `verification_source` VALUES (1375018973833199616, 'admin', '2021-03-25 04:37:24.630000', b'0', NULL, NULL, 'pic2', 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/a77deac53b5c48ff968b9936c8c64477.jpg', 'RESOURCE');
INSERT INTO `verification_source` VALUES (1375019001842761728, 'admin', '2021-03-25 04:37:31.314000', b'0', NULL, NULL, 'pic3', 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/87c783a7aae94600b92983f3b5713a94.jpg', 'RESOURCE');
INSERT INTO `verification_source` VALUES (1375019029051211776, 'admin', '2021-03-25 04:37:37.801000', b'0', NULL, NULL, 'pic4', 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/9e67a1150f7941e294330940505a8b6e.jpg', 'RESOURCE');
INSERT INTO `verification_source` VALUES (1375019056037363712, 'admin', '2021-03-25 04:37:44.235000', b'0', NULL, NULL, 'pic5', 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/c6033185c98a447c9876b8424996e89e.jpg', 'RESOURCE');
INSERT INTO `verification_source` VALUES (1375019091248545792, 'admin', '2021-03-25 04:37:52.630000', b'0', NULL, NULL, 'pic6', 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/e339a017f56b441db7d463462f0be918.jpg', 'RESOURCE');
INSERT INTO `verification_source` VALUES (1375019124496793600, 'admin', '2021-03-25 04:38:00.557000', b'0', NULL, NULL, 'pic7', 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/5fb387820c484dadbbf5ee116b95e1f7.jpg', 'RESOURCE');
INSERT INTO `verification_source` VALUES (1375019153378770944, 'admin', '2021-03-25 04:38:07.443000', b'0', NULL, NULL, 'pic8', 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/4c85191728a84bb5888a1bd1ed6e84e2.jpg', 'RESOURCE');
INSERT INTO `verification_source` VALUES (1375019191098146816, 'admin', '2021-03-25 04:38:16.436000', b'0', NULL, NULL, 'pic9', 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/c9cfbc77ce904f49945777fec9330f19.jpg', 'RESOURCE');
INSERT INTO `verification_source` VALUES (1375019231891947520, 'admin', '2021-03-25 04:38:26.162000', b'0', NULL, NULL, 'pic10', 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/89e6149e3e094e3685bab50df9f931de.jpg', 'RESOURCE');
INSERT INTO `verification_source` VALUES (1375019260119613440, 'admin', '2021-03-25 04:38:32.892000', b'0', NULL, NULL, 'pic11', 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/cd9cb96bfcc2448d98c8b62ba7e6c54a.jpg', 'RESOURCE');
INSERT INTO `verification_source` VALUES (1375019287202234368, 'admin', '2021-03-25 04:38:39.349000', b'0', NULL, NULL, 'pic12', 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/ecf765f942074b0bb9fecb07f06ea04a.jpg', 'RESOURCE');
INSERT INTO `verification_source` VALUES (1375019324225355776, 'admin', '2021-03-25 04:38:48.176000', b'0', NULL, NULL, 'pic13', 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/876e040152c5417fa210b0e081230081.jpg', 'RESOURCE');
INSERT INTO `verification_source` VALUES (1375019354214629376, 'admin', '2021-03-25 04:38:55.326000', b'0', NULL, NULL, 'pic14', 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/22b20912533d424cb792463c65c87086.jpg', 'RESOURCE');
INSERT INTO `verification_source` VALUES (1375019402356850688, 'admin', '2021-03-25 04:39:06.804000', b'0', NULL, NULL, 'pic15', 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/63698a4f91884de0b2101f0217c37f21.jpg', 'RESOURCE');
INSERT INTO `verification_source` VALUES (1375019434212589568, 'admin', '2021-03-25 04:39:14.399000', b'0', NULL, NULL, 'pic16', 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/2b051d6d62964eff85eb94469af774e4.jpg', 'RESOURCE');
INSERT INTO `verification_source` VALUES (1375019466777165824, 'admin', '2021-03-25 04:39:22.163000', b'0', NULL, NULL, 'pic17', 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/f53f1a83e67d4a829b83871b14556a80.jpg', 'RESOURCE');
INSERT INTO `verification_source` VALUES (1375019499257856000, 'admin', '2021-03-25 04:39:29.907000', b'0', 'admin', '2021-06-21 00:32:17.551000', 'pic18', 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/f4dba40016384c2ea7ec7e3fe14178e1.jpg', 'RESOURCE');
INSERT INTO `verification_source` VALUES (1375019533244301312, 'admin', '2021-03-25 04:39:38.010000', b'0', NULL, NULL, 'pic19', 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/ae1c3a94dd65492da685e8d795142811.jpg', 'RESOURCE');
INSERT INTO `verification_source` VALUES (1375019562025615360, 'admin', '2021-03-25 04:39:44.872000', b'0', NULL, NULL, 'pic20', 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/c64d0ca543d64e14a50e12eb4b4a6170.jpg', 'RESOURCE');
INSERT INTO `verification_source` VALUES (1375019589565415424, 'admin', '2021-03-25 04:39:51.438000', b'0', NULL, NULL, 'pic21', 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/3f01fd63b7634a7f892b096946ba92b1.jpg', 'RESOURCE');
INSERT INTO `verification_source` VALUES (1375019614437638144, 'admin', '2021-03-25 04:39:57.368000', b'0', NULL, NULL, 'pic22', 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/e0f5300053b64bf68c1e5a72da553f79.jpg', 'RESOURCE');
INSERT INTO `verification_source` VALUES (1375019769773686784, 'admin', '2021-03-25 04:40:34.403000', b'0', 'admin', '2021-07-13 18:48:35.397000', 'slider2', 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/af08d2babd2a4573b4d0bfb1a77de9ec.png', 'SLIDER');
INSERT INTO `verification_source` VALUES (1375019802841579520, 'admin', '2021-03-25 04:40:42.287000', b'0', NULL, NULL, 'slider3', 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/d0554cdd219d479593ef5ef8b13f5a35.png', 'SLIDER');
INSERT INTO `verification_source` VALUES (1375019834093338624, 'admin', '2021-03-25 04:40:49.738000', b'0', NULL, NULL, 'slider4', 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/a0e910a6d8af44e0b1180fd61caa6a26.png', 'SLIDER');
INSERT INTO `verification_source` VALUES (1375019863013064704, 'admin', '2021-03-25 04:40:56.633000', b'0', NULL, NULL, 'slider5', 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/5df7b1d5e2924fe9aa7625abeca8ecad.png', 'SLIDER');
INSERT INTO `verification_source` VALUES (1375019899201519616, 'admin', '2021-03-25 04:41:05.261000', b'0', NULL, NULL, 'slider6', 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/1107d6be276849118353f8a63d387aea.png', 'SLIDER');

-- ----------------------------
-- Table structure for wallet_log
-- ----------------------------
DROP TABLE IF EXISTS `wallet_log`;
CREATE TABLE `wallet_log`  (
                                  `id` bigint NOT NULL COMMENT 'ID',
                                  `create_by` varchar(255)  COMMENT '创建者',
                                  `create_at` datetime(6) COMMENT '创建时间',
                                  `detail` varchar(255)  COMMENT '日志明细',
                                  `member_id` varchar(255)  COMMENT '会员id',
                                  `member_name` varchar(255)  COMMENT '会员名称',
                                  `money` decimal(10, 2) COMMENT '金额',
                                  `service_type` varchar(255)  COMMENT '业务类型',
                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wallet_log
-- ----------------------------

-- ----------------------------
-- Table structure for wechat_message
-- ----------------------------
DROP TABLE IF EXISTS `wechat_message`;
CREATE TABLE `wechat_message`  (
                                      `id` bigint NOT NULL,
                                      `create_by` varchar(255)NULL DEFAULT NULL,
                                      `create_at` datetime(6) NULL DEFAULT NULL,
                                      `delete_flag` bit(1) NULL DEFAULT NULL,
                                      `update_by` varchar(255)NULL DEFAULT NULL,
                                      `update_at` datetime(6) NULL DEFAULT NULL,
                                      `code` varchar(255)NULL DEFAULT NULL,
                                      `enable` bit(1) NULL DEFAULT NULL,
                                      `first` varchar(255)NULL DEFAULT NULL,
                                      `keywords` varchar(255)NULL DEFAULT NULL,
                                      `name` varchar(255)NULL DEFAULT NULL,
                                      `order_status` varchar(255)NULL DEFAULT NULL,
                                      `remark` varchar(255)NULL DEFAULT NULL,
                                      PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wechat_message
-- ----------------------------

-- ----------------------------
-- Table structure for wechat_mp_message
-- ----------------------------
DROP TABLE IF EXISTS `wechat_mp_message`;
CREATE TABLE `wechat_mp_message`  (
                                         `id` bigint NOT NULL,
                                         `create_by` varchar(255)NULL DEFAULT NULL,
                                         `create_at` datetime(6) NULL DEFAULT NULL,
                                         `delete_flag` bit(1) NULL DEFAULT NULL,
                                         `update_by` varchar(255)NULL DEFAULT NULL,
                                         `update_at` datetime(6) NULL DEFAULT NULL,
                                         `code` varchar(255)NULL DEFAULT NULL,
                                         `enable` bit(1) NULL DEFAULT NULL,
                                         `keywords` varchar(255)NULL DEFAULT NULL,
                                         `keywords_text` varchar(255)NULL DEFAULT NULL,
                                         `name` varchar(255)NULL DEFAULT NULL,
                                         `order_status` varchar(255)NULL DEFAULT NULL,
                                         `template_id` varchar(255)NULL DEFAULT NULL,
                                         PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wechat_mp_message
-- ----------------------------
INSERT INTO `wechat_mp_message` VALUES (1466026565142130690, 'admin', '2021-12-01 20:49:05.243000', b'0', NULL, NULL, 'LL33E9vpSiFw-rgM6Sh_dtvwZ-Smt345lLwhgJrD-Z8', b'1', '[\"ORDER_SN\",\"MEMBER_NAME\",\"PRICE\",\"GOODS_INFO\"]', '[\"character_string9\",\"thing23\",\"amount12\",\"thing24\"]', '订单支付成功,准备发货', 'UNDELIVERED', '487');
INSERT INTO `wechat_mp_message` VALUES (1466026569038639106, 'admin', '2021-12-01 20:49:06.173000', b'0', NULL, NULL, '8UqLGc3kBUDN3md_czOLARv-_SQuuNPevYIAnf0yf4s', b'1', '[\"ORDER_SN\",\"GOODS_INFO\",\"LOGISTICS_NAME\",\"LOGISTICS_NO\"]', '[\"character_string15\",\"thing23\",\"thing14\",\"character_string13\"]', '订单发货成功', 'DELIVERED', '374');
INSERT INTO `wechat_mp_message` VALUES (1466026574944219137, 'admin', '2021-12-01 20:49:07.581000', b'0', NULL, NULL, 'knckkHDSRC7eYuX_4sOJaaVg2owWgiB9v07KbkHae18', b'1', '[\"SHOP_NAME\",\"GOODS_INFO\"]', '[\"thing1\",\"thing3\"]', '订单完成', 'COMPLETED', '3606');

-- ----------------------------
-- Table structure for wholesale
-- ----------------------------
DROP TABLE IF EXISTS `wholesale`;
CREATE TABLE `wholesale`  (
                                 `id` bigint NOT NULL,
                                 `create_by` varchar(255) ,
                                 `create_at` datetime(6) NULL DEFAULT NULL,
                                 `delete_flag` bit(1) NULL DEFAULT NULL,
                                 `update_by` varchar(255) ,
                                 `update_at` datetime(6) NULL DEFAULT NULL,
                                 `price` decimal(10, 2) COMMENT '价格',
                                 `goods_id` bigint COMMENT '商品id',
                                 `sku_id` bigint COMMENT '商品skuId',
                                 `num` int COMMENT '起购量',
                                 `template_id` varchar(255) ,
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '批发规则表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wholesale
-- ----------------------------

-- ----------------------------
-- Table structure for payment_log
-- ----------------------------
DROP TABLE IF EXISTS `payment_log`;
CREATE TABLE `payment_log`  (
                                `id` bigint NOT NULL,
                                `create_by` varchar(255) ,
                                `create_at` datetime(6) NULL DEFAULT NULL,
                                `delete_flag` bit(1) NULL DEFAULT NULL,
                                `update_by` varchar(255) ,
                                `update_at` datetime(6) NULL DEFAULT NULL,
                                `client_type` varchar(255) ,
                                `flow_price` decimal(10, 2) NULL DEFAULT NULL,
                                `member_id` varchar(255) ,
                                `member_name` varchar(255) ,
                                `order_type` varchar(255) ,
                                `pay_order_no` varchar(255) ,
                                `pay_status` varchar(255) ,
                                `payment_method` varchar(255) ,
                                `payment_time` datetime(6) NULL DEFAULT NULL,
                                `receivable_no` varchar(255) ,
                                `sn` varchar(255) ,
                                `store_id` varchar(255) ,
                                `store_name` varchar(255) ,
                                `trade_sn` varchar(255) ,
                                PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of payment_log
-- ----------------------------

-- --------------------------ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin ROW_FORMAT = DYNAMIC--
-- Table structure for xxl_job_group
-- ----------------------------
DROP TABLE IF EXISTS `xxl_job_group`;
CREATE TABLE `xxl_job_group`  (
                                  `id` int NOT NULL AUTO_INCREMENT,
                                  `app_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '执行器AppName',
                                  `title` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '执行器名称',
                                  `address_type` tinyint NOT NULL DEFAULT 0 COMMENT '执行器地址类型：0=自动注册、1=手动录入',
                                  `address_list` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '执行器地址列表，多地址逗号分隔',
                                  `update_at` datetime NULL DEFAULT NULL,
                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of xxl_job_group
-- ----------------------------
INSERT INTO `xxl_job_group` VALUES (1, 'xxl-job-executor-sample', '示例执行器', 0, NULL, '2024-04-15 19:35:53');
INSERT INTO `xxl_job_group` VALUES (2, 'xxl-job-executor-lilishop', 'lilishop', 0, 'http://211.149.128.133:8891/', '2024-04-15 19:35:53');

-- ----------------------------
-- Table structure for xxl_job_info
-- ----------------------------
DROP TABLE IF EXISTS `xxl_job_info`;
CREATE TABLE `xxl_job_info`  (
                                 `id` int NOT NULL AUTO_INCREMENT,
                                 `job_group` int NOT NULL COMMENT '执行器主键ID',
                                 `job_desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                                 `add_time` datetime NULL DEFAULT NULL,
                                 `update_at` datetime NULL DEFAULT NULL,
                                 `author` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '作者',
                                 `alarm_email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '报警邮件',
                                 `schedule_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'NONE' COMMENT '调度类型',
                                 `schedule_conf` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '调度配置，值含义取决于调度类型',
                                 `misfire_strategy` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'DO_NOTHING' COMMENT '调度过期策略',
                                 `executor_route_strategy` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '执行器路由策略',
                                 `executor_handler` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '执行器任务handler',
                                 `executor_param` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '执行器任务参数',
                                 `executor_block_strategy` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '阻塞处理策略',
                                 `executor_timeout` int NOT NULL DEFAULT 0 COMMENT '任务执行超时时间，单位秒',
                                 `executor_fail_retry_count` int NOT NULL DEFAULT 0 COMMENT '失败重试次数',
                                 `glue_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'GLUE类型',
                                 `glue_source` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'GLUE源代码',
                                 `glue_remark` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT 'GLUE备注',
                                 `glue_updatetime` datetime COMMENT 'GLUE更新时间',
                                 `child_jobid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '子任务ID，多个逗号分隔',
                                 `trigger_status` tinyint NOT NULL DEFAULT 0 COMMENT '调度状态：0-停止，1-运行',
                                 `trigger_last_time` bigint NOT NULL DEFAULT 0 COMMENT '上次调度时间',
                                 `trigger_next_time` bigint NOT NULL DEFAULT 0 COMMENT '下次调度时间',
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of xxl_job_info
-- ----------------------------
INSERT INTO `xxl_job_info` VALUES (1, 1, '测试任务1', '2018-11-03 22:21:31', '2018-11-03 22:21:31', 'XXL', '', 'CRON', '0 0 0 * * ? *', 'DO_NOTHING', 'FIRST', 'demoJobHandler', '', 'SERIAL_EXECUTION', 0, 0, 'BEAN', '', 'GLUE代码初始化', '2018-11-03 22:21:31', '', 0, 0, 0);
INSERT INTO `xxl_job_info` VALUES (2, 2, '每小时执行任务', '2020-12-24 11:01:24', '2020-12-24 15:03:03', 'admin', '', 'CRON', '0 0 0/1 * * ?', 'DO_NOTHING', 'ROUND', 'everyHourExecuteJobHandler', '', 'SERIAL_EXECUTION', 0, 0, 'BEAN', '', 'GLUE代码初始化', '2020-12-24 11:01:24', '', 1, 1641733200000, 1713182400000);
INSERT INTO `xxl_job_info` VALUES (3, 2, '每分钟执行', '2020-12-24 11:02:58', '2020-12-24 15:02:49', 'admin', '', 'CRON', '0 0/1 * * * ?', 'DO_NOTHING', 'ROUND', 'everyMinuteExecute', '', 'SERIAL_EXECUTION', 0, 0, 'BEAN', '', 'GLUE代码初始化', '2020-12-24 11:02:58', '', 1, 1713180960000, 1713181020000);
INSERT INTO `xxl_job_info` VALUES (4, 2, '每天执行', '2020-12-24 11:03:41', '2020-12-24 15:02:28', 'admin', '', 'CRON', '0 5 2 1/1 * ?', 'DO_NOTHING', 'ROUND', 'everyDayExecuteJobHandler', '', 'SERIAL_EXECUTION', 0, 0, 'BEAN', '', 'GLUE代码初始化', '2020-12-24 11:03:41', '', 1, 1641751500000, 1713204300000);

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
                                `executor_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '执行器地址，本次执行的地址',
                                `executor_handler` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '执行器任务handler',
                                `executor_param` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '执行器任务参数',
                                `executor_sharding_param` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '执行器任务分片参数，格式如 1/2',
                                `executor_fail_retry_count` int NOT NULL DEFAULT 0 COMMENT '失败重试次数',
                                `trigger_time` datetime COMMENT '调度-时间',
                                `trigger_code` int NOT NULL COMMENT '调度-结果',
                                `trigger_msg` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '调度-日志',
                                `handle_time` datetime COMMENT '执行-时间',
                                `handle_code` int NOT NULL COMMENT '执行-状态',
                                `handle_msg` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '执行-日志',
                                `alarm_status` tinyint NOT NULL DEFAULT 0 COMMENT '告警状态：0-默认、1-无需告警、2-告警成功、3-告警失败',
                                PRIMARY KEY (`id`) USING BTREE,
                                INDEX `I_trigger_time`(`trigger_time` ASC) USING BTREE,
                                INDEX `I_handle_code`(`handle_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of xxl_job_log
-- ----------------------------
INSERT INTO `xxl_job_log` VALUES (1, 2, 3, 'http://211.149.128.133:8891/', 'everyMinuteExecute', '', NULL, 0, '2024-04-15 19:28:00', 500, '任务触发类型：Cron触发<br>调度机器：172.20.0.4<br>执行器-注册方式：自动注册<br>执行器-地址列表：[http://211.149.128.133:8891/]<br>路由策略：轮询<br>阻塞处理策略：单机串行<br>任务超时时间：0<br>失败重试次数：0<br><br><span style=\"color:#00c0ef;\" > >>>>>>>>>>>触发调度<<<<<<<<<<< </span><br>触发调度：<br>address：http://211.149.128.133:8891/<br>code：500<br>msg：xxl-rpc remoting error(No route to host (Host unreachable)), for url : http://211.149.128.133:8891/run', NULL, 0, NULL, 1);
INSERT INTO `xxl_job_log` VALUES (2, 2, 3, 'http://211.149.128.133:8891/', 'everyMinuteExecute', '', NULL, 0, '2024-04-15 19:29:00', 500, '任务触发类型：Cron触发<br>调度机器：172.20.0.4<br>执行器-注册方式：自动注册<br>执行器-地址列表：[http://211.149.128.133:8891/]<br>路由策略：轮询<br>阻塞处理策略：单机串行<br>任务超时时间：0<br>失败重试次数：0<br><br><span style=\"color:#00c0ef;\" > >>>>>>>>>>>触发调度<<<<<<<<<<< </span><br>触发调度：<br>address：http://211.149.128.133:8891/<br>code：500<br>msg：xxl-rpc remoting error(No route to host (Host unreachable)), for url : http://211.149.128.133:8891/run', NULL, 0, NULL, 1);
INSERT INTO `xxl_job_log` VALUES (3, 2, 3, 'http://211.149.128.133:8891/', 'everyMinuteExecute', '', NULL, 0, '2024-04-15 19:30:00', 500, '任务触发类型：Cron触发<br>调度机器：172.20.0.4<br>执行器-注册方式：自动注册<br>执行器-地址列表：[http://211.149.128.133:8891/]<br>路由策略：轮询<br>阻塞处理策略：单机串行<br>任务超时时间：0<br>失败重试次数：0<br><br><span style=\"color:#00c0ef;\" > >>>>>>>>>>>触发调度<<<<<<<<<<< </span><br>触发调度：<br>address：http://211.149.128.133:8891/<br>code：500<br>msg：xxl-rpc remoting error(No route to host (Host unreachable)), for url : http://211.149.128.133:8891/run', NULL, 0, NULL, 1);
INSERT INTO `xxl_job_log` VALUES (4, 2, 3, 'http://211.149.128.133:8891/', 'everyMinuteExecute', '', NULL, 0, '2024-04-15 19:31:00', 500, '任务触发类型：Cron触发<br>调度机器：172.20.0.4<br>执行器-注册方式：自动注册<br>执行器-地址列表：[http://211.149.128.133:8891/]<br>路由策略：轮询<br>阻塞处理策略：单机串行<br>任务超时时间：0<br>失败重试次数：0<br><br><span style=\"color:#00c0ef;\" > >>>>>>>>>>>触发调度<<<<<<<<<<< </span><br>触发调度：<br>address：http://211.149.128.133:8891/<br>code：500<br>msg：xxl-rpc remoting error(No route to host (Host unreachable)), for url : http://211.149.128.133:8891/run', NULL, 0, NULL, 1);
INSERT INTO `xxl_job_log` VALUES (5, 2, 3, 'http://211.149.128.133:8891/', 'everyMinuteExecute', '', NULL, 0, '2024-04-15 19:32:00', 500, '任务触发类型：Cron触发<br>调度机器：172.20.0.4<br>执行器-注册方式：自动注册<br>执行器-地址列表：[http://211.149.128.133:8891/]<br>路由策略：轮询<br>阻塞处理策略：单机串行<br>任务超时时间：0<br>失败重试次数：0<br><br><span style=\"color:#00c0ef;\" > >>>>>>>>>>>触发调度<<<<<<<<<<< </span><br>触发调度：<br>address：http://211.149.128.133:8891/<br>code：500<br>msg：xxl-rpc remoting error(No route to host (Host unreachable)), for url : http://211.149.128.133:8891/run', NULL, 0, NULL, 1);
INSERT INTO `xxl_job_log` VALUES (6, 2, 3, 'http://211.149.128.133:8891/', 'everyMinuteExecute', '', NULL, 0, '2024-04-15 19:33:00', 500, '任务触发类型：Cron触发<br>调度机器：172.20.0.4<br>执行器-注册方式：自动注册<br>执行器-地址列表：[http://211.149.128.133:8891/]<br>路由策略：轮询<br>阻塞处理策略：单机串行<br>任务超时时间：0<br>失败重试次数：0<br><br><span style=\"color:#00c0ef;\" > >>>>>>>>>>>触发调度<<<<<<<<<<< </span><br>触发调度：<br>address：http://211.149.128.133:8891/<br>code：500<br>msg：xxl-rpc remoting error(No route to host (Host unreachable)), for url : http://211.149.128.133:8891/run', NULL, 0, NULL, 1);
INSERT INTO `xxl_job_log` VALUES (7, 2, 3, 'http://211.149.128.133:8891/', 'everyMinuteExecute', '', NULL, 0, '2024-04-15 19:34:00', 500, '任务触发类型：Cron触发<br>调度机器：172.20.0.4<br>执行器-注册方式：自动注册<br>执行器-地址列表：[http://211.149.128.133:8891/]<br>路由策略：轮询<br>阻塞处理策略：单机串行<br>任务超时时间：0<br>失败重试次数：0<br><br><span style=\"color:#00c0ef;\" > >>>>>>>>>>>触发调度<<<<<<<<<<< </span><br>触发调度：<br>address：http://211.149.128.133:8891/<br>code：500<br>msg：xxl-rpc remoting error(No route to host (Host unreachable)), for url : http://211.149.128.133:8891/run', NULL, 0, NULL, 1);
INSERT INTO `xxl_job_log` VALUES (8, 2, 3, 'http://211.149.128.133:8891/', 'everyMinuteExecute', '', NULL, 0, '2024-04-15 19:35:00', 500, '任务触发类型：Cron触发<br>调度机器：172.20.0.4<br>执行器-注册方式：自动注册<br>执行器-地址列表：[http://211.149.128.133:8891/]<br>路由策略：轮询<br>阻塞处理策略：单机串行<br>任务超时时间：0<br>失败重试次数：0<br><br><span style=\"color:#00c0ef;\" > >>>>>>>>>>>触发调度<<<<<<<<<<< </span><br>触发调度：<br>address：http://211.149.128.133:8891/<br>code：500<br>msg：xxl-rpc remoting error(No route to host (Host unreachable)), for url : http://211.149.128.133:8891/run', NULL, 0, NULL, 1);
INSERT INTO `xxl_job_log` VALUES (9, 2, 3, 'http://211.149.128.133:8891/', 'everyMinuteExecute', '', NULL, 0, '2024-04-15 19:36:00', 500, '任务触发类型：Cron触发<br>调度机器：172.20.0.4<br>执行器-注册方式：自动注册<br>执行器-地址列表：[http://211.149.128.133:8891/]<br>路由策略：轮询<br>阻塞处理策略：单机串行<br>任务超时时间：0<br>失败重试次数：0<br><br><span style=\"color:#00c0ef;\" > >>>>>>>>>>>触发调度<<<<<<<<<<< </span><br>触发调度：<br>address：http://211.149.128.133:8891/<br>code：500<br>msg：xxl-rpc remoting error(No route to host (Host unreachable)), for url : http://211.149.128.133:8891/run', NULL, 0, NULL, 1);

-- ----------------------------
-- Table structure for xxl_job_log_report
-- ----------------------------
DROP TABLE IF EXISTS `xxl_job_log_report`;
CREATE TABLE `xxl_job_log_report`  (
                                       `id` int NOT NULL AUTO_INCREMENT,
                                       `trigger_day` datetime COMMENT '调度-时间',
                                       `running_count` int NOT NULL DEFAULT 0 COMMENT '运行中-日志数量',
                                       `suc_count` int NOT NULL DEFAULT 0 COMMENT '执行成功-日志数量',
                                       `fail_count` int NOT NULL DEFAULT 0 COMMENT '执行失败-日志数量',
                                       `update_at` datetime NULL DEFAULT NULL,
                                       PRIMARY KEY (`id`) USING BTREE,
                                       UNIQUE INDEX `i_trigger_day`(`trigger_day` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 189 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of xxl_job_log_report
-- ----------------------------

-- ----------------------------
-- Table structure for xxl_job_logglue
-- ----------------------------
DROP TABLE IF EXISTS `xxl_job_logglue`;
CREATE TABLE `xxl_job_logglue`  (
                                    `id` int NOT NULL AUTO_INCREMENT,
                                    `job_id` int NOT NULL COMMENT '任务，主键ID',
                                    `glue_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT 'GLUE类型',
                                    `glue_source` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'GLUE源代码',
                                    `glue_remark` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'GLUE备注',
                                    `add_time` datetime NULL DEFAULT NULL,
                                    `update_at` datetime NULL DEFAULT NULL,
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
                                     `update_at` datetime NULL DEFAULT NULL,
                                     PRIMARY KEY (`id`) USING BTREE,
                                     INDEX `i_g_k_v`(`registry_group` ASC, `registry_key` ASC, `registry_value` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of xxl_job_registry
-- ----------------------------
INSERT INTO `xxl_job_registry` VALUES (1, 'EXECUTOR', 'xxl-job-executor-lilishop', 'http://211.149.128.133:8891/', '2024-04-15 19:36:09');

-- ----------------------------
-- Table structure for xxl_job_user
-- ----------------------------
DROP TABLE IF EXISTS `xxl_job_user`;
CREATE TABLE `xxl_job_user`  (
                                 `id` int NOT NULL AUTO_INCREMENT,
                                 `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '账号',
                                 `password` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
                                 `role` tinyint NOT NULL COMMENT '角色：0-普通用户、1-管理员',
                                 `permission` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '权限：执行器ID列表，多个逗号分割',
                                 PRIMARY KEY (`id`) USING BTREE,
                                 UNIQUE INDEX `i_username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of xxl_job_user
-- ----------------------------
INSERT INTO `xxl_job_user` VALUES (1, 'admin', '96e79218965eb72c92a549dd5a330112', 1, NULL);

SET FOREIGN_KEY_CHECKS = 1;
-- ----------------------------
-- Table structure for brand
-- ----------------------------
DROP TABLE IF EXISTS `brand`;
CREATE TABLE `brand`  (
                             `id` bigint NOT NULL COMMENT 'ID',
                             `create_by` varchar(255)COMMENT '创建者',
                             `create_at` datetime COMMENT '创建时间',
                             `delete_flag` bit(1) COMMENT '删除标志 true/false 删除/未删除',
                             `update_by` varchar(255)COMMENT '更新者',
                             `update_at` datetime COMMENT '更新时间',
                             `logo` varchar(255)COMMENT '品牌图标',
                             `name` varchar(255)COMMENT '品牌名称',
                             PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = COMPACT;
-- ----------------------------
-- Table structure for bill
-- ----------------------------
DROP TABLE IF EXISTS `bill`;
CREATE TABLE `bill`  (
                            `id` bigint NOT NULL COMMENT 'ID',
                            `create_at` datetime(6) COMMENT '创建时间',
                            `bank_account_name` varchar(255)  COMMENT '银行开户名',
                            `bank_account_number` varchar(255)  COMMENT '公司银行账号',
                            `bank_code` varchar(255)  COMMENT '支行联行号',
                            `bank_name` varchar(255)  COMMENT '开户银行支行名称',
                            `bill_price` decimal(10, 2) COMMENT '最终结算金额',
                            `bill_status` varchar(255)  COMMENT '状态 OUT(已出账),CHECK(已对账),EXAMINE(已审核),PAY(已付款)',
                            `commission_price` decimal(10, 2) COMMENT '平台收取佣金',
                            `distribution_commission` decimal(10, 2) COMMENT '分销返现支出',
                            `distribution_refund_commission` decimal(10, 2) COMMENT '分销订单退还',
                            `order_price` decimal(10, 2) COMMENT '结算周期内订单付款总金额',
                            `pay_time` datetime(6) COMMENT '平台付款时间',
                            `refund_commission_price` decimal(10, 2) COMMENT '退单产生退还佣金金额',
                            `refund_price` decimal(10, 2) COMMENT '退单金额',
                            `store_id` bigint COMMENT '店铺ID',
                            `store_name` varchar(255)  COMMENT '店铺名称',
                            `site_coupon_commission` decimal(10, 2) COMMENT '平台优惠券补贴',
                            `site_coupon_refund_commission` decimal(10, 2) COMMENT '退货平台优惠券补贴返还',
                            `sn` varchar(255)  COMMENT '账单号',
                            `end_time` datetime(6) COMMENT '结算结束时间',
                            `start_time` datetime(6) COMMENT '结算开始时间',
                            `point_settlement_price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '积分商品结算金额 ',
                            `kanjia_settlement_price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '砍价商品结算金额',
                            `point_refund_settlement_price` decimal(10, 2) COMMENT '退货积分补贴返还',
                            `kanjia_refund_settlement_price` decimal(10, 2) COMMENT '退货砍价补贴返还',
                            PRIMARY KEY (`id`) USING BTREE,
                            INDEX `store_id`(`store_id` ASC) USING BTREE COMMENT '店铺id索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admin_user
-- ----------------------------
DROP TABLE IF EXISTS `admin_user`;
CREATE TABLE `admin_user`  (
                                  `id` bigint NOT NULL COMMENT 'ID',
                                  `create_by` varchar(255)COMMENT '创建者',
                                  `create_at` datetime(6) COMMENT '创建时间',
                                  `delete_flag` bit(1) COMMENT '删除标志 true/false 删除/未删除',
                                  `update_by` varchar(255)COMMENT '更新者',
                                  `update_at` datetime(6) COMMENT '更新时间',
                                  `avatar` varchar(1000)COMMENT '用户头像',
                                  `department_id` varchar(255)COMMENT '所属部门ID',
                                  `description` varchar(255)COMMENT '备注',
                                  `email` varchar(255)COMMENT '邮件',
                                  `is_super` bit(1) COMMENT '是否是超级管理员 超级管理员/普通管理员',
                                  `mobile` varchar(255)COMMENT '手机',
                                  `nick_name` varchar(255)COMMENT '昵称',
                                  `password` varchar(255)COMMENT '密码',
                                  `sex` varchar(255)NULL DEFAULT NULL,
                                  `status` bit(1) COMMENT '状态 默认true正常 false禁用',
                                  `username` varchar(200)NOT NULL COMMENT '用户名',
                                  `role_ids` varchar(255)COMMENT '角色ID集合',
                                  PRIMARY KEY (`id`) USING BTREE,
                                  UNIQUE INDEX `UK_sh2dyl78jk1vxtlyohwr5wht9`(`username` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of admin_user
-- ----------------------------
INSERT INTO `admin_user` VALUES (1337306110277476352, '', '2020-12-11 07:59:57.000000', b'0', 'admin', '2021-12-01 16:44:56.648000', 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/65e87ffa718b42bb9c201712566dbc9a.png', '1364415918628667392', 'aaa', 'aaa@aa.com', b'1', '13012345678', '初一', '$2a$10$sVnczXsvm2V0SBKCx3e96eRr6Ssl69bh56iD3RJNJE3o2LzqEO4qW', NULL, b'1', 'admin', '1394944593522327552');

-- ----------------------------
-- Table structure for after_sale
-- ----------------------------
DROP TABLE IF EXISTS `after_sale`;
CREATE TABLE `after_sale`  (
                                  `id` bigint NOT NULL COMMENT 'ID',
                                  `create_by` varchar(64)  COMMENT '创建者',
                                  `create_at` datetime(6) COMMENT '创建时间',
                                  `delete_flag` bit(1) COMMENT '删除标志 true/false 删除/未删除',
                                  `update_by` varchar(64)  COMMENT '更新者',
                                  `update_at` datetime(6) COMMENT '更新时间',
                                  `account_type` varchar(255)  COMMENT '账号类型',
                                  `actual_refund_price` decimal(10, 2) COMMENT '实际退款金额',
                                  `after_sale_image` varchar(255)  COMMENT '评价图片',
                                  `apply_refund_price` decimal(10, 2) COMMENT '申请退款金额',
                                  `audit_remark` varchar(255)  COMMENT '商家备注',
                                  `bank_account_name` varchar(255)  COMMENT '银行开户名',
                                  `bank_account_number` varchar(255)  COMMENT '银行账户',
                                  `bank_deposit_name` varchar(255)  COMMENT '银行开户行',
                                  `flow_price` decimal(10, 2) COMMENT '实际金额',
                                  `goods_id` bigint COMMENT '商品ID',
                                  `goods_image` varchar(255)  COMMENT '商品图片',
                                  `goods_name` varchar(255)  COMMENT '商品名称',
                                  `m_deliver_time` datetime(6) COMMENT '买家 发货时间',
                                  `m_logistics_code` varchar(255)  COMMENT '买家 物流公司CODE',
                                  `m_logistics_name` varchar(255)  COMMENT '买家 物流公司名称',
                                  `m_logistics_no` varchar(255)  COMMENT '买家 发货单号',
                                  `member_id` bigint COMMENT '用户ID',
                                  `member_name` varchar(255)  COMMENT '用户名称',
                                  `num` int COMMENT '申请数量',
                                  `order_sn` varchar(255)  COMMENT '订单编号',
                                  `pay_order_no` varchar(255)  COMMENT '订单支付方式返回的交易号',
                                  `problem_desc` varchar(255)  COMMENT '问题描述',
                                  `reason` varchar(255)  COMMENT '申请原因',
                                  `refund_point` int COMMENT '退还积分',
                                  `refund_time` datetime(6) COMMENT '退款时间',
                                  `refund_way` varchar(255)  COMMENT '退款方式',
                                  `store_id` bigint COMMENT '店铺ID',
                                  `store_name` varchar(255)  COMMENT '店铺名称',
                                  `service_status` varchar(255)  COMMENT '售后单状态',
                                  `service_type` varchar(255)  COMMENT '售后类型',
                                  `sku_id` bigint COMMENT '货品ID',
                                  `sn` varchar(255)  COMMENT '售后服务单号',
                                  `specs` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '规格json',
                                  `trade_sn` varchar(255)  COMMENT '交易编号',
                                  `order_item_sn` varchar(255)  COMMENT '订单货物编号',
                                  PRIMARY KEY (`id`) USING BTREE,
                                  INDEX `member_id`(`member_id` ASC) USING BTREE COMMENT '会员id索引',
                                  INDEX `store_id`(`store_id` ASC) USING BTREE COMMENT '店铺id索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of after_sale
-- ----------------------------

-- ----------------------------
-- Table structure for after_sale_log
-- ----------------------------
DROP TABLE IF EXISTS `after_sale_log`;
CREATE TABLE `after_sale_log`  (
                                      `id` bigint NOT NULL COMMENT 'ID',
                                      `create_by` varchar(64)  COMMENT '创建者',
                                      `create_at` datetime(6) COMMENT '创建时间',
                                      `message` varchar(255)  COMMENT '日志信息',
                                      `operator_id` bigint COMMENT '操作者ID(可以是店铺)',
                                      `operator_name` varchar(255)  COMMENT '操作者名称',
                                      `operator_type` varchar(255)  COMMENT '操作者类型',
                                      `sn` varchar(255)  COMMENT '售后服务单号',
                                      PRIMARY KEY (`id`) USING BTREE,
                                      INDEX `sn`(`sn` ASC) USING BTREE COMMENT '售后服务单号索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of after_sale_log
-- ----------------------------

-- ----------------------------
-- Table structure for after_sale_reason
-- ----------------------------
DROP TABLE IF EXISTS `after_sale_reason`;
CREATE TABLE `after_sale_reason`  (
                                         `id` bigint NOT NULL COMMENT 'ID',
                                         `create_by` varchar(64)  COMMENT '创建者',
                                         `create_at` datetime(6) COMMENT '创建时间',
                                         `delete_flag` bit(1) COMMENT '删除标志 true/false 删除/未删除',
                                         `update_by` varchar(64)  COMMENT '更新者',
                                         `update_at` datetime(6) COMMENT '更新时间',
                                         `reason` varchar(255)  COMMENT '售后原因',
                                         `service_type` varchar(255)  COMMENT '原因类型',
                                         PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of after_sale_reason
-- ----------------------------
INSERT INTO `after_sale_reason` VALUES (1357581861634703360, 'admin', '2021-02-05 06:48:33.151000', b'0', NULL, NULL, '未收到货', 'COMPLAIN');
INSERT INTO `after_sale_reason` VALUES (1357581919558041600, 'admin', '2021-02-05 06:48:46.933000', b'0', NULL, NULL, '订单货物数量不对', 'COMPLAIN');
INSERT INTO `after_sale_reason` VALUES (1357582072222318592, 'admin', '2021-02-05 06:49:23.331000', b'0', NULL, NULL, '不按时发货', 'RETURN_MONEY');
INSERT INTO `after_sale_reason` VALUES (1357583466371219456, 'admin', '2021-02-05 06:54:55.722000', b'0', NULL, NULL, '地址或商品选择错误', 'RETURN_MONEY');
INSERT INTO `after_sale_reason` VALUES (1357583504233201664, 'admin', '2021-02-05 06:55:04.748000', b'0', NULL, NULL, '其他', 'RETURN_MONEY');
INSERT INTO `after_sale_reason` VALUES (1357583533337477120, 'admin', '2021-02-05 06:55:11.688000', b'0', NULL, NULL, '商品选择错误', 'CANCEL');
INSERT INTO `after_sale_reason` VALUES (1357583555026223104, 'admin', '2021-02-05 06:55:16.858000', b'0', NULL, NULL, '不想要了', 'CANCEL');
INSERT INTO `after_sale_reason` VALUES (1357583611645132800, 'admin', '2021-02-05 06:55:30.357000', b'0', NULL, NULL, '不合适', 'RETURN_GOODS');
INSERT INTO `after_sale_reason` VALUES (1357583649075101696, 'admin', '2021-02-05 06:55:39.282000', b'0', NULL, NULL, '不想要了', 'RETURN_GOODS');
INSERT INTO `after_sale_reason` VALUES (1357583690120560640, 'admin', '2021-02-05 06:55:49.067000', b'0', NULL, NULL, '其他原因', 'RETURN_GOODS');
INSERT INTO `after_sale_reason` VALUES (1416907707173408770, 'admin', '2021-07-19 07:48:16.920000', b'0', NULL, NULL, '不想要了', 'RETURN_GOODS');

-- ----------------------------
-- Table structure for app_version
-- ----------------------------
DROP TABLE IF EXISTS `app_version`;
CREATE TABLE `app_version`  (
                                   `id` bigint NOT NULL COMMENT 'ID',
                                   `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '创建者',
                                   `create_at` datetime COMMENT '创建时间',
                                   `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '更新内容',
                                   `download_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '下载地址',
                                   `force_update` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '是否强制更新',
                                   `category` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '类型',
                                   `version` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '版本号',
                                   `version_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '版本名称',
                                   `version_update_date` datetime COMMENT '版本更新时间',
                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of app_version
-- ----------------------------
INSERT INTO `app_version` VALUES (1456542404861841409, 'admin', '2021-11-05 16:42:25', ' 1', '1', '1', 'ANDROID', '1', '1', '2021-11-05 16:42:07');

-- ----------------------------
-- Table structure for article
-- ----------------------------
DROP TABLE IF EXISTS `article`;
CREATE TABLE `article`  (
                               `id` bigint NOT NULL COMMENT 'ID',
                               `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '创建者',
                               `create_at` datetime COMMENT '创建时间',
                               `delete_flag` bit(1) COMMENT '删除标志 true/false 删除/未删除',
                               `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '更新者',
                               `update_at` datetime COMMENT '更新时间',
                               `category_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类ID',
                               `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '文章内容',
                               `sort` int COMMENT '文章排序',
                               `title` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '文章标题',
                               `category` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '文章类型',
                               `open_status` bit(1) NULL DEFAULT NULL,
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of article
-- ----------------------------
INSERT INTO `article` VALUES (1347428995495124992, 'admin', '2021-01-08 14:24:41', b'0', 'admin', '2021-10-28 11:44:46', '1347456734864367616', '<p>商家账号及会员账号。</p><p>用户需到前台先注册会员账号才能申请店铺入驻。</p>', 1, '商家账号注册', 'OTHER', b'0');
INSERT INTO `article` VALUES (1347429491832283136, 'admin', '2021-01-08 14:26:40', b'0', 'admin', '2021-10-28 11:44:46', '1347456734864367616', '<p>注册商家账号后。</p><p>1.同意店铺入驻协议</p><p>2.填写公司信息。</p><p>3.填写结算账户。</p><p>4.填写店铺基础信息、申请入驻经营的商品类目。</p><p>5.提交审核，平台通过后开店成功。</p>', 2, '商家申请开店', 'OTHER', b'0');
INSERT INTO `article` VALUES (1347430272568418304, 'admin', '2021-01-08 14:29:46', b'0', 'admin', '2021-01-08 17:33:01', '1347458431707799552', '<p><span style=\"font-size: 1em;\">可设置店铺的基础信息、退货收件地址、自提点</span></p>', 2, '店铺设置', 'OTHER', b'1');
INSERT INTO `article` VALUES (1347430502135259136, 'admin', '2021-01-08 14:30:40', b'0', 'admin', '2021-10-29 16:25:22', '1347458431707799552', '<p>可设置多个商家客服坐席</p>', 3, '客服设置', 'OTHER', b'1');
INSERT INTO `article` VALUES (1347430608918044672, 'admin', '2021-01-08 14:31:06', b'0', 'admin', '2021-10-29 16:25:17', '1347458431707799552', '<p>点击 设置-物流设置进行 物流设置页面。<br></p><p>可根据不同的地区设置不同的物流模板。</p><p>可选择店铺所支持的物流公司。</p>', 4, '物流设置', 'OTHER', b'1');
INSERT INTO `article` VALUES (1347430851030048768, 'admin', '2021-01-08 14:32:04', b'0', 'admin', '2021-10-14 16:56:36', '1369921726825103362', '<p>1. 设置分组名称、设置排序。</p><p>2.店铺二级商品分类最多可设置二级</p>', 11, '店铺分类维护', 'OTHER', b'0');
INSERT INTO `article` VALUES (1347431056441892864, 'admin', '2021-01-08 14:32:53', b'0', 'admin', '2021-05-08 17:28:37', '1369921726825103362', '<p>发布商品</p><p><strong>1、设置商品基础信息</strong>，根据系统提示设置商品名、商品图、价格、库存等，“&nbsp;*&nbsp;” 为必填项。商品图建议尺寸：800*800像素，最多上传5张商品图。<br/></p><p><strong>2、添加商品库存</strong>，如果您上传的商品为多规格的商品，您可以在商品内容中添加新的商品规格。点击添加规格图片，您可以在每个规格中添加图片，买家下单时，可以看到您上传的不同规格的不同图片。划线价：商品没有优惠的情况下，划线价在商品详情会以划线形式显示。<br/></p><p><strong>3、</strong><strong>选择物流信息，</strong>选择商品支持配送方式：快递发货、同城配送、到店自提。<br/></p><p><strong>4、编辑商品详情，</strong>您可以按照商品内容设置商品详情。<br/></p>', 1, '新建／发布商品教程', 'OTHER', b'1');
INSERT INTO `article` VALUES (1347431909718515712, 'admin', '2021-01-08 14:36:16', b'0', 'admin', '2021-05-08 17:32:11', '1369921726825103362', '<p>满减营销<span style=\"font-size: 1em;\">功能支持设置买家购买商品达到一定金额或件数时，可享受包邮、减金额、打折、送积分、送赠品等优惠。</span></p><p>操作路径：电脑端后台 - 营销管理 - 满额管理- 满减/满送/满包邮。</p><p>设置满减的门槛。</p><p>设置满额活动的优惠内容。</p><p>例如：</p><ul><li>支持满X元减现金/打X折 &nbsp; &nbsp;例如：满100元减10元，满100元打8折选其一。</li><li>支持满X元免邮 &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 例如：满100全场包邮。</li><li>支持满X元送积分 &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 例如：满100元送积分5个。</li><li>支持满X元送优惠 &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 例如：满100元送20元优惠券/20元优惠码选其一。</li><li>支持满X元送赠品 &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 例如：满100送手机壳一个。</li></ul><p>选择参与活动的商品。</p>', 1, '如果设置满减营销', 'OTHER', b'1');
INSERT INTO `article` VALUES (1347432371884679168, 'admin', '2021-01-08 14:38:06', b'0', 'admin', '2021-01-08 17:11:21', '1369921726825103362', '<p>电脑端后台 - 营销管理 - 拼团管理</p><p><br></p><p><strong>【基础设置】</strong></p><p>1）活动名称：保存后，支持再次修改；</p><p>2）活动时间：</p><p>a、开始时间不得早于当前时间，结束时间不得早于开始时间，不得超过店铺有效期</p><p>b、保存之后，“开始时间”不支持再次修改，“结束时间”可以进行延长，但不能提前</p><p>c、活动时间结束之后，未成团订单将自动关闭并退款，已成团订单仍需及时处理</p><p>&nbsp;</p><p>3）拼团商品：</p><p>a、可选择多个规格商品。</p><p>&nbsp;</p><p>4）拼团价设置：不同的sku可设置不同的拼团价格。</p><p>&nbsp;</p><p>5）拼团有效期：</p><p>a、开团后在有效期内邀请足够好友参团则拼团成功</p><p>b、创建后，买家可在拼团详情页看到有效期倒计时</p><p>&nbsp;</p><p>6）参团人数：开通万人团上限可至10000人，默认3人</p><p><br></p><p>虚拟成团：</p><p>a、拼团达到结束时间未达到要求，使用匿名用户拼团成功。</p>', 2, '拼团管理发布', 'OTHER', b'1');
INSERT INTO `article` VALUES (1347432438783827968, 'admin', '2021-01-08 14:38:22', b'0', 'admin', '2021-05-08 17:33:37', '1369921726825103362', '<p><strong>功能路径</strong><br/>商城电脑端后台 - 营销管理 - 秒杀。<br/></p><p>展示秒杀列表，商家可选择秒杀活动参与活动。</p><p>选择参与活动的SKU，参与时间段，设置秒杀库存。</p>', 3, '秒杀使用教程', 'OTHER', b'1');
INSERT INTO `article` VALUES (1347432646200549376, 'admin', '2021-01-08 14:39:12', b'0', 'admin', '2021-05-08 17:30:33', '1369921726825103362', '<p>您可以在财务对账模块<span style=\"font-size: 1em;\">查看一段时间内您店铺待对账的结算单.</span></p><p>点击明细可以查看这段时间内您店铺每笔订单/退单的收退款记录。<span style=\"font-size: 1em;\"><br/></span></p><p><span style=\"font-size: 1em;\"><br/></span></p>', 1, '如何财务对账', 'OTHER', b'1');
INSERT INTO `article` VALUES (1347432993837047808, 'admin', '2021-01-08 14:40:34', b'0', 'admin', '2021-05-08 17:30:28', '1369921726825103362', '<p>可按照日期筛选结算单列表。</p><p>点击查看结算单详情：展示结算金额、订单/退款/分销流水列表。</p>', 2, '如何查看店铺流水历史', 'OTHER', b'1');
INSERT INTO `article` VALUES (1347434714533486592, 'admin', '2021-01-08 14:47:25', b'0', 'admin', '2021-05-08 17:32:06', '1369921726825103362', '<h3>1. App Clips Experiences</h3><p>App Clips Experiences 是 Session 着重介绍的内容之一，不妨这么理解「体验」：用户通过 NFC 标签、二维码、Message 信息、Map 、Safari、Siri 建议等途径唤起 App Clips 应用程序，在未安装主应用的情况下，体验主应用中的某些服务和功能。<strong>App Clips 可视为为主应用程序导流的又一新途径，不仅仅是在使用过程中给出下载主应用的建议，一旦设备下载安装了主应用程序，之前 App Clips 得到的权限授权以及数据都将一并移入到新下载的主应用中。</strong></p><p>那么如何来唯一标识你开发的 App Clip 提供的不同「体验」呢？答案是 URL 链接。开发者很容易联想到 iOS 9 引入的通用链接（Universal Links）功能，简单罗列对比下异同：</p><ul><li>相同点：两者都可以通过 URL 打开应用程序，代码层面对 URL 的解析、处理逻辑和流程相似；</li><li>不同点：App Clip 体验 URL 必须在 App Store Connect 进行注册，更多详细配置点击&nbsp;<a href=\"https://xiaozhuanlan.com/topic/4063519872\" target=\"_blank\">Configure and Link Your App Clips</a>&nbsp;一文；通用链接则是需要先在开发者中心开启 Associated Domains 选项，接着在工程配置中 Associated Domains 一栏填入你想支持的域名，最后一步是将支持的 URL 以 JSON 格式写入到 apple-app-site-association 文件中，放到域名服务器下的&nbsp;<code>.well-known</code>&nbsp;子文件夹中，注意服务器必须支持 HTTPS。</li></ul><p>了解了 URL 唯一标识 App Clip 提供的体验服务，有哪些途径用于唤醒 App Clip 呢？</p><ol><li>NFC ，关于 NFC 的知识点，<a href=\"https://hacpai.com/article/1365949196429\" target=\"_blank\">引用大佬的解释</a>：「简单的说，就是一个标签，内部含电子元件，可以记录一些信息，通常1KB左右存储容量的就能满足常用需要。而当你用手机接近（开启NFC）标签时，NFC会自动扫描标签内所含有的信息，然后在手机上面显示标签信息或者执行标签所包含的命令。使用NFC可以提供一些便利，如到家时，你想连接家里的WiFI，然后登陆QQ等，你可以自己制作一个这样的标签，然后贴在家门口处，回家一打开门，用手机一扫描此标签，手机会自动执行上述命令。当然，你也可以发挥自己的想象力，如给手机上面安装远程开机功能，然后制作成这样的标签，回家一扫描，电脑自动打开，当然还有更多的创意，只要你能想得到」</li><li>QR codes，熟知的二维码；</li><li>Maps</li><li>Siri 建议</li><li>Safari，同通用链接类似，需要修改 apple-app-site-association 配置文件，加入对 App Clip 的支持，这样当你打开某个注册过的链接时，顶部就会呈现用于唤起 App Clip 的横幅 UI</li><li>Message 应用中识别 App Clip 体验链接，然后以卡片形式呈现。</li></ol><p>苹果将于年底推出 App Clip codes，结合了 NFC 的易用性，同时可视化图形展示，可以通过轻碰和扫描来快速识别。</p><p><img src=\"https://images.xiaozhuanlan.com/photo/2020/d2e564e9a2692e0ecca01b004a8bce65.png\" alt=\"\"/></p>', 1, '一笔订单可以使用多张优惠券吗？', 'OTHER', b'1');
INSERT INTO `article` VALUES (1347434895337349120, 'admin', '2021-01-08 14:48:08', b'0', 'admin', '2021-05-08 17:33:42', '1369921726825103362', '<p>一张优惠券不支持核销多次，只能核销一次。<br/></p>', 4, '一张优惠券可以核销多次吗？', 'OTHER', b'1');
INSERT INTO `article` VALUES (1347435331821789184, 'admin', '2021-01-08 14:49:52', b'0', 'admin', '2021-10-28 11:45:04', '1347450411015757824', '<p>已经发放到客户账号里面的优惠券可以作废，停止用户用券，但是不支持回收。</p><p>停止用券的操作路径：微商城电脑端后台 - 应用 - 营销玩法 - 优惠券 - 点击【已领取】的数量 - 进入已领取的客户列表 - 点击【停止用户用券】。</p>', 5, '如何停用发放给客户的优惠券？', 'OTHER', b'0');
INSERT INTO `article` VALUES (1347435630384930816, 'admin', '2021-01-08 14:51:03', b'0', 'admin', '2021-10-28 11:45:04', '1347450411015757824', '<p>优惠券不能用来抵扣订单中的运费。<br></p>', 6, '优惠券可以抵扣运费吗？', 'OTHER', b'0');
INSERT INTO `article` VALUES (1347435809779507200, 'admin', '2021-01-08 14:51:46', b'0', 'admin', '2021-05-08 17:34:24', '1369921726825103362', '<p>可以使用优惠券</p>', 7, '多人拼团订单可以使用优惠券吗？', 'OTHER', b'1');
INSERT INTO `article` VALUES (1347435999282356224, 'admin', '2021-01-08 14:52:31', b'0', 'admin', '2021-12-01 21:19:39', '1347450411015757824', '<p>优惠券只能在自己店铺使用，不能实现多店通用。</p>', 8, '优惠券可以多个店铺通用吗？', 'OTHER', b'0');
INSERT INTO `article` VALUES (1347439387113971712, 'admin', '2021-01-08 15:05:59', b'0', 'admin', '2021-05-08 17:29:02', '1369921726825103362', '<p>给商品创建分组后，店铺主页可以按照商品分组进行展示不同类目的商品。<br/></p>', 5, '添加商品分组，有什么作用？', 'OTHER', b'1');
INSERT INTO `article` VALUES (1347439575983480832, 'admin', '2021-01-08 15:06:44', b'0', 'admin', '2021-05-08 17:29:21', '1369921726825103362', '<p>可以上传5张，超过就无法上传了<br/></p>', 6, '商品主图可以上传多少张？', 'OTHER', b'1');
INSERT INTO `article` VALUES (1347439915625635840, 'admin', '2021-01-08 15:08:05', b'0', 'admin', '2021-05-08 17:28:30', '1369921726825103362', '<p>可上架后最晚十分钟可以展示。<br/></p>', 7, '商品上架后，多久会显示？', 'OTHER', b'1');
INSERT INTO `article` VALUES (1347440117820448768, 'admin', '2021-01-08 15:08:53', b'0', 'admin', '2021-11-09 10:41:21', '1369921726825103362', '<p>库存最大值不能超过10000000件，建议您根据实际库存进行设置。<br/></p>', 8, '商品库存有数量限制吗？', 'OTHER', b'1');
INSERT INTO `article` VALUES (1347440480636133376, 'admin', '2021-01-08 15:10:19', b'0', 'admin', '2021-12-01 21:19:38', '1369921726825103362', '<p>目前商品类型分为实物商品、虚拟商品。</p><p>实物商品与虚拟商品的区别：</p><p>实物商品：需实物进行发货。</p><p>虚拟商品：不需要快递，购买后获取验证码。</p>', 9, '商品类型有哪些？', 'OTHER', b'0');
INSERT INTO `article` VALUES (1347441567598403584, 'admin', '2021-01-08 15:14:39', b'0', 'admin', '2021-12-01 21:19:37', '1369921726825103362', '<p>商品编辑里面的规格名、规格值历史记录目前不支持删除。<br/></p>', 10, '商品编辑里面的规格名、规格值历史记录可以删除吗？', 'OTHER', b'0');
INSERT INTO `article` VALUES (1347441948684476416, 'admin', '2021-01-08 15:16:09', b'0', 'admin', '2021-05-08 17:28:55', '1369921726825103362', '<p>一个商品可以添加3个主规格，每个主规格可以添加200个子规格（注：子规格总字数需在5000字以下）。</p><p>超过600个的情况下建议发布成多个商品。</p>', 3, '一个商品可以添加多少规格？', 'OTHER', b'1');
INSERT INTO `article` VALUES (1347444467808624640, 'admin', '2021-01-08 15:26:10', b'0', 'admin', '2021-05-08 17:28:43', '1369921726825103362', '<p>商品销量不会减少哦。商品销量是历史已发生的，不会因为删除规格而改变。<br/></p>', 4, '商品如果删除其中一个规格，商品详情中展示的总销量会减少吗？', 'OTHER', b'1');
INSERT INTO `article` VALUES (1347445328169426944, 'admin', '2021-01-08 15:29:35', b'0', 'admin', '2021-08-17 11:30:51', '1369921726825103362', '<p>多人拼团有虚拟成团的功能。</p><p>在新建多人拼团活动的时候，如果想要提高成团率，可以展开高级设置，勾选模拟成团。如果不希望买家虚拟成团，则可以不勾选虚拟成团。<br/>开启模拟成团后，满足条件的团，系统将会模拟“匿名买家”凑满该团，仅需对真实拼团买家发货。建议合理开启，以提高成团率。</p>', 18, '多人拼团有虚拟成团的功能吗？', 'OTHER', b'1');
INSERT INTO `article` VALUES (1347445482180075520, 'admin', '2021-01-08 15:30:12', b'0', 'admin', '2021-10-02 12:23:56', '1369921726825103362', '<p>已成团订单其中1笔申请退款，按正常订单处理，这个团的其他订单不受影响，还是可以正常发货的。<br/></p>', 19, '已成团订单，其中有一人申请退款，对同团其他订单有影响吗？', 'OTHER', b'1');
INSERT INTO `article` VALUES (1347445690712481792, 'admin', '2021-01-08 15:31:02', b'0', 'admin', '2021-05-08 17:32:29', '1369921726825103362', '<p>123</p>', 1, '多人拼团活动结束后，还未成团的订单会怎么样？', 'OTHER', b'1');
INSERT INTO `article` VALUES (1347445984301178880, 'admin', '2021-01-08 15:32:12', b'0', 'admin', '2021-10-28 11:45:05', '1347450411015757824', '<h3>1. App Clips Experiences</h3><p>App Clips Experiences 是 Session 着重介绍的内容之一，不妨这么理解「体验」：用户通过 NFC 标签、二维码、Message 信息、Map 、Safari、Siri 建议等途径唤起 App Clips 应用程序，在未安装主应用的情况下，体验主应用中的某些服务和功能。<strong>App Clips 可视为为主应用程序导流的又一新途径，不仅仅是在使用过程中给出下载主应用的建议，一旦设备下载安装了主应用程序，之前 App Clips 得到的权限授权以及数据都将一并移入到新下载的主应用中。</strong></p><p>那么如何来唯一标识你开发的 App Clip 提供的不同「体验」呢？答案是 URL 链接。开发者很容易联想到 iOS 9 引入的通用链接（Universal Links）功能，简单罗列对比下异同：</p><ul><li>相同点：两者都可以通过 URL 打开应用程序，代码层面对 URL 的解析、处理逻辑和流程相似；</li><li>不同点：App Clip 体验 URL 必须在 App Store Connect 进行注册，更多详细配置点击 <a href=\"https://xiaozhuanlan.com/topic/4063519872\" target=\"_blank\">Configure and Link Your App Clips</a> 一文；通用链接则是需要先在开发者中心开启 Associated Domains 选项，接着在工程配置中 Associated Domains 一栏填入你想支持的域名，最后一步是将支持的 URL 以 JSON 格式写入到 apple-app-site-association 文件中，放到域名服务器下的 <code>.well-known</code> 子文件夹中，注意服务器必须支持 HTTPS。</li></ul><p>了解了 URL 唯一标识 App Clip 提供的体验服务，有哪些途径用于唤醒 App Clip 呢？</p><ol><li>NFC ，关于 NFC 的知识点，<a href=\"https://hacpai.com/article/1365949196429\" target=\"_blank\">引用大佬的解释</a>：「简单的说，就是一个标签，内部含电子元件，可以记录一些信息，通常1KB左右存储容量的就能满足常用需要。而当你用手机接近（开启NFC）标签时，NFC会自动扫描标签内所含有的信息，然后在手机上面显示标签信息或者执行标签所包含的命令。使用NFC可以提供一些便利，如到家时，你想连接家里的WiFI，然后登陆QQ等，你可以自己制作一个这样的标签，然后贴在家门口处，回家一打开门，用手机一扫描此标签，手机会自动执行上述命令。当然，你也可以发挥自己的想象力，如给手机上面安装远程开机功能，然后制作成这样的标签，回家一扫描，电脑自动打开，当然还有更多的创意，只要你能想得到」</li><li>QR codes，熟知的二维码；</li><li>Maps</li><li>Siri 建议</li><li>Safari，同通用链接类似，需要修改 apple-app-site-association 配置文件，加入对 App Clip 的支持，这样当你打开某个注册过的链接时，顶部就会呈现用于唤起 App Clip 的横幅 UI</li><li>Message 应用中识别 App Clip 体验链接，然后以卡片形式呈现。</li></ol><p>苹果将于年底推出 App Clip codes，结合了 NFC 的易用性，同时可视化图形展示，可以通过轻碰和扫描来快速识别。</p><p><img src=\"https://images.xiaozhuanlan.com/photo/2020/d2e564e9a2692e0ecca01b004a8bce65.png\" alt=\"\"></p>', 1, '如何取消多人拼团订单？', 'OTHER', b'0');
INSERT INTO `article` VALUES (1347449374255112192, 'admin', '2021-01-08 15:45:40', b'0', 'admin', '2021-10-28 11:45:05', '1347450411015757824', '<p>App Clips 是今年 WWDC20 大会的亮点之一，罗列几个开发者感兴趣的问题：什么是 App Clips；实际应用场景中是如何交互的；构建 App Clips 的有哪些前置条件；如何在已有项目中添加对 App Clips 的支持等一系列问题。</p><p>在回答上述几个问题之前，我们先来明确三个概念：1. App，即我们开发的应用程序，提供完整服务功能，亦称之为主应用；2. App Clip Experience，本质是一个 URL 链接，唯一标识某项体验服务，e.g. 餐饮类应用程序中的点餐服务、预定餐桌服务、付款操作等；3. App Clip，能够让用户在未安装你发布的完整应用程序情况下，就能体验其中的部分功能，session 中开篇就提到 “App Clips are an additive feature.” ，即附加功能，这意味着你必须先拥有一个 App 主应用程序，才能继续开发该应用的 App Clip，更多关于 App 主应用程序和 App Clip 的关联关系此处先按下不表，下文展开，此外 App Clips 不难让人联想到 Android Instant apps 、微信小程序以及 PWA，三者的横向比较推荐一波卓同学的<a href=\"https://juejin.im/post/5ef41c9b6fb9a07e6143b0d0\" target=\"_blank\">苹果 App Clip 技术详解一文</a>。</p><p><img src=\"https://images.xiaozhuanlan.com/photo/2020/424e3a484ff1d6ea9bc30f71d673fa6c.png\" alt=\"\"></p><h3>1. App Clips Experiences</h3><p>App Clips Experiences 是 Session 着重介绍的内容之一，不妨这么理解「体验」：用户通过 NFC 标签、二维码、Message 信息、Map 、Safari、Siri 建议等途径唤起 App Clips 应用程序，在未安装主应用的情况下，体验主应用中的某些服务和功能。<strong>App Clips 可视为为主应用程序导流的又一新途径，不仅仅是在使用过程中给出下载主应用的建议，一旦设备下载安装了主应用程序，之前 App Clips 得到的权限授权以及数据都将一并移入到新下载的主应用中。</strong></p><p>那么如何来唯一标识你开发的 App Clip 提供的不同「体验」呢？答案是 URL 链接。开发者很容易联想到 iOS 9 引入的通用链接（Universal Links）功能，简单罗列对比下异同：</p><ul><li>相同点：两者都可以通过 URL 打开应用程序，代码层面对 URL 的解析、处理逻辑和流程相似；</li><li>不同点：App Clip 体验 URL 必须在 App Store Connect 进行注册，更多详细配置点击 <a href=\"https://xiaozhuanlan.com/topic/4063519872\" target=\"_blank\">Configure and Link Your App Clips</a> 一文；通用链接则是需要先在开发者中心开启 Associated Domains 选项，接着在工程配置中 Associated Domains 一栏填入你想支持的域名，最后一步是将支持的 URL 以 JSON 格式写入到 apple-app-site-association 文件中，放到域名服务器下的 <code>.well-known</code> 子文件夹中，注意服务器必须支持 HTTPS。</li></ul><p>了解了 URL 唯一标识 App Clip 提供的体验服务，有哪些途径用于唤醒 App Clip 呢？</p><ol><li>NFC ，关于 NFC 的知识点，<a href=\"https://hacpai.com/article/1365949196429\" target=\"_blank\">引用大佬的解释</a>：「简单的说，就是一个标签，内部含电子元件，可以记录一些信息，通常1KB左右存储容量的就能满足常用需要。而当你用手机接近（开启NFC）标签时，NFC会自动扫描标签内所含有的信息，然后在手机上面显示标签信息或者执行标签所包含的命令。使用NFC可以提供一些便利，如到家时，你想连接家里的WiFI，然后登陆QQ等，你可以自己制作一个这样的标签，然后贴在家门口处，回家一打开门，用手机一扫描此标签，手机会自动执行上述命令。当然，你也可以发挥自己的想象力，如给手机上面安装远程开机功能，然后制作成这样的标签，回家一扫描，电脑自动打开，当然还有更多的创意，只要你能想得到」</li><li>QR codes，熟知的二维码；</li><li>Maps</li><li>Siri 建议</li><li>Safari，同通用链接类似，需要修改 apple-app-site-association 配置文件，加入对 App Clip 的支持，这样当你打开某个注册过的链接时，顶部就会呈现用于唤起 App Clip 的横幅 UI</li><li>Message 应用中识别 App Clip 体验链接，然后以卡片形式呈现。</li></ol><p>苹果将于年底推出 App Clip codes，结合了 NFC 的易用性，同时可视化图形展示，可以通过轻碰和扫描来快速识别。</p><p><img src=\"https://images.xiaozhuanlan.com/photo/2020/d2e564e9a2692e0ecca01b004a8bce65.png\" alt=\"\"></p><h3>2. App Clips</h3><p>App Clips 发布之时，想必部分 iOSer 当时第一反应就是<strong>“完了，又要适配新功能拉“</strong>。实际上为已有项目添加对 App Clip 的支持非常简单，就是新增一个 App Clip 应用程序的 target，请确保你已经安装了 Xcode12 beta 版本，那么在 <code>New => Target</code> 弹出的面板中会发现多了 App Clip 选项：</p><p><img src=\"https://images.xiaozhuanlan.com/photo/2020/72d5e03f4575f8bee94a5a4dbfebeef3.png\" alt=\"\"></p><p>关于 App Clip，我们需要明确如下四点：</p><ol><li>App Clip 是第一个独立的应用程序，包含了必要的功能代码和资源文件，专门负责处理「体验」服务请求；</li><li>App Clip 必须关联一个主应用程序，需要在 App Store Connect 中创建一个版本，和主应用一起提交审核，session 中提及 <em>”You cannot upload an App Clip or its app independently of one another“</em>，这么看来两者都不能单独提审；</li><li>主应用程序和其关联的 App Clip 提交到 App Store 之后，两者就被分开存储，独立存在。假设用户通过上面某种途径打开「体验」链接，设备上没有安装主应用程序，那么系统就会下载 App Clip 到设备上，接着会被唤起调用；否则主应用程序总是第一选择，实际上用户一旦安装了主应用程序，系统就会移除掉 App Clip，因此请牢记一点：<strong>主应用程序也必须有处理 App Clip 体验链接的代码，否则一旦用户安装了主应用程序（此时 App Clip 会被系统移除），再识别体验链接时就会跳转至主应用程序进行处理，此时若未相关处理，就只是打开应用程序而已了；</strong></li><li>App Clip 包大小限制在 10 MB，满足基本功能前提下包体积越小越好，某些资源文件的下载可适当延后，另外技术群里也提到 App Clip 是否可以作为一个壳，内容呈现是基于 WKWebview 展示，目前存疑；</li></ol><p>关于 App Clip 的注意事项就是上述几点，那么我们如何基于主应用程序来开发和构建 App Clip 呢？通常市面上大部分应用程序都是基于 Tab Bar 控制器来划分功能模块，如下提供了四个功能模块：</p><p><img src=\"https://images.xiaozhuanlan.com/photo/2020/13d541fc5a77ab0e8f9470f1670b8fea.png\" alt=\"\"></p><p>现在开始设计 App Clip 所需的功能，我们仅保留对外提供服务的代码和资源，去除类似用户信息面板等不必要的功能模块。</p><p><img src=\"https://images.xiaozhuanlan.com/photo/2020/50682b3df946de0d45c70785c8a6ab90.png\" alt=\"\"></p><p>显然 App Clip 旨在提供「体验」服务，我们希望唤起 App Clip 后，让用户快速到达目标功能，因此 Tab Bar 结构并不适用，调整如下：</p><p><img src=\"https://images.xiaozhuanlan.com/photo/2020/5d265abc0367878f750545bce3b16db9.png\" alt=\"\"></p><p>App Clips 负责处理体验链接，建议单次处理一个体验链接请求。</p><h3>3. Xcode 上手</h3><p>苹果官方提供了一个名为 Fruta 的 Demo ，用于演示如何在已有项目中新增对 App Clip 的支持，Demo 下载地址<a href=\"https://docs-assets.developer.apple.com/published/b7ada4cd51/FrutaBuildingAFeatureRichAppWithSwiftUI.zip\" target=\"_blank\">传送门</a>。</p><p>上文说到已有项目添加对 App Clip 的支持非常简单，就是新增一个 App Clip 应用程序的 target。</p><p><img src=\"https://images.xiaozhuanlan.com/photo/2020/20529ac1d6f74ee63ede45405f8fd14b.png\" alt=\"\"></p><p>选中 App Clip 新建一个 target，填写必要信息即可。</p><p><img src=\"https://images.xiaozhuanlan.com/photo/2020/2d6e07e301de628ac8bc762b2426bc2d.png\" alt=\"\"></p><p>由于主应用程序(iOS or macOS)和 App Clip 某些资源文件是共用的，因此我们可以新建一个共享的 Asset Catalog ，<strong>此处注意勾选需要共享的几个 target</strong>。</p><p><img src=\"https://images.xiaozhuanlan.com/photo/2020/40aa8627ed61dc595d552bfb2c7a37a0.png\" alt=\"\"></p><p>接着将共用的资源文件都放入到新建的 Asset 文件夹下：</p><p><img src=\"https://images.xiaozhuanlan.com/photo/2020/0f2b161973a64341cb1547abd6c008a3.png\" alt=\"\"></p><p><img src=\"https://images.xiaozhuanlan.com/photo/2020/c33b8d6455b2ff50d7f9a3c91ac116e7.png\" alt=\"\"></p><p>接着我们要为 App Clip Target 按需「添加」主应用中已经实现的类、文件或是整个模块，对于文件来说，只需要在 Target Membership 中勾选上 Clip target 即可。</p><p><img src=\"https://images.xiaozhuanlan.com/photo/2020/17ae9337f84fac2a46ba7c5df7a6bc68.png\" alt=\"\"></p><p>这里涉及到共用代码不同平台的区分处理问题，因此我们需要为 Clip target 的 Debug 和 Release 配置项中添加一个 APPCLIP 宏，如下所示：</p><p><img src=\"https://images.xiaozhuanlan.com/photo/2020/9dc3cb402d4ad499f835f33bf38c4e41.png\" alt=\"\"></p><p>相应地，我们需要在代码中使用宏条件进行分支处理：</p><p><img src=\"https://images.xiaozhuanlan.com/photo/2020/e56d3e9c38627f29bb23a9ba26ab99db.png\" alt=\"\"></p><p>此处可能有读者要问了，上文说到通用链接的处理和 App Clip 体验链接的处理两者类似，是如何解析链接处理请求呢？官方提供的 Demo 中 <code>FrutaApp.swift</code> 整个文件被 iOS 主应用程序和 App Clip 共享，免不了内部用 <code>APPCLIP</code> 宏来做分支处理，注意下面的 <code>handleUserActivity</code> 方法就是用来处理体验链接。</p><p>```swift<br>import SwiftUI</p><p><br></p>', 1, '一个商品只能参加一个拼团活动吗？', 'OTHER', b'0');
INSERT INTO `article` VALUES (1347449767181705216, 'admin', '2021-01-08 15:47:14', b'0', 'admin', '2021-10-28 11:45:07', '1347450411015757824', '<p>App Clips 是今年 WWDC20 大会的亮点之一，罗列几个开发者感兴趣的问题：什么是 App Clips；实际应用场景中是如何交互的；构建 App Clips 的有哪些前置条件；如何在已有项目中添加对 App Clips 的支持等一系列问题。</p><p>在回答上述几个问题之前，我们先来明确三个概念：1. App，即我们开发的应用程序，提供完整服务功能，亦称之为主应用；2. App Clip Experience，本质是一个 URL 链接，唯一标识某项体验服务，e.g. 餐饮类应用程序中的点餐服务、预定餐桌服务、付款操作等；3. App Clip，能够让用户在未安装你发布的完整应用程序情况下，就能体验其中的部分功能，session 中开篇就提到 “App Clips are an additive feature.” ，即附加功能，这意味着你必须先拥有一个 App 主应用程序，才能继续开发该应用的 App Clip，更多关于 App 主应用程序和 App Clip 的关联关系此处先按下不表，下文展开，此外 App Clips 不难让人联想到 Android Instant apps 、微信小程序以及 PWA，三者的横向比较推荐一波卓同学的<a href=\"https://juejin.im/post/5ef41c9b6fb9a07e6143b0d0\" target=\"_blank\">苹果 App Clip 技术详解一文</a>。</p><p><img src=\"https://images.xiaozhuanlan.com/photo/2020/424e3a484ff1d6ea9bc30f71d673fa6c.png\" alt=\"\"></p><h3>1. App Clips Experiences</h3><p>App Clips Experiences 是 Session 着重介绍的内容之一，不妨这么理解「体验」：用户通过 NFC 标签、二维码、Message 信息、Map 、Safari、Siri 建议等途径唤起 App Clips 应用程序，在未安装主应用的情况下，体验主应用中的某些服务和功能。<strong>App Clips 可视为为主应用程序导流的又一新途径，不仅仅是在使用过程中给出下载主应用的建议，一旦设备下载安装了主应用程序，之前 App Clips 得到的权限授权以及数据都将一并移入到新下载的主应用中。</strong></p><p>那么如何来唯一标识你开发的 App Clip 提供的不同「体验」呢？答案是 URL 链接。开发者很容易联想到 iOS 9 引入的通用链接（Universal Links）功能，简单罗列对比下异同：</p><ul><li>相同点：两者都可以通过 URL 打开应用程序，代码层面对 URL 的解析、处理逻辑和流程相似；</li><li>不同点：App Clip 体验 URL 必须在 App Store Connect 进行注册，更多详细配置点击 <a href=\"https://xiaozhuanlan.com/topic/4063519872\" target=\"_blank\">Configure and Link Your App Clips</a> 一文；通用链接则是需要先在开发者中心开启 Associated Domains 选项，接着在工程配置中 Associated Domains 一栏填入你想支持的域名，最后一步是将支持的 URL 以 JSON 格式写入到 apple-app-site-association 文件中，放到域名服务器下的 <code>.well-known</code> 子文件夹中，注意服务器必须支持 HTTPS。</li></ul><p>了解了 URL 唯一标识 App Clip 提供的体验服务，有哪些途径用于唤醒 App Clip 呢？</p><ol><li>NFC ，关于 NFC 的知识点，<a href=\"https://hacpai.com/article/1365949196429\" target=\"_blank\">引用大佬的解释</a>：「简单的说，就是一个标签，内部含电子元件，可以记录一些信息，通常1KB左右存储容量的就能满足常用需要。而当你用手机接近（开启NFC）标签时，NFC会自动扫描标签内所含有的信息，然后在手机上面显示标签信息或者执行标签所包含的命令。使用NFC可以提供一些便利，如到家时，你想连接家里的WiFI，然后登陆QQ等，你可以自己制作一个这样的标签，然后贴在家门口处，回家一打开门，用手机一扫描此标签，手机会自动执行上述命令。当然，你也可以发挥自己的想象力，如给手机上面安装远程开机功能，然后制作成这样的标签，回家一扫描，电脑自动打开，当然还有更多的创意，只要你能想得到」</li><li>QR codes，熟知的二维码；</li><li>Maps</li><li>Siri 建议</li><li>Safari，同通用链接类似，需要修改 apple-app-site-association 配置文件，加入对 App Clip 的支持，这样当你打开某个注册过的链接时，顶部就会呈现用于唤起 App Clip 的横幅 UI</li><li>Message 应用中识别 App Clip 体验链接，然后以卡片形式呈现。</li></ol><p>苹果将于年底推出 App Clip codes，结合了 NFC 的易用性，同时可视化图形展示，可以通过轻碰和扫描来快速识别。</p><p><img src=\"https://images.xiaozhuanlan.com/photo/2020/d2e564e9a2692e0ecca01b004a8bce65.png\" alt=\"\"></p><h3>2. App Clips</h3><p>App Clips 发布之时，想必部分 iOSer 当时第一反应就是<strong>“完了，又要适配新功能拉“</strong>。实际上为已有项目添加对 App Clip 的支持非常简单，就是新增一个 App Clip 应用程序的 target，请确保你已经安装了 Xcode12 beta 版本，那么在 <code>New => Target</code> 弹出的面板中会发现多了 App Clip 选项：</p><p><img src=\"https://images.xiaozhuanlan.com/photo/2020/72d5e03f4575f8bee94a5a4dbfebeef3.png\" alt=\"\"></p><p>关于 App Clip，我们需要明确如下四点：</p><ol><li>App Clip 是第一个独立的应用程序，包含了必要的功能代码和资源文件，专门负责处理「体验」服务请求；</li><li>App Clip 必须关联一个主应用程序，需要在 App Store Connect 中创建一个版本，和主应用一起提交审核，session 中提及 <em>”You cannot upload an App Clip or its app independently of one another“</em>，这么看来两者都不能单独提审；</li><li>主应用程序和其关联的 App Clip 提交到 App Store 之后，两者就被分开存储，独立存在。假设用户通过上面某种途径打开「体验」链接，设备上没有安装主应用程序，那么系统就会下载 App Clip 到设备上，接着会被唤起调用；否则主应用程序总是第一选择，实际上用户一旦安装了主应用程序，系统就会移除掉 App Clip，因此请牢记一点：<strong>主应用程序也必须有处理 App Clip 体验链接的代码，否则一旦用户安装了主应用程序（此时 App Clip 会被系统移除），再识别体验链接时就会跳转至主应用程序进行处理，此时若未相关处理，就只是打开应用程序而已了；</strong></li><li>App Clip 包大小限制在 10 MB，满足基本功能前提下包体积越小越好，某些资源文件的下载可适当延后，另外技术群里也提到 App Clip 是否可以作为一个壳，内容呈现是基于 WKWebview 展示，目前存疑；</li></ol><p>关于 App Clip 的注意事项就是上述几点，那么我们如何基于主应用程序来开发和构建 App Clip 呢？通常市面上大部分应用程序都是基于 Tab Bar 控制器来划分功能模块，如下提供了四个功能模块：</p><p><img src=\"https://images.xiaozhuanlan.com/photo/2020/13d541fc5a77ab0e8f9470f1670b8fea.png\" alt=\"\"></p><p>现在开始设计 App Clip 所需的功能，我们仅保留对外提供服务的代码和资源，去除类似用户信息面板等不必要的功能模块。</p><p><img src=\"https://images.xiaozhuanlan.com/photo/2020/50682b3df946de0d45c70785c8a6ab90.png\" alt=\"\"></p><p>显然 App Clip 旨在提供「体验」服务，我们希望唤起 App Clip 后，让用户快速到达目标功能，因此 Tab Bar 结构并不适用，调整如下：</p><p><img src=\"https://images.xiaozhuanlan.com/photo/2020/5d265abc0367878f750545bce3b16db9.png\" alt=\"\"></p><p>App Clips 负责处理体验链接，建议单次处理一个体验链接请求。</p><h3>3. Xcode 上手</h3><p>苹果官方提供了一个名为 Fruta 的 Demo ，用于演示如何在已有项目中新增对 App Clip 的支持，Demo 下载地址<a href=\"https://docs-assets.developer.apple.com/published/b7ada4cd51/FrutaBuildingAFeatureRichAppWithSwiftUI.zip\" target=\"_blank\">传送门</a>。</p><p>上文说到已有项目添加对 App Clip 的支持非常简单，就是新增一个 App Clip 应用程序的 target。</p><p><img src=\"https://images.xiaozhuanlan.com/photo/2020/20529ac1d6f74ee63ede45405f8fd14b.png\" alt=\"\"></p><p>选中 App Clip 新建一个 target，填写必要信息即可。</p><p><img src=\"https://images.xiaozhuanlan.com/photo/2020/2d6e07e301de628ac8bc762b2426bc2d.png\" alt=\"\"></p><p>由于主应用程序(iOS or macOS)和 App Clip 某些资源文件是共用的，因此我们可以新建一个共享的 Asset Catalog ，<strong>此处注意勾选需要共享的几个 target</strong>。</p>', 1, '拼团商品可以选择自提吗？', 'OTHER', b'0');
INSERT INTO `article` VALUES (1347450051882672128, 'admin', '2021-01-08 15:48:21', b'0', 'admin', '2021-06-07 11:46:16', '1369921726825103362', '<p>阿迪散发的撒发</p><p>sad发生地方</p>', 1, '虚拟商品可以参加多人拼团吗？', 'OTHER', b'1');
INSERT INTO `article` VALUES (1347455276571975680, 'admin', '2021-01-08 16:09:07', b'0', 'admin', '2021-05-08 17:28:49', '1369921726825103362', '<p>设置商品规格后，要在[商品库存]区域分别去单独设置不同规格的价格、库存等信息。</p><p>商品可以添加3个主规格，每个主规格可以添加200个子规格（在该子规格总字数不超过5000个字的情况下）。</p><p>商品名称您可以手动输入，例如颜色、款式。注意：输入具体规格参数时，点击【回车键】即可键入。<br/></p><p>在规格名中您可以填写规格值，例如衣服、鞋子尺码。在一个商品中，支持设置多个规格名和规格值。<br/></p><p>您可以设置多个商品规格，但是商品规格为包含关系，即买家需要全选您设置的规格，例如您设置了3个规格名，买家需要选择全部三个规格才可以下单，不然无法下单。因此建议您仅设置一个规格名，多个规格值哦。<br/></p><p>勾选添加规格图片，您可以在您的每一个分规格中添加图片。仅支持为第一组规格设置规格图片（最多40张图），买家选择不同规格会看到对应规格图片，建议尺寸：800 x 800像素</p><p>如果您需要删除商品规格请您编辑商品选择商品规格，点击右侧×图标即可。</p>', 2, '商品规格设置教程', 'OTHER', b'1');
INSERT INTO `article` VALUES (1347460085660278784, 'admin', '2021-01-08 16:28:14', b'0', 'admin', '2021-12-01 09:50:10', '1369921726825103362', '<p><b>直接在商品管理页面，鼠标悬放至对应商品，修改商品库存。<br/></b></p><p><b>不同的规格可设置不同的库存数量</b></p>', 12, '怎么修改商品库存？', 'OTHER', b'1');
INSERT INTO `article` VALUES (1347466553893609472, 'admin', '2021-01-08 16:53:56', b'0', 'admin', '2021-05-08 17:29:57', '1369921726825103362', '<p>操作路径：商城电脑端后台 - 订单 - 交易列表 - 查看详情 - 收货人信息 - 修改。<br/></p><p>1.只有待付款或待发货的订单支持修改</p><p>2.只有配送方式是快递发货的订单支持修改<br/></p>', 1, '如何修改订单收货地址？', 'OTHER', b'1');
INSERT INTO `article` VALUES (1347467444453400576, 'admin', '2021-01-08 16:57:28', b'0', 'admin', '2021-05-08 17:30:02', '1369921726825103362', '<p>订单发货操作路径：电脑端后台 - 订单 - 订单查询，找到待发货的订单并点击发货。<br/></p><p>选择快递公司并填写运单号。</p>', 1, '如何订单发货', 'OTHER', b'1');
INSERT INTO `article` VALUES (1347471480271429632, 'admin', '2021-01-08 17:13:30', b'0', 'admin', '2021-12-01 21:19:36', '1347450411015757824', '<p>一个秒杀活动只能参与一次，不能重复提交参加。<br></p>', 10, '一个秒杀活动能参与多次吗？', 'OTHER', b'0');
INSERT INTO `article` VALUES (1347476270544084992, 'admin', '2021-01-08 17:32:32', b'0', NULL, NULL, '1347458431707799552', '<p>店铺首页分为：移动端、PC端。</p><p>店铺可以有多个首页，可选择其中一个进行发布。</p><p>页面设置中，商家可拖拽各种模块进行装修。</p>', 1, '如果发布店铺首页', 'OTHER', b'1');
INSERT INTO `article` VALUES (1347488132622999552, 'admin', '2021-01-08 18:19:41', b'0', 'admin', '2021-05-08 17:30:19', '1369921726825103362', '<p>订单金额为：周期内的付款订单。</p><p>退款金额为：周期内的退款金额。</p><p>佣金金额为：周期内订单平台收取的佣金。</p><p>商品分销金额：周期内订单产生的分销佣金。</p><p>平台优惠券金额：周期内订单使用的平台优惠券平台承担部分的金额。</p><p>退还佣金：退款订单平台收取的佣金。</p><p>退还平台优惠券：退款订单商家收取的平台优惠券金额。</p><p><br/></p><p>结算金额=订单金额-平台分佣-退单金额-商品分销费用-退还平台优惠券+退还佣金+平台优惠券+积分抵现金额。</p><p><br/></p><p>商品分销费用不进行退还。</p>', 4, '结算规则', 'OTHER', b'1');
INSERT INTO `article` VALUES (1347488451167805440, 'admin', '2021-01-08 18:20:57', b'0', 'admin', '2021-11-25 23:09:07', '1369921726825103362', '<p>目前平台支持开具增值税普通发票。</p><p>发票管理只展示用户提交的发票信息，不进行实际发票开具操作。</p>', 3, '商家开具发票', 'OTHER', b'0');
INSERT INTO `article` VALUES (1347731598330322944, 'admin', '2021-01-09 10:27:07', b'0', 'admin', '2021-08-03 01:51:15', '1347730780659146752', '<p>用户前台创建订单。订单状态为：待付款。</p><p>点击 订单-订单列表-详情-确定收款，进行收款。</p><p>后台确定收款的订单，退款时需填写收款银行信息。hhhh</p>', 1, '平台如何在后台收款？', 'OTHER', b'1');
INSERT INTO `article` VALUES (1347732213374672896, 'admin', '2021-01-09 10:29:34', b'0', NULL, NULL, '1347732008847826944', '<p>同一时间只能发布一个秒杀活动。</p>', 1, '同一时间可以发布多个秒杀活动吗？', 'OTHER', b'1');
INSERT INTO `article` VALUES (1347733073714503680, 'admin', '2021-01-09 10:32:59', b'0', NULL, NULL, '1347732307880730624', '<p>每个商家有不同的结算周期。</p><p>商家按照不同的结算周期生成结算单。</p><p>商家对账完结算单后此结算单可进行结算。</p><p>平台按照商家入驻时填写的银行进行进行线下结算。</p>', 1, '如何与商家结算？', 'OTHER', b'1');
INSERT INTO `article` VALUES (1364720226167422976, 'admin', '2021-02-24 23:33:52', b'0', 'admin', '2021-11-25 23:08:38', '1369921726825103362', '一个商品同一时间段只能设置 1 个满额活动，不同时段可以设置不同的满额活动。', 20, '一个商品可以设置几个满额活动&#xff1f;', 'OTHER', b'1');
INSERT INTO `article` VALUES (1364722349428965376, 'admin', '2021-02-24 23:42:18', b'0', 'admin', '2021-12-02 11:49:47', '1347450302295203840', '促销活动：分为单品促销、店铺促销、优惠券。<img /><img src=\"https://lilishop-oss.oss-cn-beijing.aliyuncs.com/9da42062f4414443947f4d40c70bb7ed.jpg\" />', 22, '促销计算规则', 'OTHER', b'1');
INSERT INTO `article` VALUES (1369921919213633536, 'admin', '2021-03-11 02:03:32', b'0', 'admin', '2021-09-09 16:11:49', '1369921726825103360', '<p><span style=\"font-size: 1em;\">一个商品同一时间段只能设置 1 个满额活动，不同时段可以设置不同的满额活动。</span><br></p>', 1, '一个商品可以设置几个满额活动？', 'OTHER', b'0');
INSERT INTO `article` VALUES (1369922025446965248, 'admin', '2021-03-11 02:03:57', b'0', 'admin', '2021-09-09 16:11:47', '1369921726825103360', '<p>公告公告公告，嘿嘿嘿~~~~</p><p>一个秒杀活动只能参与一次，不能重复提交参加。</p>', 2, '一个秒杀活动能参与多次吗？', 'OTHER', b'0');
INSERT INTO `article` VALUES (1369922111602163712, 'admin', '2021-03-11 02:04:18', b'0', 'admin', '2021-10-22 19:33:23', '1369921726825103360', '<p><span style=\"font-size: 1em;\">订单金额为：周期内的付款订单。</span><br></p><p><br>退款金额为：周期内的退款金额。<br><br>佣金金额为：周期内订单平台收取的佣金。<br><br>商品分销金额：周期内订单产生的分销佣金。<br><br>平台优惠券金额：周期内订单使用的平台优惠券平台承担部分的金额。<br><br>退还佣金：退款订单平台收取的佣金。<br><br>退还平台优惠券：退款订单商家收取的平台优惠券金额。<br><br><br><br>结算金额=订单金额-平台分佣-退单金额-商品分销费用-退还平台优惠券+退还佣金+平台优惠券+积分抵现金额。</p>', 3, '店铺与平台的结算规则', 'OTHER', b'0');
INSERT INTO `article` VALUES (1370177884043345920, 'admin', '2021-03-11 19:00:39', b'0', 'admin', '2021-09-09 16:11:48', '1369921726825103360', '<p><span style=\"font-size: 1em;\">多人拼团有虚拟成团的功能。</span><br></p><p>在新建多人拼团活动的时候，如果想要提高成团率，可以展开高级设置，勾选模拟成团。如果不希望买家虚拟成团，则可以不勾选虚拟成团。<br>开启模拟成团后，满足条件的团，系统将会模拟“匿名买家”凑满该团，仅需对真实拼团买家发货。建议合理开启，以提高成团率。</p>', 1, '多人拼团有虚拟成团的功能吗？', 'OTHER', b'0');
INSERT INTO `article` VALUES (1370178083092430848, 'admin', '2021-03-11 19:01:26', b'0', NULL, NULL, '', '<p><span style=\"font-size: 1em;\">商品编辑里面的规格名、规格值历史记录目前不支持删除。</span><br></p>', 1, '商品编辑里面的规格名、规格值历史记录可以删除吗？', 'OTHER', b'1');
INSERT INTO `article` VALUES (1370178171978121216, 'admin', '2021-03-11 19:01:48', b'0', NULL, NULL, '', '<p>已成团订单其中1笔申请退款，按正常订单处理，这个团的其他订单不受影响，还是可以正常发货的。</p>', 1, '已成团订单，其中有一人申请退款，对同团其他订单有影响吗？', 'OTHER', b'1');
INSERT INTO `article` VALUES (1370178254094204928, 'admin', '2021-03-11 19:02:07', b'0', 'admin', '2021-09-09 16:11:51', '1369921726825103360', '<p>优惠券不能用来抵扣订单中的运费。</p>', 1, '优惠券可以抵扣运费吗？', 'OTHER', b'0');
INSERT INTO `article` VALUES (1371779927900160000, 'admin', '2021-03-16 06:06:36', b'0', 'admin', '2021-11-04 19:10:47', '1371779742369316864', '<p>APP隐私协议<br />\n以下隐私协议是xxx公司（以下简称\"我们\"） 对用户隐私保护的许诺，请您务必仔细阅读，以了解我们关于管理您个人信息的情况。本隐私协议使用协议\"的重要组成部分，与其具有同等法律效力。</p>', 1, '隐私策略', 'PRIVACY_POLICY', b'1');
INSERT INTO `article` VALUES (1371780016521609216, 'admin', '2021-03-16 06:06:57', b'0', 'admin', '2021-11-04 19:28:07', '1371779742369316864', '<p>关于，关于我们</p><p><img src=\"https://lilishop-oss.oss-cn-beijing.aliyuncs.com/b280f3d1b3504cbeb0a8dc0fab3a24ba.png\" /></p>', 1, '关于我们', 'ABOUT', b'1');
INSERT INTO `article` VALUES (1371992635077558272, 'admin', '2021-03-16 20:11:49', b'0', 'admin', '2021-11-04 19:28:07', '1371779742369316864', '<p>证照</p>', 1, '证照信息', 'LICENSE_INFORMATION', b'1');
INSERT INTO `article` VALUES (1371992704333905920, 'admin', '2021-03-16 20:12:06', b'0', 'admin', '2021-11-04 19:28:05', '1371779742369316864', '<p>用户注册及使用APP隐私协议<br />\n<br />\n在此特别提醒您（用户）在注册成为用户之前，请认真阅读本《用户协议》（以下简称“协议”），确保您充分理解本协议中各条款。请您审慎阅读并选择接受或不接受本协议。您的注册、登录、使用等行为将视为对本协议的接受，并同意接受本协议各项条款的约束。</p>', 1, '用户协议', 'USER_AGREEMENT', b'1');
INSERT INTO `article` VALUES (1394449958765068288, 'admin', '2021-05-18 08:29:12', b'0', 'admin', '2021-09-09 16:12:03', '1394449540941086720', '<p>1.加载“统一社会信用代码”的营业执照（根据2014年10月1日生效的《企业经营异常名录管理暂行办法》，需确保未在企业经营异常名录中且所售商品属于经营范围内）；</p><p>2.一般纳税人资格证复印件（选择FBP模式或“家电”类目商家必须提供，其他模式尽量提供）；</p><p>3.银行开户许可证复印件；（须有中国人民银行盖章，法人代表与营业执照一致，不一致请提供工商局出具的变更证明；）</p><p>4.法定代表人身份证正反面复印件。</p>', 1, '店铺入驻协议', 'STORE_REGISTER', b'0');
INSERT INTO `article` VALUES (1427235479678693377, 'admin', '2021-08-16 19:47:10', b'0', 'admin', '2021-11-23 09:41:04', '1390968079827075072', '<h1><br /></h1>', 1, '关于我们', 'OTHER', b'0');

-- ----------------------------
-- Table structure for article_category
-- ----------------------------
DROP TABLE IF EXISTS `article_category`;
CREATE TABLE `article_category`  (
                                        `id` bigint NOT NULL COMMENT 'ID',
                                        `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '创建者',
                                        `create_at` datetime COMMENT '创建时间',
                                        `delete_flag` bit(1) COMMENT '删除标志 true/false 删除/未删除',
                                        `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '更新者',
                                        `update_at` datetime COMMENT '更新时间',
                                        `level` int COMMENT '层级',
                                        `parent_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '父分类ID',
                                        `sort` int COMMENT '排序',
                                        `article_category_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '分类名称',
                                        `category` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '分类类型',
                                        PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of article_category
-- ----------------------------
INSERT INTO `article_category` VALUES (1347433752083324928, 'admin', '2021-01-08 14:43:35', b'0', 'admin', '2021-11-23 22:45:41', 0, '0', 1, '常见问题', 'OTHER');
INSERT INTO `article_category` VALUES (1347450302295203840, 'admin', '2021-01-08 15:49:21', b'0', 'admin', '2021-09-24 19:55:07', 1, '1347433752083324928', 1, '商品管理', 'OTHER');
INSERT INTO `article_category` VALUES (1347450353352466432, 'admin', '2021-01-08 15:49:33', b'0', 'admin', '2021-01-08 16:24:08', 1, '1347433752083324928', 3, '订单管理', 'OTHER');
INSERT INTO `article_category` VALUES (1347450411015757824, 'admin', '2021-01-08 15:49:47', b'0', 'admin', '2021-01-08 16:23:55', 1, '1347433752083324928', 4, '营销管理', 'OTHER');
INSERT INTO `article_category` VALUES (1347456734864367616, 'admin', '2021-01-08 16:14:55', b'0', 'admin', '2021-06-02 18:54:47', 1, '1347433752083324928', 2, '店铺开店', 'OTHER');
INSERT INTO `article_category` VALUES (1347458431707799552, 'admin', '2021-01-08 16:21:39', b'0', 'admin', '2021-01-08 16:24:13', 1, '1347433752083324928', 5, '设置', 'OTHER');
INSERT INTO `article_category` VALUES (1347469221429010432, 'admin', '2021-01-08 17:04:32', b'0', NULL, NULL, 1, '1347433752083324928', 6, '财务管理', 'OTHER');
INSERT INTO `article_category` VALUES (1347730672995557376, 'admin', '2021-01-09 10:23:27', b'0', 'admin', '2021-05-08 14:59:49', 0, '0', 2, '平台管理常见问题', 'OTHER');
INSERT INTO `article_category` VALUES (1347730780659146752, 'admin', '2021-01-09 10:23:52', b'0', NULL, NULL, 1, '1347730672995557376', 1, '订单', 'OTHER');
INSERT INTO `article_category` VALUES (1347730985055969280, 'admin', '2021-01-09 10:24:41', b'0', NULL, NULL, 1, '1347730672995557376', 2, '商品', 'OTHER');
INSERT INTO `article_category` VALUES (1347732008847826944, 'admin', '2021-01-09 10:28:45', b'0', NULL, NULL, 1, '1347730672995557376', 3, '营销', 'OTHER');
INSERT INTO `article_category` VALUES (1347732307880730624, 'admin', '2021-01-09 10:29:57', b'0', NULL, NULL, 1, '1347730672995557376', 4, '财务', 'OTHER');
INSERT INTO `article_category` VALUES (1369921726825103360, 'admin', '2021-03-11 02:02:46', b'0', NULL, NULL, 1, '1390968079827075072', 3, '平台公告', 'ANNOUNCEMENT');
INSERT INTO `article_category` VALUES (1369921726825103362, 'admin', '2021-05-08 14:18:43', b'0', 'admin', '2021-11-13 16:01:18', 0, '0', 2, '公告', 'STORE_ARTICLE');
INSERT INTO `article_category` VALUES (1371779742369316864, 'admin', '2021-03-16 06:05:52', b'0', 'admin', '2021-05-18 08:27:39', 1, '1390968079827075072', 2, '平台信息', 'PLATFORM_INFORMATION');
INSERT INTO `article_category` VALUES (1390968079827075072, 'admin', '2021-05-08 17:53:28', b'0', 'admin', '2021-10-22 11:25:49', 0, '0', 1, '关于我们', NULL);
INSERT INTO `article_category` VALUES (1394449540941086720, 'admin', '2021-05-18 08:27:33', b'0', NULL, NULL, 1, '1390968079827075072', 3, '店铺入驻协议', 'OTHER');
INSERT INTO `article_category` VALUES (1419486877636169730, 'admin', '2021-07-26 10:36:59', b'0', NULL, NULL, 0, '0', 1, '招商代理', 'OTHER');

-- ----------------------------
-- Table structure for category_brand
-- ----------------------------
DROP TABLE IF EXISTS `category_brand`;
CREATE TABLE `category_brand`  (
                                      `id` bigint NOT NULL COMMENT 'ID',
                                      `create_by` varchar(255)COMMENT '创建者',
                                      `create_at` datetime(6) COMMENT '创建时间',
                                      `brand_id` varchar(255)COMMENT '品牌ID',
                                      `category_id` varchar(255)COMMENT '分类ID',
                                      PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of category_brand
-- ----------------------------

-- ----------------------------
-- Table structure for category_parameter_group
-- ----------------------------
DROP TABLE IF EXISTS `category_parameter_group`;
CREATE TABLE `category_parameter_group`  (
                                                `id` bigint NOT NULL COMMENT 'ID',
                                                `create_by` varchar(255)COMMENT '创建者',
                                                `create_at` datetime(6) COMMENT '创建时间',
                                                `delete_flag` bit(1) COMMENT '删除标志 true/false 删除/未删除',
                                                `update_by` varchar(255)COMMENT '更新者',
                                                `update_at` datetime(6) COMMENT '更新时间',
                                                `category_id` varchar(255)NOT NULL COMMENT '关联分类ID',
                                                `group_name` varchar(50)COMMENT '参数组名称',
                                                `sort` int COMMENT '排序',
                                                PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of category_parameter_group
-- ----------------------------

-- ----------------------------
-- Table structure for category_specification
-- ----------------------------
DROP TABLE IF EXISTS `category_specification`;
CREATE TABLE `category_specification`  (
                                              `id` bigint NOT NULL COMMENT 'ID',
                                              `create_by` varchar(255)NULL DEFAULT NULL COMMENT '创建者',
                                              `create_at` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                              `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                              `update_by` varchar(255)NULL DEFAULT NULL COMMENT '更新者',
                                              `update_at` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                              `category_id` varchar(255)NULL DEFAULT NULL COMMENT '分类ID',
                                              `specification_id` varchar(255)NULL DEFAULT NULL COMMENT '规格ID',
                                              PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of category_specification
-- ----------------------------
INSERT INTO `category_specification` VALUES (1440526235510771713, 'admin', '2021-09-22 11:59:52.887000', b'0', NULL, NULL, '1348576427264204944', '1376748749111427072');
INSERT INTO `category_specification` VALUES (1440573693875982338, 'admin', '2021-09-22 15:08:27.842000', b'0', NULL, NULL, '1348576427264204943', '1376371867132100608');
INSERT INTO `category_specification` VALUES (1440573693909536769, 'admin', '2021-09-22 15:08:27.851000', b'0', NULL, NULL, '1348576427264204943', '1376748749111427072');

-- ----------------------------
-- Table structure for clerk
-- ----------------------------
DROP TABLE IF EXISTS `clerk`;
CREATE TABLE `clerk`  (
                             `id` bigint NOT NULL COMMENT 'ID',
                             `create_by` varchar(255)  COMMENT '创建者',
                             `create_at` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                             `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                             `update_by` varchar(255)  COMMENT '更新者',
                             `update_at` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                             `clerk_name` varchar(255)  COMMENT '店员名称',
                             `member_id` varchar(255)  COMMENT '会员id',
                             `store_id` varchar(255)  COMMENT '店铺id',
                             `department_id` varchar(255)  COMMENT '部门id',
                             `role_ids` varchar(255)  COMMENT '角色',
                             `shopkeeper` bit(1) NULL DEFAULT NULL COMMENT '是否是店主',
                             `is_super` bit(1) NULL DEFAULT NULL COMMENT '是否是超级管理员 超级管理员/普通管理员',
                             `status` bit(1) NULL DEFAULT NULL COMMENT '状态',
                             PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of clerk
-- ----------------------------
INSERT INTO `clerk` VALUES (1376369067769724928, '15810610731', '2022-11-22 00:00:00.000000', NULL, NULL, NULL, '15810610731', '1376364798236688384', '1376369067769724928', NULL, NULL, b'1', b'1', b'1');
INSERT INTO `clerk` VALUES (1376433565247471616, '13011111111', '2022-11-22 00:00:00.000000', NULL, NULL, NULL, '13011111111', '1376417684140326912', '1376433565247471616', NULL, NULL, b'1', b'1', b'1');

-- ----------------------------
-- Table structure for clerk_role
-- ----------------------------
DROP TABLE IF EXISTS `clerk_role`;
CREATE TABLE `clerk_role`  (
                                  `id` bigint NOT NULL COMMENT 'ID',
                                  `clerk_id` varchar(255)  COMMENT '店员唯一id',
                                  `role_id` varchar(255)  COMMENT '角色唯一id',
                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of clerk_role
-- ----------------------------

-- ----------------------------
-- Table structure for commodity
-- ----------------------------
DROP TABLE IF EXISTS `commodity`;
CREATE TABLE `commodity`  (
                                 `id` bigint NOT NULL,
                                 `create_by` varchar(64) ,
                                 `create_at` datetime(6) NULL DEFAULT NULL,
                                 `delete_flag` bit(1) NULL DEFAULT NULL,
                                 `update_by` varchar(64) ,
                                 `update_at` datetime(6) NULL DEFAULT NULL,
                                 `audit_id` bigint NULL DEFAULT NULL,
                                 `audit_status` varchar(255) ,
                                 `goods_id` bigint NULL DEFAULT NULL,
                                 `goods_image` varchar(255) ,
                                 `live_goods_id` int NULL DEFAULT NULL,
                                 `name` varchar(255) ,
                                 `price` decimal(10, 2) NULL DEFAULT NULL,
                                 `price2` decimal(10, 2) NULL DEFAULT NULL,
                                 `price_type` int NULL DEFAULT NULL,
                                 `sku_id` bigint NULL DEFAULT NULL,
                                 `store_id` bigint NULL DEFAULT NULL,
                                 `url` varchar(255) ,
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of commodity
-- ----------------------------
INSERT INTO `commodity` VALUES (1411890810665107458, '15810610731', '2021-07-05 11:32:55.582000', b'0', NULL, NULL, 510443646, '2', 1376836083676872704, 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/0c32095777704e2db7a5f858c32403e9.jpg?x-oss-process=style/200X200', 50, '三星Galaxy S21 ', 2.00, NULL, 1, 1386125116261269504, 1376369067769724928, 'pages/product/goods?id=1386125116261269504&goodsId=1376836083676872704');
INSERT INTO `commodity` VALUES (1411891969442250753, '13011111111', '2021-07-05 11:37:31.857000', b'0', NULL, NULL, 510443648, '2', 1376443041593688064, 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/54dcb9cd0fea4f1f8243e12d49c28af9.png?x-oss-process=style/200X200', 51, '百草味 蜜饯 原味芒果干 ', 15.00, NULL, 1, 1381791842915319808, 1376433565247471616, 'pages/product/goods?id=1381791842915319808&goodsId=1376443041593688064');
INSERT INTO `commodity` VALUES (1411892561510203394, '13011111111', '2021-07-05 11:39:53.016000', b'0', NULL, NULL, 510443651, '2', 1377057500825649152, 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/6ce1382ba55f4284a86f893cfc79eed8.jpg?x-oss-process=style/200X200', 52, '雀巢（Nestle）咖啡 ', 39.00, NULL, 1, 1381791962713030656, 1376433565247471616, 'pages/product/goods?id=1381791962713030656&goodsId=1377057500825649152');
INSERT INTO `commodity` VALUES (1411892607165202434, '13011111111', '2021-07-05 11:40:03.901000', b'0', NULL, NULL, 510443653, '2', 1377127936569638912, 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/389b2ce90fd0425a8a9d988890ad46d2.jpg?x-oss-process=style/200X200', 53, 'ARMANI阿玛尼 新款蓝', 666.00, NULL, 1, 1381789991541145600, 1376433565247471616, 'pages/product/goods?id=1381789991541145600&goodsId=1377127936569638912');
INSERT INTO `commodity` VALUES (1411893337494192129, '13011111111', '2021-07-05 11:42:58.025000', b'0', NULL, NULL, 510443655, '2', 1377127936569638912, 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/ab2876454f9b4e5ebd99218a82b45da5.jpg?x-oss-process=style/200X200', 54, 'ARMANI阿玛尼 新款蓝', 1.00, NULL, 1, 1381789991545339904, 1376433565247471616, 'pages/product/goods?id=1381789991545339904&goodsId=1377127936569638912');
INSERT INTO `commodity` VALUES (1411894391178854401, '13011111111', '2021-07-05 11:47:09.244000', b'0', NULL, NULL, 510443660, '2', 1377064344218501120, 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/5f9d06f672a047b78df128be989bece7.jpg?x-oss-process=style/200X200', 55, '肉类零食 白芝麻猪肉脯 百', 36.00, NULL, 1, 1381792263696285696, 1376433565247471616, 'pages/product/goods?id=1381792263696285696&goodsId=1377064344218501120');
INSERT INTO `commodity` VALUES (1412315783061929985, '15810610731', '2021-07-06 15:41:36.897000', b'0', NULL, NULL, 510443917, '2', 1377802526690115584, 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/b491452a01b44a099fef6bb2aa90327a.jpeg?x-oss-process=style/200X200', 56, '惠普(HP)星青春版', 2999.00, NULL, 1, 1386930238503518208, 1376369067769724928, 'pages/product/goods?id=1386930238503518208&goodsId=1377802526690115584');
INSERT INTO `commodity` VALUES (1416626400858066945, '13011111111', '2021-07-18 13:10:28.269000', b'0', NULL, NULL, 510446454, '2', 1377132926042374144, 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/28964af5041e42f4becd1af31d4782bd.png?x-oss-process=style/200X200', 58, '纳斯（NARS) Blus', 222.00, NULL, 1, 1377132926088511488, 1376433565247471616, 'pages/product/goods?id=1377132926088511488&goodsId=1377132926042374144');

-- ----------------------------
-- Table structure for connect
-- ----------------------------
DROP TABLE IF EXISTS `connect`;
CREATE TABLE `connect`  (
                               `id` bigint NOT NULL COMMENT 'ID',
                               `create_by` varchar(255)NULL DEFAULT NULL COMMENT '创建者',
                               `create_at` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                               `union_id` varchar(255)NULL DEFAULT NULL COMMENT '联合登录ID',
                               `union_type` varchar(255)NULL DEFAULT NULL COMMENT '联合登录类型',
                               `user_id` varchar(255)NULL DEFAULT NULL COMMENT '用户ID',
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of connect
-- ----------------------------

-- ----------------------------
-- Table structure for connect_config
-- ----------------------------
DROP TABLE IF EXISTS `connect_config`;
CREATE TABLE `connect_config`  (
                                      `id` bigint NOT NULL COMMENT 'ID',
                                      `create_by` varchar(255)NULL DEFAULT NULL COMMENT '创建者',
                                      `create_at` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                      `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                      `update_by` varchar(255)NULL DEFAULT NULL COMMENT '更新者',
                                      `update_at` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                      `config_key` varchar(255)NULL DEFAULT NULL COMMENT '配置key',
                                      `config_value` varchar(255)NULL DEFAULT NULL COMMENT '配置',
                                      PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of connect_config
-- ----------------------------

-- ----------------------------
-- Table structure for coupon
-- ----------------------------
DROP TABLE IF EXISTS `coupon`;
CREATE TABLE `coupon`  (
                              `id` bigint NOT NULL COMMENT 'ID',
                              `create_by` varchar(255)NULL DEFAULT NULL COMMENT '创建者',
                              `create_at` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                              `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                              `update_by` varchar(255)NULL DEFAULT NULL COMMENT '更新者',
                              `update_at` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                              `end_time` datetime(6) NULL DEFAULT NULL COMMENT '活动结束时间',
                              `promotion_name` varchar(255)NULL DEFAULT NULL COMMENT '活动名称',
                              `store_id` varchar(255)NULL DEFAULT NULL COMMENT '店铺ID',
                              `store_name` varchar(255)NULL DEFAULT NULL COMMENT '店铺名称',
                              `start_time` datetime(6) NULL DEFAULT NULL COMMENT '活动开始时间',
                              `consume_threshold` decimal(10, 2) NULL DEFAULT NULL COMMENT '消费门槛',
                              `coupon_discount` decimal(10, 2) NULL DEFAULT NULL COMMENT '折扣',
                              `coupon_limit_num` int NULL DEFAULT NULL COMMENT '领取限制',
                              `coupon_name` varchar(255)NULL DEFAULT NULL COMMENT '优惠券名称',
                              `coupon_type` varchar(255)NULL DEFAULT NULL COMMENT '活动类型',
                              `description` varchar(255)NULL DEFAULT NULL COMMENT '活动描述',
                              `get_type` varchar(255)NULL DEFAULT NULL COMMENT '优惠券类型',
                              `price` decimal(10, 2) NULL DEFAULT NULL COMMENT '面额',
                              `publish_num` int NULL DEFAULT NULL COMMENT '发行数量',
                              `received_num` int NULL DEFAULT NULL COMMENT '已被领取的数量',
                              `scope_id` textNULL COMMENT '范围关联的ID',
                              `scope_type` varchar(255)NULL DEFAULT NULL COMMENT '关联范围类型',
                              `store_commission` decimal(10, 2) NULL DEFAULT NULL COMMENT '店铺承担比例',
                              `used_num` int NULL DEFAULT NULL COMMENT '已被使用的数量',
                              `range_day_type` varchar(32)NULL DEFAULT NULL,
                              `effective_days` int NULL DEFAULT NULL,
                              PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of coupon
-- ----------------------------

-- ----------------------------
-- Table structure for coupon_activity
-- ----------------------------
DROP TABLE IF EXISTS `coupon_activity`;
CREATE TABLE `coupon_activity`  (
                                       `id` bigint NOT NULL,
                                       `create_by` varchar(255) ,
                                       `create_at` datetime(6) NULL DEFAULT NULL,
                                       `delete_flag` bit(1) NULL DEFAULT NULL,
                                       `update_by` varchar(255) ,
                                       `update_at` datetime(6) NULL DEFAULT NULL,
                                       `end_time` datetime(6) NULL DEFAULT NULL,
                                       `promotion_name` varchar(255) ,
                                       `start_time` datetime(6) NULL DEFAULT NULL,
                                       `store_id` varchar(255) ,
                                       `store_name` varchar(255) ,
                                       `activity_scope` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                                       `activity_scope_info` varchar(255) ,
                                       `coupon_activity_type` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
                                       `scope_id` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '范围关联的ID',
                                       `scope_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'PORTION_GOODS' COMMENT '关联范围类型',
                                       `coupon_frequency_enum` varchar(255)  COMMENT '领取周期',
                                       PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of coupon_activity
-- ----------------------------

-- ----------------------------
-- Table structure for coupon_activity_item
-- ----------------------------
DROP TABLE IF EXISTS `coupon_activity_item`;
CREATE TABLE `coupon_activity_item`  (
                                            `id` bigint NOT NULL,
                                            `create_by` varchar(255) ,
                                            `create_at` datetime(6) NULL DEFAULT NULL,
                                            `delete_flag` bit(1) NULL DEFAULT NULL,
                                            `update_by` varchar(255) ,
                                            `update_at` datetime(6) NULL DEFAULT NULL,
                                            `activity_id` varchar(255) ,
                                            `coupon_id` varchar(255) ,
                                            `num` int NULL DEFAULT NULL,
                                            PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of coupon_activity_item
-- ----------------------------

-- ----------------------------
-- Table structure for custom_words
-- ----------------------------
DROP TABLE IF EXISTS `custom_words`;
CREATE TABLE `custom_words`  (
                                    `id` bigint NOT NULL COMMENT 'ID',
                                    `create_by` varchar(255)NULL DEFAULT NULL COMMENT '创建者',
                                    `create_at` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                    `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                    `update_by` varchar(255)NULL DEFAULT NULL COMMENT '更新者',
                                    `update_at` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                    `disabled` int NULL DEFAULT NULL COMMENT '是否禁用',
                                    `name` varchar(20)NULL DEFAULT NULL COMMENT '名称',
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of custom_words
-- ----------------------------
INSERT INTO `custom_words` VALUES (1426013201641037826, 'admin', '2021-08-13 10:50:15.972000', b'0', NULL, NULL, NULL, '米');
INSERT INTO `custom_words` VALUES (1426014704225280002, 'admin', '2021-08-13 10:56:14.217000', b'0', 'admin', '2021-08-18 13:52:34.374000', NULL, '小');
INSERT INTO `custom_words` VALUES (1430197443518361601, 'admin', '2021-08-24 23:56:56.975000', b'0', NULL, NULL, NULL, '浪');
INSERT INTO `custom_words` VALUES (1434685838898536449, 'admin', '2021-09-06 09:12:13.825000', b'0', NULL, NULL, NULL, '测试');
INSERT INTO `custom_words` VALUES (1434685854694285314, 'admin', '2021-09-06 09:12:17.591000', b'0', NULL, NULL, NULL, '我');
INSERT INTO `custom_words` VALUES (1434859360199417858, 'admin', '2021-09-06 20:41:44.528000', b'0', NULL, NULL, NULL, '粽子');
INSERT INTO `custom_words` VALUES (1434859380810227714, 'admin', '2021-09-06 20:41:49.441000', b'0', NULL, NULL, NULL, '粽');
INSERT INTO `custom_words` VALUES (1438746947498635265, 'admin', '2021-09-17 14:09:37.567000', b'0', NULL, NULL, NULL, '手机');
INSERT INTO `custom_words` VALUES (1448636645448634370, 'admin', '2021-10-14 21:07:45.300000', b'0', NULL, NULL, NULL, '水电费');
INSERT INTO `custom_words` VALUES (1450074350040735745, 'admin', '2021-10-18 20:20:40.786000', b'0', 'admin', '2021-10-26 15:04:18.755000', NULL, '华为手机');
INSERT INTO `custom_words` VALUES (1455466891321851906, 'admin', '2021-11-02 17:28:42.815000', b'0', NULL, NULL, NULL, '郭宇');
INSERT INTO `custom_words` VALUES (1456132721256742913, 'admin', '2021-11-04 13:34:29.043000', b'0', NULL, NULL, NULL, '电脑');
INSERT INTO `custom_words` VALUES (1456132765783474177, 'admin', '2021-11-04 13:34:39.658000', b'0', 'admin', '2021-11-08 18:25:02.045000', NULL, '电脑1');
INSERT INTO `custom_words` VALUES (1457252170026274817, 'admin', '2021-11-07 15:42:46.427000', b'0', NULL, NULL, NULL, '妮维雅');
INSERT INTO `custom_words` VALUES (1463729201022849026, 'admin', '2021-11-25 12:40:10.955000', b'0', NULL, NULL, NULL, '耐克');

-- ----------------------------
-- Table structure for department
-- ----------------------------
DROP TABLE IF EXISTS `department`;
CREATE TABLE `department`  (
                                  `id` bigint NOT NULL COMMENT 'ID',
                                  `create_by` varchar(255)NULL DEFAULT NULL COMMENT '创建者',
                                  `create_at` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                  `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                  `update_by` varchar(255)NULL DEFAULT NULL COMMENT '更新者',
                                  `update_at` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                  `parent_id` varchar(255)NULL DEFAULT NULL COMMENT '父ID',
                                  `sort_order` decimal(10, 2) NULL DEFAULT NULL COMMENT '排序值',
                                  `title` varchar(255)NULL DEFAULT NULL COMMENT '部门名称',
                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of department
-- ----------------------------
INSERT INTO `department` VALUES (1363424504218386432, 'admin', '2021-02-21 09:45:07.000000', b'0', 'admin', '2022-01-12 23:31:44.852000', '0', 0.00, '财务部');
INSERT INTO `department` VALUES (1364415918628667392, 'admin', '2021-02-24 03:24:39.000000', b'0', 'admin', '2021-09-29 16:29:28.277000', '0', 0.00, '销售部');
INSERT INTO `department` VALUES (1371978420262535168, 'admin', '2021-03-16 19:15:20.000000', b'0', 'admin', '2021-10-29 17:34:30.546000', '1363424504218386432', 1.00, '财务一部');
INSERT INTO `department` VALUES (1371978462067163136, 'admin', '2021-03-16 19:15:30.000000', b'0', 'admin', '2021-11-24 19:24:16.418000', '1363424504218386432', 2.00, '财务二部');
INSERT INTO `department` VALUES (1423484408787369986, 'admin', '2021-08-06 11:21:44.000000', b'0', 'admin', '2021-08-06 11:23:57.790000', '0', 1.00, '运营部');
INSERT INTO `department` VALUES (1423484886954803202, 'admin', '2021-08-06 11:23:38.778000', b'0', NULL, NULL, '1423484408787369986', 0.00, '运营专员');
INSERT INTO `department` VALUES (1423484932957929473, 'admin', '2021-08-06 11:23:49.747000', b'0', NULL, NULL, '1423484408787369986', 0.00, '运营主管');
INSERT INTO `department` VALUES (1431176858761805825, 'admin', '2021-08-27 16:48:47.000000', b'0', 'admin', '2021-08-27 16:49:27.297000', '0', 0.00, 'cc');
INSERT INTO `department` VALUES (1455359452782047233, 'admin', '2021-11-02 10:21:47.471000', b'0', NULL, NULL, '0', 0.00, '电商部');
INSERT INTO `department` VALUES (1455373067090259970, 'admin', '2021-11-02 11:15:53.376000', b'0', NULL, NULL, '1455359452782047233', 0.00, '电商一部');
INSERT INTO `department` VALUES (1455818288894623746, 'admin', '2021-11-03 16:45:02.526000', b'0', NULL, NULL, '0', 0.00, '财务二部');

-- ----------------------------
-- Table structure for department_role
-- ----------------------------
DROP TABLE IF EXISTS `department_role`;
CREATE TABLE `department_role`  (
                                       `id` bigint NOT NULL COMMENT 'ID',
                                       `create_by` varchar(255)NULL DEFAULT NULL COMMENT '创建者',
                                       `create_at` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                       `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                       `update_by` varchar(255)NULL DEFAULT NULL COMMENT '更新者',
                                       `update_at` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                       `department_id` varchar(255)NULL DEFAULT NULL COMMENT '部门ID',
                                       `role_id` varchar(255)NULL DEFAULT NULL COMMENT '角色ID',
                                       PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of department_role
-- ----------------------------

-- ----------------------------
-- Table structure for distribution
-- ----------------------------
DROP TABLE IF EXISTS `distribution`;
CREATE TABLE `distribution`  (
                                    `id` bigint NOT NULL COMMENT 'ID',
                                    `create_by` varchar(255)NULL DEFAULT NULL COMMENT '创建者',
                                    `create_at` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                    `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                    `update_by` varchar(255)NULL DEFAULT NULL COMMENT '更新者',
                                    `update_at` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                    `can_rebate` decimal(10, 2) NULL DEFAULT NULL COMMENT '可提现金额',
                                    `commission_frozen` decimal(10, 2) NULL DEFAULT NULL COMMENT '冻结金额',
                                    `distribution_status` varchar(255)NULL DEFAULT NULL COMMENT '分销商状态',
                                    `member_id` varchar(255)NULL DEFAULT NULL COMMENT '用户ID',
                                    `member_name` varchar(255)NULL DEFAULT NULL COMMENT '用户名称',
                                    `rebate_total` decimal(10, 2) NULL DEFAULT NULL COMMENT '分销总额',
                                    `id_number` varchar(255)NULL DEFAULT NULL COMMENT '身份证号',
                                    `name` varchar(255)NULL DEFAULT NULL COMMENT '姓名',
                                    `distribution_order_count` int NULL DEFAULT NULL COMMENT '分销订单数',
                                    `settlement_bank_account_name` varchar(200)NULL DEFAULT NULL,
                                    `settlement_bank_account_num` varchar(200)NULL DEFAULT NULL,
                                    `settlement_bank_branch_name` varchar(200)NULL DEFAULT NULL,
                                    `distribution_order_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '分销订单金额',
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of distribution
-- ----------------------------

-- ----------------------------
-- Table structure for distribution_cash
-- ----------------------------
DROP TABLE IF EXISTS `distribution_cash`;
CREATE TABLE `distribution_cash`  (
                                         `id` bigint NOT NULL COMMENT 'ID',
                                         `create_by` varchar(255)NULL DEFAULT NULL COMMENT '创建者',
                                         `create_at` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                         `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                         `update_by` varchar(255)NULL DEFAULT NULL COMMENT '更新者',
                                         `update_at` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                         `distribution_cash_status` varchar(255)NULL DEFAULT NULL COMMENT '状态',
                                         `distribution_id` varchar(255)NULL DEFAULT NULL COMMENT '分销员ID',
                                         `distribution_name` varchar(255)NULL DEFAULT NULL COMMENT '分销员名称',
                                         `pay_time` datetime(6) NULL DEFAULT NULL COMMENT '支付时间',
                                         `price` decimal(10, 2) NULL DEFAULT NULL COMMENT '分销佣金',
                                         `sn` varchar(255)NULL DEFAULT NULL COMMENT '分销佣金编号',
                                         `name` varchar(255)NULL DEFAULT NULL COMMENT '会员姓名',
                                         `id_number` varchar(255)NULL DEFAULT NULL COMMENT '身份证号',
                                         `settlement_bank_account_name` varchar(255)NULL DEFAULT NULL COMMENT '结算银行开户行名称',
                                         `settlement_bank_account_num` varchar(255)NULL DEFAULT NULL COMMENT '结算银行开户账号',
                                         `settlement_bank_branch_name` varchar(255)NULL DEFAULT NULL COMMENT '结算银行开户支行名称',
                                         PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of distribution_cash
-- ----------------------------

-- ----------------------------
-- Table structure for distribution_goods
-- ----------------------------
DROP TABLE IF EXISTS `distribution_goods`;
CREATE TABLE `distribution_goods`  (
                                          `id` bigint NOT NULL COMMENT 'ID',
                                          `create_by` varchar(255)NULL DEFAULT NULL COMMENT '创建者',
                                          `create_at` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                          `commission` decimal(10, 2) NOT NULL COMMENT '佣金金额',
                                          `goods_id` varchar(255)NOT NULL COMMENT '商品ID',
                                          `goods_name` varchar(255)NULL DEFAULT NULL COMMENT '商品名称',
                                          `price` decimal(10, 2) NULL DEFAULT NULL COMMENT '商品价格',
                                          `quantity` int NULL DEFAULT NULL COMMENT '库存',
                                          `store_id` varchar(255)NOT NULL COMMENT '店铺ID',
                                          `sku_id` varchar(255)NOT NULL COMMENT '规格ID',
                                          `specs` textNULL COMMENT '规格信息json',
                                          `thumbnail` varchar(255)NULL DEFAULT NULL COMMENT '缩略图路径',
                                          `store_name` varchar(255)NOT NULL COMMENT 'storeName',
                                          PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of distribution_goods
-- ----------------------------

-- ----------------------------
-- Table structure for distribution_order
-- ----------------------------
DROP TABLE IF EXISTS `distribution_order`;
CREATE TABLE `distribution_order`  (
                                          `id` bigint NOT NULL,
                                          `create_at` datetime(6) NULL DEFAULT NULL,
                                          `distribution_id` varchar(255)NULL DEFAULT NULL,
                                          `distribution_name` varchar(255)NULL DEFAULT NULL,
                                          `distribution_order_status` varchar(255)NULL DEFAULT NULL,
                                          `goods_id` bigint,
                                          `goods_name` varchar(255)NULL DEFAULT NULL,
                                          `image` varchar(255)NULL DEFAULT NULL,
                                          `member_id` varchar(255)NULL DEFAULT NULL,
                                          `member_name` varchar(255)NULL DEFAULT NULL,
                                          `num` int NULL DEFAULT NULL,
                                          `order_item_sn` varchar(255)NULL DEFAULT NULL,
                                          `order_sn` varchar(255)NULL DEFAULT NULL,
                                          `rebate` decimal(10, 2) NULL DEFAULT NULL,
                                          `sell_back_rebate` decimal(10, 2) NULL DEFAULT NULL,
                                          `settle_cycle` datetime(6) NULL DEFAULT NULL,
                                          `sku_id` varchar(255)NULL DEFAULT NULL,
                                          `specs` textNULL,
                                          `store_id` varchar(255)NULL DEFAULT NULL,
                                          `store_name` varchar(255)NULL DEFAULT NULL,
                                          `refund_num` int NULL DEFAULT NULL COMMENT '退款商品数量',
                                          PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of distribution_order
-- ----------------------------

-- ----------------------------
-- Table structure for distribution_selected_goods
-- ----------------------------
DROP TABLE IF EXISTS `distribution_selected_goods`;
CREATE TABLE `distribution_selected_goods`  (
                                                   `id` bigint NOT NULL COMMENT 'ID',
                                                   `distribution_goods_id` varchar(255)NULL DEFAULT NULL COMMENT '分销商品ID',
                                                   `distribution_id` varchar(255)NULL DEFAULT NULL COMMENT '分销商ID',
                                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of distribution_selected_goods
-- ----------------------------

-- ----------------------------
-- Table structure for draft_goods
-- ----------------------------
DROP TABLE IF EXISTS `draft_goods`;
CREATE TABLE `draft_goods`  (
                                   `id` bigint NOT NULL COMMENT 'ID',
                                   `create_by` varchar(255)NULL DEFAULT NULL COMMENT '创建者',
                                   `create_at` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                   `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                   `update_by` varchar(255)NULL DEFAULT NULL COMMENT '更新者',
                                   `update_at` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                   `brand_id` varchar(255)NULL DEFAULT NULL COMMENT '品牌ID',
                                   `buy_count` int NULL DEFAULT NULL COMMENT '购买数量',
                                   `category_name_json` textNULL COMMENT '分类名称json',
                                   `category_path` varchar(255)NULL DEFAULT NULL COMMENT '分类path',
                                   `comment_num` int NULL DEFAULT NULL COMMENT '评论数量',
                                   `cost` decimal(10, 2) NULL DEFAULT NULL COMMENT '成本价格',
                                   `enable_quantity` int NULL DEFAULT NULL COMMENT '可用库存',
                                   `freight_payer` varchar(255)NULL DEFAULT NULL COMMENT '运费承担者',
                                   `goods_gallery_list_json` textNULL COMMENT '商品图片json',
                                   `goods_name` varchar(255)NULL DEFAULT NULL COMMENT '商品名称',
                                   `goods_params_list_json` textNULL COMMENT '商品参数json',
                                   `goods_unit` varchar(255)NULL DEFAULT NULL COMMENT '计量单位',
                                   `goods_video` varchar(255)NULL DEFAULT NULL COMMENT '商品视频',
                                   `grade` decimal(10, 2) NULL DEFAULT NULL COMMENT '商品好评率',
                                   `intro` varchar(255)NULL DEFAULT NULL COMMENT '商品详情',
                                   `mobile_intro` varchar(255)NULL DEFAULT NULL COMMENT '商品移动端详情',
                                   `original` varchar(255)NULL DEFAULT NULL COMMENT '原图路径',
                                   `price` decimal(10, 2) NULL DEFAULT NULL COMMENT '商品价格',
                                   `quantity` int NULL DEFAULT NULL COMMENT '库存',
                                   `recommend` bit(1) NULL DEFAULT NULL COMMENT '是否为推荐商品',
                                   `sales_model` varchar(255)NULL DEFAULT NULL COMMENT '销售模式',
                                   `save_type` varchar(255)NULL DEFAULT NULL COMMENT '草稿商品保存类型',
                                   `self_operated` bit(1) NULL DEFAULT NULL COMMENT '是否自营',
                                   `store_id` varchar(255)NULL DEFAULT NULL COMMENT '店铺ID',
                                   `store_name` varchar(255)NULL DEFAULT NULL COMMENT '店铺名称',
                                   `selling_point` varchar(255)NULL DEFAULT NULL COMMENT '卖点',
                                   `shop_category_path` varchar(255)NULL DEFAULT NULL COMMENT '店铺分类',
                                   `sku_list_json` textNULL COMMENT '货品列表json',
                                   `small` varchar(255)NULL DEFAULT NULL COMMENT '小图路径',
                                   `sn` varchar(30)NULL DEFAULT NULL COMMENT '商品编号',
                                   `template_id` varchar(255)NULL DEFAULT NULL COMMENT '运费模板ID',
                                   `thumbnail` varchar(255)NULL DEFAULT NULL COMMENT '缩略图',
                                   `weight` decimal(10, 2) NULL DEFAULT NULL COMMENT '重量',
                                   `store_category_path` varchar(255)NULL DEFAULT NULL COMMENT '店铺分类路径',
                                   `big` varchar(255)NULL DEFAULT NULL COMMENT '大图路径',
                                   `market_enable` varchar(255)NULL DEFAULT NULL,
                                   `goods_type` varchar(255)NULL DEFAULT NULL,
                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of draft_goods
-- ----------------------------

-- ----------------------------
-- Table structure for feedback
-- ----------------------------
DROP TABLE IF EXISTS `feedback`;
CREATE TABLE `feedback`  (
                                `id` bigint NOT NULL COMMENT 'ID',
                                `create_at` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                `context` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '反馈内容',
                                `images` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图片，多个图片使用：(，)分割',
                                `mobile` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
                                `category` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '类型',
                                `user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '会员名称',
                                PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of feedback
-- ----------------------------

-- ----------------------------
-- Table structure for file
-- ----------------------------
DROP TABLE IF EXISTS `file`;
CREATE TABLE `file`  (
                            `id` bigint NOT NULL COMMENT 'ID',
                            `create_by` varchar(255)NULL DEFAULT NULL COMMENT '创建者',
                            `create_at` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                            `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                            `update_by` varchar(255)NULL DEFAULT NULL COMMENT '更新者',
                            `update_at` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                            `file_key` varchar(255)NULL DEFAULT NULL COMMENT '存储文件名',
                            `file_size` bigint NULL DEFAULT NULL COMMENT '大小',
                            `file_type` varchar(255)NULL DEFAULT NULL COMMENT '文件类型',
                            `name` varchar(255)NULL DEFAULT NULL COMMENT '原文件名',
                            `owner_id` varchar(255)NULL DEFAULT NULL COMMENT '拥有者ID',
                            `url` varchar(255)NULL DEFAULT NULL COMMENT '路径',
                            `user_enums` varchar(255)NULL DEFAULT NULL COMMENT '用户类型',
                            `file_directory_id` varchar(255)NULL DEFAULT NULL COMMENT '文件夹ID',
                            `owner_name` varchar(255)NULL DEFAULT NULL COMMENT '拥有者名称',
                            PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of file
-- ----------------------------

-- ----------------------------
-- Table structure for file_directory
-- ----------------------------
DROP TABLE IF EXISTS `file_directory`;
CREATE TABLE `file_directory`  (
                                      `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                      `create_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                      `create_by` varchar(64)  COMMENT '创建者',
                                      `update_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                      `update_by` varchar(64)  COMMENT '更新者',
                                      `delete_flag` tinyint(1) NULL DEFAULT 0 COMMENT '删除标志 true/false 删除/未删除',
                                      `directory_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件目录类型',
                                      `directory_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '拥有者名称',
                                      `owner_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '拥有者id',
                                      `parent_id` bigint NULL DEFAULT NULL COMMENT '父分类ID',
                                      `level` int NULL DEFAULT NULL COMMENT '层级',
                                      PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1698937596963311619 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文件夹' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of file_directory
-- ----------------------------

-- ----------------------------
-- Table structure for foot_print
-- ----------------------------
DROP TABLE IF EXISTS `foot_print`;
CREATE TABLE `foot_print`  (
                                  `id` bigint NOT NULL COMMENT 'ID',
                                  `create_by` varchar(255)NULL DEFAULT NULL COMMENT '创建者',
                                  `create_at` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                  `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                  `update_by` varchar(255)NULL DEFAULT NULL COMMENT '更新者',
                                  `update_at` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                  `goods_id` bigint COMMENT '商品ID',
                                  `member_id` varchar(255)NULL DEFAULT NULL COMMENT '会员ID',
                                  `sku_id` varchar(255)NULL DEFAULT NULL COMMENT '规格ID',
                                  `store_id` varchar(255)NULL DEFAULT NULL COMMENT '店铺ID',
                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of foot_print
-- ----------------------------

-- ----------------------------
-- Table structure for freight_template
-- ----------------------------
DROP TABLE IF EXISTS `freight_template`;
CREATE TABLE `freight_template`  (
                                        `id` bigint NOT NULL COMMENT 'ID',
                                        `create_by` varchar(255)NULL DEFAULT NULL COMMENT '创建者',
                                        `create_at` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                        `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                        `update_by` varchar(255)NULL DEFAULT NULL COMMENT '更新者',
                                        `update_at` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                        `name` varchar(255)NULL DEFAULT NULL COMMENT '模板名称',
                                        `pricing_method` varchar(255)NULL DEFAULT NULL COMMENT '计价方式 按件、按重量',
                                        `store_id` varchar(255)NULL DEFAULT NULL COMMENT '店铺ID',
                                        PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of freight_template
-- ----------------------------
INSERT INTO `freight_template` VALUES (1376425599173656576, '15810610731', '2021-03-29 01:46:50.000000', b'0', '15810610731', '2021-07-19 11:52:20.386000', '默认运费', 'FREE', '1376364798236688384');
INSERT INTO `freight_template` VALUES (1376434171555086336, '13011111111', '2021-03-29 15:20:54.000000', b'0', '13011111111', '2021-08-06 11:13:13.687000', '默认模板', 'FREE', '1376417684140326912');
INSERT INTO `freight_template` VALUES (1402510797398441985, '13011111111', '2021-06-09 14:20:06.000000', b'0', '13011111111', '2021-07-27 16:02:54.450000', '测试模板', 'FREE', '1376417684140326912');

-- ----------------------------
-- Table structure for freight_template_child
-- ----------------------------
DROP TABLE IF EXISTS `freight_template_child`;
CREATE TABLE `freight_template_child`  (
                                              `id` bigint NOT NULL COMMENT 'ID',
                                              `create_by` varchar(255)NULL DEFAULT NULL COMMENT '创建者',
                                              `create_at` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                              `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                              `update_by` varchar(255)NULL DEFAULT NULL COMMENT '更新者',
                                              `update_at` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                              `area` textNULL COMMENT '地址',
                                              `area_id` textNULL COMMENT '地区ID',
                                              `continued_company` decimal(10, 2) NULL DEFAULT NULL COMMENT '续重/续件',
                                              `continued_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '续费',
                                              `first_company` decimal(10, 2) NULL DEFAULT NULL COMMENT '首重/首件',
                                              `first_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '运费',
                                              `freight_template_id` varchar(255)NULL DEFAULT NULL COMMENT '店铺模板ID',
                                              PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of freight_template_child
-- ----------------------------
INSERT INTO `freight_template_child` VALUES (1376425599370788864, '15810610731', '2021-07-19 11:52:20.466000', b'0', NULL, NULL, '广东省,辽宁省,新疆维吾尔自治区,河南省,内蒙古自治区,湖北省,黑龙江省,上海市,贵州省,重庆市,山东省,陕西省,西藏自治区,青海省,江苏省,安徽省,福建省,湖南省,海南省,宁夏回族自治区,广西壮族自治区,浙江省,河北省,甘肃省,四川省,天津市,云南省,北京市,江西省,吉林省,山西省,', '1401797451504943105,1401797451509137428,1401797451509137475,1401797451509137591,1401797451509137679,1401797451513331786,1401797451513331822,1401797451513331954,1401797451517526076,1401797451517526203,1401797451517526279,1401797451517526386,1401797451517526447,1401797451521720355,1401797451521720502,1401797451521720571,1401797451521720686,1401797451521720813,1401797451521720912,1401797451521720937,1401797451521720973,1401797451521721164,1401797451525914753,1401797451525914915,1401797451525915031,1401797451525915129,1401797451525915209,1401797451525915270,1401797451525915362,1401797451530109011,1401797451530109131,1401797451530109206,1401797451530109262,1401797451530109320,1401797451530109430,1401797451530109607,1401797451534303280,1401797451534303320,1401797451534303323,1401797451534303330,1401797451534303336,1401797451534303341,1401797451534303415,1401797451534303427,1401797451534303559,1401797451534303568,1401797451534303683,1401797451534303813,1401797451534303986,1401797451538497573,1401797451538497580,1401797451538497701,1401797451538497943,1401797451538497963,1401797451538497974,1401797451538498013,1401797451538498063,1401797451538498125,1401797451538498147,1401797451538498153,1401797451538498360,1401797451538498447,1401797451542691900,1401797451542692008,1401797451542692274,1401797451542692507,1401797451542692524,1401797451542692621,1401797451546886255,1401797451546886308,1401797451546886429,1401797451546886645,1401797451546886802,1401797451546886948,1401797451551080489,1401797451551080617,1401797451551080836,1401797451551081043,1401797451551081140,1401797451551081345,1401797451555274828,1401797451555274854,1401797451555274892,1401797451555275042,1401797451555275139,1401797451555275255,1401797451555275354,1401797451555275550,1401797451559469161,1401797451559469257,1401797451559469286,1401797451559469498,1401797451559469637,1401797451559469764,1401797451563663459,1401797451563663637,1401797451563663802,1401797451563663892,1401797451563663918,1401797451563664018,1401797451563664072,1401797451567857715,1401797451567857724,1401797451567857750,1401797451567857888,1401797451567857951,1401797451567857983,1401797451567858045,1401797451567858094,1401797451567858175,1401797451572052077,1401797451572052426,1401797451572052660,1401797451576246484,1401797451576246596,1401797451576246746,1401797451576246867,1401797451576246985,1401797451580440656,1401797451580440759,1401797451580441007,1401797451580441276,1401797451584634937,1401797451584635224,1401797451584635372,1401797451584635468,1401797451584635568,1401797451588829190,1401797451588829423,1401797451588829590,1401797451593023750,1401797451593024145,1401797451593024312,1401797451597217854,1401797451597218000,1401797451597218145,1401797451597218315,1401797451597218412,1401797451597218548,1401797451597218647,1401797451601412123,1401797451601412180,1401797451601412251,1401797451601412428,1401797451601412601,1401797451601412696,1401797451601412841,1401797451601413013,1401797451605606574,1401797451605606771,1401797451605606814,1401797451605606933,1401797451605607061,1401797451605607211,1401797451609800747,1401797451609800878,1401797451609801035,1401797451609801185,1401797451647549540,1401797451647549662,1401797451647549707,1401797451647549781,1401797451651743773,1401797451651743869,1401797451651744093,1401797451651744142,1401797451651744245,1401797451651744299,1401797451651744350,1401797451651744388,1401797451651744444,1401797451651744529,1401797451651744569,1401797451655938108,1401797451655938220,1401797451655938348,1401797451655938516,1401797451655938622,1401797451655938735,1401797451655938803,1401797451660132409,1401797451660132508,1401797451660132602,1401797451660132674,1401797451660132801,1401797451660132984,1401797451664326699,1401797451664326748,1401797451664326786,1401797451664326855,1401797451664326924,1401797451664327040,1401797451664327211,1401797451664327299,1401797451664327406,1401797451668521055,1401797451668521179,1401797451668521282,1401797451668521370,1401797451668521474,1401797451668521627,1401797451668521783,1401797451672715313,1401797451672715485,1401797451672715668,1401797451672715875,1401797451672715934,1401797451672716073,1401797451676909653,1401797451676909814,1401797451676909956,1401797451676910161,1401797451676910250,1401797451681103874,1401797451681104020,1401797451681104239,1401797451681104432,1401797451681104561,1401797451681104636,1401797451681104756,1401797451685298347,1401797451685298522,1401797451685298726,1401797451685298831,1401797451685298927,1401797451685298941,1401797451685298954,1401797451685298972,1401797451685298988,1401797451685299000,1401797451685299015,1401797451689492480,1401797451689492497,1401797451689492510,1401797451689492527,1401797451689492548,1401797451689492572,1401797451689492590,1401797451689492594,1401797451689492609,1401797451689492626,1401797451689492681,1401797451689492695,1401797451689492705,1401797451689492776,1401797451689492827,1401797451689492871,1401797451689492927,1401797451689492995,1401797451689493145,1401797451689493180,1401797451689493258,1401797451693686837,1401797451693686912,1401797451693687076,1401797451693687143,1401797451693687223,1401797451693687374,1401797451693687411,1401797451693687529,1401797451693687609,1401797451697881185,1401797451697881278,1401797451697881319,1401797451697881399,1401797451697881592,1401797451697881735,1401797451702075456,1401797451702075640,1401797451702075797,1401797451702075873,1401797451702075984,1401797451702076092,1401797451706269750,1401797451706270007,1401797451706270130,1401797451706270243,1401797451710464101,1401797451710464361,1401797451710464663,1401797451714658583,1401797451714658814,1401797451714659030,1401797451718852810,1401797451718852975,1401797451718853183,1401797451718853204,1401797451718853332,1401797451723046917,1401797451723047045,1401797451723047145,1401797451723047151,1401797451723047239,1401797451723047352,1401797451723047491,1401797451723047620,1401797451723047758,1401797451727241305,1401797451727241415,1401797451727241574,1401797451727241827,1401797451727241919,1401797451727242062,1401797451731435599,1401797451731435886,1401797451731435981,1401797451731436113,1401797451731436322,1401797451731436409,1401797451735629832,1401797451735629922,1401797451735630069,1401797451735630215,1401797451735630408,1401797451735630461,1401797451735630558,1401797451739824187,1401797451739824293,1401797451739824622,1401797451777573166,1401797451781767390,1401797451781767529,1401797451781767689,1401797451785961527,1401797451785961576,1401797451785961661,1401797451785961695,1401797451785961820,1401797451785961853,1401797451785961967,1401797451785962037,1401797451785962121,1401797451785962235,1401797451790155787,1401797451790155880,1401797451790155937,1401797451790156094,1401797451790156447,1401797451794350142,1401797451794350336,1401797451794350582,1401797451794350647,1401797451794350709,1401797451794350942,1401797451798544462,1401797451798544505,1401797451798544770,1401797451798544836,1401797451798545078,1401797451798545236,1401797451802738716,1401797451802738937,1401797451802739080,1401797451802739122,1401797451802739235,1401797451802739325,1401797451802739457,1401797451806932993,1401797451806933042,1401797451806933165,1401797451806933251,1401797451806933430,1401797451806933587,1401797451811127321,1401797451811127415,1401797451811127566,1401797451811127749,1401797451811127939,', 5.00, 1.00, 1.00, 1.00, '1376425599173656576');
INSERT INTO `freight_template_child` VALUES (1388033691770421248, '13011111111', '2021-08-06 11:13:13.742000', b'0', NULL, NULL, '广东省,新疆维吾尔自治区,贵州省,陕西省,林芝市,山南市,', '1401797451504943105,1401797451509137428,1401797451509137475,1401797451509137591,1401797451509137679,1401797451513331786,1401797451513331822,1401797451513331954,1401797451517526076,1401797451517526203,1401797451517526279,1401797451517526386,1401797451517526447,1401797451521720355,1401797451521720502,1401797451521720571,1401797451521720686,1401797451521720813,1401797451521720912,1401797451521720937,1401797451521720973,1401797451530109607,1401797451534303280,1401797451534303320,1401797451534303323,1401797451534303330,1401797451534303336,1401797451534303341,1401797451534303415,1401797451534303427,1401797451534303559,1401797451534303568,1401797451534303683,1401797451534303813,1401797451534303986,1401797451538497573,1401797451538497580,1401797451538497701,1401797451538497943,1401797451538497963,1401797451538497974,1401797451538498013,1401797451538498063,1401797451538498125,1401797451538498147,1401797451580441007,1401797451580441276,1401797451584634937,1401797451584635224,1401797451584635372,1401797451584635468,1401797451584635568,1401797451588829190,1401797451588829423,1401797451601413013,1401797451605606574,1401797451605606771,1401797451605606814,1401797451605606933,1401797451605607061,1401797451605607211,1401797451609800747,1401797451609800878,1401797451609801035,1401797451647549781,1401797451651743773,', 200.00, 1.00, 100.00, 1.00, '1376434171555086336');
INSERT INTO `freight_template_child` VALUES (1402510797566214146, '13011111111', '2021-07-27 16:02:54.502000', b'0', NULL, NULL, '新疆维吾尔自治区,内蒙古自治区,黑龙江省,西藏自治区,海南省,北京市,', '1401797451530109607,1401797451534303280,1401797451534303320,1401797451534303323,1401797451534303330,1401797451534303336,1401797451534303341,1401797451534303415,1401797451534303427,1401797451534303559,1401797451534303568,1401797451534303683,1401797451534303813,1401797451534303986,1401797451538497573,1401797451538497580,1401797451538497701,1401797451538497943,1401797451538497963,1401797451538497974,1401797451538498013,1401797451538498063,1401797451538498125,1401797451538498147,1401797451551081043,1401797451551081140,1401797451551081345,1401797451555274828,1401797451555274854,1401797451555274892,1401797451555275042,1401797451555275139,1401797451555275255,1401797451555275354,1401797451555275550,1401797451559469161,1401797451567857983,1401797451567858045,1401797451567858094,1401797451567858175,1401797451572052077,1401797451572052426,1401797451572052660,1401797451576246484,1401797451576246596,1401797451576246746,1401797451576246867,1401797451576246985,1401797451580440656,1401797451609801185,1401797451647549540,1401797451647549662,1401797451647549707,1401797451647549781,1401797451651743773,1401797451651743869,1401797451685298927,1401797451685298941,1401797451685298954,1401797451685298972,1401797451685298988,1401797451685299000,1401797451685299015,1401797451689492480,1401797451689492497,1401797451689492510,1401797451689492527,1401797451689492548,1401797451689492572,1401797451689492590,1401797451689492594,1401797451689492609,1401797451689492626,1401797451689492681,1401797451689492695,1401797451790156094,', 1.00, 10.00, 1.00, 10.00, '1402510797398441985');
INSERT INTO `freight_template_child` VALUES (1402510797658488834, '13011111111', '2021-07-27 16:02:54.522000', b'0', NULL, NULL, '河南省,', '1401797451538498153,1401797451538498360,1401797451538498447,1401797451542691900,1401797451542692008,1401797451542692274,1401797451542692507,1401797451542692524,1401797451542692621,1401797451546886255,1401797451546886308,1401797451546886429,1401797451546886645,1401797451546886802,1401797451546886948,1401797451551080489,1401797451551080617,1401797451551080836,', 1.00, 1.00, 1.00, 1.00, '1402510797398441985');

-- ----------------------------
-- Table structure for full_discount
-- ----------------------------
DROP TABLE IF EXISTS `full_discount`;
CREATE TABLE `full_discount`  (
                                     `id` bigint NOT NULL COMMENT 'ID',
                                     `create_by` varchar(255)NULL DEFAULT NULL COMMENT '创建者',
                                     `create_at` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                     `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                     `update_by` varchar(255)NULL DEFAULT NULL COMMENT '更新者',
                                     `update_at` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                     `end_time` datetime(6) NULL DEFAULT NULL COMMENT '活动结束时间',
                                     `promotion_name` varchar(255)NULL DEFAULT NULL COMMENT '活动名称',
                                     `store_id` varchar(255)NULL DEFAULT NULL COMMENT '店铺ID',
                                     `store_name` varchar(255)NULL DEFAULT NULL COMMENT '店铺名称',
                                     `start_time` datetime(6) NULL DEFAULT NULL COMMENT '活动开始时间',
                                     `coupon_id` varchar(255)NULL DEFAULT NULL COMMENT '优惠券ID',
                                     `description` varchar(255)NULL DEFAULT NULL COMMENT '活动说明',
                                     `full_minus` decimal(10, 2) NULL DEFAULT NULL COMMENT '减现金',
                                     `full_money` decimal(10, 2) NOT NULL COMMENT '优惠门槛金额',
                                     `full_rate` decimal(10, 2) NULL DEFAULT NULL COMMENT '打折',
                                     `gift_id` varchar(255)NULL DEFAULT NULL COMMENT '赠品ID',
                                     `coupon_flag` bit(1) NULL DEFAULT NULL COMMENT '是否赠优惠券',
                                     `free_freight_flag` bit(1) NULL DEFAULT NULL COMMENT '是否包邮',
                                     `full_minus_flag` bit(1) NULL DEFAULT NULL COMMENT '活动是否减现金',
                                     `full_rate_flag` bit(1) NULL DEFAULT NULL COMMENT '是否打折',
                                     `gift_flag` bit(1) NULL DEFAULT NULL COMMENT '是否有赠品',
                                     `point_flag` bit(1) NULL DEFAULT NULL COMMENT '是否赠送积分',
                                     `point` int NULL DEFAULT NULL COMMENT '赠送多少积分',
                                     `title` varchar(255)NULL DEFAULT NULL COMMENT '活动标题',
                                     `scope_id` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '范围关联的ID',
                                     `scope_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'PORTION_GOODS' COMMENT '关联范围类型',
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of full_discount
-- ----------------------------
INSERT INTO `full_discount` VALUES (1393830445262569472, '13011111111', '2021-05-16 15:27:28.000000', b'0', '13011111111', '2021-05-16 15:38:50.183000', '2021-05-23 00:00:00.000000', '试试', '1376433565247471616', '生活百货', '2021-05-16 16:00:00.000000', NULL, '100-10', 10.00, 100.00, NULL, NULL, NULL, b'1', b'1', NULL, NULL, NULL, NULL, '满100.0 减10.0', NULL, 'PORTION_GOODS');
INSERT INTO `full_discount` VALUES (1402945304551251969, '13011111111', '2021-06-10 19:06:40.000000', b'0', '13011111111', '2021-06-10 19:07:17.015000', '2021-06-12 00:00:00.000000', '123', '1376433565247471616', '生活百货', '2021-06-10 19:08:00.000000', NULL, '123', 12.00, 55.00, 8.50, '1377066518889627648', b'0', b'1', b'1', b'1', b'1', b'1', 11, '满55.0 减12.0', NULL, 'PORTION_GOODS');
INSERT INTO `full_discount` VALUES (1428511096822927361, '15810610731', '2021-08-20 08:16:00.594000', b'0', NULL, NULL, '2021-08-21 00:00:00.000000', '满额活动2021-8-20', '1376369067769724928', 'Lilishop自营店', '2021-08-20 08:20:00.000000', NULL, '活动描述', 10.00, 100.00, NULL, NULL, NULL, NULL, b'1', NULL, NULL, NULL, NULL, '满100.0 减10.0', NULL, 'PORTION_GOODS');
INSERT INTO `full_discount` VALUES (1438708787704037378, 'admin', '2021-09-17 11:37:59.563000', b'0', 'admin', '2021-09-26 15:15:49.314000', '2021-10-28 00:00:00.000000', '1111', '1413749887712206849', '君和', '2021-09-18 00:00:00.000000', NULL, '1111', 111.00, 1111.00, NULL, NULL, b'0', b'1', b'1', b'0', b'0', b'0', NULL, '满1111.0 减111.0', NULL, 'PORTION_GOODS');
INSERT INTO `full_discount` VALUES (1438770386032717826, 'admin', '2021-09-17 15:42:45.000000', b'0', 'admin', '2021-09-17 16:36:30.687000', '2021-10-31 00:00:00.000000', '0000', '1413749887712206849', '君和', '2021-10-30 00:00:00.000000', NULL, '1111111', 111.00, 111.00, NULL, NULL, b'0', b'1', b'1', b'0', b'0', b'0', NULL, '满111.0 减111.0', NULL, 'PORTION_GOODS');
INSERT INTO `full_discount` VALUES (1465936750249865217, '13011111111', '2021-12-01 14:52:11.705000', b'0', NULL, NULL, '2021-12-18 00:00:00.000000', '测试测试', '1376433565247471616', '家家乐', '2021-12-03 00:00:00.000000', NULL, 'test', 25.00, 100.00, NULL, NULL, b'0', b'1', b'1', b'0', b'0', b'0', NULL, '满100.0 减25.0', NULL, 'PORTION_GOODS');



-- ----------------------------
-- Records of goods_words
-- ----------------------------

-- ----------------------------
-- Table structure for hot_words_history
-- ----------------------------
DROP TABLE IF EXISTS `hot_words_history`;
CREATE TABLE `hot_words_history`  (
                                         `id` bigint NOT NULL COMMENT 'ID',
                                         `create_at` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                         `keywords` varchar(255)  COMMENT '热词',
                                         `score` int NULL DEFAULT NULL COMMENT '热词分数',
                                         PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin COMMENT = '热词历史表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of hot_words_history
-- ----------------------------

-- ----------------------------
-- Table structure for im_message
-- ----------------------------
DROP TABLE IF EXISTS `im_message`;
CREATE TABLE `im_message`  (
                                  `id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                  `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建者',
                                  `create_at` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                  `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标识',
                                  `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '修改者',
                                  `update_at` datetime(6) NULL DEFAULT NULL COMMENT '修改标识',
                                  `from_user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '发送用户Id',
                                  `to_user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '接收用户Id',
                                  `is_read` bit(1) NULL DEFAULT NULL COMMENT '已读标识',
                                  `message_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '聊天类型',
                                  `text` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '聊天内容',
                                  `talk_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '聊天Id',
                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of im_message
-- ----------------------------

-- ----------------------------
-- Table structure for im_talk
-- ----------------------------
DROP TABLE IF EXISTS `im_talk`;
CREATE TABLE `im_talk`  (
                               `id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                               `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建者',
                               `create_at` datetime NULL DEFAULT NULL COMMENT '创建时间',
                               `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志',
                               `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '修改者',
                               `update_at` datetime(6) NULL DEFAULT NULL COMMENT '修改时间',
                               `user_id1` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户1Id',
                               `user_id2` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户2Id',
                               `name1` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户1名称',
                               `name2` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户2名称',
                               `face1` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户1头像',
                               `face2` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户2头像',
                               `top1` bit(1) NULL DEFAULT NULL COMMENT '用户1置顶标识',
                               `top2` bit(1) NULL DEFAULT NULL COMMENT '用户2置顶标识',
                               `disable1` bit(1) NULL DEFAULT NULL COMMENT '用户1禁用标识',
                               `disable2` bit(1) NULL DEFAULT NULL COMMENT '用户2禁用标识',
                               `store_flag1` bit(1) NULL DEFAULT NULL COMMENT '用户1店铺标识',
                               `store_flag2` bit(1) NULL DEFAULT NULL COMMENT '用户2店铺标识',
                               `last_talk_time` datetime NULL DEFAULT NULL COMMENT '最后聊天时间',
                               `last_talk_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '最后的消息',
                               `last_message_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '最后消息类型',
                               `talk_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '聊天Id',
                               `tenant_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '坐席Id',
                               `tenant_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '坐席名称',
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of im_talk
-- ----------------------------

-- ----------------------------
-- Table structure for kanjia_activity
-- ----------------------------
DROP TABLE IF EXISTS `kanjia_activity`;
CREATE TABLE `kanjia_activity`  (
                                       `id` bigint NOT NULL,
                                       `create_by` varchar(255) ,
                                       `create_at` datetime(6) NULL DEFAULT NULL,
                                       `delete_flag` bit(1) NULL DEFAULT NULL,
                                       `update_by` varchar(255) ,
                                       `update_at` datetime(6) NULL DEFAULT NULL,
                                       `goods_name` varchar(255) ,
                                       `kanjia_activity_goods_id` varchar(255) ,
                                       `member_id` varchar(255) ,
                                       `member_name` varchar(255) ,
                                       `purchase_price` decimal(10, 2) NULL DEFAULT NULL,
                                       `sku_id` varchar(255) ,
                                       `status` varchar(255) ,
                                       `surplus_price` decimal(10, 2) NULL DEFAULT NULL,
                                       `thumbnail` varchar(255) ,
                                       PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of kanjia_activity
-- ----------------------------

-- ----------------------------
-- Table structure for kanjia_activity_goods
-- ----------------------------
DROP TABLE IF EXISTS `kanjia_activity_goods`;
CREATE TABLE `kanjia_activity_goods`  (
                                             `id` bigint NOT NULL,
                                             `create_by` varchar(255) ,
                                             `create_at` datetime(6) NULL DEFAULT NULL,
                                             `delete_flag` bit(1) NULL DEFAULT NULL,
                                             `update_by` varchar(255) ,
                                             `update_at` datetime(6) NULL DEFAULT NULL,
                                             `end_time` datetime(6) NULL DEFAULT NULL,
                                             `promotion_name` varchar(255) ,
                                             `start_time` datetime(6) NULL DEFAULT NULL,
                                             `store_id` varchar(255) ,
                                             `store_name` varchar(255) ,
                                             `goods_name` varchar(255) ,
                                             `highest_price` decimal(10, 2) NULL DEFAULT NULL,
                                             `lowest_price` decimal(10, 2) NULL DEFAULT NULL,
                                             `original_price` decimal(10, 2) NULL DEFAULT NULL,
                                             `purchase_price` decimal(10, 2) NULL DEFAULT NULL,
                                             `settlement_price` decimal(10, 2) NULL DEFAULT NULL,
                                             `sku_id` varchar(255) ,
                                             `stock` int NULL DEFAULT NULL,
                                             `thumbnail` varchar(255) ,
                                             `scope_id` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '范围关联的ID',
                                             `scope_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'PORTION_GOODS' COMMENT '关联范围类型',
                                             `goods_id` varchar(255) ,
                                             PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of kanjia_activity_goods
-- ----------------------------

-- ----------------------------
-- Table structure for kanjia_activity_log
-- ----------------------------
DROP TABLE IF EXISTS `kanjia_activity_log`;
CREATE TABLE `kanjia_activity_log`  (
                                           `id` bigint NOT NULL,
                                           `create_by` varchar(255) ,
                                           `create_at` datetime(6) NULL DEFAULT NULL,
                                           `delete_flag` bit(1) NULL DEFAULT NULL,
                                           `update_by` varchar(255) ,
                                           `update_at` datetime(6) NULL DEFAULT NULL,
                                           `kanjia_activity_id` varchar(255) ,
                                           `kanjia_member_face` varchar(255) ,
                                           `kanjia_member_id` varchar(255) ,
                                           `kanjia_member_name` varchar(255) ,
                                           `kanjia_price` decimal(10, 2) NULL DEFAULT NULL,
                                           `surplus_price` decimal(10, 2) NULL DEFAULT NULL,
                                           PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of kanjia_activity_log
-- ----------------------------

-- ----------------------------
-- Table structure for logistics
-- ----------------------------
DROP TABLE IF EXISTS `logistics`;
CREATE TABLE `logistics`  (
                                 `id` bigint NOT NULL COMMENT 'ID',
                                 `create_by` varchar(255)  COMMENT '创建者',
                                 `create_at` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                 `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                 `update_by` varchar(255)  COMMENT '更新者',
                                 `update_at` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                 `code` varchar(255)  COMMENT '物流公司code',
                                 `disabled` varchar(255)  COMMENT '禁用状态',
                                 `form_items` varchar(255)  COMMENT '物流公司电子面单表单',
                                 `name` varchar(255)  COMMENT '物流公司名称',
                                 `stand_by` varchar(255)  COMMENT '支持电子面单',
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of logistics
-- ----------------------------
INSERT INTO `logistics` VALUES (1364825407932596224, 'admin', '2021-02-25 06:31:49.181000', b'0', 'admin', '2021-07-30 16:34:01.851000', 'SF', 'OPEN', 'SF', '顺丰速运', 'null');
INSERT INTO `logistics` VALUES (1364825461946843136, 'admin', '2021-02-25 06:32:02.017000', b'0', 'admin', '2021-07-26 15:29:42.125000', 'HTKY', 'OPEN', 'HTKY', '百世快递', 'null');
INSERT INTO `logistics` VALUES (1364825604276355072, 'admin', '2021-02-25 06:32:35.950000', b'0', 'admin', '2021-07-22 11:13:50.968000', 'YTO', 'OPEN', 'YTO', '圆通速递', 'true');
INSERT INTO `logistics` VALUES (1364825661964812288, 'admin', '2021-02-25 06:32:49.704000', b'0', 'admin', '2021-07-22 11:13:46.810000', 'YD', 'OPEN', 'YD', '韵达速递', 'null');
INSERT INTO `logistics` VALUES (1364825703807188992, 'admin', '2021-02-25 06:32:59.681000', b'0', 'admin', '2021-07-21 13:16:32.073000', 'STO', 'OPEN', 'STO', '申通快递', 'null');
INSERT INTO `logistics` VALUES (1364825753534857216, 'admin', '2021-02-25 06:33:11.537000', b'0', 'admin', '2021-07-21 13:16:29.948000', 'YZPY', 'OPEN', 'YZPY', '邮政快递包裹', 'null');
INSERT INTO `logistics` VALUES (1364825783545102336, 'admin', '2021-02-25 06:33:18.691000', b'0', 'admin', '2021-07-21 15:34:26.353000', 'EMS', 'OPEN', 'EMS', 'EMS', 'null');
INSERT INTO `logistics` VALUES (1364825828369629184, 'admin', '2021-02-25 06:33:29.379000', b'0', 'admin', '2021-07-20 12:06:31.990000', 'HHTT', 'OPEN', 'HHTT', '天天快递', 'null');
INSERT INTO `logistics` VALUES (1364825870564327424, 'admin', '2021-02-25 06:33:39.439000', b'0', 'admin', '2021-06-21 00:31:45.307000', 'JD', 'OPEN', 'JD', '京东快递', 'null');
INSERT INTO `logistics` VALUES (1364825911689478144, 'admin', '2021-02-25 06:33:49.243000', b'0', 'admin', '2021-03-07 19:32:19.660000', 'UC', 'OPEN', 'UC', '优速快递', 'null');
INSERT INTO `logistics` VALUES (1364825959177388032, 'admin', '2021-02-25 06:34:00.565000', b'0', 'admin', '2021-08-03 00:27:06.688000', 'DBL', 'OPEN', 'DBL', '德邦快递', 'false');
INSERT INTO `logistics` VALUES (1425472379076792321, 'admin', '2021-08-11 23:01:13.829000', b'0', NULL, NULL, 'ZTO', 'OPEN', NULL, '中通', NULL);



-- ----------------------------
-- Records of member_withdraw_apply
-- ----------------------------

-- ----------------------------
-- Table structure for menu
-- ----------------------------
DROP TABLE IF EXISTS `menu`;
CREATE TABLE `menu`  (
                            `id` bigint NOT NULL COMMENT 'ID',
                            `create_by` varchar(255)NULL DEFAULT NULL COMMENT '创建者',
                            `create_at` datetime NULL DEFAULT NULL COMMENT '创建时间',
                            `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                            `update_by` varchar(255)NULL DEFAULT NULL COMMENT '更新者',
                            `update_at` datetime NULL DEFAULT NULL COMMENT '更新时间',
                            `description` varchar(255)NULL DEFAULT NULL COMMENT '说明备注',
                            `front_route` varchar(255)NULL DEFAULT NULL COMMENT '前端路由',
                            `icon` varchar(255)NULL DEFAULT NULL COMMENT '图标',
                            `level` int NULL DEFAULT NULL COMMENT '层级',
                            `name` varchar(255)NULL DEFAULT NULL COMMENT '菜单/权限名称',
                            `parent_id` varchar(255)NULL DEFAULT NULL COMMENT '父id',
                            `path` varchar(255)NULL DEFAULT NULL COMMENT '赋权API地址,正则表达式',
                            `sort_order` decimal(10, 2) NULL DEFAULT NULL COMMENT '排序值',
                            `title` varchar(255)NULL DEFAULT NULL COMMENT '菜单标题',
                            `front_component` varchar(255)NULL DEFAULT NULL COMMENT '文件地址',
                            `permission` varchar(255)NULL DEFAULT NULL COMMENT '权限url',
                            PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of menu
-- ----------------------------
INSERT INTO `menu` VALUES (1348810750596767744, 'admin', '2021-01-12 09:55:17', b'0', 'admin', '2021-01-15 09:42:50', 'null', '1', 'ios-american-football', 0, 'settings', '0', '1', 2.00, '设置', 'null', NULL);
INSERT INTO `menu` VALUES (1348810864748945408, 'admin', '2021-01-12 09:55:45', b'0', 'admin', '2021-03-15 20:57:12', 'null', 'null', 'ios-american-football', 0, 'log', '0', 'null', 3.00, '日志', 'null', '/manager/setting/log*');
INSERT INTO `menu` VALUES (1349237129847005184, 'admin', '2021-01-13 14:09:34', b'0', 'admin', '2021-01-15 09:43:16', 'null', 'Main', 'ios-american-football', 1, 'sys', '1348810750596767744', '/sys', 1.00, '系统设置', 'null', NULL);
INSERT INTO `menu` VALUES (1349237207378714624, 'admin', '2021-01-13 14:09:53', b'0', 'admin', '2021-07-27 16:07:49', 'null', 'Main', 'ios-american-football', 1, 'member', '1348810750596767744', '/member', 0.00, '用户管理', 'null', '/manager/permission/department*,/manager/passport/user*,/manager/permission/role*,/manager/permission/menu*,/manager/passport/user/admin/edit*');
INSERT INTO `menu` VALUES (1349237928434098176, 'admin', '2021-01-13 14:13:03', b'0', 'admin', '2021-07-27 16:09:11', 'null', 'Main', 'ios-american-football', 1, 'log', '1348810864748945408', '/log', 1.00, '系统监控', 'null', '/manager/log*');
INSERT INTO `menu` VALUES (1349246048900243456, 'admin', '2021-01-13 14:45:00', b'0', 'admin', '2021-07-27 16:07:57', 'null', 'sys/setting-manage/settingManage', 'ios-american-football', 2, 'setting', '1349237129847005184', 'setting', 1.00, '系统设置', 'null', '/manager/system/setting/get*,/manager/system/setting/put*,/manager/setting/setting*');
INSERT INTO `menu` VALUES (1349246347597602816, 'admin', '2021-01-13 14:46:12', b'0', 'admin', '2021-07-27 16:08:03', 'null', 'sys/oss-manage/ossManage', 'ios-american-football', 2, 'oss-manage', '1349237129847005184', 'oss-manage', 3.00, 'OSS资源', '', '/manager/common/file*');
INSERT INTO `menu` VALUES (1349246468775239680, 'admin', '2021-01-13 14:46:41', b'0', 'admin', '2021-07-27 16:08:14', 'null', 'region/index', 'ios-american-football', 2, 'region', '1349237129847005184', 'region', 4.00, '行政地区', 'null', '/manager/region*');
INSERT INTO `menu` VALUES (1349246671158796288, 'admin', '2021-01-13 14:47:29', b'0', 'admin', '2021-07-27 16:08:09', 'null', 'logistics/index', 'ios-american-football', 2, 'logistics', '1349237129847005184', 'logistics', 5.00, '物流公司', 'null', '/manager/other/logistics*');
INSERT INTO `menu` VALUES (1349246896661356544, 'admin', '2021-01-13 14:48:23', b'0', 'admin', '2021-07-27 16:08:23', 'null', 'sys/setting-manage/settingManage', 'ios-american-football', 2, 'authLogin', '1349237129847005184', 'authLogin', 6.00, '信任登录', 'null', '/manager/system/setting/get*,/manager/system/setting/put*,/manager/setting/setting*\r\n');
INSERT INTO `menu` VALUES (1349247081504333824, 'admin', '2021-01-13 14:49:07', b'0', 'admin', '2021-07-27 16:08:45', 'null', 'sys/setting-manage/settingManage', 'ios-american-football', 2, 'pay', '1349237129847005184', 'pay', 7.00, '支付设置', 'null', '/manager/system/setting/get*,/manager/system/setting/put*,/manager/system/setting*');
INSERT INTO `menu` VALUES (1349247640584085504, 'admin', '2021-01-13 14:51:20', b'0', 'admin', '2021-07-27 16:08:56', 'null', 'sensitive-words/index', 'ios-american-football', 2, 'sensitive-words', '1349237129847005184', 'sensitive-words', 8.00, '敏感词', 'null', '/manager/other/sensitiveWords*');
INSERT INTO `menu` VALUES (1349254815809298432, 'admin', '2021-01-13 15:19:51', b'0', 'admin', '2021-01-15 11:15:40', 'null', 'sys/user-manage/userManage', 'ios-american-football', 2, 'user-manage', '1349237207378714624', 'user-manage', 1.00, '用户管理', 'null', NULL);
INSERT INTO `menu` VALUES (1349255214977015808, 'admin', '2021-01-13 15:21:26', b'0', 'admin', '2021-01-15 11:16:21', 'null', 'sys/department-manage/departmentManage', 'ios-american-football', 2, 'department-manage', '1349237207378714624', 'department-manage', 3.00, '部门管理', 'null', '/manager/permission/department*,/manager/permission/departmentRole*,');
INSERT INTO `menu` VALUES (1349255404425338880, 'admin', '2021-01-13 15:22:11', b'0', 'admin', '2021-02-24 09:22:21', 'null', 'sys/role-manage/roleManage', 'ios-american-football', 2, 'role-manage', '1349237207378714624', 'role-manage', 4.00, '角色权限', 'null', '/manager/permission/role*,/manager/permission/roleMenu*');
INSERT INTO `menu` VALUES (1349256082979840000, 'admin', '2021-01-13 15:24:53', b'0', 'admin', '2021-01-15 11:18:14', 'null', 'sys/log-manage/logManage', 'ios-american-football', 2, 'log-manage', '1349237928434098176', 'log-manage', 2.00, '日志管理', 'null', NULL);
INSERT INTO `menu` VALUES (1357584224760102912, 'admin', '2021-02-05 06:57:57', b'0', 'admin', '2021-07-27 16:09:02', 'null', 'sys/app-version/appVersion', 'ios-american-football', 2, 'appVersion', '1349237129847005184', 'appVersion', 9.00, 'APP版本', 'null', '/manager/other/appVersion*');
INSERT INTO `menu` VALUES (1357873097859923969, 'admin', '2021-02-24 09:53:02', b'0', 'admin', '2021-02-24 09:53:12', NULL, 'sys/menu-manage/menuManage', 'ios-american-football', 2, 'menuManage', '1349237207378714624', 'menu-manage', 2.00, '菜单管理', NULL, NULL);
INSERT INTO `menu` VALUES (1367038467288072192, 'admin', '2021-03-03 09:05:44', b'0', 'admin', '2021-03-03 09:09:27', 'null', 'null', 'ios-person-add', 0, 'member', '0', 'null', 0.00, '会员', 'null', NULL);
INSERT INTO `menu` VALUES (1367039534616805376, 'admin', '2021-03-03 09:09:58', b'0', 'admin', '2021-05-18 10:51:12', 'null', 'null', 'md-reorder', 0, 'order', '0', 'null', 0.00, '订单', 'null', NULL);
INSERT INTO `menu` VALUES (1367039950368800768, 'admin', '2021-03-03 09:11:37', b'0', NULL, NULL, NULL, NULL, 'ios-share', 0, 'goods', '0', NULL, 0.20, '商品', NULL, NULL);
INSERT INTO `menu` VALUES (1367040067201138688, 'admin', '2021-03-03 09:12:05', b'0', 'admin', '2021-12-02 19:45:22', NULL, 'null', 'ios-hammer', 0, 'promotions', '0', 'null', 0.30, '促销', NULL, 'null');
INSERT INTO `menu` VALUES (1367040599596728320, 'admin', '2021-03-03 09:14:12', b'0', 'admin', '2021-03-03 09:52:13', 'null', 'null', 'ios-color-palette', 0, 'operate', '0', 'null', 0.50, '运营', 'null', NULL);
INSERT INTO `menu` VALUES (1367040819248234496, 'admin', '2021-03-03 09:15:04', b'0', 'liftyy', '2022-03-01 15:13:04', NULL, 'null', 'ios-stats', 0, 'statistics', '0', 'null', 0.70, '统计', NULL, 'null');
INSERT INTO `menu` VALUES (1367041332861730816, 'admin', '2021-03-03 09:17:07', b'0', NULL, NULL, NULL, 'Main', 'ios-aperture', 1, '/', '1367038467288072192', '/', 0.00, '会员管理', NULL, NULL);
INSERT INTO `menu` VALUES (1367041461194850304, 'admin', '2021-03-03 09:17:37', b'0', 'admin', '2021-07-27 16:02:17', NULL, 'member/list/index', 'ios-aperture', 2, 'memberList', '1367041332861730816', 'memberList', 0.00, '会员列表', NULL, '/manager/common/file*,/manager/passport/member*');
INSERT INTO `menu` VALUES (1367041575619657728, 'admin', '2021-03-03 09:18:05', b'0', 'admin', '2021-07-27 15:59:50', NULL, 'member/list/memberRecycle', 'ios-aperture', 2, 'memberRecycle', '1367041332861730816', 'memberRecycle', 1.00, '回收站', NULL, '/manager/member*');
INSERT INTO `menu` VALUES (1367042490443497472, 'admin', '2021-03-03 09:21:43', b'0', NULL, NULL, NULL, 'Main', 'ios-aperture', 1, '/', '1367038467288072192', '/', 1.00, '预存款', NULL, NULL);
INSERT INTO `menu` VALUES (1367042664410644480, 'admin', '2021-03-03 09:22:24', b'0', 'admin', '2021-07-27 16:02:44', 'null', 'member/advance/walletLog', 'ios-aperture', 2, 'walletLog', '1367042490443497472', 'walletLog', 0.00, '会员资金', 'null', '/manager/wallet/log*');
INSERT INTO `menu` VALUES (1367042804944994304, 'admin', '2021-03-03 09:22:58', b'0', 'admin', '2021-07-27 16:02:48', NULL, 'member/advance/recharge', 'ios-alert', 2, 'recharge', '1367042490443497472', 'recharge', 1.00, '充值记录', NULL, '/manager/wallet/recharge*');
INSERT INTO `menu` VALUES (1367042804944994305, 'admin', '2021-03-03 09:22:58', b'0', 'admin', '2021-07-27 16:02:52', NULL, 'member/advance/withdrawApply', 'ios-alert', 2, 'withdrawApply', '1367042490443497472', 'withdrawApply', 1.00, '提现申请', NULL, '/manager/wallet/withdrawApply*');
INSERT INTO `menu` VALUES (1367042917113266176, 'admin', '2021-03-03 09:23:25', b'0', NULL, NULL, NULL, 'Main', 'ios-aperture', 1, 'commont', '1367038467288072192', '/', 0.00, '评价', NULL, NULL);
INSERT INTO `menu` VALUES (1367043020976816128, 'admin', '2021-03-03 09:23:49', b'0', 'admin', '2021-07-27 16:02:35', NULL, 'goods/goods-review/index', 'md-aperture', 2, 'goodsReview', '1367042917113266176', 'goodsReview', 0.00, '会员评价', NULL, '/manager/memberEvaluation*');
INSERT INTO `menu` VALUES (1367043443917848576, 'admin', '2021-03-03 09:25:30', b'0', 'admin', '2021-07-27 16:03:00', NULL, 'Main', 'md-aperture', 1, 'order', '1367039534616805376', '/', 0.00, '订单', NULL, '/manager/orders*');
INSERT INTO `menu` VALUES (1367043505771249664, 'admin', '2021-03-03 09:25:45', b'0', NULL, NULL, NULL, 'Main', 'md-aperture', 1, 'aftersale', '1367039534616805376', '/', 0.00, '售后', NULL, NULL);
INSERT INTO `menu` VALUES (1367043642379730944, 'admin', '2021-03-03 09:26:17', b'0', NULL, NULL, NULL, 'order/order/orderList', 'ios-aperture', 2, 'orderList', '1367043443917848576', 'orderList', 0.00, '商品订单', NULL, '/manager/order/order*');
INSERT INTO `menu` VALUES (1367043791105556480, 'admin', '2021-03-03 09:26:53', b'0', NULL, NULL, NULL, 'order/order/fictitiousOrderList', 'ios-aperture', 2, 'fictitiousOrderList', '1367043443917848576', 'fictitiousOrderList', 1.00, '虚拟订单', NULL, '/manager/order/order*');
INSERT INTO `menu` VALUES (1367043980407078912, 'admin', '2021-03-03 09:27:38', b'0', 'admin', '2021-07-27 16:03:43', NULL, 'order/after-order/afterSaleOrder', 'md-alert', 2, 'afterSaleOrder', '1367043505771249664', 'afterSaleOrder', 0.00, '售后管理', NULL, '/manager/order/afterSale*');
INSERT INTO `menu` VALUES (1367044121163726848, 'admin', '2021-03-03 09:28:12', b'0', 'admin', '2021-07-27 16:03:48', NULL, 'order/after-order/orderComplaint', 'md-alert', 2, 'orderComplaint', '1367043505771249664', 'orderComplaint', 2.00, '交易投诉', NULL, '/manager/order/complain*');
INSERT INTO `menu` VALUES (1367044247978508288, 'admin', '2021-03-03 09:28:42', b'0', 'admin', '2021-07-27 16:03:52', NULL, 'order/after-order/afterSale', 'md-aperture', 2, 'afterSaleReason', '1367043505771249664', 'afterSaleReason', 3.00, '售后原因', NULL, '/manager/order/afterSaleReason*');
INSERT INTO `menu` VALUES (1367044376391319552, 'admin', '2021-03-03 09:29:12', b'0', 'admin', '2021-07-27 16:04:08', NULL, 'Main', 'md-aperture', 1, 'goodsManager', '1367039950368800768', '/', 0.00, '商品管理', NULL, '/manager/goods*');
INSERT INTO `menu` VALUES (1367044657296441344, 'admin', '2021-03-03 09:30:19', b'0', NULL, NULL, NULL, 'Main', 'ios-aperture', 1, 'association', '1367039950368800768', '/', 1.00, '关联管理', NULL, NULL);
INSERT INTO `menu` VALUES (1367045529720061952, 'admin', '2021-03-03 09:33:47', b'0', 'admin', '2021-07-27 15:38:46', NULL, 'goods/goods-info/goods', 'md-aperture', 2, 'managerGoods', '1367044376391319552', 'managerGoods', 0.00, '平台商品', NULL, 'null');
INSERT INTO `menu` VALUES (1367045630710513664, 'admin', '2021-03-03 09:34:11', b'0', 'admin', '2021-07-27 15:38:56', NULL, 'goods/goods-info/goodsApply', 'ios-alert', 2, 'applyGoods', '1367044376391319552', 'applyGoods', 1.00, '商品审核', NULL, 'null');
INSERT INTO `menu` VALUES (1367045794284175360, 'admin', '2021-03-03 09:34:50', b'0', 'admin', '2021-07-27 16:04:18', NULL, 'goods/goods-manage/category', 'md-alert', 2, 'goodsCategory', '1367044657296441344', 'goodsCategory', 0.00, '商品分类', NULL, '/manager/goods/category*,/manager/goods/brand*,/manager/goods/spec*,/manager/goods/parameters*');
INSERT INTO `menu` VALUES (1367045921434501120, 'admin', '2021-03-03 09:35:21', b'0', 'admin', '2021-07-27 16:04:23', NULL, 'goods/goods-manage/brand', 'md-alert', 2, 'goodsBrand', '1367044657296441344', 'goodsBrand', 1.00, '品牌列表', NULL, '/manager/goods/brand*');
INSERT INTO `menu` VALUES (1367046068369358848, 'admin', '2021-03-03 09:35:56', b'0', 'admin', '2021-07-27 16:04:27', NULL, 'goods/goods-manage/spec', 'md-aperture', 2, 'goodsSpec', '1367044657296441344', 'goodsSpec', 2.00, '规格列表', NULL, '/manager/goods/spec*');
INSERT INTO `menu` VALUES (1367046266214678528, 'admin', '2021-03-03 09:36:43', b'0', 'admin', '2021-07-27 16:04:32', NULL, 'goods-unit/index', 'md-alert', 2, 'goodsUnit', '1367044657296441344', 'goodsUnit', 4.00, '计量单位', NULL, '/manager/goods/goodsUnit*');
INSERT INTO `menu` VALUES (1367048084701315072, 'admin', '2021-03-03 09:43:57', b'0', 'admin', '2021-03-03 09:52:17', 'null', 'null', 'ios-pricetags', 0, 'shop', '0', 'null', 0.40, '店铺', 'null', NULL);
INSERT INTO `menu` VALUES (1367048684339986432, 'admin', '2021-03-03 09:46:20', b'0', NULL, NULL, NULL, 'Main', 'md-aperture', 1, 'shopManager', '1367048084701315072', '/', 0.00, '店铺管理', NULL, NULL);
INSERT INTO `menu` VALUES (1367048754229673984, 'admin', '2021-03-03 09:46:36', b'0', NULL, NULL, NULL, 'Main', 'md-aperture', 1, 'bill', '1367048084701315072', '/', 0.00, ' 店铺结算', NULL, NULL);
INSERT INTO `menu` VALUES (1367048832210173952, 'admin', '2021-03-03 09:46:55', b'0', 'admin', '2021-07-27 16:05:30', NULL, 'seller/shop/shopList', 'md-aperture', 2, 'shopList', '1367048684339986432', 'shopList', 0.00, '店铺列表', NULL, '/manager/order/order*,/manager/store*');
INSERT INTO `menu` VALUES (1367048967635861504, 'admin', '2021-03-03 09:47:27', b'0', 'admin', '2021-07-27 16:05:32', NULL, 'seller/shop/shopAuditList', 'md-alert', 2, 'shopAuth', '1367048684339986432', 'shopAuth', 1.00, '店铺审核', NULL, '/manager/store*');
INSERT INTO `menu` VALUES (1367049068122996736, 'admin', '2021-03-03 09:47:51', b'0', 'admin', '2021-07-27 16:05:36', NULL, 'seller/bill/bill', 'md-alert', 2, 'billList', '1367048754229673984', 'billList', 0.00, '店铺结算', NULL, '/manager/order/bill*');
INSERT INTO `menu` VALUES (1367049214198022144, 'admin', '2021-03-03 09:48:26', b'0', 'admin', '2021-12-02 19:45:28', NULL, 'Main', 'md-aperture', 1, 'promotionsManager', '1367040067201138688', '/', 0.00, '促销管理', NULL, 'null');
INSERT INTO `menu` VALUES (1367049384792948736, 'admin', '2021-03-03 09:49:07', b'0', 'admin', '2021-12-02 19:54:12', NULL, 'promotions/coupon/coupon', 'md-alert', 2, 'promotions/coupon', '1367049214198022144', 'promotions/coupon', 0.00, '优惠券', NULL, '/manager/promotion/coupon*');
INSERT INTO `menu` VALUES (1367049500782231552, 'admin', '2021-03-03 09:49:34', b'0', 'admin', '2021-12-02 19:41:37', 'null', 'promotions/full-discount/full-discount', 'md-alert', 2, 'promotions/full-discount', '1367049214198022144', 'promotions/full-discount', 1.00, '满额活动', 'null', '/manager/promotion/fullDiscount*');
INSERT INTO `menu` VALUES (1367049611578966016, 'admin', '2021-03-03 09:50:01', b'0', 'admin', '2021-12-02 20:16:10', 'null', 'promotions/seckill/seckill', 'md-alert', 2, 'promotions/seckill', '1367049214198022144', 'promotions/seckill', 2.00, '秒杀活动', 'null', '/manager/promotion/seckill*');
INSERT INTO `menu` VALUES (1367049712657498112, 'admin', '2021-03-03 09:50:25', b'0', 'admin', '2021-12-02 20:22:04', 'null', 'promotions/pintuan/pintuan', 'md-alert', 2, 'promotions/pintuan', '1367049214198022144', 'promotions/pintuan', 3.00, '拼团活动', 'null', '/manager/promotion/pintuan*');
INSERT INTO `menu` VALUES (1367050250249830400, 'admin', '2021-03-03 09:52:33', b'0', 'admin', '2021-03-22 20:38:14', 'null', 'Main', 'md-aperture', 1, 'document', '1367040599596728320', '/', 2.00, '文章管理', 'null', NULL);
INSERT INTO `menu` VALUES (1367050320584114176, 'admin', '2021-03-03 09:52:50', b'0', 'admin', '2021-07-27 16:05:49', NULL, 'Main', 'md-aperture', 1, 'floor', '1367040599596728320', '/', 0.00, '楼层装修', NULL, '/manager/pageData*,/manager/file*,/manager/article-category*,/manager/article*,/manager/promotion*,/manager/goods*,/manager/store*');
INSERT INTO `menu` VALUES (1367050530030878720, 'admin', '2021-03-03 09:53:40', b'0', 'admin', '2022-10-21 11:12:24', 'null', 'page-decoration/floorList', 'md-alert', 2, 'pcFloor', '1367050320584114176', 'page-decoration/pc', 0.00, 'PC端', 'null', '/manager/other/pageData*');
INSERT INTO `menu` VALUES (1367050673312497664, 'admin', '2021-03-03 09:54:14', b'0', 'admin', '2022-10-21 11:13:17', 'null', 'page-decoration/wap/wapList', 'md-aperture', 2, 'wapList', '1367050320584114176', 'page-decoration/wap', 1.00, '移动端', 'null', '/manager/other/pageData*');
INSERT INTO `menu` VALUES (1367050829697122304, 'admin', '2021-03-03 09:54:51', b'0', 'admin', '2021-07-27 16:06:32', 'null', 'page/article-manage/hotWords', 'md-aperture', 2, 'hotKeyWord', '1367050250249830400', 'hotKeyWord', 0.00, '搜索热词', 'null', '/manager/hotwords*');
INSERT INTO `menu` VALUES (1367050939084570624, 'admin', '2021-03-03 09:55:17', b'0', 'admin', '2021-07-27 16:06:38', NULL, 'page/article-manage/ArticleCategory', 'md-aperture', 2, 'article-category', '1367050250249830400', 'article-category', 1.00, '文章分类', NULL, '/manager/other/articleCategory*');
INSERT INTO `menu` VALUES (1367051048232943616, 'admin', '2021-03-03 09:55:43', b'0', 'admin', '2021-07-27 16:06:42', NULL, 'page/article-manage/articleList', 'md-alert', 2, 'articleList', '1367050250249830400', 'articleList', 3.00, '文章管理', NULL, '/manager/other/article*,/manager/other/articleCategory*');
INSERT INTO `menu` VALUES (1367052616634204160, 'admin', '2021-03-03 10:01:57', b'0', 'admin', '2021-07-27 16:07:38', NULL, 'Main', 'md-aperture', 1, 'statistics', '1367040819248234496', '/', 0.00, '统计', NULL, '/manager/store*,/manager/member*');
INSERT INTO `menu` VALUES (1367052705725415424, 'admin', '2021-03-03 10:02:18', b'0', 'admin', '2021-03-11 22:11:05', 'null', 'statistics/member', 'md-alert', 2, 'memberStatistics', '1367052616634204160', 'memberStatistics', 0.00, '会员统计', 'null', NULL);
INSERT INTO `menu` VALUES (1367052805503713280, 'admin', '2021-03-03 10:02:42', b'0', 'admin', '2021-03-11 22:11:14', 'null', 'statistics/order', 'md-alert', 2, 'orderStatistics', '1367052616634204160', 'orderStatistics', 1.00, '订单统计', 'null', NULL);
INSERT INTO `menu` VALUES (1367052915314786304, 'admin', '2021-03-03 10:03:08', b'0', 'admin', '2021-03-11 22:11:23', 'null', 'statistics/goods', 'md-alert', 2, 'goodsStatistics', '1367052616634204160', 'goodsStatistics', 2.00, '商品统计', 'null', NULL);
INSERT INTO `menu` VALUES (1367053087121866752, 'admin', '2021-03-03 10:03:49', b'0', 'admin', '2021-03-11 22:11:34', 'null', 'statistics/traffic', 'md-alert', 2, 'trafficStatistics', '1367052616634204160', 'trafficStatistics', 4.00, '流量统计', 'null', NULL);
INSERT INTO `menu` VALUES (1372807928452481024, 'admin', '2021-03-19 02:11:30', b'0', NULL, NULL, NULL, 'Main', 'ios-aperture', 1, 'flow', '1367039534616805376', '/', 3.00, '流水', NULL, NULL);
INSERT INTO `menu` VALUES (1372808148565360640, 'admin', '2021-03-19 02:12:23', b'0', 'admin', '2021-07-27 16:03:57', NULL, 'order/flow/paymentLog', 'md-alert', 2, 'paymentLog', '1372807928452481024', 'paymentLog', 1.00, '收款记录', NULL, '/manager/order/paymentLog*');
INSERT INTO `menu` VALUES (1372808352295288832, 'admin', '2021-03-19 02:13:11', b'0', 'admin', '2021-07-27 16:04:01', NULL, 'order/flow/refundLog', 'ios-aperture', 2, 'refundLog', '1372807928452481024', 'refundLog', 2.00, '退款流水', NULL, '/manager/order/refundLog*');
INSERT INTO `menu` VALUES (1373166892465782784, 'admin', '2021-03-20 01:57:54', b'0', 'admin', '2021-03-22 20:13:48', 'null', 'Main', 'ios-aperture', 1, '/', '1367038467288072192', '/', 0.00, '积分', 'null', NULL);
INSERT INTO `menu` VALUES (1373167227385151488, 'admin', '2021-03-20 01:59:14', b'0', 'admin', '2021-07-27 16:02:40', 'null', 'member/point/point', 'ios-aperture', 2, 'point', '1373166892465782784', 'point', 0.00, '积分历史', 'null', '/manager/member/memberPointsHistory*');
INSERT INTO `menu` VALUES (1373791578371391488, 'admin', '2021-03-21 19:20:11', b'0', 'admin', '2021-07-27 16:05:38', NULL, 'seller/bill/accountStatementBill', 'md-alert', 2, 'accountStatementBill', '1367048754229673984', 'accountStatementBill', 0.00, '商家对账', NULL, '/manager/order/bill*');
INSERT INTO `menu` VALUES (1374154349697040384, 'admin', '2021-03-22 19:21:42', b'0', 'admin', '2021-07-27 16:06:55', 'null', 'Main', 'md-aperture', 1, 'feedback', '1367040599596728320', '/', 3.00, '意见反馈', 'null', '/manager/other/feedback*');
INSERT INTO `menu` VALUES (1374155741123837952, 'admin', '2021-03-22 19:27:14', b'0', 'admin', '2021-07-27 15:41:40', 'null', 'page/feedback/feedback', 'md-aperture', 2, 'feedback', '1374154349697040384', 'feedback', 0.00, '意见反馈', 'null', 'null');
INSERT INTO `menu` VALUES (1374173575405109248, 'admin', '2021-03-22 20:38:06', b'0', 'admin', '2021-03-22 20:52:58', 'null', 'Main', 'ios-analytics', 1, 'distributionManager', '1367040599596728320', '/', 1.00, '分销管理', 'null', NULL);
INSERT INTO `menu` VALUES (1374177618072436736, 'admin', '2021-03-22 20:54:10', b'0', 'admin', '2021-07-27 16:05:58', 'null', 'distribution/distributionSetting', 'ios-basketball', 2, 'distributionSetting', '1374173575405109248', 'distributionSetting', 0.00, '分销设置', 'null', '/manager/system/setting/put/DISTRIBUTION_SETTING*,/manager/system/setting/get/DISTRIBUTION_SETTING*');
INSERT INTO `menu` VALUES (1374177789581721600, 'admin', '2021-03-22 20:54:51', b'0', 'admin', '2021-07-27 16:06:15', 'null', 'distribution/distributionGoods', 'ios-chatbubbles', 2, 'distributionGoods', '1374173575405109248', 'distributionGoods', 3.00, '分销商品', 'null', '/manager/distribution/goods*');
INSERT INTO `menu` VALUES (1374177910411231232, 'admin', '2021-03-22 20:55:19', b'0', 'admin', '2021-07-27 16:06:20', 'null', 'distribution/distributionOrder', 'ios-cloudy', 2, 'distributionOrder', '1374173575405109248', 'distributionOrder', 4.00, '分销订单', 'null', '/manager/distribution/order*,/manager/store*');
INSERT INTO `menu` VALUES (1374178079181635584, 'admin', '2021-03-22 20:56:00', b'0', 'admin', '2021-07-27 16:06:05', 'null', 'distribution/distributionApply', 'md-egg', 2, 'distributionApply', '1374173575405109248', 'distributionApply', 1.00, '分销申请', 'null', '/manager/distribution*');
INSERT INTO `menu` VALUES (1374178303975358464, 'admin', '2021-03-22 20:56:53', b'0', 'admin', '2021-07-27 16:06:08', 'null', 'distribution/distribution', 'md-person', 2, 'distribution', '1374173575405109248', 'distribution', 2.00, '分销员', 'null', '/manager/distribution*');
INSERT INTO `menu` VALUES (1374916594269945856, 'admin', '2021-03-24 21:50:35', b'0', 'admin', '2021-07-27 16:08:51', NULL, 'sys/slider/slider', 'ios-aperture', 2, 'slider', '1349237129847005184', 'slider', 7.00, '验证码', NULL, '/manager/other/verificationSource*');
INSERT INTO `menu` VALUES (1376450531517530112, 'admin', '2021-03-29 03:25:55', b'0', NULL, NULL, NULL, 'Main', 'md-basketball', 1, 'notice', '1367040599596728320', '/', 5.00, '站内信', NULL, NULL);
INSERT INTO `menu` VALUES (1376450662098796544, 'admin', '2021-03-29 03:26:26', b'0', 'admin', '2021-07-27 16:07:23', NULL, 'sys/message/noticeMessageTemplate', 'ios-american-football', 2, 'noticeMessageTemplate', '1376450531517530112', 'noticeMessageTemplate', 1.00, '站内信', NULL, '/manager/other/message*');
INSERT INTO `menu` VALUES (1376450766817984512, 'admin', '2021-03-29 03:26:51', b'0', 'admin', '2021-03-29 03:27:25', 'null', 'Main', 'md-checkmark', 1, 'sms', '1367040599596728320', '/', 6.00, '短信管理', 'null', NULL);
INSERT INTO `menu` VALUES (1376450876423536640, 'admin', '2021-03-29 03:27:17', b'0', 'admin', '2021-07-27 16:07:29', NULL, 'sys/message/sms', 'ios-timer', 2, 'sms', '1376450766817984512', 'sms', 1.00, '短信', NULL, '/manager/sms/sms*,/manager/passport/member*');
INSERT INTO `menu` VALUES (1384035281702748160, 'admin', '2021-04-19 14:45:00', b'0', 'admin', '2021-07-27 16:08:18', 'null', 'member/message-manage/weChatMessageManager', 'md-aperture', 2, 'message-manage', '1349237129847005184', 'message-manage', 5.00, '微信消息', 'null', '/manager/wechat/wechatMessage*');
INSERT INTO `menu` VALUES (1403988156444962818, 'admin', '2021-06-13 16:10:36', b'0', 'admin', '2021-12-02 19:54:37', 'null', 'promotions/coupon-activity/coupon', '', 2, 'promotions/coupon-activity', '1367049214198022144', 'promotions/coupon-activity', 0.00, '券活动', 'null', '/manager/promotion/couponActivity*');
INSERT INTO `menu` VALUES (1407601962899230721, 'admin', '2021-06-23 15:30:35', b'0', 'admin', '2021-07-27 16:05:08', NULL, 'Main', '', 1, 'liveManage', '1367040067201138688', '/', 2.00, '直播管理', NULL, '/manager/broadcast*');
INSERT INTO `menu` VALUES (1407602049759072258, 'admin', '2021-06-23 15:30:55', b'0', 'admin', '2021-12-07 10:54:54', NULL, 'promotions/live/live', '', 2, 'promotions/live', '1407601962899230721', 'promotions/live', 1.00, '直播管理', NULL, 'null');
INSERT INTO `menu` VALUES (1407602441964244994, 'admin', '2021-06-23 15:32:29', b'0', NULL, NULL, NULL, 'Main', '', 1, 'pointManage', '1367040067201138688', '/', 3.00, '积分活动', NULL, NULL);
INSERT INTO `menu` VALUES (1407602516912263170, 'admin', '2021-06-23 15:32:47', b'0', 'admin', '2021-12-03 19:18:30', NULL, 'promotions/points-goods/points-goods', '', 2, 'promotions/points-goods', '1407602441964244994', 'promotions/points-goods', 1.00, '积分商品', NULL, '/manager/promotion/pointsGoods*,/manager/goods*');
INSERT INTO `menu` VALUES (1407602673334636546, 'admin', '2021-06-23 15:33:24', b'0', 'admin', '2021-12-03 19:19:23', NULL, 'promotions/points-goods-category/points-goods-category', '', 2, 'promotions/points-goods-category', '1407602441964244994', 'promotions/points-goods-category', 2.00, '积分分类', NULL, '/manager/promotion/pointsGoodsCategory*');
INSERT INTO `menu` VALUES (1410862675914764290, 'admin', '2021-07-02 15:27:29', b'0', 'admin', '2021-07-27 16:06:26', 'null', 'distribution/distributionCash', '', 2, 'distributionCash', '1374173575405109248', 'distributionCash', 5.00, '分销提现', 'null', '/manager/distribution/cash*');
INSERT INTO `menu` VALUES (1419926569920536578, 'admin', '2021-07-27 15:44:10', b'0', 'admin', '2022-10-21 11:03:41', NULL, 'custom-words/index', NULL, 2, 'custom-words', '1367050250249830400', 'custom-words', 4.00, 'ES分词', NULL, '/manager/manager/custom-words*');
INSERT INTO `menu` VALUES (1430799171593535490, 'admin', '2021-08-26 15:48:00', b'0', 'admin', '2021-12-02 20:21:34', NULL, 'promotions/kanjia/kanjia-activity-goods', NULL, 2, 'promotions/kanjia', '1367049214198022144', 'promotions/kanjia', 6.00, '砍价活动', NULL, '/manager/promotion/kanJiaGoods*');

-- ----------------------------
-- Table structure for message
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message`  (
                               `id` bigint NOT NULL COMMENT 'ID',
                               `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建者',
                               `create_at` datetime NULL DEFAULT NULL COMMENT '创建时间',
                               `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                               `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新者',
                               `update_at` datetime NULL DEFAULT NULL COMMENT '更新时间',
                               `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '内容',
                               `message_range` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '发送范围',
                               `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标题',
                               `create_send` bit(1) NULL DEFAULT NULL,
                               `category` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                               `message_client` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '发送客户端',
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of message
-- ----------------------------

-- ----------------------------
-- Table structure for notice_message
-- ----------------------------
DROP TABLE IF EXISTS `notice_message`;
CREATE TABLE `notice_message`  (
                                      `id` bigint NOT NULL,
                                      `create_by` varchar(255)NULL DEFAULT NULL,
                                      `create_at` datetime(6) NULL DEFAULT NULL,
                                      `delete_flag` bit(1) NULL DEFAULT NULL,
                                      `update_by` varchar(255)NULL DEFAULT NULL,
                                      `update_at` datetime(6) NULL DEFAULT NULL,
                                      `notice_content` varchar(255)NULL DEFAULT NULL,
                                      `notice_node` varchar(255)NULL DEFAULT NULL,
                                      `notice_status` varchar(255)NULL DEFAULT NULL,
                                      `notice_title` varchar(255)NULL DEFAULT NULL,
                                      `variable` varchar(255)NULL DEFAULT NULL,
                                      PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of notice_message
-- ----------------------------

-- ----------------------------
-- Table structure for notice_message_template
-- ----------------------------
DROP TABLE IF EXISTS `notice_message_template`;
CREATE TABLE `notice_message_template`  (
                                               `id` bigint NOT NULL COMMENT 'ID',
                                               `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建者',
                                               `create_at` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                               `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                               `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新者',
                                               `update_at` datetime NULL DEFAULT NULL COMMENT '更新时间',
                                               `message_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '消息CODE',
                                               `notice_content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '站内信内容',
                                               `notice_state` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '站内信是否开启',
                                               `category` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '类型',
                                               `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '模板名称',
                                               PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

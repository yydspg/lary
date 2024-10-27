
-- ----------------------------
-- Table structure for store
-- ----------------------------
DROP TABLE IF EXISTS `store`;
CREATE TABLE `store`  (
                             `id` bigint NOT NULL COMMENT 'ID',
                             `member_id` varchar(255)COMMENT '会员Id',
                             `member_name` varchar(255)COMMENT '会员名称',
                             `self_operated` bit(1) NOT NULL COMMENT '是否自营',
                             `store_disable` varchar(255)COMMENT '店铺状态',
                             `store_end_time` datetime(6) COMMENT '店铺关闭时间',
                             `store_logo` varchar(255)COMMENT '店铺logo',
                             `store_name` varchar(255)COMMENT '店铺名称',
                             `store_address_detail` varchar(255)COMMENT '详细地址',
                             `store_address_id_path` varchar(255)COMMENT '地址id',
                             `store_address_path` varchar(255)COMMENT '地址名称',
                             `store_center` varchar(255)COMMENT '经纬度',
                             `store_desc` varchar(200)COMMENT '店铺简介',
                             `delivery_score` decimal(10, 2) COMMENT '物流评分',
                             `description_score` decimal(10, 2) COMMENT '描述评分',
                             `service_score` decimal(10, 2) COMMENT '服务评分',
                             `goods_num` int COMMENT '商品数量',
                             `collection_num` int COMMENT '收藏数量',
                             `yzf_mp_sign` varchar(255)NULL DEFAULT NULL,
                             `yzf_sign` varchar(255)NULL DEFAULT NULL,
                             `merchant_euid` varchar(255)NULL DEFAULT NULL,
                             `page_show` bit(1) COMMENT '默认页面是否开启',
                             `self_pick_flag` bit(1) COMMENT '是否开启自提',
                             is_delete bool ,
                             create_by varchar(40) ,
                             update_by varchar(40) ,
                             create_at timestamp,
                             update_at timestamp,
                             PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of store
-- ----------------------------
INSERT INTO `store` VALUES (1376369067769724928, 'admin', '2021-03-28 22:02:12.109000', b'0', 'admin', '2021-10-18 14:47:22.227000', '1376364798236688384', '15810610731', b'1', 'OPEN', NULL, 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/e3e1c6ef28a844f98a0c907bd74a4085.png', '自营店', '北京五道口', '1371783043718578711,1371783043718578712,1371783043722772487,1371783043722772492', '北京市 北京城区 海淀区 学院路街道', '39.992552,116.338611', 'Lilishop自营店简介', 4.00, 4.00, 4.00, 54, 13, '4a971f4f4ff9637cd9286a0197b0573bf2f65de052c21791f90d4235fca41456e1cc145075567f5f47b8e13c895f3fc8cacec5789f9afee8df670f7cbe5c0f82', '37ef9b97807d03c6741298ed4eb5b536d2d238e08a3c00fb01fe48f03a569974c99ad767e72c04b3165ef29aca2c488b505fe4ca', NULL, NULL, NULL);
INSERT INTO `store` VALUES (1376433565247471616, 'admin', '2021-03-29 02:18:29.507000', b'0', 'admin', '2021-12-02 10:25:56.165000', '1376417684140326912', '13011111111', b'1', 'OPEN', NULL, 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/158bff831cff499a8d45a226a6e020c2.png', '家家乐', '家家乐超市', '1401797451790156093,1401797451790156094,1401797451790156416,1401797451790156442', '北京市 北京城区 海淀区 西三旗街道', '116.378877,40.063637', '店223232', 4.41, 4.47, 4.58, 189, 69, '32b8ff6f8d1c240be8d7fe51bdd6d44a6776ea86930afbe5c3c342825e942c914fc6126b6be1f003ab04aee1af9f442d2c33e1427529300671588866edaa4b12', '37ef9b97807d03c6741298ed4eb5b536d2d238e08a3c00fb01fe48f03a569974c99ad767e72c04b3165ef29aca2c488b505fe4ca', NULL, NULL, NULL);

-- ----------------------------
-- Table structure for store_address
-- ----------------------------
DROP TABLE IF EXISTS `store_address`;
CREATE TABLE `store_address`  (
                                     `id` bigint NOT NULL COMMENT 'ID',
                                     `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '地址',
                                     `address_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '自提点名称',
                                     `center` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '经纬度',
                                     `mobile` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '电话',
                                     `store_id` bigint  COMMENT '店铺ID',
                                     is_delete bool ,
                                     create_by varchar(40) ,
                                     update_by varchar(40) ,
                                     create_at timestamp,
                                     update_at timestamp,
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;


-- ----------------------------
-- Table structure for store_collection
-- ----------------------------
DROP TABLE IF EXISTS `store_collection`;
CREATE TABLE `store_collection`  (
                                        `id` bigint NOT NULL COMMENT 'ID',

                                        `member_id` bigint COMMENT '会员ID',
                                        `store_id` bigint COMMENT '店铺ID',
                                        is_delete bool ,
                                        create_by varchar(40) ,
                                        update_by varchar(40) ,
                                        create_at timestamp,
                                        update_at timestamp,
                                        PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of store_collection
-- ----------------------------

-- ----------------------------
-- Table structure for store_department
-- ----------------------------
DROP TABLE IF EXISTS `store_department`;
CREATE TABLE `store_department`  (
                                        `id` bigint NOT NULL COMMENT 'ID',
                                        `title` varchar(255)  COMMENT '部门名称',
                                        `store_id` bigint  COMMENT '店铺id',
                                        `parent_id` bigint COMMENT '父id',
                                        `sort_order` decimal(20, 2) COMMENT '排序值',
                                        is_delete bool ,
                                        create_by varchar(40) ,
                                        update_by varchar(40) ,
                                        create_at timestamp,
                                        update_at timestamp,
                                        PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of store_department
-- ----------------------------

-- ----------------------------
-- Table structure for store_department_role
-- ----------------------------
DROP TABLE IF EXISTS `store_department_role`;
CREATE TABLE `store_department_role`  (
                                             `id` bigint NOT NULL COMMENT 'ID',
                                             `role_id` varchar(255)  COMMENT '角色id',
                                             `department_id` varchar(255)  COMMENT '部门id',
                                             is_delete bool ,
                                             create_by varchar(40) ,
                                             update_by varchar(40) ,
                                             create_at timestamp,
                                             update_at timestamp,
                                             PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of store_department_role
-- ----------------------------

-- ----------------------------
-- Table structure for store_detail
-- ----------------------------
DROP TABLE IF EXISTS `store_detail`;
CREATE TABLE `store_detail`  (
                                    `id` bigint NOT NULL COMMENT 'ID',
                                    `company_address` varchar(255)COMMENT '公司地址',
                                    `company_address_id_path` varchar(255)COMMENT '公司地址地区ID',
                                    `company_address_path` varchar(255)COMMENT '公司地址地区',
                                    `company_email` varchar(255)COMMENT '电子邮箱',
                                    `company_name` varchar(255)COMMENT '公司名称',
                                    `company_phone` varchar(255)COMMENT '公司电话',
                                    `dd_code` varchar(255)COMMENT '同城配送达达店铺编码',
                                    `employee_num` int COMMENT '员工总数',
                                    `goods_management_category` text NULL COMMENT '店铺经营类目',
                                    `legal_id` varchar(255)COMMENT '法人身份证',
                                    `legal_name` varchar(255)COMMENT '法人姓名',
                                    `legal_photo` varchar(255)COMMENT '法人身份证照片',
                                    `licence_photo` varchar(255)COMMENT '营业执照电子版',
                                    `license_num` varchar(255)COMMENT '营业执照号',
                                    `link_name` varchar(255)COMMENT '联系人姓名',
                                    `link_phone` varchar(255)COMMENT '联系人电话',
                                    `registered_capital` decimal(10, 2) COMMENT '注册资金',
                                    `scope` varchar(255)COMMENT '法定经营范围',
                                    `settlement_bank_account_name` varchar(255)COMMENT '结算银行开户行名称',
                                    `settlement_bank_account_num` varchar(255)COMMENT '结算银行开户账号',
                                    `settlement_bank_branch_name` varchar(255)COMMENT '结算银行开户支行名称',
                                    `settlement_bank_joint_name` varchar(255)COMMENT '结算银行支行联行号',
                                    `store_id` bigint COMMENT '店铺ID',
                                    `store_name` varchar(255)COMMENT '店铺名称',
                                    `stock_warning` int COMMENT '库存预警数量',
                                    `settlement_cycle` varchar(255)COMMENT '结算周期',
                                    `sales_consignee_address_id` varchar(255)COMMENT '退货地址Id',
                                    `sales_consignee_address_path` varchar(255)COMMENT '退货地址名称',
                                    `sales_consignee_detail` varchar(255)COMMENT '退货详细地址',
                                    `sales_consignee_mobile` varchar(255)COMMENT '退货收件人手机',
                                    `sales_consignee_name` varchar(255)COMMENT '退货收货人姓名',
                                    `settlement_day` datetime(6) COMMENT '店铺上次结算日',
                                    `sales_consignor_address_id` varchar(255)  COMMENT '发货地址id',
                                    `sales_consignor_address_path` varchar(255)  COMMENT '发货地址名称',
                                    `sales_consignor_detail` varchar(255)  COMMENT '发货详细地址',
                                    `sales_consignor_mobile` varchar(255)  COMMENT '发货人手机',
                                    `sales_consignor_name` varchar(255)  COMMENT '发货人姓名',
                                    `app_secret_key` varchar(255)NULL DEFAULT NULL,
                                    `merchant_number` varchar(255)NULL DEFAULT NULL,
                                    `app_merchant_key` varchar(255)NULL DEFAULT NULL,
                                    is_delete bool ,
                                    create_by varchar(40) ,
                                    update_by varchar(40) ,
                                    create_at timestamp,
                                    update_at timestamp

) ;

-- ----------------------------
-- Records of store_detail
-- ----------------------------
INSERT INTO `store_detail` VALUES (1376369068555857921, '公司详细地址', '1371783040048562731,1371783040048562732,1371783040048562834,1371783040048562835', '上海市 , 上海城区 , 黄浦区 , 淮海中路街道', 'a@aa.com', 'xxxxxxx科技有限公司', '13232323232', '', 111, '1348576427264204941,1348576427268399553,1348576427260010496,1348576427268399363,1348576427268399172,1348576427268399318,1348576427268399274,1348576427268399217,1348576427268399486,1348576427268399674,1414475616154390529', '133552199004050306', '张三', 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/62879a7747f4498699cc1753f88b8d70.png', 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/62879a7747f4498699cc1753f88b8d70.png', '91110113MA01LM5H3X', '张三', '13333332222', 500.00, '技术开发、技术咨询、技术转让、技术推广、技术服务；销售服装鞋帽、日用品、玩具、新鲜蔬菜、针纺织品、文化用品、体育用品（不含弩）、家用电器、电子产品、计算机、软件及辅助设备；软件开发；基础软件服务；应用软件服务；设计、制作、代理、发布广告；承办展览展示活动；会议服务；包装装潢设计；模型设计；计算机系统集成服务；信息咨询；(不含中介服务)', '北京宏业汇成科技有限公司', '16333322333', '光大银行五道口支行', '553232', '1376369067769724928', '自营店', 10, ',1,15,20,', '1371783040048562731,1371783040048562732,1371783040048562834,1371783040048562835', '上海市 , 上海城区 , 黄浦区 , 淮海中路街道', '详细地址', '13333333333', '张三', '2021-12-01 02:05:00.825000', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `store_detail` VALUES (1376433566338019329, '详细地址', '1371783043718578711,1371783043718578712,1371783043718578732,1371783043718578734', '北京市 , 北京城区 , 怀柔区 , 琉璃庙镇', 'a@aa.com', '北京宏业汇成科技有限公司', '0316-3417993', '', 10000, '1428225316812566530,1428225832128950273,1423098713371631617,1424978145065689089,1427280840698966017,1428231712513798145,1348576427264204941,1348576427268399553,1414475616154390529,1348576427260010496,1348576427268399363,1348576427268399172,1348576427268399318,1348576427268399274,1348576427268399217,1348576427268399486,1348576427268399674,1441232615244111873,1440597181027655681,1438723752259317761,1450388246647627778', '150132199008020635', '张三', 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/62879a7747f4498699cc1753f88b8d70.png', 'https://lilishop-oss.oss-cn-beijing.aliyuncs.com/62879a7747f4498699cc1753f88b8d70.png', '91110113MA01LM5H3X', '张三', '13333333333', 10000.00, '技术开发、技术咨询、技术转让、技术推广、技术服务；销售服装鞋帽、日用品、玩具、新鲜蔬菜、针纺织品、文化用品、体育用品（不含弩）、家用电器、电子产品、计算机、软件及辅助设备；软件开发；基础软件服务；应用软件服务；设计、制作、代理、发布广告；承办展览展示活动；会议服务；包装装潢设计；模型设计；计算机系统集成服务；信息咨询；(不含中介服务)', '北京宏业汇成科技有限公司', '20101001', '支行', '2582222', '1376433565247471616', '家家乐', 20, '23,11', '1401797451521721163,1401797451525914915,1401797451525914972,1401797451525914976', '辽宁省 , 锦州市 , 凌海市 , 新庄子镇', '详细地址12号', '13333333333', '张牛逼', '2021-11-23 02:05:00.875000', '1401797451521721163,1401797451525914753,1401797451525914787,1401797451525914789', '辽宁省 , 大连市 , 庄河市 , 城山镇', ' 桥洞下', '13017580355', ' 张某三', NULL, NULL, NULL);

-- ----------------------------
-- Table structure for store_flow
-- ----------------------------
DROP TABLE IF EXISTS `store_flow`;
CREATE TABLE `store_flow`  (
                                  `id` bigint NOT NULL COMMENT 'ID',
                                  `bill_price` decimal(10, 2) COMMENT '最终结算金额',
                                  `category_id` varchar(255)  COMMENT '分类ID',
                                  `commission_price` decimal(10, 2) COMMENT '平台收取交易佣金',
                                  `create_at` datetime(6) COMMENT '创建时间',
                                  `distribution_rebate` decimal(10, 2) COMMENT '单品分销返现支出',
                                  `final_price` decimal(10, 2) COMMENT '流水金额',
                                  `flow_type` varchar(255)  COMMENT '流水类型',
                                  `goods_id` varchar(255)  COMMENT '商品ID',
                                  `goods_name` varchar(255)  COMMENT '商品名称',
                                  `image` varchar(255)  COMMENT '图片',
                                  `member_id` varchar(255)  COMMENT '会员ID',
                                  `member_name` varchar(255)  COMMENT '会员名称',
                                  `num` int COMMENT '销售量',
                                  `order_item_sn` varchar(255)  COMMENT '子订单编号',
                                  `payment_name` varchar(255)  COMMENT '支付方式名称',
                                  `refund_sn` varchar(255)  COMMENT '售后SN',
                                  `site_coupon_commission` decimal(10, 2) COMMENT '站点优惠券佣金',
                                  `site_coupon_point` decimal(10, 2) COMMENT '站点优惠券佣金比例',
                                  `site_coupon_price` decimal(10, 2) COMMENT '平台优惠券使用金额',
                                  `sku_id` varchar(255)  COMMENT '货品ID',
                                  `sn` varchar(255)  COMMENT '编号',
                                  `specs` text CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL COMMENT '规格',
                                  `store_id` bigint COMMENT '店铺ID',
                                  `store_name` varchar(255)  COMMENT '店铺名称',
                                  `transaction_id` varchar(255)  COMMENT '第三方交易流水号',
                                  `order_sn` varchar(255) ,
                                  `point_settlement_price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '积分商品结算金额',
                                  `kanjia_settlement_price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '砍价商品结算金额',
                                  `order_promotion_type` varchar(255) ,
                                  `bill_time` datetime(6) COMMENT '结算时间',
                                  `full_refund` bit(1) COMMENT '是否全部退款',
                                  `profit_sharing_status` varchar(255)  COMMENT '分账状态',
                                  `profit_sharing` varchar(255)  COMMENT '分账详情',
                                  is_delete bool ,
                                  create_by varchar(40) ,
                                  update_by varchar(40) ,
                                  create_at timestamp,
                                  update_at timestamp,
                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of store_flow
-- ----------------------------

-- ----------------------------
-- Table structure for store_goods_label
-- ----------------------------
DROP TABLE IF EXISTS `store_goods_label`;
CREATE TABLE `store_goods_label`  (
                                         `id` bigint NOT NULL COMMENT 'ID',
                                         `create_by` varchar(255)COMMENT '创建者',
                                         `create_at` datetime(6) COMMENT '创建时间',
                                         `delete_flag` bit(1) COMMENT '删除标志 true/false 删除/未删除',
                                         `update_by` varchar(255)COMMENT '更新者',
                                         `update_at` datetime(6) COMMENT '更新时间',
                                         `label_name` varchar(255)COMMENT '店铺商品分类名称',
                                         `level` int COMMENT '层级',
                                         `parent_id` varchar(255)COMMENT '父ID',
                                         `store_id` varchar(255)COMMENT '店铺ID',
                                         `sort_order` decimal(19, 2) COMMENT '店铺商品分类排序',
                                         PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;


-- ----------------------------
-- Table structure for store_logistics
-- ----------------------------
DROP TABLE IF EXISTS `store_logistics`;
CREATE TABLE `store_logistics`  (
                                       `id` bigint NOT NULL COMMENT 'ID',
                                       `create_by` varchar(255)COMMENT '创建者',
                                       `create_at` datetime(6) COMMENT '创建时间',
                                       `delete_flag` bit(1) COMMENT '删除标志',
                                       `update_by` varchar(255)COMMENT '更新者',
                                       `update_at` datetime(6) COMMENT '更新时间',
                                       `logistics_id` varchar(255)NOT NULL COMMENT '物流公司ID',
                                       `store_id` varchar(255)COMMENT '店铺ID',
                                       `customer_name` varchar(255)COMMENT '客户代码',
                                       `customer_pwd` varchar(255)COMMENT '客户密码',
                                       `month_code` varchar(255)COMMENT '月结号/密钥',
                                       `send_site` varchar(255)COMMENT '归属网点',
                                       `send_staff` varchar(255)COMMENT '收件快递员',
                                       `face_sheet_flag` bit(1) COMMENT '是否使用电子面单',
                                       `pay_type` varchar(255)  COMMENT '支付方式',
                                       `exp_type` varchar(255)  COMMENT '快递类型',
                                       PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;


-- ----------------------------
-- Table structure for store_menu
-- ----------------------------
DROP TABLE IF EXISTS `store_menu`;
CREATE TABLE `store_menu`  (
                                  `id` bigint NOT NULL COMMENT 'ID',
                                  `create_by` varchar(255)  COMMENT '创建者',
                                  `create_at` datetime(6) COMMENT '创建时间',
                                  `delete_flag` bit(1) COMMENT '删除标志 true/false 删除/未删除',
                                  `update_by` varchar(255)  COMMENT '更新者',
                                  `update_at` datetime COMMENT '更新时间',
                                  `description` varchar(255)  COMMENT '说明备注',
                                  `front_route` varchar(255)  COMMENT '前端路由',
                                  `icon` varchar(255)  COMMENT '图标',
                                  `level` int COMMENT '层级',
                                  `name` varchar(255)  COMMENT '菜单/权限名称',
                                  `parent_id` varchar(255)  COMMENT '父id',
                                  `path` varchar(255)  COMMENT '赋权API地址,正则表达式',
                                  `sort_order` varchar(255)  COMMENT '排序值',
                                  `title` varchar(255)  COMMENT '菜单标题',
                                  `permission` varchar(255)  COMMENT '权限url',
                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;


-- ----------------------------
-- Table structure for store_menu_role
-- ----------------------------
DROP TABLE IF EXISTS `store_menu_role`;
CREATE TABLE `store_menu_role`  (
                                       `id` bigint NOT NULL COMMENT 'ID',
                                       `create_by` varchar(255)  COMMENT '创建者',
                                       `create_at` datetime(6) COMMENT '创建时间',
                                       `delete_flag` bool COMMENT '删除标志 true/false 删除/未删除',
                                       `update_by` varchar(255)  COMMENT '更新者',
                                       `update_at` datetime(6) COMMENT '更新时间',
                                       `role_id` varchar(255)  COMMENT '角色id',
                                       `menu_id` varchar(255)  COMMENT '菜单',
                                       `store_id` varchar(255)  COMMENT '店铺id',
                                       `is_super` bool COMMENT '是否拥有操作数据权限，为否则只有查看权限',
                                       PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;



-- ----------------------------
-- Table structure for store_message
-- ----------------------------
DROP TABLE IF EXISTS `store_message`;
CREATE TABLE `store_message`  (
                                     `id` bigint NOT NULL COMMENT 'ID',
                                     `create_by` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '创建者',
                                     `create_at` datetime COMMENT '创建时间',
                                     `message_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '关联消息ID',
                                     `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '状态',
                                     `store_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '关联店铺ID',
                                     `store_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '关联店铺名称',
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for store_role
-- ----------------------------
DROP TABLE IF EXISTS `store_role`;
CREATE TABLE `store_role`  (
                                  `id` bigint NOT NULL COMMENT 'ID',
                                  `name` varchar(255)  COMMENT '角色名称',
                                  `store_id` bigint COMMENT '店铺id',
                                  `default_role` bit(1) COMMENT '是否为注册默认角色',
                                  `description` varchar(255)  COMMENT '备注',
                                  is_delete bool ,
                                  create_by varchar(40)null default null,
                                  update_by varchar(40)null default null,
                                  create_at timestamp,
                                  update_at timestamp,
                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;


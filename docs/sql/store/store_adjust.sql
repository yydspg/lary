DROP TABLE IF EXISTS `store`;
-- 店铺表
CREATE TABLE `store`(
    store_id bigint primary key ,
    store_name varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '店铺名称',
    store_address_detail varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '详细地址',
    store_address_id_path varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '地址id',
    store_address_path varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '地址名称',
    store_center varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '经纬度',
    store_desc varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '店铺简介',
    delivery_score decimal(10, 2) NULL DEFAULT NULL COMMENT '物流评分',
    description_score decimal(10, 2) NULL DEFAULT NULL COMMENT '描述评分',
    service_score decimal(10, 2) NULL DEFAULT NULL COMMENT '服务评分',
    goods_num int NULL DEFAULT NULL COMMENT '商品数量',
    collection_num int NULL DEFAULT NULL COMMENT '收藏数量',
    `page_show` bit(1) NULL DEFAULT NULL COMMENT '默认页面是否开启',
    `self_pick_flag` bit(1) NULL DEFAULT NULL COMMENT '是否开启自提',
    is_delete bool ,
    create_by varchar(40) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci  ,
    update_by varchar(40) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci  ,
    create_at timestamp,
    update_at timestamp
)ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- 店铺地址
drop table if exists  `store_address`;

create table `store_address`(
    store_address_id bigint ,
    store_id bigint ,
    address varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '地址',
    address_name varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '自提点名称',
    center varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '经纬度',
    mobile varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '电话',
    is_delete bool ,
    create_by varchar(40) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci  ,
    update_by varchar(40) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci  ,
    create_at timestamp,
    update_at timestamp
)ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;


drop table if exists `store_department`;

create table `store_department` (
    store_department_id bigint ,
    store_id bigint ,
    parent_id bigint ,
    title varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci  NULL DEFAULT NULL COMMENT '部门名称',
    is_delete bool ,
    create_by varchar(40) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci  ,
    update_by varchar(40) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci  ,
    create_at timestamp,
    update_at timestamp
)ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

DROP TABLE IF EXISTS `store_department_role`;
CREATE TABLE `store_department_role`(
    store_department_role_id bigint ,
    role_id bigint,
    department_id bigint comment '部门id',
    is_delete bool ,
    create_by varchar(40) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci  ,
    update_by varchar(40) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci  ,
    create_at timestamp,
    update_at timestamp
);
DROP TABLE IF EXISTS `store_detail`;
CREATE TABLE `store_detail`  (
    `store_detail_id` bigint NOT NULL COMMENT 'ID' primary key ,
    `store_id` bigint ,
    `store_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '店铺名称',
    `stock_warning` int NULL DEFAULT NULL COMMENT '库存预警数量',
    `settlement_cycle` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '结算周期',
    `company_address` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '公司地址',
    `company_address_id_path` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '公司地址地区ID',
    `company_address_path` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '公司地址地区',
    `company_email` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '电子邮箱',
    `company_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '公司名称',
    `company_phone` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '公司电话',
    `dd_code` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '同城配送达达店铺编码',
    `employee_num` int NULL DEFAULT NULL COMMENT '员工总数',
    `goods_management_category` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL COMMENT '店铺经营类目',
    `legal_id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '法人身份证',
    `legal_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '法人姓名',
    `legal_photo` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '法人身份证照片',
    `licence_photo` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '营业执照电子版',
    `license_num` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '营业执照号',
    `link_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '联系人姓名',
    `link_phone` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '联系人电话',
    `registered_capital` decimal(10, 2) NULL DEFAULT NULL COMMENT '注册资金',
    `scope` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '法定经营范围',
    `settlement_bank_account_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '结算银行开户行名称',
    `settlement_bank_account_num` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '结算银行开户账号',
    `settlement_bank_branch_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '结算银行开户支行名称',
    `settlement_bank_joint_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '结算银行支行联行号',
    `sales_consignee_address_id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '退货地址Id',
    `sales_consignee_address_path` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '退货地址名称',
    `sales_consignee_detail` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '退货详细地址',
    `sales_consignee_mobile` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '退货收件人手机',
    `sales_consignee_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '退货收货人姓名',
    `settlement_day` datetime(6) NULL DEFAULT NULL COMMENT '店铺上次结算日',
    `sales_consignor_address_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '发货地址id',
    `sales_consignor_address_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '发货地址名称',
    `sales_consignor_detail` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '发货详细地址',
    `sales_consignor_mobile` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '发货人手机',
    `sales_consignor_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '发货人姓名',
    `app_secret_key` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
    `merchant_number` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
    `app_merchant_key` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
    is_delete bool ,
    create_by varchar(40) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci  ,
    update_by varchar(40) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci  ,
    create_at timestamp,
    update_at timestamp
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;
DROP TABLE IF EXISTS `store_flow`;
CREATE TABLE `store_flow`  (
                               `store_flow_id` bigint NOT NULL primary key COMMENT 'ID',
                               `bill_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '最终结算金额',
                               `category_id` bigint  COMMENT '分类ID',
                               `commission_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '平台收取交易佣金',
                               `distribution_rebate` decimal(10, 2) NULL DEFAULT NULL COMMENT '单品分销返现支出',
                               `final_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '流水金额',
                               `flow_type` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT '流水类型',
                               `goods_id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT '商品ID',
                               `goods _name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT '商品名称',
                               `image` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT '图片',
                               `member_id` bigint COMMENT '会员ID',
                               `member_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT '会员名称',
                               `num` int NULL DEFAULT NULL COMMENT '销售量',
                               `order_item_sn` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT '子订单编号',
                               `payment_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT '支付方式名称',
                               `refund_sn` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT '售后SN',
                               `site_coupon_commission` decimal(10, 2) NULL DEFAULT NULL COMMENT '站点优惠券佣金',
                               `site_coupon_point` decimal(10, 2) NULL DEFAULT NULL COMMENT '站点优惠券佣金比例',
                               `site_coupon_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '平台优惠券使用金额',
                               `sku_id` bigint COMMENT '货品ID',
                               `sn` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT '编号',
                               `specs` text CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL COMMENT '规格',
                               `store_id` bigint COMMENT '店铺ID',
                               `store_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT '店铺名称',
                               `transaction_id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT '第三方交易流水号',
                               `order_sn` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
                               `point_settlement_price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '积分商品结算金额',
                               `haggle_settlement_price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '砍价商品结算金额',
                               `order_promotion_type` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
                               `bill_time` datetime NULL DEFAULT NULL COMMENT '结算时间',
                               `full_refund` bool NULL DEFAULT NULL COMMENT '是否全部退款',
                               `profit_sharing_status` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT '分账状态',
                               `profit_sharing` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT '分账详情',
                               is_delete bool ,
                               create_by varchar(40) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci  ,
                               update_by varchar(40) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci  ,
                               create_at timestamp,
                               update_at timestamp
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin ROW_FORMAT = DYNAMIC;

DROP TABLE IF EXISTS `store_menu`;
CREATE TABLE `store_menu`  (
                               `id` bigint NOT NULL COMMENT 'ID',
                               `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '创建者',
                               `create_at` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                               `delete_flag` bit(1) NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                               `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '更新者',
                               `update_at` datetime NULL DEFAULT NULL COMMENT '更新时间',
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
-- Table structure for store_menu_role
-- ----------------------------
DROP TABLE IF EXISTS `store_menu_role`;
CREATE TABLE `store_menu_role`  (
                                    `id` bigint NOT NULL COMMENT 'ID',
                                    `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '创建者',
                                    `create_at` datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
                                    `delete_flag` bool NULL DEFAULT NULL COMMENT '删除标志 true/false 删除/未删除',
                                    `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '更新者',
                                    `update_at` datetime(6) NULL DEFAULT NULL COMMENT '更新时间',
                                    `role_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '角色id',
                                    `menu_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '菜单',
                                    `store_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '店铺id',
                                    `is_super` bool NULL DEFAULT NULL COMMENT '是否拥有操作数据权限，为否则只有查看权限',
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;



-- ----------------------------
-- Table structure for store_message
-- ----------------------------

-- 商店消息关联表
DROP TABLE IF EXISTS `store_message`;
CREATE TABLE `store_message`  (
                                  `store_message_id` bigint NOT NULL COMMENT 'ID',
                                  `message_id` bigint COMMENT '关联消息ID',
                                  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '状态',
                                  `store_id` bigint COMMENT '关联店铺ID',
                                  `store_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '关联店铺名称',
                                  `create_by` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建者',
                                  `create_at` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                  PRIMARY KEY (`store_message_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for store_role
-- ----------------------------
-- 商店角色
DROP TABLE IF EXISTS `store_role`;
CREATE TABLE `store_role`  (
                               `store_role_id` bigint NOT NULL COMMENT 'ID',
                               `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '角色名称',
                               store_id bigint COMMENT '店铺id',
                               is_default_role bool  NULL DEFAULT NULL COMMENT '是否为注册默认角色',
                               description varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
                               is_delete bool ,
                               create_by varchar(40) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci null default null,
                               update_by varchar(40) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci null default null,
                               create_at timestamp,
                               update_at timestamp,
                               PRIMARY KEY (`store_role_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;
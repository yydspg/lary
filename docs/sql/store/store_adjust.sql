DROP TABLE IF EXISTS store;
-- 店铺表
CREATE TABLE store(
                      id bigint unsigned primary key auto_increment ,
                      name varchar(255) not null COMMENT '店铺名称',
                      avatar varchar(255)  not null comment '店铺头像',
                      description varchar(255)  COMMENT '店铺简介',
                      delivery_score decimal(10, 2) COMMENT '物流评分',
                      description_score decimal(10, 2) COMMENT '描述评分',
                      service_score decimal(10, 2) COMMENT '服务评分',
                      goods_num int COMMENT '商品数量',
                      collection_num int COMMENT '收藏数量',
                      is_delete bool ,
                      create_at timestamp,
                      update_at timestamp
);


DROP TABLE IF EXISTS store_detail;
CREATE TABLE store_detail  (
                               id bigint NOT NULL primary key auto_increment ,
                               store_id bigint not null ,
                               store_name varchar(255)  COMMENT '店铺名称',
                               stock_warning int COMMENT '库存预警数量',
                               settlement_cycle varchar(255)  COMMENT '结算周期',
                               company_address varchar(255)  COMMENT '公司地址',
                               company_address_id_path varchar(255)  COMMENT '公司地址地区ID',
                               company_address_path varchar(255)  COMMENT '公司地址地区',
                               company_email varchar(255)  COMMENT '电子邮箱',
                               company_name varchar(255)  COMMENT '公司名称',
                               company_phone varchar(255)  COMMENT '公司电话',
                               dd_code varchar(255)  COMMENT '同城配送达达店铺编码',
                               employee_num int COMMENT '员工总数',
                               goods_management_category text NULL COMMENT '店铺经营类目',
                               legal_id varchar(255)  COMMENT '法人身份证',
                               legal_name varchar(255)  COMMENT '法人姓名',
                               legal_photo varchar(255)  COMMENT '法人身份证照片',
                               licence_photo varchar(255)  COMMENT '营业执照电子版',
                               license_num varchar(255)  COMMENT '营业执照号',
                               link_name varchar(255)  COMMENT '联系人姓名',
                               link_phone varchar(255)  COMMENT '联系人电话',
                               registered_capital decimal(10, 2) COMMENT '注册资金',
                               scope varchar(255)  COMMENT '法定经营范围',
                               settlement_bank_account_name varchar(255)  COMMENT '结算银行开户行名称',
                               settlement_bank_account_num varchar(255)  COMMENT '结算银行开户账号',
                               settlement_bank_branch_name varchar(255)  COMMENT '结算银行开户支行名称',
                               settlement_bank_joint_name varchar(255)  COMMENT '结算银行支行联行号',
                               sales_consignee_address_id varchar(255)  COMMENT '退货地址Id',
                               sales_consignee_address_path varchar(255)  COMMENT '退货地址名称',
                               sales_consignee_detail varchar(255)  COMMENT '退货详细地址',
                               sales_consignee_mobile varchar(255)  COMMENT '退货收件人手机',
                               sales_consignee_name varchar(255)  COMMENT '退货收货人姓名',
                               settlement_day datetime(6) COMMENT '店铺上次结算日',
                               consignor_address_path varchar(255)  COMMENT '发货地址名称',
                               consignor_detail varchar(255)  COMMENT '发货详细地址',
                               consignor_mobile varchar(255)  COMMENT '发货人手机',
                               consignor_name varchar(255)  COMMENT '发货人姓名',
                               app_secret_key varchar(255) ,
                               merchant_number varchar(255) ,
                               app_merchant_key varchar(255) ,
                               is_delete bool ,
                               create_at timestamp,
                               update_at timestamp
) ;

DROP TABLE IF EXISTS store_menu;
CREATE TABLE store_menu  (
                             id bigint primary key auto_increment NOT NULL COMMENT 'ID',
                             description varchar(255)  COMMENT '说明备注',
                             front_route varchar(255)  COMMENT '前端路由',
                             icon varchar(255)  COMMENT '图标',
                             level int COMMENT '层级',
                             name varchar(255)  COMMENT '菜单/权限名称',
                             parent_id varchar(255)  COMMENT '父id',
                             path varchar(255)  COMMENT '赋权API地址,正则表达式',
                             sort_order varchar(255)  COMMENT '排序值',
                             title varchar(255)  COMMENT '菜单标题',
                             permission varchar(255)  COMMENT '权限url',
                             is_delete bool ,
                             create_at timestamp,
                             update_at timestamp

) ;

-- ----------------------------
-- Table structure for store_menu_role
-- ----------------------------
DROP TABLE IF EXISTS store_menu_role;
CREATE TABLE store_menu_role  (
                                  id bigint primary key auto_increment NOT NULL COMMENT 'ID',
                                  role_id bigint  COMMENT '角色id',
                                  menu_id bigint  COMMENT '菜单',
                                  store_id bigint  COMMENT '店铺id',
                                  is_super bool COMMENT '是否拥有操作数据权限，为否则只有查看权限'
) ;



-- ----------------------------
-- Table structure for store_role
-- ----------------------------
-- 商店角色
DROP TABLE IF EXISTS store_role;
CREATE TABLE store_role  (
                             id bigint primary key NOT NULL COMMENT 'ID' auto_increment,
                             name varchar(255)  COMMENT '角色名称',
                             store_id bigint COMMENT '店铺id',
                             is_default_role bool  COMMENT '是否为注册默认角色',
                             description varchar(255)  COMMENT '备注',
                             is_delete bool ,
                             create_at timestamp,
                             update_at timestamp
) ;
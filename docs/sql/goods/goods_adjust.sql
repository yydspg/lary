DROP TABLE IF EXISTS `goods`;
CREATE TABLE `goods`  (
                          id bigint primary key NOT NULL COMMENT 'id',
                          brand_id bigint COMMENT '品牌ID',
                          brand_name varchar(20) comment '品牌名称',
                          buy_count int NULL DEFAULT 0 COMMENT '购买数量',
                          category_path varchar(255)COMMENT '分类路径',
                          comment_num int COMMENT '评论数量',
                          cost decimal(10, 2) COMMENT '成本价格',
                          name varchar(255)COMMENT '商品名称',
                          category int NULL DEFAULT NULL comment '商品分类',
                          unit varchar(20)COMMENT '计量单位,克,个,米.etc',
                          video varchar(255)COMMENT '商品视频',
                          grade decimal(10, 2) COMMENT '商品好评率',
                          intro varchar(1024) COMMENT '商品详情',
                          price decimal(10, 2) COMMENT '商品价格',
                          quantity int  not null DEFAULT 0 COMMENT '商品库存',
                          auth_status int COMMENT '审核状态',
                          auth_message varchar(255)COMMENT '审核信息',
                          market_status int COMMENT '上架状态',
                          mobile_intro text NULL COMMENT '商品移动端详情',
                          is_recommend bool COMMENT '是否为推荐商品',
                          sales_model varchar(255)COMMENT '销售模式',
                          store_id bigint COMMENT '店铺ID',
                          store_name varchar(255)COMMENT '店铺名称',
                          store_category varchar(255)COMMENT '店铺分类',
                          store_category_path varchar(255)COMMENT '店铺分类路径',
                          selling_point varchar(255)COMMENT '卖点',
                          small_avatar varchar(255) COMMENT '小图路径',
                          big_avatar varchar(255) COMMENT '大图路径',
                          thumbnail varchar(255)COMMENT '缩略图路径',
                          original varchar(255)COMMENT '原图路径',
                          template_id varchar(255)COMMENT '运费模板ID',
                          under_message varchar(255) COMMENT '下架原因',
                          is_delete bool not null default false,
                          create_by varchar(40) ,
                          update_by varchar(40) ,
                          create_at timestamp,
                          update_at timestamp
);
DROP TABLE IF EXISTS `goods_sku`;
CREATE TABLE `goods_sku`
(
    id bigint primary key NOT NULL auto_increment COMMENT 'id',
    goods_id bigint unsigned  NOT NULL COMMENT '商品id',
    sku bigint unsigned not null comment 'sku',
    cost decimal(10, 2) COMMENT '成本价格',
    name varchar(255)COMMENT '商品名称',
    is_recommend bool COMMENT '是否为推荐商品',
    price decimal(10, 2) COMMENT '商品价格',
    quantity int  not null DEFAULT 0 COMMENT '商品库存',
    point varchar(20) not null default '' comment '促销点:商家自定义',
    is_delete bool not null default false,
    create_by varchar(40) ,
    update_by varchar(40) ,
    create_at timestamp,
    update_at timestamp
);
drop table if exists `store_goods_view`;
create table `store_goods_view` (
                                    id bigint primary key NOT NULL  auto_increment comment 'id',
                                    store_id bigint COMMENT '店铺ID',
                                    idx_db int not null comment '库索引',
                                    idx_table int not null comment '表索引',
                                    goods_id bigint NOT NULL COMMENT '商品id',
                                    name varchar(255)COMMENT '商品名称',
                                    brand_name varchar(20) comment '品牌名称',
                                    buy_count int NULL DEFAULT 0 COMMENT '购买数量',
                                    category_path varchar(255)COMMENT '分类路径',
                                    price decimal(10, 2) COMMENT '商品价格',
                                    small_avatar varchar(255) COMMENT '小图路径',
                                    create_at timestamp
);
drop table if exists `user_goods_view`;
create table `user_goods_view`(
                                  id         bigint primary key NOT NULL auto_increment COMMENT 'id',
                                  idx_db     int    not null comment '库索引',
                                  idx_table  int    not null comment '表索引',
                                  goods_id   bigint NOT NULL COMMENT '商品id',
                                  store_id bigint COMMENT '店铺ID',
                                  name       varchar(255) COMMENT '商品名称',
                                  brand_name varchar(20) comment '品牌名称',
                                  buy_count  int    NULL DEFAULT 0 COMMENT '购买数量',
                                  price decimal(10, 2) COMMENT '商品价格',
                                  big_avatar varchar(255) COMMENT '大图路径',
                                  create_at timestamp
);


drop table if exists `gift`;

create table  `gift` (
    id bigint auto_increment primary key ,
    gift_id bigint not null default 0 comment 'gift id',
    type bigint not null default 0 comment 'gift type',
    type_name varchar(40) not null ,
    price int not null default 0 comment 'virtual currency price',
    real_price int unsigned default 0 comment 'real pay price CNY',
    name varchar(40) not null,
    purchase_num int not null default 0 comment '购买数量',
    picture_url varchar(255) not null comment 'gift logo',
    is_delete bool ,
    create_by varchar(40) ,
    update_by varchar(40) ,
    create_at timestamp,
    update_at timestamp
);

drop table if exists `gift_type` ;
create table `gift_type` (
    id bigint primary key auto_increment,
    type_id bigint not null default 0 comment '类型id',
    name varchar(256) not null,
    count int default 0 comment 'current type gift num',
    avatar_url varchar(255) not null default '' comment '类别图片地址',
    is_privilege bool default false comment '是否特殊',
    is_delete bool ,
    create_by varchar(40) ,
    update_by varchar(40) ,
    create_at timestamp,
    update_at timestamp
);
drop table if exists    `gift_buy_channel`;
create table `gift_buy_channel` (
    id bigint not null primary key auto_increment,
    channel_id varchar(40) not null default '' comment 'gift buy channel',
    is_alive bool not null default true comment '是否存活',
    is_delete bool ,
    create_by varchar(40) ,
    update_by varchar(40) ,
    create_at timestamp,
    update_at timestamp
);
drop table if exists `gift_order`;
create table `gift_order` (
    id bigint primary key auto_increment ,
    order_id varchar(40) not null comment '记录id',
    uid varchar(40) not null  comment '用户id',
    anchor_uid varchar(40) not null default '' comment '主播uid',
    buy_channel_id varchar(40) not null default '' comment '购买通道id',
    danmaku_id varchar(40) not null comment 'wk 弹幕流id',
    notify_url varchar(40) comment '异步通知地址',
    is_sync bool comment '是否同步用户数据成功',
    fail_reason varchar(255) comment '上游异常原因',
    stream_id varchar(40) comment '直播流id',
    is_to_anchor bool default false comment '是否直接向主播支付,不通过wallet或者余额不足',
    status smallint not null default 0 comment '购买状态 0 未支付，1 已支付 2 取消支付 3 支付失败',
    sn varchar(255)  NULL DEFAULT NULL COMMENT '订单编号',
    complete_at timestamp comment '完成时间',
    cost int unsigned comment '花费',
    client_type smallint comment '支付来源',
    gift_id varchar(40) not null default '' comment '礼物uid',
    gift_num int not null  default 1 comment '礼物购买数量',
    is_delete bool ,
    create_by varchar(40) ,
    update_by varchar(40) ,
    create_at timestamp,
    update_at timestamp
);
drop table if exists  `anchor_turnover`;

create table  `anchor_turnover` (
    id bigint primary key auto_increment,
    anchor_id varchar(40) not null default '' comment '主播id',
    buy_channel_id varchar(40) not null default '' comment 'gift购买通道',
    order_id varchar(40) not null default '' comment '记录id',
    buy_uid varchar(40) not null default '' comment '购买者id',
    stream_id bigint comment '直播流id',
    income int comment '单笔收入',
    client_type smallint comment '收入来源web,app',
    complete_time timestamp comment '交易完成时间',
    gift_id varchar(40) not null default '' comment '礼物uid',
    gift_num int not null default 0,
    is_fan bool comment '是否是粉丝',
    is_delete bool ,
    create_by varchar(40)   ,
    update_by varchar(40)  ,
    create_at timestamp,
    update_at timestamp
);

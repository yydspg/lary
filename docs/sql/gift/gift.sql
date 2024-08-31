drop table if exists `gift`;

create table  `gift` (
    id bigint auto_increment primary key ,
    type int not null default 0 comment 'gift type',
    type_name varchar(40) not null ,
    price int not null default 0 comment 'virtual currency price',
    real_price int unsigned default 0 comment 'real pay price CNY',
    name varchar(40) not null,
    purchase_num bigint not null default 0,
    picture_url varchar(255) not null comment 'gift logo',
    is_delete bool ,
    create_by varchar(40) ,
    update_by varchar(40) ,
    create_at timestamp,
    update_at timestamp
);
drop table if exists `gift_buy_record`;

drop table if exists `gift_type` ;
create table `gift_type` (
    id bigint primary key auto_increment,
    name varchar(256) not null,
    count int default 0 comment 'current type gift num',
    is_privilege bool default false comment '是否特殊',
    is_delete bool ,
    create_by varchar(40) ,
    update_by varchar(40) ,
    create_at timestamp,
    update_at timestamp
);
create table `gift_buy_record` (
    id bigint primary key auto_increment ,
    uid varchar(40)  comment 'user_id',
    app_id varchar(20) comment 'app_Id',
    notify_url varchar(40) comment '异步通知地址',
    is_sync bool comment '是否同步用户数据成功',
    room_id bigint ,
    fail_reason varchar(255) comment '上游异常原因',
    stream_id bigint comment '直播流id',
    anchor_uid varchar(40) comment '',
    status int ,
    sn varchar(255)  NULL DEFAULT NULL COMMENT '订单编号',
    complete_at timestamp comment '完成时间',
    end_at timestamp comment '结束时间',
    is_direct bool comment 'whether to anchor',
    cost bigint unsigned comment 'cost CNY',
    origin_type smallint comment 'payment original',
    gift_id bigint not null ,
    gift_num int default 1,
    is_delete bool ,
    create_by varchar(40)  ,
    update_by varchar(40)   ,
    create_at timestamp,
    update_at timestamp
);
drop table if exists  `anchor_turnover`;

create table  `anchor_turnover` (
    id bigint primary key auto_increment,
    anchor_id varchar(40) not null,
    room_id bigint ,
    stream_id bigint comment '直播流id',
    income int comment '单笔收入',
    client_type smallint comment '收入来源web,app',
    complete_time timestamp comment '交易完成时间',
    gift_id bigint ,
    gift_num int,
    uid varchar(40),
    is_fan bool comment '是否是粉丝',
    is_delete bool ,
    create_by varchar(40)   ,
    update_by varchar(40)  ,
    create_at timestamp,
    update_at timestamp
);
drop table if exists `gift_buy_status` ;
create table `gift_buy_status` (
    id int primary key auto_increment,
    name varchar(10),
    is_delete bool ,
    create_by varchar(40)   ,
    update_by varchar(40)   ,
    create_at timestamp,
    update_at timestamp
);
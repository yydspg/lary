 drop table if exists  `room`;
create table `room` (
    id bigint not null primary key auto_increment ,
    anchor_uid bigint not null comment 'user id',
    url varchar(80) not null comment 'srs url',
    stream_type bigint not null ,
    stream_url varchar(256)  comment '直播地址',
    channel_id varchar(256)   comment '弹幕流id',
    alive bool comment '是否在直播',
    remark varchar(156) comment '当前直播简介',
    is_hot bool comment '是否是著名主播',
    is_block bool comment '是否被封禁',
    block_type varchar(255) comment '封禁类型',
    is_delete bool ,
    create_by bigint ,
    update_by bigint ,
    create_at timestamp,
    update_at timestamp
);
drop table if exists `stream_type`;
create table `stream_type` (
    id bigint not null primary key auto_increment ,
    name varchar(40)   not null,
    is_delete bool ,
    create_by bigint ,
    update_by bigint ,
    create_at timestamp,
    update_at timestamp
);
drop table if exists `stream_record`;
create table `stream_record` (
    stream_id bigint not null primary key auto_increment,
    uid bigint not null ,
    channel_id bigint not null,
    start_at timestamp,
    end_at timestamp,
    duration int not null default 0 comment '开播时长以s为单位',
    watch_num bigint not null default 0,
    new_fans_num bigint not null default 0,
    star_num bigint not null default 0,
    watch_fan_num bigint not null default 0 comment '粉丝观看数量' ,
    gift_num bigint not null default 0 comment '送礼人数',
    is_delete bool ,
    create_by bigint ,
    update_by bigint ,
    create_at timestamp,
    update_at timestamp
);
drop table if exists `stream_block_type`;
create table `stream_block_type`(
    id bigint primary key auto_increment,
    description text comment '封禁详情',
    is_delete bool ,
    create_by bigint ,
    update_by bigint ,
    create_at timestamp,
    update_at timestamp
);

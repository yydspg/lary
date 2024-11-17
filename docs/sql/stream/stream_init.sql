 drop table if exists  `room`;
create table `room` (
    id bigint not null primary key auto_increment ,
    uid bigint not null comment 'uid 等同于 room id',
    stream_type_id varchar(40) not null ,
    is_alive bool comment '是否在直播',
    remark varchar(256) comment '直播简介',
    is_hot bool comment '是否是著名主播',
    is_upload_cover bool comment '是否上传封面',
    cover_url varchar(255) not null default '' comment '封面地址',
    score bigint default 0 comment '评分',
    block_num int default 0 comment '拉黑数',
    follow_num int default 0 comment '粉丝数',
    is_block bool comment '是否被封禁',
    block_type_id varchar(40) comment '封禁类型',
    block_description varchar(255) comment '封禁详情',
    is_delete bool not null default false,
    create_by varchar(40) ,
    update_by varchar(40) ,
    create_at timestamp,
    update_at timestamp
);

drop table if exists `stream_type`;
create table `stream_type` (
    id bigint not null primary key auto_increment ,
    type_id varchar(40) not null,
    parent_id varchar(40) not null,
    name varchar(40)   not null,
    is_delete bool not null default false,
    create_by varchar(40) ,
    update_by varchar(40) ,
    create_at timestamp,
    update_at timestamp
);
drop table if exists `stream_record`;
create table `stream_record` (
    id bigint not null primary key auto_increment,
    stream_id varchar(40) not null ,
    uid varchar(40) not null default '' comment '主播id',
    channel_id  varchar(40)  not null default '' comment '弹幕id',
    buy_channel_id varchar(40) not null default '' comment 'gift 购买通道',
    start_at timestamp,
    end_at timestamp,
    remark varchar(256) comment '直播简介',
    watch_num int not null default 0 comment '观看人数',
    duration int not null default 0 comment '开播时长以s为单位',
    new_fans_num bigint not null default 0,
    star_num bigint not null default 0,
    watch_fan_num bigint not null default 0 comment '粉丝观看数量' ,
    gift_num bigint not null default 0 comment '送礼人数',
    gift_cost bigint not null default 0 comment '礼物花费',
    is_normal_off bool not null default true comment '是否正常关播',
    is_block bool comment '是否被封禁',
    block_type_id varchar(40) comment '封禁类型',
    block_description varchar(255) comment '封禁详情',
    is_delete bool not null default false,
    create_by varchar(40) ,
    update_by varchar(40) ,
    create_at timestamp,
    update_at timestamp
);
drop table if exists `stream_block_type`;
create table `stream_block_type`(
    id bigint primary key auto_increment,
    block_type_id varchar(40) not null,
    description text comment '封禁详情',
    is_delete bool not null default false,
    create_by varchar(40) ,
    update_by varchar(40) ,
    create_at timestamp,
    update_at timestamp
);
 drop table if exists `follow_apply` ;
 create table `follow_apply` (
                                 id bigint primary key auto_increment,
                                 uid varchar(40) not null default '关注者id',
                                 to_uid varchar(40) not null default '' comment '被关注id',
                                 source int  not null default 0 comment '业务来源',
                                 is_block bool not null default false comment '是否被封禁',
                                 is_delete bool not null default false,
                                 create_by varchar(40) ,
                                 update_by varchar(40) ,
                                 create_at timestamp,
                                 update_at timestamp
 );
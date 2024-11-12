drop table if exists  `wallet`;
create table `wallet`
(
    id  bigint      not null primary key auto_increment,
    uid varchar(40) not null default '' comment '用户id',
    vc_outcome bigint unsigned not null default 0 comment 'vc 收入总数量',
    vc_outcome bigint unsigned not null default 0 comment 'vc 使用总数量',
    vc_fee bigint unsigned not null default 0 comment 'vc 剩余总数量',
    vc_locked bigint not null default 0 comment '被锁定的vc',
    is_anchor bool not null default false comment '是否为主播',
    is_block bool not null default false comment '是否被锁定',
    sec_question varchar(255) not null default '' comment '安全问题，用于解锁钱包',
    sec_answer varchar(255) not null default '' comment  '安全问题答案',
    is_delete bool ,
    create_at timestamp,
    update_at timestamp
);
drop table if exists `wallet_outcome`;
create table `wallet_outcome` (
    id bigint unsigned not null primary key auto_increment,
    uid int not null default 0 comment '用户id',
    to_uid bigint not null default 0 comment 'id',
    channel bigint not null default 0 comment '频道id',
    category smallint not null comment '频道类型',
    category smallint not null comment '交易类型',
    cost long not null default 0 comment '花费',
    is_delete bool ,
    create_at timestamp,
    update_at timestamp
);
drop table if exists `wallet_outcome`;
create table `wallet_outcome` (
    id bigint unsigned not null primary key auto_increment,
    uid int not null default 0 comment '用户id',
    to_uid int not null default 0 comment 'id',
    channel_id int not null default 0 comment '频道id',
    channel_type smallint not null comment '频道类型',
    category smallint not null comment '交易类型',
    cost long not null default 0 comment '花费',
    is_delete bool ,
    create_at timestamp,
    update_at timestamp
);
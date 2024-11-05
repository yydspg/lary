drop table if exists `raffle_event`;
create table `raffle_event` (
     id bigint unsigned not null auto_increment primary key ,
     stream_id bigint unsigned not null  comment '直播id',
     uid bigint unsigned not null comment '用户id',
     category int unsigned not null comment '抽奖类型',
     param varchar(255) not null comment '抽奖条件参数',
     num int unsigned not null  comment '抽奖人数',
     sync_status int not null comment '同步状态',
     title varchar(256) not null comment '抽奖标题',
     content varchar(256) not null default '' comment '抽奖物品描述',
     item_num int not null default 0 comment '物品数目',
     is_delete bool not null default false,
     create_at timestamp
);

drop table if exists `raffle_reocrd`;
create table `raffle_record` (
      id bigint unsigned not null auto_increment primary key ,
      uid bigint unsigned not null comment '用户id',
      stream_id bigint unsigned not null  comment '直播id',
      to_uid bigint unsigned not null comment '用户id',
      content varchar(256) not null default '' comment '抽奖物品描述',
      item_num int not null default 0 comment '物品数目',
      sync_status int not null comment '同步状态',
      is_delete bool not null default false,
      create_at timestamp
);
-- +migrate Up

drop table if exists `lary_channel`;
create table `lary_channel`(
          id    bigint     not null primary key AUTO_INCREMENT,
            type            int               not null default 0,
            uid integer not null default '' comment '用户id',
            isDelete bool default false,
          create_at              -- 创建时间
          -- 更新时间
);


drop table if exists `lary_channel`;
create table `lary_channel`(
    id bigint unsigned primary key auto_increment,
    cid bigint unsigned not null comment '频道id',
    type bigint  unsigned not null comment '频道类型',
    uid bigint unsigned not null comment '用户id',
    rid bigint unsigned not null comment '直播间id',
    status int unsigned comment '状态',
    create_at bigint unsigned
);
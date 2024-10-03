-- +migrate Up

drop table if exists `lary_channel`;
create table `lary_channel`(
          id                      integer                not null primary key AUTO_INCREMENT,
          channel_type            smallint               not null default 0,
            uid integer not null default '' comment '用户id',
            isDelete bool default false,
          create_at              timeStamp              not null DEFAULT CURRENT_TIMESTAMP,      -- 创建时间
          update_at              timeStamp              not null DEFAULT CURRENT_TIMESTAMP       -- 更新时间
);



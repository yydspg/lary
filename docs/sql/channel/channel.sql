-- +migrate Up


create table `lary_channel`(
                                  id                      integer                not null primary key AUTO_INCREMENT,
                                  channel_id              VARCHAR(40)            not null default '',
                                  channel_type            smallint               not null default 0,
                                uid varchar(40) not null default '' comment '用户id',
                                  create_at              timeStamp              not null DEFAULT CURRENT_TIMESTAMP,      -- 创建时间
                                  update_at              timeStamp              not null DEFAULT CURRENT_TIMESTAMP       -- 更新时间
);



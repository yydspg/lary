drop table if exists `user_setting`;
create table `user_setting`(
                               id bigint not null auto_increment primary key ,
                               uid int unsigned not null  ,
                               is_show_fan_list bool not null default true comment '粉丝列表是否展示',
                               is_show_medal bool not null default true comment '勋章是否展示',
                               is_show_dynamic bool not null default true comment '动态是否展示',
                               is_no_disturb bool default false comment '新消息提醒',
                               is_delete bool not null default false,
                               create_at timestamp ,
                               update_at timestamp
);

drop table if exists `user_index`;
create table `user_index` (
    id bigint unsigned not null auto_increment primary key ,
    phone char(11) not null comment '手机号',
    username varchar(11) not null comment '用户名',
    external varchar(256) not null comment 'Oauth',
    oid int not null comment 'Oauth id',
    create_at bigint not null default unix_timestamp()
);
create unique index phone_idx on `user_index` (phone);
create unique index username_idx on `user_index` (username);
create unique index external_idx on `user_index` (external);
drop table if exists `user`;
create table `user` (
    uid varchar not null primary key ,
    name varchar not null ,
    sex smallint not null default 1 comment '0 is man,1 is woman',
    birthday varchar not null ,
    regin varchar ,
    bio varchar ,
    level int not null default 1,
    avatar_file_name varchar not null ,
    avatar_url varchar not null,
    deleted bool not null default false,
    is_anchor bool not null default false,
    anchor_announce varchar ,
    create_at timestamp ,
    update_at timestamp,
);
drop table if exists 'user_home_setting';
create table `user_setting`(
    uid varchar not null primary key ,
    fan_list bool not null default true,
    medal bool not null default true,
    dynamic bool not null default true,
    create_at timestamp ,
    update_at timestamp,
);
drop table if exists 'follow';
create table `follow`(
    uid varchar not null primary key ,
    to_uid varchar not null ,
    -- this below all belong to to_uid
    fan_num int not null default 0,
    name varchar not null ,
    avatar_file_name varchar not null ,
    avatar_url varchar not null,
    is_anchor bool not null default false,
    create_at timestamp ,
    update_at timestamp,
);
drop table if exists 'user_detail';
create table  `user_detail` (
    uid varchar not null primary key ,
    fan_num int not null default 0,
    star_num int not null default 0,
    level int not null default 0 comment 'user level',
    create_at timestamp ,
    update_at timestamp,
)
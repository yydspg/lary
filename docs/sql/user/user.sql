use lary;
drop table if exists `user`;
create table `user` (
                        uid varchar(40)  not null primary key ,
                        name varchar(40)  not null ,
                        sex tinyint not null default 1 comment '0 is man,1 is woman',
                        birthday varchar(80)  not null ,
                        regin varchar(80)  ,
                        bio varchar(256) ,
                        level int not null default 1,
                        avatar_file_name varchar(40) not null ,
                        avatar_url varchar(256) not null,
                        deleted bool not null default false,
                        is_anchor bool not null default false,
                        anchor_announce varchar(256) ,
                        create_at timestamp ,
                        update_at timestamp
);
drop table if exists `user_home_setting`;
create table `user_home_setting`(
                                    uid varchar(40) not null primary key ,
                                    fan_list bool not null default true comment '粉丝列表是否展示',
                                    medal bool not null default true comment '勋章是否展示',
                                    dynamic bool not null default true comment '动态是否展示',
                                    create_at timestamp ,
                                    update_at timestamp
);
drop table if exists `user_detail`;
create table  `user_detail` (
                                uid varchar(40) not null primary key ,
                                fan_num int not null default 0,
                                star_num int not null default 0,
                                level int not null default 0 comment 'user level',
                                create_at timestamp ,
                                update_at timestamp
);
drop table if exists `user_fan_level`;
create table `user_fan_level` (
                                  uid varchar(40)    ,
                                  anchor_id varchar(40)   ,
                                  fan_level smallint comment '粉丝等级',
                                  cost bigint comment '总计消费金额',
                                  fan_level_url varchar(255)  comment '粉丝等级图片路径',
                                  is_delete bool ,
                                  create_by varchar(40)  ,
                                  update_by varchar(40)  ,
                                  create_at timestamp,
                                  update_at timestamp
)
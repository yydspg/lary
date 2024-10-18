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
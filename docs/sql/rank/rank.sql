drop table if exists `rank_event`;
create table `rank_event` (
    id bigint unsigned primary key auto_increment not null ,
    eid bigint unsigned not null unique  comment '事件id',
    name varchar(20) not null comment '榜单id',
    len_limit int not null comment '榜单长度限制',
    sort int not null comment '榜单排序方式',
    start_at timestamp not null comment '开始时间',
    complete_at timestamp comment '结束时间',
    time int not null comment '时间类型,月,周,天,分钟',
    time_amount int not null comment '时间数量',
    item int not null comment '参与者,主播,用户,直播间,道具.etc',
    create_at timestamp,
    update_at timestamp
);

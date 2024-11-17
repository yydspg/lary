drop table if exists `invest`;
create table `invest` (
    id bigint not null auto_increment primary key ,
    lid bigint not null comment '投放id',
    uid bigint not null comment '用户id',
    rid bigint not null comment '直播间id',
    policy int not null comment '投放策略,长期,单次',
    duration bigint not null comment '持续时间',
    category int not null comment '直播开始投放,直播全程投放,当全程投放生效时,持续时间无效,立刻生效',
    create_at bigint not null comment '创建时间'
);

drop table if exists `invest_record`;
create table `invest_record`(
    id bigint not null auto_increment primary key ,
    sid bigint not null comment '直播id',
    lid bigint not null comment '投放id',
    rid bigint not null comment '直播间id',
    uid bigint not null comment 'uid',
    sync int not null comment '状态',
    create_at bigint not null comment '创建时间'
);

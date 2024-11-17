drop table if exists `advertisement`;
create table `advertisement` (
    id bigint not null auto_increment primary key ,
    aid bigint not null comment   '广告id',
    pid bigint not null comment '服务商id',
    startAt bigint not null comment '起始时间：天',
    endAt bigint not null comment '终止时间：天',
    begin bigint not null comment '起始时间：每天的分',
    off bigint not null comment '结束时间,每天的分',
    policy int not null comment '投放策略',
    remark varchar(256) not null comment '广告标语',
    source varchar(256) not null comment '广告链接',
    image varchar(256) not null comment '广告图片',
    vid bigint not null comment '虚拟id',
    username bigint not null comment '虚拟用户名称',
    avatar varchar(256) not null comment '虚拟用户头像',
    amount decimal(10,2) not null comment '投入',
    click bigint not null comment '点击数',
    create_at bigint not null comment '创建时间'
);

drop table if exists `prodiver`;
create table `provider` (
    id   bigint not null auto_increment primary key ,
    pid bigint not null comment '服务商id',
    name bigint not null comment '名称',
    amount bigint not null comment '总投入',
    level int not null comment '服务商等级',
    create_at bigint not null comment '创建时间'
)
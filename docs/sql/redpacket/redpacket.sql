drop table if exists `redpacket_event`;
create table `redpacket_event` (
    id bigint unsigned not null primary key auto_increment,
    stream bigint unsigned not null comment '直播id',
    uid bigint unsigned not null default '' comment  '用户uid',
    amount decimal(10,2) unsigned not null comment '红包金额',
    title varchar(40) not null comment '红包标题',
    num int unsigned not null  comment '红包人数',
    category int not null comment '红包类型',
    out_trade_no varchar(40) not null  comment '外部订单号',
    status int not null default 0 comment '购买状态 0 未支付，1 已支付 2 取消支付 3 支付失败',
    sync_status int not null comment '同步状态',
    complete_at timestamp comment '完成时间',
    notify_url varchar(40) comment '异步通知地址',
    fail_reason varchar(255) comment '上游异常原因',
    pay_way int not null comment '支付来源',
    start_at timestamp not null comment '开启时间',
    duration timestamp not null comment '持续时间',
    is_delete bool not null default false,
    create_at timestamp
);


drop table if exists `redpacket_reocrd`;
create table `redpacket_record` (
    id bigint unsigned not null auto_increment primary key ,
    uid bigint unsigned not null comment '用户id',
    stream_id bigint unsigned not null  comment '直播id',
    amount decimal(10,2) unsigned not null comment '红包金额',
    sync_status int not null comment '同步状态',
    is_delete bool not null default false,
    create_at timestamp
);
drop table if exists `recharge_log`;
create table `recharge_log` (
    id bigint not null primary key auto_increment,
    recharge_id varchar(40) not null default '' comment '充值id，充值编号',
    uid varchar(40) not null default '' comment  '用户uid',
    status smallint not null default 0 comment '购买状态 0 未支付，1 已支付 2 取消支付 3 支付失败',
    is_sync bool not null default false comment '是否同步，比如wallet是否同步成功',
    complete_at timestamp comment '完成时间',
    cost int unsigned not null default  0 comment '花费',
    star_num int unsigned not null default 0 comment '获得的虚拟货币数目',
    notify_url varchar(40) comment '异步通知地址',
    fail_reason varchar(255) comment '上游异常原因',
    client_type smallint comment '支付来源',
    is_delete bool not null default false,
    create_by varchar(40) ,
    update_by varchar(40) ,
    create_at timestamp,
    update_at timestamp
);
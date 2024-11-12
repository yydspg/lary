drop table if exists  `wallet`;
create table `wallet`
(
    id  bigint      not null primary key auto_increment,
    uid  bigint not null  comment '用户id',
    outcome decimal(10,2)  not null  comment '收入流水总金额',
    outcome decimal(10,2)  not null  comment '支出流水总金额',
    amount decimal(10,2) not null comment '总金额',
    vc_count bigint not null comment '虚拟货币数量',
    role int not null comment '用户角色',
    status int not null comment '钱包状态',
    is_delete bool ,
    create_at timestamp,
    update_at timestamp
);
drop table if exists `wallet_outcome`;
create table `wallet_outcome` (
                                 id bigint unsigned not null primary key auto_increment,
                                 uid bigint not null default 0 comment '用户id',
                                 to_uid bigint not null default 0 comment 'id',
                                 channel bigint not null default 0 comment '频道id',
                                 category smallint not null comment '频道类型',
                                 category smallint not null comment '交易类型',
                                 cost long not null default 0 comment '花费',
                                sync_status int not null comment '同步状态',
                                 is_delete bool ,
                                 create_at timestamp
);
drop table if exists `wallet_outcome`;
create table `wallet_outcome` (
                                  id bigint unsigned not null primary key auto_increment,
                                  uid bigint not null default 0 comment '用户id',
                                  to_uid bigint not null default 0 comment 'id',
                                  channel bigint not null default 0 comment '频道id',
                                  category smallint not null comment '频道类型',
                                  category int not null comment '交易类型',
                                  amount decimal(10,2) not null default 0 comment '花费',
                                  sync_status int not null comment '同步状态',
                                  is_delete bool ,
                                  create_at timestamp
);

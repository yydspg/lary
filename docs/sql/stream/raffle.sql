drop table if exists `raffle`;
create table `raffle` (
    id bigint unsigned not null auto_increment primary key ,
    uid int unsigned not null comment '用户id',
    stream_id int unsigned not null  comment '直播id',
    category smallint unsigned not null comment '抽奖类型',
    param json not null comment '抽奖条件参数',
    num int unsigned not null  comment '抽奖人数',
    is_sync bool not null  comment '是否同步成功',
    title varchar(256) not null comment '抽奖标题',
    content varchar(256) not null default '' comment '抽奖物品描述',
    item_num int not null default 0 comment '物品数目',
    is_delete bool not null default false,
    create_at timestamp,
    update_at timestamp
)
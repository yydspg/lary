drop table if exists `red_packet`;
create table  `red_packet`(
    id  bigint unsigned not null auto_increment primary key ,
    uid int unsigned not null comment '用户id',
    stream_id int unsigned not null  comment '直播id',
    vc int unsigned not null comment '虚拟货币数量',
    num int unsigned not null comment '红包数目',
    vc_all int unsigned not null comment '红包总金额',
    is_sync bool not null comment '是否同步成功',
    title varchar(256) not null comment '红包标题',

    is_delete bool not null default false,
    create_at timestamp,
    update_at timestamp
)
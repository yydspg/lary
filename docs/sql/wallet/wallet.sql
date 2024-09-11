drop table if exists  `wallet`;
create table `wallet`
(
    id  bigint      not null primary key auto_increment,
    uid varchar(40) not null default '' comment '用户id',
    star_num int not null default 0 comment 'vc 数量',
    is_block bool not null default false comment '是否被锁定',
    is_delete bool ,
    create_by varchar(40) ,
    update_by varchar(40) ,
    create_at timestamp,
    update_at timestamp
)
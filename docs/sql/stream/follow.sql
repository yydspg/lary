drop table if exists `follow`;
create table `follow` (
                          id bigint unsigned not null primary key auto_increment,
                          uid int unsigned not null comment '用户id',
                          to_uid int unsigned not null comment '被关注id',
                          username varchar(40) not null default '' comment '被关注姓名',
                          source smallint not null default 0 comment '业务来源',
                          bio varchar(256) not null default '' comment '被关注者简介',
                          avatar varchar(256) not null default '' comment '被关注者头像',
                          remark varchar(40) not null default '' comment '备注',
                          is_anchor bool not null default true comment '是否是主播',
                          is_block bool not null default false comment '是否被拉黑',
                          is_unfollow bool not null default false comment '是否不关注,取消关注后此字段为 true',
                          is_one_way bool not null default false comment '是否是单向好友',
                          is_delete bool not null default false comment '是否删除',
                          level int not null default 0 comment '粉丝等级',
                          cost long not null  comment '对主播的花费',
                          create_at timestamp ,
                          update_at timestamp
) ;
drop table if exists `follow_setting`;
create table `follow_setting` (
                                  id bigint unsigned not null primary key auto_increment,
                                  uid int unsigned not null comment '用户id',
                                  to_uid int unsigned not null comment '被关注id',
                                  is_mute bool default false comment '是否静默',
                                  is_top bool default false comment '是否置顶',
                                  is_delete bool not null default false comment '是否删除',
                                  create_at timestamp,
                                  update_at timestamp
);
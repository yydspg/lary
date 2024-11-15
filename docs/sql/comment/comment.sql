use lary;
drop table if exists `comment`;
create table `comment`(
                          id bigint unsigned not null auto_increment primary key ,
                          cid bigint unsigned not null comment '评论id',
#     sid bigint unsigned not null  comment '评论root id',
#     pid bigint unsigned not null comment '评论父id',
                          eid bigint unsigned not null comment '评论事件id',
                          uid bigint unsigned not null comment '用户uid',
                          name varchar(20) not null comment '用户名',
                          avatar varchar(255) not null comment '用户头像',
                          content varchar(1024) not null comment '评论内容',
                          mentions varchar(1024) not null comment '被提及用户',
                          images varchar(1024) not null comment '评论图片',
                          level int not null comment '评论等级',
                          policy int not null comment '评论策略',
                          ip_label varchar(40) not null comment '评论Ip',
                          status int not null  comment '评论状态',
                          reply_count int not null comment '回复数量',
                          star_count int not null comment '点赞数量',
                          create_at bigint not null comment '创建时间'
);
create index idx_eid on `comment` (eid);

drop table if exists `next_comment`;
create table `next_comment`(
                               id bigint unsigned not null auto_increment primary key ,
                               cid bigint unsigned not null comment '评论id',
                               rid bigint unsigned not null  comment '评论root id',
                               pid bigint unsigned not null comment '评论父id',
#                           eid bigint unsigned not null comment '评论事件id',
                               uid bigint unsigned not null comment '用户uid',
                               name varchar(20) not null comment '用户名',
                               avatar varchar(255) not null comment '用户头像',
                               content varchar(1024) not null comment '评论内容',
                               mentions varchar(1024) not null comment '被提及用户',
                               images varchar(1024) not null comment '评论图片',
                               level int not null comment '评论等级',
                               policy int not null comment '评论策略',
                               ip_label varchar(40) not null comment '评论Ip',
                               status int not null  comment '评论状态',
                               reply_count int not null comment '回复数量',
                               star_count int not null comment '点赞数量',
                               create_at bigint not null comment '创建时间'
);
create index idx_rid on `next_comment` (rid);

drop table if exists `comment_event`;
create table `comment_event`(
                                id bigint unsigned not null auto_increment primary key ,
                                eid bigint unsigned not null comment '评论事件id',
                                tid bigint unsigned not null  comment '事件类型id',
                                uid bigint unsigned not null comment '用户id',
                                content varchar(255) not null comment '事件描述',
                                create_at bigint not null comment '创建时间'
)
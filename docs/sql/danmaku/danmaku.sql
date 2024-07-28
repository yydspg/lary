drop table if exists `danmaku`;

create table `danmaku` (
    id bigint ,
    danmaku_type smallint ,
    digg_count int comment '点赞数量',
    has_emoji bool default false,
    from_copy bool default false,
    dislike smallint ,
    score float default 0,
    item_id  bigint not null comment 'video_id',
    offset_time int ,
    text text not null,
    uid bigint not null,
    create_at timestamp,
    update_at timestamp
);

drop table if exists `danmaku_dislike_type`;

create table `danmaku_dislike_type` (
    id bigint ,
    dislike_name varchar(40)  ,
    is_delete bool ,
    create_by varchar(40) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci ,
    update_by varchar(40) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci ,
    create_at timestamp,
    update_at timestamp
);
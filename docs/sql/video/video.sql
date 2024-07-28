drop table if exists `video`;

create table `video` (
    video_id long not null primary key ,
    uid long not null comment 'user_id',
    video_name varchar(40) not null,
    remark text not null,
    key_word varchar(128) not null,
    video_category int not null ,
    view_num long not null default 0,
    avatar_url varchar(256) not null ,
    url varchar(256) not null ,
    file_name varchar(40) not null,
    is_deleted bool not null default false,
    fps int not null ,
    encoding_type varchar(20) not null,
    video_format varchar(20) not null,
    create_at timestamp ,
    update_at timestamp
);
drop table if exists  `video_category`;
create table `video_category` (
    video_category int not null primary key ,
    name varchar(40) not null,
    create_at timestamp ,
    update_at timestamp
)
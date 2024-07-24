drop table if exists `gift`;

create table  `gift` (
    id int auto_increment primary key ,
    type int not null default 0 comment 'gift type',
    type_name varchar not null ,
    price int not null default 0 comment 'virtual currency price',
    name varchar not null,
    purchase_num bigint not null default 0,
    picture_file_name varchar not null,
    picture_url varchar not null,
    create_at timestamp ,
    update_at timestamp,
);

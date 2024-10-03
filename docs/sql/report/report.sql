create table IF NOT EXISTS  `report`
(
    id integer PRIMARY KEY AUTO_INCREMENT,
    uid         integer  not null DEFAULT '' comment '举报用户',
    category_no integer  not null DEFAULT '' comment '类别编号',
    channel_id  integer  not null DEFAULT '' comment '频道ID',
    channel_type smallint  not null DEFAULT 0 comment '频道类型',
    image_url       VARCHAR(1000) not null DEFAULT '' comment '图片路径集合',
    remark      VARCHAR(800) not null DEFAULT '' comment '备注',
    status smallint not null default 0 comment '状态 0,未处理，1,已处理',
    created_at timeStamp    not null DEFAULT CURRENT_TIMESTAMP,
    updated_at timeStamp    not null DEFAULT CURRENT_TIMESTAMP
);
drop table if exists `report_category`;
create table IF NOT EXISTS  `report_category`
(
    id integer PRIMARY KEY AUTO_INCREMENT comment '类别编号',
    name VARCHAR(40)  not null DEFAULT '' comment '类别名称',
    parent_id integer  not null DEFAULT '' comment '父类别编号',
    created_at timeStamp    not null DEFAULT CURRENT_TIMESTAMP,
    updated_at timeStamp    not null DEFAULT CURRENT_TIMESTAMP
);
drop table if exists `payment`;
create table `payment` (
    id bigint not null primary key auto_increment,
    uid varchar(40)  not null default '' comment '用户uid',
    order_id varchar(40) not null default '' comment '业务订单编号，id',
    order_type tinyint not null default 0 comment '订单类型 1 充值钱包 2 购买礼物,根据业务类型变更',
    trade_no varchar(64) not null default '' comment '第三方支付成功交易号',
    pay_way tinyint not null default 0 comment '支付方式 1 支付宝 2 微信支付',
    pay_status tinyint not null default 0 comment '支付状态 1 成功 2 失败',
    pay_cost decimal(10,2) not null default 0 comment '支付金额',
    post_status tinyint default 0 COMMENT '请求支付状态，1：成功，2：失败',
    return_status tinyint DEFAULT 0  COMMENT '回调状态，1：成功，2：失败',
    post_json varchar(2000) DEFAULT '' COMMENT '提交post json 数据',
    return_json varchar(2000) DEFAULT '' COMMENT '回调post return data json',
    result_code  varchar(30) DEFAULT '' COMMENT '第三方返回错误码',
    err_code_str  varchar(255) DEFAULT '' COMMENT '第三方返回的错误记录',
    is_delete bool not null default false,
    create_by varchar(40) ,
    update_by varchar(40) ,
    create_at timestamp,
    update_at timestamp
);

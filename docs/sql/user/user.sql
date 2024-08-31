use lary;
drop table if exists `user`;
create table `user` (
                        id bigint not null auto_increment primary key ,
                        uid varchar(40)  not null  ,
                        name varchar(40)  not null ,
                        short_no varchar(40) not null default '' comment '短编码',
                        sex smallint not null default 1 comment '0 is man,1 is woman',
                        username varchar(40) not null default '',
                        password varchar(40) not null default '',
                        is_upload_avatar bool not null default false comment '是否上传密码',
                        birthday varchar(80)  not null ,
                        regin varchar(40) not null default '' ,
                        is_robot bool not null default false,
                        bio varchar(256) ,
                        level int not null default 1,
                        email varchar(100) not null default '',
                        avatar_url varchar(256) not null,
                        deleted bool not null default false,
                        is_anchor bool not null default false,
                        anchor_announce varchar(256) ,
                        create_at timestamp ,
                        update_at timestamp
);
drop table if exists `user_setting`;
create table `user_setting`(
                               id bigint not null auto_increment primary key ,
                               uid varchar(40) not null  ,
                               fan_list bool not null default true comment '粉丝列表是否展示',
                               medal bool not null default true comment '勋章是否展示',
                               dynamic bool not null default true comment '动态是否展示',
                               new_message_notice bool default true comment '新消息提醒',
                               create_at timestamp ,
                               update_at timestamp
);
drop table if exists `user_detail`;
create table  `user_detail` (
                                id bigint not null auto_increment primary key ,
                                uid varchar(40) not null ,
                                fan_num int not null default 0,
                                star_num int not null default 0,
                                level int not null default 0 comment 'user level',
                                create_at timestamp ,
                                update_at timestamp
);
drop table if exists `user_fan_level`;
create table `user_fan_level` (
                                  id bigint not null auto_increment primary key ,
                                  uid varchar(40)    ,
                                  anchor_id varchar(40)   ,
                                  fan_level smallint comment '粉丝等级',
                                  cost bigint comment '总计消费金额',
                                  fan_level_url varchar(255)  comment '粉丝等级图片路径',
                                  is_delete bool ,
                                  create_by varchar(40)  ,
                                  update_by varchar(40)  ,
                                  create_at timestamp,
                                  update_at timestamp
);
drop table if exists `device`;
create table `device`
(
    id                integer             not null primary key AUTO_INCREMENT,
    uid               VARCHAR(40)         not null default '',                      -- 设备所属用户uid
    device_id         VARCHAR(40)         not null default '',                      -- 设备唯一ID
    device_name       VARCHAR(100)        not null default '',                      -- 设备名称
    device_model      VARCHAR(100)        not null default '',                      -- 设备型号
    last_login        integer             not null DEFAULT 0,                       -- 最后一次登录时间(时间戳 10位)
    created_at        timeStamp           not null DEFAULT CURRENT_TIMESTAMP,       -- 创建时间
    updated_at        timeStamp           not null DEFAULT CURRENT_TIMESTAMP        -- 更新时间
);
CREATE unique INDEX device_uid_device_id on `device` (uid, device_id);
CREATE INDEX device_uid on `device` (uid);
CREATE INDEX device_device_id on `device` (device_id);
drop table if exists `friend`;
create table `friend`
(
    id                integer               not null primary key AUTO_INCREMENT,
    uid               VARCHAR(40)           not null default '' comment '用户UID',
    to_uid            VARCHAR(40)           not null default '' comment '好友uid',
    flag              smallint              not null default 0 comment '好友标示',
    version           bigint                not null default 0 comment '版本号',
    vercode           VARCHAR(100)          not null default '' comment '验证码 加好友来源',
    source_vercode    varchar(100)          not null default '' comment '好友来源',
    is_deleted        smallint              not null default 0 comment '是否已删除',
    is_alone          smallint              not null default 0 comment  '单项好友',
    initiator         smallint              not null default 0 comment '加好友发起方',
    created_at        timeStamp             not null DEFAULT CURRENT_TIMESTAMP comment '创建时间',
    updated_at        timeStamp             not null DEFAULT CURRENT_TIMESTAMP comment '更新时间'
);
drop table if exists `login_log`;
CREATE TABLE IF NOT EXISTS login_log(
                                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                        uid VARCHAR(40) DEFAULT '' NOT NULL  COMMENT '用户OpenId',
                                        login_ip    VARCHAR(40) DEFAULT '' NOT NULL COMMENT '最后一次登录ip',
                                        created_at  timeStamp     not null DEFAULT CURRENT_TIMESTAMP comment '创建时间',
                                        updated_at  timeStamp     not null DEFAULT CURRENT_TIMESTAMP comment '更新时间'
) CHARACTER SET utf8mb4;
drop table if exists `device_flag`;
create table `device_flag`
(
    id         bigint         not null primary key AUTO_INCREMENT,
    device_flag  smallint    not null default 0 COMMENT '设备标记 0. app 1.Web 2.PC',
    `weight`       integer       not null default 0 COMMENT '设备权重 值越大越优先',
    remark    VARCHAR(100)    not null default '' COMMENT '备注',
    created_at timeStamp      not null DEFAULT CURRENT_TIMESTAMP, -- 创建时间
    updated_at timeStamp      not null DEFAULT CURRENT_TIMESTAMP  -- 更新时间
);

CREATE UNIQUE INDEX `udx_device_flag` on `device_flag` (`device_flag`);


insert into device_flag(device_flag,`weight`,remark) values(2,'80000','PC');
insert into device_flag(device_flag,`weight`,remark) values(1,'70000','Web');
insert into device_flag(device_flag,`weight`,remark) values(0,'90000','手机');
drop table if exists `friend_apply_record`;
create table `friend_apply_record`
(
    id         bigint         not null primary key AUTO_INCREMENT,
    uid        VARCHAR(40)    not null default '',                -- 用户uid
    to_uid     VARCHAR(40)    not null default '',                -- 申请者uid
    remark     VARCHAR(200)   not null default '',                -- 申请备注
    status     smallint       not null DEFAULT 1,                 -- 状态 0.未处理 1.通过 2.拒绝
    token      VARCHAR(200)   not null default '',                -- 通过好友所需验证
    created_at timeStamp      not null DEFAULT CURRENT_TIMESTAMP, -- 创建时间
    updated_at timeStamp      not null DEFAULT CURRENT_TIMESTAMP  -- 更新时间
);

CREATE INDEX `friend_apply_record_uidx` on `friend_apply_record` (`uid`);
CREATE UNIQUE INDEX `friend_apply_record_uid_touidx` on `friend_apply_record` (`uid`,`to_uid`);
drop table if exists `user_red_dot`;
-- 用户业务红点
CREATE table `user_red_dot`(
                               id         bigint         not null primary key AUTO_INCREMENT,
                               uid        VARCHAR(40)    not null default '',                -- 用户uid
                               count      smallint       not null default 0,                 -- 未读数量
                               category   VARCHAR(40)    not null default '',                -- 红点分类
                               is_dot     smallint       not null default 0,                 -- 是否显示红点 1.是 0.否
                               created_at timeStamp      not null DEFAULT CURRENT_TIMESTAMP, -- 创建时间
                               updated_at timeStamp      not null DEFAULT CURRENT_TIMESTAMP  -- 更新时间
);

CREATE UNIQUE INDEX `user_red_dot_uid_categoryx` on `user_red_dot` (`uid`,`category`);
drop table if exists `user_online`;
-- 用户在线表，理解为登陆日志
create table `user_online`
(
    id         bigint        not null primary key AUTO_INCREMENT,
    uid        VARCHAR(40)   not null default '', -- 用户uid
    device_flag      smallint    not null default 0, -- 设备flag 0.APP 1. WEB
    last_online integer       not null DEFAULT 0, -- 最后一次在线时间
    last_offline integer     not null DEFAULT 0, -- 最后一次离线时间
    online     tinyint(1)     not null default 0, -- 用户是否在线
    `version`    bigint       not null default 0, -- 数据版本
    created_at timeStamp     not null DEFAULT CURRENT_TIMESTAMP, -- 创建时间
    updated_at timeStamp     not null DEFAULT CURRENT_TIMESTAMP  -- 更新时间
);

CREATE UNIQUE INDEX `uid_device` on `user_online` (`uid`,device_flag);

CREATE  INDEX `online_idx` on `user_online` (`online`);
CREATE  INDEX `uid_idx` on `user_online` (`uid`);
drop table if exists `friend_setting`;
create table `friend_setting`
(
    id               integer       not null primary key AUTO_INCREMENT,
    uid              VARCHAR(40)   not null default '',                              -- 用户UID
    to_uid           VARCHAR(40)   not null default '',                              -- 对方uid
    mute             smallint      not null DEFAULT 0,                               --  是否免打扰
    remark           varchar(100) not null default '',
    top              smallint      not null DEFAULT 0,                               -- 是否置顶
    blacklist        smallint      not null DEFAULT 0,                               -- 是否黑名单 0:正常1:黑名单
    chat_pwd_on      smallint      not null DEFAULT 0,                               -- 是否开启聊天密码
    screenshot       smallint      not null DEFAULT 1,                               -- 截屏通知
    revoke_remind    smallint      not null DEFAULT 1,                               -- 撤回通知
    receipt          smallint      not null default 1,                               -- 消息是否回执
    version          BIGINT        not null DEFAULT 0,                               -- 版本
    created_at       timeStamp     not null DEFAULT CURRENT_TIMESTAMP,               -- 创建时间
    updated_at       timeStamp     not null DEFAULT CURRENT_TIMESTAMP                -- 更新时间
);

CREATE  INDEX uid_idx on `user_setting` (uid);
-- 用户身份表 （signal protocol使用）
drop table if exists `signal_identities`;
create table `signal_identities`
(
    id         bigint        not null primary key AUTO_INCREMENT,
    uid         varchar(40) not null DEFAULT '', --  用户uid
    registration_id bigint  not null DEFAULT 0, -- 身份ID
    identity_key text     not null, -- 用户身份公钥
    signed_prekey_id integer not null DEFAULT 0, -- 签名key的id
    signed_pubkey text       not null,  -- 签名key的公钥
    signed_signature   text          not null, -- 由身份密钥签名的signed_pubkey
    created_at timeStamp     not null DEFAULT CURRENT_TIMESTAMP, -- 创建时间
    updated_at timeStamp     not null DEFAULT CURRENT_TIMESTAMP  -- 更新时间
);

CREATE UNIQUE INDEX  identities_index_id ON signal_identities(uid);

drop table if exists `signal_onetime_prekeys`;
-- 一次性公钥
create table `signal_onetime_prekeys`
(
    id         bigint        not null primary key AUTO_INCREMENT,
    uid         varchar(40) not null DEFAULT '', -- 用户uid
    key_id     integer not null DEFAULT 0,
    pubkey   text           not null,   -- 公钥
    created_at timeStamp     not null DEFAULT CURRENT_TIMESTAMP, -- 创建时间
    updated_at timeStamp     not null DEFAULT CURRENT_TIMESTAMP  -- 更新时间
);
CREATE UNIQUE INDEX  key_id_uid_index_id ON signal_onetime_prekeys(uid,key_id);
drop table if exists `short_no`;
create table `short_no`
(
    id         bigint         not null primary key AUTO_INCREMENT,
    short_no        VARCHAR(40)    not null default '' COMMENT '唯一短编号',
    used       smallint       not null default 0 COMMENT '是否被用',
    hold       smallint       not null default 0 COMMENT '保留，保留的号码将不会再被分配',
    locked       smallint       not null default 0 COMMENT '是否被锁定，锁定了的短编号将不再被分配,直到解锁',
    business    VARCHAR(40)    not null default '' COMMENT '被使用的业务，比如 user',
    created_at timeStamp      not null DEFAULT CURRENT_TIMESTAMP, -- 创建时间
    updated_at timeStamp      not null DEFAULT CURRENT_TIMESTAMP  -- 更新时间
);

CREATE UNIQUE INDEX `udx_short_no` on `short_no` (`short_no`);
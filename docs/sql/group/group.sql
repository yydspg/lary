-- +migrate Up


-- 群组
drop table if exists `group`;
create table `group`
(
    id         bigint     not null primary key AUTO_INCREMENT,
    group_id   int unsigned not null comment '群id',
    name       int unsigned not null comment '群名字',
    creator    int unsigned not null comment '群主uid',
    status     int    not null DEFAULT 0 comment '群状态',
    is_forbidden   bool    not null DEFAULT false comment '群禁言',
    group_avatar varchar(255) not null default '' comment '群头像路径',
    group_num int not null default 0 comment '群人数',
    is_upload_avatar bool comment '群头像是否已经被上传',
    group_type int not null default 0 comment '群类型 0. 普通群，1.超大群',
    category varchar(40) not null default 0 comment '群分类',
    is_enable_invite    bool    not null DEFAULT true comment '群邀请开关',
    is_forbidden_add_friend   bool     not null DEFAULT false comment '是否禁止群内加好友',
    is_allow_view_history_msg bool    not null DEFAULT true comment '是否允许新成员查看历史消息',
    is_delete bool ,
    create_at timestamp,
    update_at timestamp
);


-- -- +migrate StatementBegin
-- CREATE TRIGGER group_updated_at
--   BEFORE UPDATE
--   ON `group` for each row
--   BEGIN
--     set NEW.updated_at = NOW();
--   END;
-- -- +migrate StatementEnd

-- 群成员
create table `group_member`
(
    id         bigint    not null primary key AUTO_INCREMENT,
    group_no   VARCHAR(40) not null default '',                             -- 群唯一编号
    uid        VARCHAR(40) not null default '',                             -- 成员uid
    remark     VARCHAR(40) not null default '',                             -- 成员备注
    role       smallint    NOT NULL DEFAULT 0,                              -- 成员角色 '0.普通成员 1.群主  2.管理员',
    `version`    bigint     not null DEFAULT 0,                               -- 数据版本
    is_deleted smallint    not null default 0,                              -- 是否已删除
    status     smallint   not null default 1,                                   -- 成员状态
    vercode    VARCHAR(100) not null default '',                 -- 验证码 加好友来源
    robot      smallint     not null default 0,                  -- 机器人标识0.否1.是
    invite_uid VARCHAR(40) not null default '',                  -- 邀请人uid
    forbidden_expire_time int not null default 0 comment '群成员禁言时长',
    create_at timeStamp     not null DEFAULT CURRENT_TIMESTAMP, -- 创建时间
    update_at timeStamp     not null DEFAULT CURRENT_TIMESTAMP  -- 更新时间
);


-- 群设置
create table `group_setting`
(
    id                        bigint       not null primary key AUTO_INCREMENT,
    uid                       VARCHAR(40)   not null default '',                             -- 用户uid
    group_no                  VARCHAR(40)   not null default '',                             -- 群唯一编号
    remark                    VARCHAR(100) not null default '',                              -- 群备注
    mute                      bool       DEFAULT false,                              --  是否免打扰
    top                       bool       DEFAULT false,                              -- 是否置顶
    show_nick                 bool       DEFAULT false,                              -- 是否显示昵称
    save                      bool       DEFAULT false,                              -- 是否保存
    chat_pwd_on               bool       DEFAULT false,                              -- 是否开启聊天密码
    revoke_remind             bool       DEFAULT false,                              -- 撤回提醒
    join_group_remind         bool       DEFAULT false,                              -- 进群提醒
    screenshot                bool       DEFAULT false,                              -- 截屏通知
    receipt                   bool       default false,                              -- 消息是否回执
    `version`                   BIGINT        not null DEFAULT 0,                              -- 版本
    create_at                timeStamp     not null DEFAULT CURRENT_TIMESTAMP,              -- 创建时间
    update_at                timeStamp     not null DEFAULT CURRENT_TIMESTAMP               -- 更新时间
);
CREATE unique INDEX groupsetting_group_no_uid on `group_setting` (group_no, uid);
CREATE INDEX group_setting_groupNo on `group_setting` (group_no);
CREATE INDEX group_setting_uid on `group_setting` (uid);

-- -- +migrate StatementBegin
--   CREATE TRIGGER group_setting_updated_at
--   BEFORE UPDATE
--   ON `group_setting` for each row
--   BEGIN
--     set NEW.updated_at = NOW();
--   END;
-- -- +migrate StatementEnd

alter table `group` add column notice varchar(400) not null default ''; -- 群公告


-- 群邀请
CREATE TABLE `group_invite` (
                                id         bigint     not null primary key AUTO_INCREMENT,
                                invite_no  VARCHAR(40) not null default '' comment '邀请唯一编号',
                                group_no   VARCHAR(40) not null default '' comment '群唯一编号',
                                inviter    VARCHAR(40) not null default '' comment '邀请者uid',
                                remark     VARCHAR(100) not null default '' comment '备注',
                                status     smallint  not null default 0 comment '状态： 0.待确认 1.已确认',
                                allower   VARCHAR(40)  not null default '' comment '允许此次操作的用户uid',
                                create_at timeStamp     not null DEFAULT CURRENT_TIMESTAMP comment '创建时间',
                                update_at timeStamp     not null DEFAULT CURRENT_TIMESTAMP  comment '更新时间'
);

CREATE TABLE `invite_item` (
                               id         bigint     not null primary key AUTO_INCREMENT,
                               invite_no  VARCHAR(40) not null default '' comment '邀请唯一编号',
                               group_no   VARCHAR(40) not null default '' comment '群唯一编号',
                               inviter    VARCHAR(40) not null default '' comment '邀请者uid',
                               uid        VARCHAR(40) not null default '' comment '被邀请者uid',
                               status     smallint  not null default 0 comment '状态： 0.待确认 1.已确认',
                               create_at timeStamp     not null DEFAULT CURRENT_TIMESTAMP comment '创建时间',
                               update_at timeStamp     not null DEFAULT CURRENT_TIMESTAMP  comment '更新时间'
);

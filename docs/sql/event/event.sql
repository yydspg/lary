use lary;
drop table if exists   `event`;
create table `event`
(
    id         integer       not null primary key AUTO_INCREMENT,
    event      VARCHAR(40)   not null default '',                             -- 事件标示
    type       smallint      not null default 0,                             -- 事件类型
    data       VARCHAR(3000) not null default '',                             -- 事件数据
    status     smallint      NOT NULL DEFAULT 0,                              -- 事件状态 0.待发布 1.已发布 2.发布失败,
    reason     VARCHAR(1000) not null default '',                             -- 失败原因
    version_lock integer    NOT NULL DEFAULT 0   comment '乐观锁',
    create_at timeStamp     not null DEFAULT CURRENT_TIMESTAMP, -- 创建时间
    update_at timeStamp     not null DEFAULT CURRENT_TIMESTAMP  -- 更新时间
);
CREATE INDEX event_key on `event` (event);
CREATE INDEX event_type on `event` (type);
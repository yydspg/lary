use lary;
alter table `group_member`
    modify is_deleted bool default 0 not null;
alter table group_member
    modify robot bool default 0 not null;

rename table `group` to `lary_groups`;
alter table lary_groups
    modify invite bool default 0 not null comment '群邀请开关';
alter table lary_groups
    modify forbidden_add_friend bool default 0 not null;
alter table lary_groups
    modify allow_view_history_msg bool default 1 not null;
alter table lary_groups
    modify forbidden bool default 0 not null comment '群禁言';
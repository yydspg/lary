alter table app_config
    modify new_user_join_system_group bool default 1 not null comment '注册用户是否默认加入系统群';
alter table app_config
    modify search_by_phone bool default 0 not null comment '是否可通过手机号搜索';
alter table app_config
    modify register_invite_on bool default 0 not null comment '是否开启注册邀请';
alter table app_config
    modify send_welcome_message_on bool default 1 not null comment '是否开启登录欢迎语';
alter table app_config
    modify invite_system_account_join_group_on bool default 0 not null comment '是否开启系统账号进入群聊';
alter table app_config
    modify register_user_must_complete_info_on bool default 0 not null comment '注册用户是否必须完善信息';
alter table app_config
    modify can_modify_api_url bool default 0 not null comment '是否能修改服务器地址';

use lary;
alter table `user` add column   role  VARCHAR(40)  not null default '';                            -- 用户角色  admin:管理员 superAdmin
alter table   `user` add column status smallint default 0;
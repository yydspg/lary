alter table `user` modify column birthday
     varchar(80)  not null  default '';
alter table `user` modify column avatar_url
    varchar(256)  not null  default '';
alter table `user_red_dot` modify column is_dot
    bool  not null  default true;
alter table `user_red_dot` modify column count
    int default 0;
alter table `friend_apply_record` add column is_deleted
bool not null default false;
alter table `friend` modify column is_alone
bool not null default false;
alter table `friend` change column initiator is_initiator
    bool not null default false;
alter table `device` add column is_delete
bool not null default false;
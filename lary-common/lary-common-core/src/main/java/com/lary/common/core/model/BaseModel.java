package com.lary.common.core.model;

import lombok.Data;

import java.util.Date;

/**
 * @author paul 2023/12/14
 * 阿里java开发手册： 【强制】表必备三字段：id, create_time, update_time。 说明：其中 id 必为主键，类型为 bigint
 * unsigned、单表时自增、步长为 1。create_time, update_time 的类型均为 datetime
 * 类型，前者现在时表示主动式创建，后者过去分词表示被动式更新。
 */
@Data
public class BaseModel {
    protected Date createTime;
    protected Date updateTime;
}

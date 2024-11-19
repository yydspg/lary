package cn.lary.module.advertisement.dto;

import lombok.Data;

@Data
public class ADCache {
    /**
     * 广告id
     */
    private long aid;

    /**
     * 服务商id
     */
    private long pid;
    /**
     * cron 表达式
     */
    private String cron;

    /**
     * 投放策略
     */
    private Integer policy;

    /**
     * 广告标语
     */
    private String remark;

    /**
     * 广告链接
     */
    private String source;

    /**
     * 广告图片
     */
    private String image;

    /**
     * 虚拟id
     */
    private long vid;

    /**
     * 虚拟用户名称
     */
    private String username;

    /**
     * 虚拟用户头像
     */
    private String avatar;

}

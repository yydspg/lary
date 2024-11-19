package cn.lary.module.advertisement.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 
 * </p>
 *
 * @author paul
 * @since 2024-11-17
 */
@Getter
@Setter
@Accessors(chain = true)
public class Advertisement implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 广告id
     */
    private Long aid;

    /**
     * 服务商id
     */
    private Long pid;

    /**
     * 起始时间：天
     */
    private Long startAt;

    /**
     * 终止时间：天
     */
    private Long endAt;

    /**
     * 起始时间：每天的分
     */
    private Long begin;

    /**
     * 结束时间,每天的分
     */
    private Long off;

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
    private Long vid;

    /**
     * 虚拟用户名称
     */
    private String username;

    /**
     * 虚拟用户头像
     */
    private String avatar;

    /**
     * 投入
     */
    private BigDecimal amount;

    private String sn;

    /**
     * 点击数
     */
    private Long click;
    /**
     *状态
     */
    private Integer status;
    /**
     * 事件id
     */
    private Long eid;
    /**
     * 创建时间
     */
    private Long createAt;
}

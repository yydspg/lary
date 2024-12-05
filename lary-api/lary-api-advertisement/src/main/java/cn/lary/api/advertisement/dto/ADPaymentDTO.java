package cn.lary.api.advertisement.dto;


import cn.lary.api.payment.dto.BusinessPaymentDTO;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ADPaymentDTO extends BusinessPaymentDTO {


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
}

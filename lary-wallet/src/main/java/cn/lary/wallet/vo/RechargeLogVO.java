package cn.lary.wallet.vo;



import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RechargeLogVO {
    /**
     * 充值id，充值编号
     */
    private Long rechargeId;


    /**
     * 购买状态 0 未支付，1 已支付 2 取消支付 3 支付失败
     */
    private Integer status;


    /**
     * 完成时间
     */
    private LocalDateTime completeAt;

    /*
    外部的支付订单号
     */
    private String sn;

    /**
     * 花费
     */
    private Long cost;

    /**
     * 获得的虚拟货币数目
     */
    private Long starNum;

    /**
     * 异步通知地址
     */
    private String notifyUrl;

    /**
     * 支付来源
     */
    private Integer clientType;
}

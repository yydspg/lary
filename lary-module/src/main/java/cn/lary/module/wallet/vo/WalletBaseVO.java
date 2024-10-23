package cn.lary.module.wallet.vo;

import lombok.Data;

@Data
public class WalletBaseVO {
    /**
     * vc 收入总数量
     */
    private Long vcIncome;

    /**
     * vc 使用总数量
     */
    private Long vcOutcome;

    /**
     * vc 剩余总数量
     */
    private Long vcFee;

    /**
     * 被锁定的vc
     */
    private Long vcLocked;

    /**
     * 是否为主播
     */
    private Boolean isAnchor;

    /**
     * 是否被锁定
     */
    private Boolean isBlock;

    /**
     * 安全问题，用于解锁钱包
     */
    private String question;
}

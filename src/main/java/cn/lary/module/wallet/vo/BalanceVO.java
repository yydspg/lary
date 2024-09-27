package cn.lary.module.wallet.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class BalanceVO {
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
     * 密保问题
     */
    private String question;
}

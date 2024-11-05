package cn.lary.module.wallet.vo;

import cn.lary.module.wallet.entity.Wallet;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class BalanceVO {

    /**
     * 收入流水总金额
     */
    private BigDecimal income;

    /**
     * 支出流水总金额
     */
    private BigDecimal outcome;

    /**
     * 总金额
     */
    private BigDecimal amount;

    /**
     * 虚拟货币数量
     */
    private Long vcCount;

    public BalanceVO(Wallet wallet) {
        this.income = wallet.getIncome();
        this.outcome = wallet.getOutcome();
        this.amount = wallet.getAmount();
        this.vcCount = wallet.getVcCount();
    }
}

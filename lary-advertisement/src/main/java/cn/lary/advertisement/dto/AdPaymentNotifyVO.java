package cn.lary.advertisement.dto;

import cn.lary.common.kit.StringKit;
import cn.lary.payment.component.PaymentNotifyProcessPair;
import cn.lary.payment.constant.PAYMENT;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class AdPaymentNotifyVO {

    /**
     * advertisement id
     */
    private long aid;

    private BigDecimal amount;

    private String tradeNo;

    private String reason;

    public AdPaymentNotifyVO(){}

    public AdPaymentNotifyVO(PaymentNotifyProcessPair pair) {
        if (pair.getPaymentPlugin() == PAYMENT.PLUGIN.ALI) {
            this.aid = Long.parseLong(pair.getParams().get("out_trade_no"));
            this.amount =BigDecimal.valueOf(Long.parseLong(pair.getParams().get("total_amount")));
            this.tradeNo = pair.getParams().get("trade_no");
            if (StringKit.diff(pair.getParams().get("trade_status"),"TRADE_FINISHED")
                    && StringKit.diff(pair.getParams().get("trade_status"),"TRADE_SUCCESS")){
                this.reason = pair.getParams().get("sub_code");
            }
        }
    }
}

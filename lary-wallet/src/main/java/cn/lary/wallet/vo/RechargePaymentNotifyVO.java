package cn.lary.wallet.vo;

import cn.lary.common.kit.StringKit;
import cn.lary.payment.component.PaymentNotifyProcessPair;
import cn.lary.payment.constant.PAYMENT;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class RechargePaymentNotifyVO  {

    public long rechargeId;
    private BigDecimal amount;
    private String tradeNo;
    private String failReason;

    public RechargePaymentNotifyVO(){}
    public RechargePaymentNotifyVO(PaymentNotifyProcessPair pair) {
        if(pair.getPaymentPlugin() == PAYMENT.PLUGIN.ALI){
            this.rechargeId = Long.parseLong(pair.getParams().get("out_trade_no"));
            this.amount =BigDecimal.valueOf(Long.parseLong(pair.getParams().get("total_amount")));
            this.tradeNo = pair.getParams().get("trade_no");
            if (StringKit.diff(pair.getParams().get("trade_status"),"TRADE_FINISHED")
                    && StringKit.diff(pair.getParams().get("trade_status"),"TRADE_SUCCESS")){
                this.failReason = pair.getParams().get("sub_code");
            }
        }
    }
}
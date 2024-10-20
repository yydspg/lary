package cn.lary.module.wallet.vo;

import cn.lary.kit.StringKit;
import cn.lary.module.common.constant.LARY;
import cn.lary.module.pay.component.PaymentNotifyProcessPair;
import lombok.Data;

@Data
public class RechargePaymentNotifyVO  {

    public long rechargeId;
    private long cost;
    private String tradeNo;
    private String failReason;


    public RechargePaymentNotifyVO(PaymentNotifyProcessPair pair) {
        if(pair.getPaymentPlugin() == LARY.PAYMENT.PLUGIN.ALI){
            this.rechargeId = Long.parseLong(pair.getParams().get("out_trade_no"));
            this.cost = Long.parseLong(pair.getParams().get("total_amount"));
            this.tradeNo = pair.getParams().get("trade_no");
            if (StringKit.diff(pair.getParams().get("trade_status"),"TRADE_FINISHED")
                    && StringKit.diff(pair.getParams().get("trade_status"),"TRADE_SUCCESS")){
                this.failReason = pair.getParams().get("sub_code");
            }
        }
    }
}

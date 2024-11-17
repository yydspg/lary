package cn.lary.module.gift.vo;

import cn.lary.common.kit.StringKit;
import cn.lary.module.common.constant.LARY;
import cn.lary.module.pay.component.PaymentNotifyProcessPair;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class GiftPaymentNotifyVO {

    private long oid;
    private BigDecimal amount;
    private String tradeNo;
    private String reason;

    public GiftPaymentNotifyVO() {

    }

    public GiftPaymentNotifyVO(PaymentNotifyProcessPair pair) {
        if(pair.getPaymentPlugin() == LARY.PAYMENT.PLUGIN.ALI){
            this.oid = Long.parseLong(pair.getParams().get("out_trade_no"));
            this.amount =BigDecimal.valueOf( Long.parseLong(pair.getParams().get("total_amount")));
            this.tradeNo = pair.getParams().get("trade_no");
            if (StringKit.diff(pair.getParams().get("trade_status"),"TRADE_FINISHED")
                    || StringKit.diff(pair.getParams().get("trade_status"),"TRADE_SUCCESS")){
                this.reason = pair.getParams().get("sub_code");
            }
        }
    }
}

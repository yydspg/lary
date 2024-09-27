package cn.lary.module.wallet.vo;

import cn.lary.kit.StringKit;
import cn.lary.module.common.CS.Lary;
import cn.lary.module.pay.core.PayCallbackVO;
import lombok.Data;

import java.util.Map;

@Data
public class RechargePayCallbackVO implements PayCallbackVO{

    public Long rechargeId;
    private Long cost;
    private String tradeNo;
    private String failReason;

    @Override
    public RechargePayCallbackVO of(Map<String, String> params, int payWay) {
        if(params == null || params.isEmpty()){
            return this;
        }
        if(payWay == Lary.PayWay.alipay){
            this.rechargeId = Long.parseLong(params.get("out_trade_no"));
            this.cost = Long.parseLong(params.get("total_amount"));
            this.tradeNo = params.get("trade_no");
            if (StringKit.same(params.get("trade_status"),"TRADE_FINISHED") || StringKit.same(params.get("trade_status"),"TRADE_SUCCESS")){

            } else {
                this.failReason = params.get("sub_code");
            }
        }
        return null;
    }
}

package cn.lary.module.wallet.dto;

import cn.lary.module.common.constant.LARY;
import cn.lary.module.pay.dto.PayParam;
import lombok.Data;

import java.util.Map;
@Data
public class RechargePayParam extends PayParam {

    @Override
    public PayParam of(Long payId, Long price, int clientType, Map<String, String> args) {
        super.setBiz(LARY.PayBiz.recharge);
        super.setPayId(payId);
        super.setPrice(price);
        super.setClientType(clientType);
        super.setArgs(args);
        return this;
    }
}

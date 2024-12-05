package cn.lary.api.wallet.dto;


import cn.lary.api.payment.dto.CallbackParam;
import lombok.Data;

import java.util.Map;

@Data
public class RechargeCallbackParam extends CallbackParam {

    @Override
    protected CallbackParam of(boolean ok, Map<String, String> args) {

        return this;
    }
}

package cn.lary.wallet.dto;

import cn.lary.payment.dto.CallbackParam;
import lombok.Data;

import java.util.Map;

@Data
public class RechargeCallbackParam extends CallbackParam {

    @Override
    protected CallbackParam of(boolean ok, Map<String, String> args) {

        return this;
    }
}

package cn.lary.module.pay.plugin.wechat;

import cn.lary.module.common.constant.LARY;
import cn.lary.module.pay.component.AbstractPaymentPlugin;
import cn.lary.module.pay.component.PaymentNotifyProcessPair;
import cn.lary.module.pay.component.PaymentProcessPair;
import cn.lary.module.pay.plugin.PaymentPlugin;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class WechatPaymentPlugin extends AbstractPaymentPlugin {


    @Override
    protected Map<String, String> getParamsFromRequest(HttpServletRequest request) throws Exception {
        return Map.of();
    }

    @Override
    protected boolean verificationSign(Map<String, String> params) throws Exception {
        return false;
    }

    @Override
    protected void whenVerificationSignFail(PaymentNotifyProcessPair pair) {

    }

    @Override
    protected void whenConvertParamsFail(PaymentNotifyProcessPair pair) {

    }

    @Override
    protected void processNotifyWhenSuccess(PaymentNotifyProcessPair pair) {

    }

    @Override
    protected void processNotifyWhenFailure(PaymentNotifyProcessPair pair) {

    }

    @Override
    protected void processBeforePayment(PaymentProcessPair pair) {

    }

    @Override
    protected void processAfterPaymentWhenSuccess(PaymentProcessPair pair) {

    }

    @Override
    protected void processAfterPaymentWhenFail(PaymentProcessPair pair) {

    }


    @Override
    public int getPaymentPlugin() {
        return LARY.PAYMENT.PLUGIN.WECHAT;
    }
}

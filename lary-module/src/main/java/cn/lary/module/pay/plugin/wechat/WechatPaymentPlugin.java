package cn.lary.module.pay.plugin.wechat;

import cn.lary.module.common.constant.LARY;
import cn.lary.module.pay.component.PaymentQueryProcessPair;
import cn.lary.module.pay.plugin.AbstractPaymentPlugin;
import cn.lary.module.pay.component.PaymentNotifyProcessPair;
import cn.lary.module.pay.component.PaymentProcessPair;
import cn.lary.module.pay.vo.PaymentQueryVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class WechatPaymentPlugin extends AbstractPaymentPlugin {


    @Override
    public boolean whetherOrderStatusSuccess(PaymentQueryVO vo) {
        return false;
    }

    @Override
    protected boolean check(Map<String, String> params) {
        return false;
    }

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
    protected PaymentQueryVO processActiveQuery(PaymentQueryProcessPair pair) {
        return null;
    }

    @Override
    protected void processQueryWhenFail(PaymentQueryVO pair) {

    }

    @Override
    protected void processQueryWhenSuccess(PaymentQueryVO pair) {

    }

    @Override
    protected void processQueryExecuteFail(PaymentQueryVO pair) {

    }


    @Override
    public int getPaymentPlugin() {
        return LARY.PAYMENT.PLUGIN.WECHAT;
    }
}

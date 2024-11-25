package cn.lary.payment.plugin.wechat;

import cn.lary.payment.component.PaymentNotifyProcessPair;
import cn.lary.payment.component.PaymentProcessPair;
import cn.lary.payment.component.PaymentQueryProcessPair;
import cn.lary.payment.constant.PAYMENT;
import cn.lary.payment.plugin.AbstractPaymentPlugin;
import cn.lary.payment.vo.PaymentQueryVO;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
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
        return PAYMENT.PLUGIN.WECHAT;
    }
}

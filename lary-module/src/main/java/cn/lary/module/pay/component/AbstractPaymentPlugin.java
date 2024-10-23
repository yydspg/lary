package cn.lary.module.pay.component;

import cn.lary.common.exception.SystemException;
import cn.lary.module.common.constant.LARY;
import cn.lary.module.pay.dto.PaymentParamDTO;
import cn.lary.module.pay.plugin.PaymentPlugin;
import cn.lary.module.pay.vo.PaymentBuildVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * 这里不直接注入业务切面<br>
 * 不想破坏spring优雅的DI
 */
public abstract class AbstractPaymentPlugin implements PaymentPlugin {

    private final String NONSUPPORT_OPERATION = "NONSUPPORT_OPERATION";

    @Override
    public final void doNotify(PaymentNotifyProcessPair pair) {
        assert(pair != null);
        assert(pair.getRequest() != null);
        Map<String, String> params;
        try {
           params = getParamsFromRequest(pair.getRequest());
        }catch (Exception e) {
            whenConvertParamsFail(pair);
            processNotifyWhenFailure(pair);
            return;
        }
        try {
            if (verificationSign(params)) {
                processNotifyWhenSuccess(pair);
            }else{
                processNotifyWhenFailure(pair);
            }
        } catch (Exception e) {
            whenVerificationSignFail(pair);
            processNotifyWhenFailure(pair);
        }
    }

    @Override
    public final void doCallback(PaymentNotifyProcessPair pair) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PaymentBuildVO doPay(PaymentProcessPair pair) {
        processBeforePayment(pair);
        PaymentBuildVO vo = switch(pair.getPaymentWay()) {
                case LARY.PAYMENT.WAY.PC ->  pcPay(pair.getParam());
                case LARY.PAYMENT.WAY.APP ->  appPay(pair.getParam());
                default -> throw new SystemException(NONSUPPORT_OPERATION);
            };
        if (vo.isFail()) {
            processAfterPaymentWhenFail(pair);
        }else {
            processAfterPaymentWhenSuccess(pair);
        }
        return vo;
    }

    public PaymentBuildVO pcPay(PaymentParamDTO dto) {
        throw new SystemException(NONSUPPORT_OPERATION);
    }

     public PaymentBuildVO appPay(PaymentParamDTO dto) {
        throw new SystemException(NONSUPPORT_OPERATION);
    }

    /**
     * 从httpServletRequest中获取回调参数
     * @param request {@link HttpServletRequest}
     * @return params
     */
    protected abstract Map<String,String> getParamsFromRequest(HttpServletRequest request) throws Exception;

    /**
     * 验签
     * @param params p
     * @return 是否成功
     */
    protected abstract boolean verificationSign(Map<String, String> params) throws Exception;

    /**
     * 验签失败时
     * @param pair {@link PaymentNotifyProcessPair}
     */
    protected abstract void whenVerificationSignFail(PaymentNotifyProcessPair pair);

    /**
     * 转化参数失败时
     * @param pair {@link PaymentNotifyProcessPair}
     */
    protected abstract void whenConvertParamsFail(PaymentNotifyProcessPair pair);

    /**
     * 回调成功时,使用{@link BusinessPaymentNotifyManager}
     * @param pair {@link PaymentNotifyProcessPair}
     */
    protected abstract void processNotifyWhenSuccess(PaymentNotifyProcessPair pair);

    /**
     * 回调失败时,使用{@link BusinessPaymentNotifyManager}
     * @param pair {@link PaymentNotifyProcessPair}
     */
    protected abstract void processNotifyWhenFailure(PaymentNotifyProcessPair pair);

    protected abstract void processBeforePayment(PaymentProcessPair pair);

    protected abstract void processAfterPaymentWhenSuccess(PaymentProcessPair pair);

    protected abstract void processAfterPaymentWhenFail(PaymentProcessPair pair);
}

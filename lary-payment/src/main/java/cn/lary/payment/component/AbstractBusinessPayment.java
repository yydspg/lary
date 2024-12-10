package cn.lary.payment.component;

import cn.lary.common.business.BusinessSign;
import cn.lary.common.dto.ResponsePair;
import cn.lary.common.kit.BusinessKit;
import cn.lary.api.payment.dto.BusinessPaymentDTO;
import cn.lary.api.payment.vo.PaymentBuildVO;

public abstract class AbstractBusinessPayment implements BusinessPayment, BusinessSign {


    @Override
    public final ResponsePair<PaymentBuildVO> executePayment(BusinessPaymentDTO dto) {

        ResponsePair<Void> pair = doCheck(dto);
        if (pair.isFail()) {
            return BusinessKit.fail(pair.getMsg());
        }
        ResponsePair<PaymentProcessPair> responsePair = beforePay(dto);
        if (responsePair.isFail()) {
            return BusinessKit.fail(responsePair.getMsg());
        }
        if (responsePair.getData().isInternal()) {
            return BusinessKit.ok();
        }
        PaymentBuildVO vo = doPay(responsePair.getData());
        if (vo.isFail()) {
            whenTryPayFail(vo);
            return BusinessKit.fail(vo.getErrMsg());
        }
        whenTryPaySuccess(vo);
        afterPay(dto);
        return BusinessKit.ok(vo);
    }

    /**
     * 业务层 pay check
     * @param dto {@link BusinessPaymentDTO}
     * @return ok
     */
    protected abstract ResponsePair<Void> doCheck(BusinessPaymentDTO dto);
    /**
     * 业务层 pay check
     * @param dto {@link BusinessPaymentDTO}
     * @return ok
     */
    protected abstract ResponsePair<PaymentProcessPair> beforePay(BusinessPaymentDTO dto);

    protected abstract void afterPay(BusinessPaymentDTO dto);

    /**
     * 执行isv的支付逻辑
     * @param pair {@link PaymentProcessPair}
     * @return {@link PaymentBuildVO}
     */
    protected abstract PaymentBuildVO doPay(PaymentProcessPair pair);
    /**
     * isv尝试支付失败
     * @param vo {@link PaymentBuildVO}
     */
    protected abstract void whenTryPayFail(PaymentBuildVO vo);

    /**
     * isv尝试支付成功
     * @param vo {@link PaymentBuildVO}
     */
    protected abstract void whenTryPaySuccess(PaymentBuildVO vo);

}

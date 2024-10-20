package cn.lary.module.pay.component;

import cn.lary.core.business.BusinessSign;
import cn.lary.core.dto.ResponsePair;
import cn.lary.kit.BusinessKit;
import cn.lary.module.pay.dto.BusinessPaymentDTO;
import cn.lary.module.pay.vo.PaymentBuildVO;

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
            processWhenPaymentFail(vo);
            return BusinessKit.fail(vo.getErrMsg());
        }
        processWhenPaymentSuccess(vo);
        afterPay(dto);
        return BusinessKit.ok(vo);
    }

    protected abstract ResponsePair<Void> doCheck(BusinessPaymentDTO dto);

    protected abstract ResponsePair<PaymentProcessPair> beforePay(BusinessPaymentDTO dto);

    protected abstract void afterPay(BusinessPaymentDTO dto);

    protected abstract PaymentBuildVO doPay(PaymentProcessPair pair);

    protected abstract void processWhenPaymentFail(PaymentBuildVO vo);

    protected abstract void processWhenPaymentSuccess(PaymentBuildVO vo);
}

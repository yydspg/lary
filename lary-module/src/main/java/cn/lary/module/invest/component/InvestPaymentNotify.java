package cn.lary.module.invest.component;


import cn.lary.module.common.constant.LARY;
import cn.lary.module.common.service.EventService;
import cn.lary.module.invest.dto.InvestPaymentNotifyVO;
import cn.lary.module.invest.entity.Invest;
import cn.lary.module.invest.service.InvestService;
import cn.lary.module.pay.component.BusinessPaymentNotify;
import cn.lary.module.pay.component.PaymentNotifyProcessPair;
import cn.lary.module.pay.vo.PaymentQueryVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvestPaymentNotify  extends BusinessPaymentNotify<InvestPaymentNotifyVO> {

    private final TransactionTemplate transactionTemplate;
    private final EventService eventService;
    private final InvestService investService;

    @Override
    public int getSign() {
        return LARY.BUSINESS.PAYMENT.INVEST;
    }

    @Override
    public void whenSuccess(InvestPaymentNotifyVO data) {
        long lid = data.getLid();
        transactionTemplate.executeWithoutResult(status -> {
            Invest invest = investService.lambdaQuery()
                    .select(Invest::getLid, Invest::getSync,Invest::getEid)
                    .eq(Invest::getLid, lid)
                    .one();
            if (invest == null) {
                log.error("process success invest payment notify record , lid:{}", lid);
                return;
            }
            if (invest.getStatus() == LARY.INVEST.STATUS.FINISH) {
                return ;
            }
            investService.lambdaUpdate()
                    .set(Invest::getStatus, LARY.INVEST.STATUS.FINISH)
                    .set(Invest::getSn, data.getTradeNo())
                    .eq(Invest::getLid, lid)
                    .update();
            eventService.commit(invest.getEid());
        });
    }

    @Override
    public void whenFail(InvestPaymentNotifyVO data) {
        long lid = data.getLid();
        transactionTemplate.executeWithoutResult(status -> {
            Invest invest = investService.lambdaQuery()
                    .select(Invest::getLid, Invest::getSync,Invest::getEid)
                    .eq(Invest::getLid, lid)
                    .one();
            if (invest == null) {
                log.error("process invest payment fail notify record  error, lid:{}", lid);
                return;
            }
            if (invest.getStatus() == LARY.INVEST.STATUS.FINISH ||
            invest.getStatus() == LARY.INVEST.STATUS.FAIL ){
                return ;
            }
            investService.lambdaUpdate()
                    .set(Invest::getStatus, LARY.INVEST.STATUS.FAIL)
                    .set(Invest::getSn, data.getTradeNo())
                    .set(Invest::getReason,data.getReason())
                    .eq(Invest::getLid, lid)
                    .update();
            eventService.commit(invest.getEid());
        });
    }

    @Override
    public InvestPaymentNotifyVO getPaymentNotify(PaymentNotifyProcessPair pair, PaymentQueryVO data) {
        if (pair == null) {
            return new InvestPaymentNotifyVO()
                    .setReason(data.getReason())
                    .setTradeNo(data.getSn())
                    .setAmount(data.getAmount())
                    .setLid(data.getPaymentId());
        }
        return new InvestPaymentNotifyVO(pair);
    }
}

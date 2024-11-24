package cn.lary.advertisement.component;

import cn.lary.advertisement.constant.AD;
import cn.lary.advertisement.dto.AdPaymentNotifyVO;
import cn.lary.advertisement.entity.Advertisement;
import cn.lary.advertisement.entity.Provider;
import cn.lary.advertisement.service.AdvertisementService;
import cn.lary.advertisement.service.ProviderService;
import cn.lary.common.constant.LARY;
import cn.lary.payment.component.BusinessPaymentNotify;
import cn.lary.payment.component.PaymentNotifyProcessPair;
import cn.lary.payment.vo.PaymentQueryVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class ADPaymentNotify extends BusinessPaymentNotify<AdPaymentNotifyVO> {

    private final TransactionTemplate transactionTemplate;
    private final AdvertisementService advertisementService;
//    private final EventService eventService;
    private final ProviderService providerService;


    @Override
    public void whenSuccess(AdPaymentNotifyVO vo) {
        long aid = vo.getAid();
        Boolean execute = transactionTemplate.execute(status -> {
            Advertisement advertisement = advertisementService.lambdaQuery()
                    .select(Advertisement::getAid, Advertisement::getPid,
                            Advertisement::getStatus)
                    .eq(Advertisement::getAid, vo.getAid())
                    .one();
            if (advertisement == null) {
                log.error("payment notify fail,aid:{}", aid);
                return false;
            }
            if (advertisement.getStatus() == AD.STATUS.FINISH) {
                return true;
            }
            advertisementService.lambdaUpdate()
                    .set(Advertisement::getStatus, AD.STATUS.COMMON)
                    .set(Advertisement::getSn, vo.getTradeNo())
                    .eq(Advertisement::getAid, aid)
                    .update();
            Provider provider = providerService.lambdaQuery()
                    .select(Provider::getPid, Provider::getStatus, Provider::getAmount)
                    .eq(Provider::getPid, advertisement.getPid())
                    .one();
            if (provider == null) {
                log.error("no provider found,aid :{}", aid);
                return false;
            }
            providerService.lambdaUpdate()
                    .setIncrBy(Provider::getAmount, vo.getAmount())
                    .set(Provider::getLevel, level(provider.getAmount(), vo.getAmount()))
                    .eq(Provider::getPid, advertisement.getPid())
                    .update();
            return true;
        });
        if (Boolean.FALSE.equals(execute)) {
            return;
        }
    }


    @Override
    public void whenFail(AdPaymentNotifyVO vo) {
        long aid = vo.getAid();
        transactionTemplate.executeWithoutResult(status -> {
            Advertisement advertisement = advertisementService.lambdaQuery()
                    .select(Advertisement::getAid, Advertisement::getPid,Advertisement::getEid)
                    .eq(Advertisement::getAid, vo.getAid())
                    .one();
            if (advertisement == null) {
                log.error("callback ad payment notify fail,aid:{}", aid);
                return ;
            }
            advertisementService.lambdaUpdate()
                    .set(Advertisement::getStatus, AD.STATUS.FAIL)
                    .set(Advertisement::getSn, vo.getTradeNo())
                    .eq(Advertisement::getAid, aid)
                    .update();
//            eventService.commit(advertisement.getEid());
        });
    }

    @Override
    public AdPaymentNotifyVO getPaymentNotify(PaymentNotifyProcessPair pair, PaymentQueryVO data) {
        if (pair == null) {
            return new AdPaymentNotifyVO()
                    .setReason(data.getReason())
                    .setTradeNo(data.getSn())
                    .setAmount(data.getAmount())
                    .setAid(data.getPaymentId());
        }
        return new AdPaymentNotifyVO(pair);
    }

    @Override
    public int getSign() {
        return LARY.BUSINESS.PAYMENT.AD;
    }

    private int level(BigDecimal before,BigDecimal current){
        BigDecimal now = before.add(current);
        if (now.compareTo(new BigDecimal(1000000)) > 0 ){
            return AD.LEVEL.SUPER;
        }
        return AD.LEVEL.COMMON;
    }
}

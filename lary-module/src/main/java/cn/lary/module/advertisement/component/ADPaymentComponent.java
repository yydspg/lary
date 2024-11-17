package cn.lary.module.advertisement.component;

import cn.lary.common.dto.ResponsePair;
import cn.lary.common.kit.BusinessKit;
import cn.lary.module.advertisement.dto.ADPaymentDTO;
import cn.lary.module.advertisement.entity.Advertisement;
import cn.lary.module.advertisement.entity.Provider;
import cn.lary.module.advertisement.service.AdvertisementService;
import cn.lary.module.advertisement.service.ProviderService;
import cn.lary.module.common.constant.LARY;
import cn.lary.module.pay.component.AbstractBusinessPayment;
import cn.lary.module.pay.component.PaymentProcessPair;
import cn.lary.module.pay.dto.BusinessPaymentDTO;
import cn.lary.module.pay.vo.PaymentBuildVO;
import cn.lary.module.user.entity.User;
import cn.lary.module.user.service.UserService;
import com.github.houbb.sensitive.word.core.SensitiveWordHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class ADPaymentComponent  extends AbstractBusinessPayment {

    private final ProviderService providerService;
    private final UserService userService;
    private final AdvertisementService advertisementService;
    private final TransactionTemplate transactionTemplate;

    @Override
    protected ResponsePair<Void> doCheck(BusinessPaymentDTO dto) {
        return BusinessKit.ok();
    }

    @Override
    protected ResponsePair<PaymentProcessPair> beforePay(BusinessPaymentDTO paymentDTO) {
        ADPaymentDTO dto = (ADPaymentDTO) paymentDTO;
        Provider provider = providerService.lambdaQuery()
                .select(Provider::getPid)
                .eq(Provider::getPid, dto.getPid())
                .one();
        if (provider == null) {
            return BusinessKit.fail("no provider found");
        }
        if (provider.getStatus() == LARY.AD.PROVIDER.BLOCK
                || provider.getStatus() == LARY.AD.PROVIDER.DISBAND) {
            return BusinessKit.fail("provider status error");
        }
        if (dto.getStartAt() >= dto.getEndAt()) {
            return BusinessKit.fail("start time error");
        }
        if (dto.getBegin() >= dto.getOff()) {
            return BusinessKit.fail("begin time error");
        }
        long day = dto.getEndAt() - dto.getStartAt();
        long  daily = dto.getOff() - dto.getBegin();
        if (day < daily) {
            return BusinessKit.fail("day time error");
        }
        if (SensitiveWordHelper.contains(dto.getRemark())) {
            return BusinessKit.fail("remark contains bad word");
        }
        if (SensitiveWordHelper.contains(dto.getUsername())) {
            return BusinessKit.fail("username contains bad word");
        }
        if(dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return BusinessKit.fail("amount less than zero");
        }
        User virtual = new User();
        virtual.setUsername(dto.getUsername());
        virtual.setRole(LARY.USER.ROLE.VC);
        transactionTemplate.executeWithoutResult(status -> {
            userService.virtual(virtual);
            Advertisement advertisement = new Advertisement()
                    .setBegin(dto.getBegin())
                    .setOff(dto.getOff())
                    .setAmount(dto.getAmount())
                    .setRemark(dto.getRemark())
                    .setAvatar(dto.getAvatar())
                    .setAmount(dto.getAmount())
                    .setUsername(dto.getUsername())
                    .setStartAt(dto.getStartAt())
                    .setEndAt(dto.getEndAt())
                    .setStatus(LARY.AD.STATUS.INIT);
            advertisementService.save(advertisement);
        });
        return BusinessKit.ok();
    }

    @Override
    protected void afterPay(BusinessPaymentDTO dto) {

    }

    @Override
    protected PaymentBuildVO doPay(PaymentProcessPair pair) {
        return null;
    }

    @Override
    protected void processWhenPaymentFail(PaymentBuildVO vo) {

    }

    @Override
    protected void processWhenPaymentSuccess(PaymentBuildVO vo) {

    }

    @Override
    public int getSign() {
        return LARY.BUSINESS.PAYMENT.AD;
    }
}

package cn.lary.module.wallet.component;

import cn.lary.core.dto.ResponsePair;
import cn.lary.module.pay.vo.PaymentBuildVO;
import cn.lary.module.wallet.dto.RechargeDTO;
import cn.lary.module.wallet.dto.UpdateSecurityQuestionDTO;
import cn.lary.module.wallet.dto.WalletIncomePageQueryDTO;
import cn.lary.module.wallet.dto.WalletOutcomePageQueryDTO;
import cn.lary.module.wallet.service.WalletIncomeService;
import cn.lary.module.wallet.service.WalletOutcomeService;
import cn.lary.module.wallet.service.WalletService;
import cn.lary.module.wallet.vo.BalanceVO;
import cn.lary.module.wallet.vo.WalletIncomeVO;
import cn.lary.module.wallet.vo.WalletOutcomeVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalletExecute {

    private final WalletService walletService;
    private final WalletPayment walletPayment;
    private final WalletIncomeService walletIncomeService;
    private final WalletOutcomeService walletOutcomeService;
    /**
     * 充值业务
     * @param dto {@link RechargeDTO}
     * @return {@link PaymentBuildVO}
     */
    public ResponsePair<PaymentBuildVO> recharge(RechargeDTO dto)  {
        return walletPayment.executePayment(dto);
    }

    /**
     * 获取余额
     * @return  {@link BalanceVO}
     */
    public ResponsePair<BalanceVO> getBalance()  {
        return walletService.my();
    }

    /**
     * 更新问题
     * @param dto {@link UpdateSecurityQuestionDTO}
     * @return ok
     */
    public ResponsePair<Void> updateQuestion(UpdateSecurityQuestionDTO dto) {
        return walletService.updateQuestion(dto);
    }

    /**
     * 分页查询收入
     * @param dto {@link WalletIncomePageQueryDTO}
     * @return {@link WalletIncomeVO}
     */
    public ResponsePair<List<WalletIncomeVO>> getIncomeVOs(WalletIncomePageQueryDTO dto){
        return walletIncomeService.my(dto);
    }

    /**
     * 分页查询支出
     * @param dto {@link WalletOutcomePageQueryDTO}
     * @return {@link WalletOutcomeVO}
     */
    public ResponsePair<List<WalletOutcomeVO>> getOutcomeVOs(WalletOutcomePageQueryDTO dto){
        return walletOutcomeService.my(dto);
    }
}

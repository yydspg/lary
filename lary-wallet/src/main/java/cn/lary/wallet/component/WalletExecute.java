package cn.lary.wallet.component;

import cn.lary.common.dto.ResponsePair;
import cn.lary.payment.vo.PaymentBuildVO;
import cn.lary.wallet.dto.RechargeDTO;
import cn.lary.wallet.dto.WalletIncomePageQueryDTO;
import cn.lary.wallet.dto.WalletOutcomePageQueryDTO;
import cn.lary.wallet.service.WalletIncomeService;
import cn.lary.wallet.service.WalletOutcomeService;
import cn.lary.wallet.service.WalletService;
import cn.lary.wallet.vo.BalanceVO;
import cn.lary.wallet.vo.WalletIncomeVO;
import cn.lary.wallet.vo.WalletOutcomeVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalletExecute {

    private final WalletService walletService;
    private final WalletIncomeService walletIncomeService;
    private final WalletOutcomeService walletOutcomeService;
    private final RechargePayment rechargePayment;
    /**
     * 充值业务
     * @param dto {@link RechargeDTO}
     * @return {@link PaymentBuildVO}
     */
    public ResponsePair<PaymentBuildVO> recharge(RechargeDTO dto)  {
        return rechargePayment.executePayment(dto);
    }

    /**
     * 获取余额
     * @return  {@link BalanceVO}
     */
    public ResponsePair<BalanceVO> my()  {
        return walletService.my();
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

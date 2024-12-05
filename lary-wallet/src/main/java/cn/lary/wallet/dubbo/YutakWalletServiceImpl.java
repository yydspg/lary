package cn.lary.wallet.dubbo;

import cn.lary.api.payment.vo.PaymentBuildVO;
import cn.lary.api.wallet.YutakWalletService;
import cn.lary.api.wallet.dto.RechargeDTO;
import cn.lary.api.wallet.dto.WalletIncomePageQueryDTO;
import cn.lary.api.wallet.dto.WalletOutcomePageQueryDTO;
import cn.lary.api.wallet.vo.BalanceVO;
import cn.lary.api.wallet.vo.WalletIncomeVO;
import cn.lary.api.wallet.vo.WalletOutcomeVO;
import cn.lary.common.dto.ResponsePair;
import cn.lary.wallet.component.RechargePayment;
import cn.lary.wallet.service.WalletIncomeService;
import cn.lary.wallet.service.WalletOutcomeService;
import cn.lary.wallet.service.WalletService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Component
@DubboService
public class YutakWalletServiceImpl implements YutakWalletService {
    @Resource
    private WalletService walletService;
    @Resource
    private WalletIncomeService walletIncomeService;
    @Resource
    private WalletOutcomeService walletOutcomeService;
    @Resource
    private RechargePayment rechargePayment;

    @Override
    public ResponsePair<PaymentBuildVO> recharge(RechargeDTO dto)  {
        return rechargePayment.executePayment(dto);
    }

    @Override
    public ResponsePair<BalanceVO> my()  {
        return walletService.my();
    }

    @Override
    public ResponsePair<List<WalletIncomeVO>> getIncomeVOs(WalletIncomePageQueryDTO dto){
        return walletIncomeService.my(dto);
    }
    @Override
    public ResponsePair<List<WalletOutcomeVO>> getOutcomeVOs(WalletOutcomePageQueryDTO dto){
        return walletOutcomeService.my(dto);
    }
}

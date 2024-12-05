package cn.lary.api.wallet;

import cn.lary.api.payment.vo.PaymentBuildVO;
import cn.lary.api.wallet.dto.RechargeDTO;
import cn.lary.api.wallet.dto.WalletIncomePageQueryDTO;
import cn.lary.api.wallet.dto.WalletOutcomePageQueryDTO;
import cn.lary.api.wallet.vo.BalanceVO;
import cn.lary.api.wallet.vo.WalletIncomeVO;
import cn.lary.api.wallet.vo.WalletOutcomeVO;
import cn.lary.common.dto.ResponsePair;

import java.util.List;

public interface YutakWalletService {

    /**
     * 充值业务
     * @param dto {@link RechargeDTO}
     * @return {@link PaymentBuildVO}
     */
     ResponsePair<PaymentBuildVO> recharge(RechargeDTO dto);

    /**
     * 获取余额
     * @return  {@link BalanceVO}
     */
     ResponsePair<BalanceVO> my()  ;


    /**
     * 分页查询收入
     * @param dto {@link WalletIncomePageQueryDTO}
     * @return {@link WalletIncomeVO}
     */
     ResponsePair<List<WalletIncomeVO>> getIncomeVOs(WalletIncomePageQueryDTO dto);

    /**
     * 分页查询支出
     * @param dto {@link WalletOutcomePageQueryDTO}
     * @return {@link WalletOutcomeVO}
     */
     ResponsePair<List<WalletOutcomeVO>> getOutcomeVOs(WalletOutcomePageQueryDTO dto);

}

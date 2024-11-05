package cn.lary.module.wallet.service;

import cn.lary.common.dto.ResponsePair;
import cn.lary.module.wallet.dto.*;
import cn.lary.module.wallet.entity.Wallet;
import cn.lary.module.wallet.vo.BalanceVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author paul
 * @since 2024-09-13
 */
public interface WalletService extends IService<Wallet> {
    /**
     * 交易 <br>
     * 此接口进行数据转移,存在账户余额判断<br>
     * <b>uid</b>是交易发起方，<b>toUid</b>是交易接受方<br>
     * 请注意在调用此接口前，尽量减少对{@link Wallet}的查询
     * @param dto {@link TransferDTO}
     * @return {@link ResponsePair}
     */
    ResponsePair<Void> transfer(TransferDTO dto);

    /**
     * 批量数据转移<br>
     * 一个用户对多个用户转移相同数目的资产 <br>
     * 吊起此接口前对被转移用户状态检查
     * @param dto {@link BatchOutcomeFixTransferDTO}
     * @return {@link ResponsePair}
     */
    ResponsePair<Void> batchOutcomeFixTransfer(BatchOutcomeFixTransferDTO dto);
    /**
     * 批量数据转移<br>
     * 一个用户对多个用户转不同数目的资产 <br>
     * 吊起此接口前对被转移用户状态检查
     * @param dto {@link BatchOutcomeRandomTransferDTO}
     * @return {@link ResponsePair}
     */
    ResponsePair<Void> batchOutcomeRandomTransfer(BatchOutcomeRandomTransferDTO dto);

    /**
     * 系统向用户转钱
     * @param dto {@link SystemOutcomeTransferDTO}
     * @return ok
     */
    ResponsePair<Void> systemOutcomeTransfer(SystemOutcomeTransferDTO dto);
    /**
     * 用户向系统转钱
     * @param dto {@link SystemIncomeTransferDTO}
     * @return ok
     */
    ResponsePair<Void> systemIncomeTransfer(SystemIncomeTransferDTO dto);

    /**
     * 获取用户钱包<br>
     * 此方法不会返回null
     * @param members 用户
     * @return ok
     */
    ResponsePair<List<Wallet>> getWallets(List<Long> members);


    /**
     * 查询我的账户
     * @return {@link BalanceVO}
     */
    ResponsePair<BalanceVO> my();
}

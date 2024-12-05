package cn.lary.wallet.service;

import cn.lary.common.dto.ResponsePair;
import cn.lary.api.wallet.dto.WalletOutcomePageQueryDTO;
import cn.lary.api.wallet.entity.WalletOutcome;
import cn.lary.api.wallet.vo.WalletOutcomeVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author paul
 * @since 2024-10-02
 */
public interface WalletOutcomeService extends IService<WalletOutcome> {

    /**
     * 分页查询支出
     * @param dto {@link WalletOutcomePageQueryDTO}
     * @return {@link WalletOutcomeVO}
     */
    ResponsePair<List<WalletOutcomeVO>> my(WalletOutcomePageQueryDTO dto);
}

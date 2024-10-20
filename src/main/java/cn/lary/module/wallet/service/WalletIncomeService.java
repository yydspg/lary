package cn.lary.module.wallet.service;

import cn.lary.core.dto.ResponsePair;
import cn.lary.module.wallet.dto.WalletIncomePageQueryDTO;
import cn.lary.module.wallet.entity.WalletIncome;
import cn.lary.module.wallet.vo.WalletIncomeVO;
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
public interface WalletIncomeService extends IService<WalletIncome> {

        ResponsePair<List<WalletIncomeVO>> my(WalletIncomePageQueryDTO dto);
}

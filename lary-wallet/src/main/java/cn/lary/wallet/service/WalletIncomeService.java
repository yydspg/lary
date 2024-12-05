package cn.lary.wallet.service;

import cn.lary.common.dto.ResponsePair;
import cn.lary.api.wallet.dto.WalletIncomePageQueryDTO;
import cn.lary.api.wallet.entity.WalletIncome;
import cn.lary.api.wallet.vo.WalletIncomeVO;
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

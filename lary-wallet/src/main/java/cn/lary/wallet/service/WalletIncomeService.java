package cn.lary.wallet.service;

import cn.lary.common.dto.ResponsePair;
import cn.lary.wallet.dto.WalletIncomePageQueryDTO;
import cn.lary.wallet.entity.WalletIncome;
import cn.lary.wallet.vo.WalletIncomeVO;
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

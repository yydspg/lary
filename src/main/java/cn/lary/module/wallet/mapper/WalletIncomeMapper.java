package cn.lary.module.wallet.mapper;

import cn.lary.module.wallet.dto.WalletIncomePageQueryDTO;
import cn.lary.module.wallet.entity.WalletIncome;
import cn.lary.module.wallet.vo.WalletIncomeVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author paul
 * @since 2024-10-02
 */
public interface WalletIncomeMapper extends BaseMapper<WalletIncome> {

    List<WalletIncomeVO> getIncomes(WalletIncomePageQueryDTO dto);
}

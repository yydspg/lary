package cn.lary.wallet.mapper;

import cn.lary.api.wallet.dto.WalletIncomePageQueryDTO;
import cn.lary.api.wallet.entity.WalletIncome;
import cn.lary.api.wallet.vo.WalletIncomeVO;
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

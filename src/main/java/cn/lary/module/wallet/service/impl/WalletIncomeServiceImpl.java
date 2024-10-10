package cn.lary.module.wallet.service.impl;

import cn.lary.module.wallet.dto.WalletIncomePageQueryDTO;
import cn.lary.module.wallet.entity.WalletIncome;
import cn.lary.module.wallet.mapper.WalletIncomeMapper;
import cn.lary.module.wallet.service.WalletIncomeService;
import cn.lary.module.wallet.vo.WalletIncomeVO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author paul
 * @since 2024-10-02
 */
@Service
@RequiredArgsConstructor
public class WalletIncomeServiceImpl extends ServiceImpl<WalletIncomeMapper, WalletIncome> implements WalletIncomeService {

    private final WalletIncomeMapper walletIncomeMapper;
    @Override
    public List<WalletIncomeVO> getWalletIncomeVOs(WalletIncomePageQueryDTO dto) {
        if ( dto.getUid() == 0) {
            return null;
        }
        return walletIncomeMapper.getIncomes(dto);
    }
}

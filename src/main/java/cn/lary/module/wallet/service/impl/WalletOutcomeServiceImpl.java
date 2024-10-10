package cn.lary.module.wallet.service.impl;

import cn.lary.module.wallet.dto.WalletOutcomePageQueryDTO;
import cn.lary.module.wallet.entity.WalletOutcome;
import cn.lary.module.wallet.mapper.WalletOutcomeMapper;
import cn.lary.module.wallet.service.WalletOutcomeService;
import cn.lary.module.wallet.vo.WalletOutcomeVO;
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
public class WalletOutcomeServiceImpl extends ServiceImpl<WalletOutcomeMapper, WalletOutcome> implements WalletOutcomeService {
    private final WalletOutcomeMapper walletOutcomeMapper;
    @Override
    public List<WalletOutcomeVO> getWalletOutcomeVOs(WalletOutcomePageQueryDTO dto) {
        if ( dto.getUid() == 0) {
            return null;
        }
        return walletOutcomeMapper.getOutcomes(dto);
    }
}

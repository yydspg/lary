package cn.lary.module.wallet.service.impl;

import cn.lary.core.context.RequestContext;
import cn.lary.core.dto.ResponsePair;
import cn.lary.kit.BusinessKit;
import cn.lary.kit.CollectionKit;
import cn.lary.module.wallet.dto.WalletIncomePageQueryDTO;
import cn.lary.module.wallet.entity.WalletIncome;
import cn.lary.module.wallet.entity.WalletOutcome;
import cn.lary.module.wallet.mapper.WalletIncomeMapper;
import cn.lary.module.wallet.service.WalletIncomeService;
import cn.lary.module.wallet.vo.WalletIncomeVO;
import cn.lary.module.wallet.vo.WalletOutcomeVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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


    @Override
    public ResponsePair<List<WalletIncomeVO>> my(WalletIncomePageQueryDTO dto) {
        List<WalletIncome> vos = lambdaQuery()
                .eq(WalletIncome::getUid, RequestContext.getLoginUID())
                .page(new Page<>(dto.getPageIndex(), dto.getPageSize()))
                .getRecords();
        if (CollectionKit.isEmpty(vos)) {
            return BusinessKit.fail("no wallet outcome data");
        }
        return BusinessKit.ok(vos.stream()
                .map(WalletIncomeVO::new)
                .toList());
    }
}

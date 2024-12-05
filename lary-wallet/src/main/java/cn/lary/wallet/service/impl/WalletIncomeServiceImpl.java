package cn.lary.wallet.service.impl;

import cn.lary.common.context.RequestContext;
import cn.lary.common.dto.ResponsePair;
import cn.lary.common.kit.BusinessKit;
import cn.lary.common.kit.CollectionKit;
import cn.lary.api.wallet.dto.WalletIncomePageQueryDTO;
import cn.lary.api.wallet.entity.WalletIncome;
import cn.lary.wallet.mapper.WalletIncomeMapper;
import cn.lary.wallet.service.WalletIncomeService;
import cn.lary.api.wallet.vo.WalletIncomeVO;
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
                .eq(WalletIncome::getUid, RequestContext.uid())
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

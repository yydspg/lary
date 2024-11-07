package cn.lary.module.wallet.service.impl;

import cn.lary.common.context.RequestContext;
import cn.lary.common.dto.ResponsePair;
import cn.lary.common.kit.BusinessKit;
import cn.lary.common.kit.CollectionKit;
import cn.lary.module.wallet.dto.WalletOutcomePageQueryDTO;
import cn.lary.module.wallet.entity.WalletOutcome;
import cn.lary.module.wallet.mapper.WalletOutcomeMapper;
import cn.lary.module.wallet.service.WalletOutcomeService;
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
public class WalletOutcomeServiceImpl extends ServiceImpl<WalletOutcomeMapper, WalletOutcome> implements WalletOutcomeService {


    @Override
    public ResponsePair<List<WalletOutcomeVO>> my(WalletOutcomePageQueryDTO dto) {
        List<WalletOutcome> vos = lambdaQuery()
                .eq(WalletOutcome::getUid, RequestContext.uid())
                .page(new Page<>(dto.getPageIndex(), dto.getPageSize()))
                .getRecords();
        if (CollectionKit.isEmpty(vos)) {
            return BusinessKit.fail("no wallet outcome data");
        }
        return BusinessKit.ok(vos.stream()
                .map(WalletOutcomeVO::new)
                .toList());
    }
}

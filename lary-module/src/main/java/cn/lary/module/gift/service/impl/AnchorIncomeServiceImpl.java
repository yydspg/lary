package cn.lary.module.gift.service.impl;

import cn.lary.common.dto.ResponsePair;
import cn.lary.common.kit.BusinessKit;
import cn.lary.module.gift.entity.AnchorIncome;
import cn.lary.module.gift.mapper.AnchorIncomeMapper;
import cn.lary.module.gift.service.AnchorIncomeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author paul
 * @since 2024-08-16
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AnchorIncomeServiceImpl extends ServiceImpl<AnchorIncomeMapper, AnchorIncome> implements AnchorIncomeService {

    private final TransactionTemplate transactionTemplate;

    @Override
    public ResponsePair<Long> buildTurnover(int uid,int streamId) {
        return transactionTemplate.execute(status -> {
            List<AnchorIncome> data = lambdaQuery()
                    .select(AnchorIncome::getIncome)
                    .eq(AnchorIncome::getUid, uid)
                    .eq(AnchorIncome::getStreamId, streamId)
                    .list();
            Long sum = 0L;
            for (AnchorIncome a : data) {
                sum += a.getIncome();
            }
            log.info("build turnover sum:{},uid:{}.streamId:{}", sum, uid, streamId);
            return BusinessKit.ok(sum);
        });
    }
}

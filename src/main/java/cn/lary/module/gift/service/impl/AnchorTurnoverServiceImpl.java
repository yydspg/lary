package cn.lary.module.gift.service.impl;

import cn.lary.core.dto.ResponsePair;
import cn.lary.kit.BusinessKit;
import cn.lary.module.gift.entity.AnchorTurnover;
import cn.lary.module.gift.mapper.AnchorTurnoverMapper;
import cn.lary.module.gift.service.AnchorTurnoverService;
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
public class AnchorTurnoverServiceImpl extends ServiceImpl<AnchorTurnoverMapper, AnchorTurnover> implements AnchorTurnoverService {
    private final TransactionTemplate transactionTemplate;

    @Override
    public ResponsePair<Long> buildTurnover(int uid,int streamId) {
        return transactionTemplate.execute(status -> {
            List<AnchorTurnover> data = lambdaQuery()
                    .select(AnchorTurnover::getIncome)
                    .eq(AnchorTurnover::getAnchorId, uid)
                    .eq(AnchorTurnover::getStreamId, streamId)
                    .list();
            Long sum = 0L;
            for (AnchorTurnover a : data) {
                sum += a.getIncome();
            }
            log.info("build turnover sum:{},uid:{}.streamId:{}", sum, uid, streamId);
            return BusinessKit.ok(sum);
        });
    }
}

package cn.lary.module.raffle.service.impl;

import cn.lary.common.context.RequestContext;
import cn.lary.common.dto.ResponsePair;
import cn.lary.common.kit.BusinessKit;
import cn.lary.common.kit.CollectionKit;
import cn.lary.module.raffle.dto.RaffleRecordPageQueryDTO;
import cn.lary.module.raffle.entity.RaffleRecord;
import cn.lary.module.raffle.mapper.RaffleRecordMapper;
import cn.lary.module.raffle.service.RaffleRecordService;
import cn.lary.module.raffle.vo.RaffleRecordVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author paul
 * @since 2024-10-31
 */
@Service
@RequiredArgsConstructor
public class RaffleRecordServiceImpl extends ServiceImpl<RaffleRecordMapper, RaffleRecord> implements RaffleRecordService {


    @Override
    public ResponsePair<List<RaffleRecordVO>> my(RaffleRecordPageQueryDTO dto) {
        List<RaffleRecord> records = lambdaQuery()
                .select(RaffleRecord::getRaffleId, RaffleRecord::getToUid, RaffleRecord::getStreamId)
                .select(RaffleRecord::getUid, RaffleRecord::getContent, RaffleRecord::getSyncStatus)
                .eq(RaffleRecord::getUid, RequestContext.getLoginUID())
                .page(new Page<>(dto.getPageIndex(), dto.getPageSize()))
                .getRecords();
        if (CollectionKit.isEmpty(records)) {
            return BusinessKit.fail("no enough data");
        }
        return BusinessKit.ok(records.stream()
                .map(RaffleRecordVO::new)
                .toList());
    }
}

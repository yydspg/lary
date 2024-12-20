package cn.lary.raffle.service.impl;

import cn.lary.common.context.RequestContext;
import cn.lary.common.dto.ResponsePair;
import cn.lary.common.kit.BusinessKit;
import cn.lary.common.kit.CollectionKit;
import cn.lary.api.raffle.dto.RaffleRecordPageQueryDTO;
import cn.lary.api.raffle.entity.RaffleRecord;
import cn.lary.raffle.mapper.RaffleRecordMapper;
import cn.lary.raffle.service.RaffleRecordService;
import cn.lary.api.raffle.vo.RaffleRecordVO;
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
 * @since 2024-10-31
 */
@Service
@RequiredArgsConstructor
public class RaffleRecordServiceImpl extends ServiceImpl<RaffleRecordMapper, RaffleRecord> implements RaffleRecordService {


    @Override
    public ResponsePair<List<RaffleRecordVO>> my(RaffleRecordPageQueryDTO dto) {
        List<RaffleRecord> records = lambdaQuery()
                .select(RaffleRecord::getEid, RaffleRecord::getToUid,
                        RaffleRecord::getSid,RaffleRecord::getUid,
                        RaffleRecord::getContent, RaffleRecord::getSync)
                .eq(RaffleRecord::getUid, RequestContext.uid())
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

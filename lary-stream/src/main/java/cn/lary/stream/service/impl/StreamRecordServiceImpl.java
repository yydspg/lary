package cn.lary.stream.service.impl;

import cn.lary.common.context.RequestContext;
import cn.lary.common.dto.ResponsePair;
import cn.lary.common.id.LaryIDBuilder;
import cn.lary.common.kit.BusinessKit;
import cn.lary.common.kit.CollectionKit;
import cn.lary.api.stream.constant.STREAM;
import cn.lary.api.stream.entity.StreamRecord;
import cn.lary.stream.mapper.StreamRecordMapper;
import cn.lary.stream.service.StreamRecordService;
import cn.lary.api.stream.vo.StreamRecordVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
public class StreamRecordServiceImpl extends ServiceImpl<StreamRecordMapper, StreamRecord> implements StreamRecordService {

    private final LaryIDBuilder builder;

    @Override
    public StreamRecord build(StreamRecord dto) {
        dto.setSid(builder.next());
        save(dto);
        return dto;
    }

    @Override
    public ResponsePair<List<StreamRecordVO>> getPages(int page, int size) {
        List<StreamRecord> records = lambdaQuery()
                .eq(StreamRecord::getUid, RequestContext.uid())
                .eq(StreamRecord::getStatus, STREAM.STATUS.DOWN)
                .orderByDesc(StreamRecord::getCreateAt)
                .page(new Page<>(page, size))
                .getRecords();
        if(CollectionKit.isEmpty(records)) {
            return BusinessKit.fail("no records");
        }
        return BusinessKit.ok(records.stream()
                .map(StreamRecordVO::new)
                .toList());
    }
}

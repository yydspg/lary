package cn.lary.module.stream.service.impl;

import cn.lary.core.context.RequestContext;
import cn.lary.core.dto.ResponsePair;
import cn.lary.kit.BizKit;
import cn.lary.kit.CollectionKit;
import cn.lary.module.common.constant.Lary;
import cn.lary.module.stream.entity.StreamRecord;
import cn.lary.module.stream.mapper.StreamRecordMapper;
import cn.lary.module.stream.service.StreamRecordService;
import cn.lary.module.stream.vo.StreamRecordVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author paul
 * @since 2024-08-16
 */
@Service
public class StreamRecordServiceImpl extends ServiceImpl<StreamRecordMapper, StreamRecord> implements StreamRecordService {

    @Override
    public ResponsePair<List<StreamRecordVO>> getPages(int page, int size) {
        List<StreamRecord> records = lambdaQuery()
                .eq(StreamRecord::getUid, RequestContext.getLoginUID())
                .eq(StreamRecord::getStatus, Lary.Stream.Status.down)
                .orderByDesc(StreamRecord::getCreateAt)
                .page(new Page<>(page, size))
                .getRecords();
        if(CollectionKit.isEmpty(records)) {
            return BizKit.fail("no records");
        }
        List<StreamRecordVO> vos = new ArrayList<>();
        records.forEach(record -> {
            vos.add(new StreamRecordVO().of(record));
        });
        return BizKit.ok(vos);
    }
}

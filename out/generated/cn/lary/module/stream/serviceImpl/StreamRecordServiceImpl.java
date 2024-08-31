package cn.lary.module.stream.serviceImpl;

import cn.lary.module.stream.entity.StreamRecord;
import cn.lary.module.stream.mapper.StreamRecordMapper;
import cn.lary.module.stream.service.StreamRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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

}

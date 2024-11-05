package cn.lary.module.redpacket.service.impl;

import cn.lary.common.dto.ResponsePair;
import cn.lary.module.redpacket.dto.RedpacketRecordPageQueryDTO;
import cn.lary.module.redpacket.entity.RedpacketRecord;
import cn.lary.module.redpacket.mapper.RedpacketRecordMapper;
import cn.lary.module.redpacket.service.RedpacketRecordService;
import cn.lary.module.redpacket.vo.RedpacketRecordVO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
public class RedpacketRecordServiceImpl extends ServiceImpl<RedpacketRecordMapper, RedpacketRecord> implements RedpacketRecordService {

    @Override
    public ResponsePair<List<RedpacketRecordVO>> my(RedpacketRecordPageQueryDTO dto) {
        return null;
    }

}

package cn.lary.redpacket.service;

import cn.lary.common.dto.ResponsePair;
import cn.lary.api.redpacket.dto.RedpacketRecordPageQueryDTO;
import cn.lary.api.redpacket.entity.RedpacketRecord;
import cn.lary.api.redpacket.vo.RedpacketRecordVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author paul
 * @since 2024-10-31
 */
public interface RedpacketRecordService extends IService<RedpacketRecord> {


    ResponsePair<List<RedpacketRecordVO>> my(RedpacketRecordPageQueryDTO dto);

}

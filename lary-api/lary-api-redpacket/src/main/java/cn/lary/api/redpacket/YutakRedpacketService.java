package cn.lary.api.redpacket;

import cn.lary.api.redpacket.dto.RedpacketEventBuildDTO;
import cn.lary.api.redpacket.dto.RedpacketRecordPageQueryDTO;
import cn.lary.api.redpacket.vo.RedpacketEventVO;
import cn.lary.api.redpacket.vo.RedpacketRecordVO;
import cn.lary.api.redpacket.vo.RedpacketTokenVO;
import cn.lary.common.dto.MultiResponse;
import cn.lary.common.dto.SingleResponse;

public interface YutakRedpacketService {
    /**
     * 加入红包事件
     * @param toUid
     * @return
     */
    SingleResponse<RedpacketTokenVO> join( Long toUid);

    /**
     * 查询红包事件
     * @param toUid
     * @return {@link RedpacketEventVO}
     */
    SingleResponse<RedpacketEventVO>  info( Long toUid);


    /**
     * 创建红包事件
     * @param dto {@link RedpacketEventBuildDTO dto}
     * @return ok
     */
    SingleResponse<Void> build( RedpacketEventBuildDTO dto) ;

    /**
     * 查询红包记录
     * @param dto {@link RedpacketRecordPageQueryDTO }
     * @return {@link RedpacketRecordVO}
     */
    MultiResponse<RedpacketRecordVO> my( RedpacketRecordPageQueryDTO dto) ;
}

package cn.lary.redpacket.service;

import cn.lary.common.dto.ResponsePair;
import cn.lary.redpacket.dto.RedpacketFsyncDTO;
import cn.lary.redpacket.vo.RedpacketTokenVO;

public interface RedPacketInvolvedService {

    /**
     * 参与事件
     * @param eid 事件id
     * @return {@link RedpacketTokenVO}
     */
    ResponsePair<RedpacketTokenVO> join(long eid);

    ResponsePair<Void> fsync(RedpacketFsyncDTO dto);
}

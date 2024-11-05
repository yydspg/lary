package cn.lary.module.redpacket.service;

import cn.lary.common.dto.ResponsePair;
import cn.lary.module.redpacket.dto.RedpacketFsyncDTO;
import cn.lary.module.redpacket.vo.RedpacketTokenVO;

public interface RedPacketInvolvedService {


    ResponsePair<RedpacketTokenVO> join(long toUid);

    ResponsePair<Void> fsync(RedpacketFsyncDTO dto);
}

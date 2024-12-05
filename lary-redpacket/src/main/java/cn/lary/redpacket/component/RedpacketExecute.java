package cn.lary.redpacket.component;

import cn.lary.common.dto.ResponsePair;
import cn.lary.api.redpacket.dto.RedpacketEventBuildDTO;
import cn.lary.api.redpacket.dto.RedpacketFsyncDTO;
import cn.lary.api.redpacket.dto.RedpacketRecordPageQueryDTO;
import cn.lary.redpacket.service.RedPacketInvolvedService;
import cn.lary.redpacket.service.RedpacketEventService;
import cn.lary.redpacket.service.RedpacketRecordService;
import cn.lary.api.redpacket.vo.RedpacketEventVO;
import cn.lary.api.redpacket.vo.RedpacketRecordVO;
import cn.lary.api.redpacket.vo.RedpacketTokenVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedpacketExecute {

    private final RedpacketEventService redpacketEventService;
    private final RedpacketRecordService redpacketRecordService;
    private final RedPacketInvolvedService redpacketInvolvedService;

    /**
     * 创建直播红包事件
     * @param dto {@link RedpacketEventBuildDTO}
     * @return OK
     */
    public ResponsePair<Void> redpacket(RedpacketEventBuildDTO dto) {
        return redpacketEventService.redpacket(dto);
    }

    /**
     * 查询我的直播中奖记录
     * @param dto {@link RedpacketRecordPageQueryDTO}
     * @return {@link RedpacketRecordVO}
     */
    public ResponsePair<List<RedpacketRecordVO>> my(RedpacketRecordPageQueryDTO dto){
        return redpacketRecordService.my(dto);
    }

    /**
     * 参加红包活动
     * @param toUid anchor uid
     * @return OK
     */
    public ResponsePair<RedpacketTokenVO> join(long toUid) {
        return redpacketInvolvedService.join(toUid);
    }

    /**
     * 查看红包
     * @param toUid anchor uid
     * @return RedpacketEvent
     */
    public ResponsePair<RedpacketEventVO> info(long toUid) {
        return redpacketEventService.info(toUid);
    }

    /**
     * 强制红包同步
     * @param dto {@link RedpacketFsyncDTO }
     * @return OK
     */
    public ResponsePair<Void> fsync(RedpacketFsyncDTO dto) {
        return redpacketInvolvedService.fsync(dto);
    }
}

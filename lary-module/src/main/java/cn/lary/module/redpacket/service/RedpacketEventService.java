package cn.lary.module.redpacket.service;

import cn.lary.common.dto.Response;
import cn.lary.common.dto.ResponsePair;
import cn.lary.module.redpacket.dto.RedpacketEventBuildDTO;
import cn.lary.module.redpacket.entity.RedpacketEvent;
import cn.lary.module.redpacket.vo.RedpacketEventVO;
import cn.lary.module.stream.dto.RedPacketDTO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author paul
 * @since 2024-10-31
 */
public interface RedpacketEventService extends IService<RedpacketEvent> {

    /**
     * 创建红包活动
     * @param dto {@link RedPacketDTO}
     * @return ok
     */
    ResponsePair<Void> redpacket(RedpacketEventBuildDTO dto);

    /**
     * 查看直播抽奖活动
     * @param toUid anchor uid
     * @return {@link RedpacketEventVO}
     */
    ResponsePair<RedpacketEventVO> info(long toUid);
}

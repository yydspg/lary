package cn.lary.redpacket.service;

import cn.lary.common.dto.ResponsePair;
import cn.lary.api.redpacket.dto.RedpacketEventBuildDTO;
import cn.lary.api.redpacket.entity.RedpacketEvent;
import cn.lary.api.redpacket.vo.RedpacketEventVO;
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
     * @return OK
     */
    ResponsePair<Void> redpacket(RedpacketEventBuildDTO dto);

    /**
     * 查看直播抽奖活动
     * @param toUid anchor uid
     * @return {@link RedpacketEventVO}
     */
    ResponsePair<RedpacketEventVO> info(long toUid);
}

package cn.lary.module.stream.service;

import cn.lary.common.dto.ResponsePair;
import cn.lary.module.stream.dto.RedPacketDTO;
import cn.lary.module.stream.entity.RedPacket;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author paul
 * @since 2024-10-02
 */
public interface RedPacketService extends IService<RedPacket> {
    /**
     * 创建红包活动
     * @param dto {@link RedPacketDTO}
     * @return ok
     */
     ResponsePair<Void> redPacket (RedPacketDTO dto);

}

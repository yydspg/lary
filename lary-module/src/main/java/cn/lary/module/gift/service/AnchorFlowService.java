package cn.lary.module.gift.service;

import cn.lary.common.dto.ResponsePair;
import cn.lary.module.gift.entity.AnchorFLow;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author paul
 * @since 2024-08-16
 */
public interface AnchorFlowService extends IService<AnchorFLow> {

    /**
     * 计算直播收入
     * @param uid u
     * @param streamId s
     * @return 收入
     */
    // TODO  :  使用阿里的ttl优化
    ResponsePair<Long> buildTurnover(long uid,int streamId);
}

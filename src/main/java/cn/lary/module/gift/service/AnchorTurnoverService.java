package cn.lary.module.gift.service;

import cn.lary.core.dto.ResponsePair;
import cn.lary.module.gift.entity.AnchorTurnover;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author paul
 * @since 2024-08-16
 */
public interface AnchorTurnoverService extends IService<AnchorTurnover> {

    /**
     * 计算直播收入
     * @param uid u
     * @param streamId s
     * @return 收入
     */
    // TODO  :  使用阿里的ttl优化
    ResponsePair<Long> buildTurnover(int uid,int streamId);
}

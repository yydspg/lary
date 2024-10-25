package cn.lary.module.gift.service;

import cn.lary.common.dto.ResponsePair;
import cn.lary.module.gift.entity.AnchorIncome;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author paul
 * @since 2024-08-16
 */
public interface AnchorIncomeService extends IService<AnchorIncome> {

    /**
     * 计算直播收入
     * @param uid u
     * @param streamId s
     * @return 收入
     */
    // TODO  :  使用阿里的ttl优化
    ResponsePair<Long> buildTurnover(int uid,int streamId);
}

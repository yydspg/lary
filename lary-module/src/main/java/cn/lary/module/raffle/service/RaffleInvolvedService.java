package cn.lary.module.raffle.service;

import cn.lary.common.dto.ResponsePair;

public interface RaffleInvolvedService {
    /**
     * 参与抽奖活动
     * @param eid 事件id
     * @return ok
     */
    ResponsePair<Void> join(long eid);

}

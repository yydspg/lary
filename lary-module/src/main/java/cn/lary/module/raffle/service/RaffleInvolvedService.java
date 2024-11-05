package cn.lary.module.raffle.service;

import cn.lary.common.dto.ResponsePair;

public interface RaffleInvolvedService {

    ResponsePair<Void> join(long toUid);

}

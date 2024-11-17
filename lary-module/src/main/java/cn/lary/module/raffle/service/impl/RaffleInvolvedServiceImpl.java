package cn.lary.module.raffle.service.impl;

import cn.lary.common.context.RequestContext;
import cn.lary.common.dto.ResponsePair;
import cn.lary.common.kit.BusinessKit;
import cn.lary.module.raffle.component.RaffleCacheComponent;
import cn.lary.module.raffle.dto.RaffleEventCache;
import cn.lary.module.common.cache.CacheComponent;
import cn.lary.module.common.constant.LARY;
import cn.lary.module.message.service.MessageService;
import cn.lary.module.raffle.dto.RaffleCachePair;
import cn.lary.module.raffle.dto.RaffleRuleCache;
import cn.lary.module.raffle.service.RaffleInvolvedService;
import cn.lary.module.stream.entity.Follow;
import cn.lary.module.stream.service.FollowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RaffleInvolvedServiceImpl implements RaffleInvolvedService {

    private final RaffleCacheComponent raffleCacheComponent;
    private final FollowService followService;
    private final MessageService messageService;
    private final CacheComponent cacheComponent;

    @Override
    public ResponsePair<Void> join(long eid) {
        long uid = RequestContext.uid();
        RaffleEventCache raffle = raffleCacheComponent.getRaffle(eid);
        if (raffle == null) {
            return BusinessKit.fail("抽奖活动已过期");
        }
        if (raffle.getFan()) {
            Follow follow = followService.lambdaQuery()
                    .select(Follow::getUid, Follow::getToUid,Follow::getLevel, Follow::getAmount)
                    .eq(Follow::getUid, uid)
                    .one();
            if (follow == null || follow.getStatus() != LARY.FOLLOW.STATUS.COMMON) {
                return BusinessKit.fail("请先关注主播");
            }
            if (raffle.getLevel() >=0 && follow.getLevel() < raffle.getLevel()) {
                return BusinessKit.fail("粉丝等级不够,多多支持主播");
            }
            if (raffle.getAmount().longValue() != 0
                    && follow.getAmount().compareTo(raffle.getAmount()) < 0) {
                return BusinessKit.fail("送礼不足");
            }
        }
        // TODO  :  impl
        RaffleRuleCache rule = raffleCacheComponent.getRule(eid);
        RaffleCachePair pair = rule.add(uid);
        if (pair.isSend()) {
            //sync
            cacheComponent.appendInvolvedUsers(uid,pair.getJoiner());
        }
        if (pair.isOver()) {
//            messageService.asyncSendRocketMessage(new RaffleRuleLocalCacheMessage()
//                    .setUid(toUid)
//                    .setShard(rule.getShard() * 2)
//                    .setLimit(rule.getLimit()));
        }
        return BusinessKit.ok();
    }
}

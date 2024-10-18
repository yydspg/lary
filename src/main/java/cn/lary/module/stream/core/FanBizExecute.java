package cn.lary.module.stream.core;

import cn.lary.core.dto.ResponsePair;
import cn.lary.kit.BizKit;
import cn.lary.kit.StringKit;
import cn.lary.module.common.cache.KVBuilder;
import cn.lary.module.common.cache.RedisCache;
import cn.lary.module.stream.dto.LiveCacheDTO;
import cn.lary.module.stream.dto.RaffleCacheDTO;
import cn.lary.module.stream.dto.RedPacketCacheDTO;
import cn.lary.module.stream.entity.Follow;
import cn.lary.module.stream.service.FollowService;
import cn.lary.module.wallet.dto.TransferDTO;
import cn.lary.module.wallet.service.WalletService;
import cn.lary.pkg.wk.api.WKMessageService;
import cn.lary.pkg.wk.dto.message.MessageSendDTO;
import cn.lary.pkg.wk.constant.WK;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FanBizExecute {

    private final RedisCache redisCache;
    private final KVBuilder kvBuilder;
    private final WalletService walletService;
    private final WKMessageService wkMessageService;
    private final FollowService followService;

    /**
     *  参与抽奖
     * @param uid u
     * @param toUid anchor id
     * @return ok
     */
    public ResponsePair<Void> joinRaffle(int uid, int toUid) {
        Map<Object, Object> data = redisCache.getHash(kvBuilder.goLiveK(uid));
        if (data == null) {
            return BizKit.fail("no live info");
        }
        LiveCacheDTO liveCache = LiveCacheDTO.of(data);
        Map<Object, Object> map = redisCache.getHash(kvBuilder.raffleK(toUid));
        if (map == null) {
            log.error("join raffle failed, uid:{}, toUid:{}", uid, toUid);
            return BizKit.fail("no raffle info");
        }
        RaffleCacheDTO cache = RaffleCacheDTO.of(map);
        if (cache.isFan()) {
            Follow relation = followService.lambdaQuery()
                    .eq(Follow::getUid, uid)
                    .eq(Follow::getToUid, toUid)
                    .one();
            if (relation == null || relation.getIsUnfollow() ) {
                return BizKit.fail("please follow anchor first");
            }
            if (relation.getIsBlock()) {
                return BizKit.fail("you been blocked");
            }
            if (relation.getLevel() < cache.getFanLevel()) {
                return BizKit.fail("level is low");
            }
        }
        if (cache.getCost() > 0 ) {
            TransferDTO dto = new TransferDTO()
                    .setUid(uid)
                    .setAmount(cache.getCost())
                    .setChannelId(liveCache.getStreamId())
                    .setChannelType(WK.ChannelType.stream)
                    .setToUid(toUid);
            ResponsePair<Void> response = walletService.transfer(dto);
            if(response.isFail()) {
                return BizKit.fail(response.getMsg());
            }
        }
        redisCache.append(kvBuilder.raffleK(toUid),String.valueOf(uid));
        if (StringKit.isNotEmpty(cache.getMessage())){
            wkMessageService.send(new MessageSendDTO()
                    .setFromUID(uid)
                    .setChannelID(liveCache.getWkChannelId())
                    .setChannelType(WK.ChannelType.stream)
                    .setPayload(cache.getMessage().getBytes(StandardCharsets.UTF_8)));
        }
        return BizKit.ok();
    }

    /**
     * 查询抽奖信息
     * @param toUid anchor
     * @return {@link RaffleCacheDTO}
     */
    public ResponsePair<RaffleCacheDTO> getRaffleInfo(int toUid) {
        Map<Object, Object> map = redisCache.getHash(kvBuilder.raffleK(toUid));
        if (map == null) {
            return BizKit.fail("no raffle info");
        }
        return BizKit.ok(RaffleCacheDTO.of(map));
    }

    /**
     * 查询红包信息
     * @param toUid anchor
     * @return {@link RedPacketCacheDTO}
     */
    public ResponsePair<RedPacketCacheDTO> getRedPacketInfo(int toUid) {
        Map<Object, Object> map = redisCache.getHash(kvBuilder.redPacketK(toUid));
        if (map == null) {
            return BizKit.fail("no red packet info");
        }
        return BizKit.ok(RedPacketCacheDTO.of(map));
    }
    /**
     * 参与红包,
     * @param uid u
     * @param toUid anchor
     * @return ok
     */
    public ResponsePair<Void> redWars(int uid, int toUid) {
        boolean ok = true;
        if (ok) {
            return BizKit.ok();
        }else {
            return BizKit.fail("红包已被抢完了,下次加油");
        }
    }
}

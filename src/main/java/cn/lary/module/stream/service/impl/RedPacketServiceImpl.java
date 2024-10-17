package cn.lary.module.stream.service.impl;

import cn.lary.core.context.RequestContext;
import cn.lary.core.dto.ResponsePair;
import cn.lary.kit.BizKit;
import cn.lary.kit.StringKit;
import cn.lary.module.app.service.EventService;
import cn.lary.module.common.cache.KVBuilder;
import cn.lary.module.common.cache.RedisCache;
import cn.lary.module.event.dto.RedPacketEventDTO;
import cn.lary.module.message.dto.stream.CreateRedPacketNotifyDTO;
import cn.lary.module.stream.dto.LiveCacheDTO;
import cn.lary.module.stream.dto.RedPacketCloseMessage;
import cn.lary.module.stream.dto.RedPacketDTO;
import cn.lary.module.stream.entity.RedPacket;
import cn.lary.module.stream.mapper.RedPacketMapper;
import cn.lary.module.stream.service.RedPacketService;
import cn.lary.module.wallet.entity.Wallet;
import cn.lary.module.wallet.service.WalletService;
import cn.lary.pkg.wk.api.WKMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.houbb.sensitive.word.core.SensitiveWordHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author paul
 * @since 2024-10-02
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedPacketServiceImpl extends ServiceImpl<RedPacketMapper, RedPacket> implements RedPacketService {
    private final KVBuilder kvBuilder;
    private final EventService eventService;
    private final WalletService walletService;

    // external
    private final WKMessageService wkMessageService;
    private final RedisCache redisCache;
    private final RocketMQTemplate rocketMQTemplate;

    @Override
    public ResponsePair<Void> redPacket(RedPacketDTO dto) {
        int uid = RequestContext.getLoginUID();
        Map<Object, Object> map = redisCache.getHash(kvBuilder.goLiveK(uid));
        if(map == null){
            log.error("create red pack fail,no live info, uid:{}", uid);
            return BizKit.fail("no live info");
        }
        LiveCacheDTO cache = LiveCacheDTO.of(map);
        Map<Object, Object> data = redisCache.getHash(kvBuilder.redPacketK(uid));
        if(data != null){
            log.error("have unfinished red packet service,uid:{}", uid);
            return BizKit.fail("have unfinished red packet");
        }
        String badWord = SensitiveWordHelper.findFirst(dto.getTitle());
        if (StringKit.isNotEmpty(badWord)) {
            return BizKit.fail("title has bad word:"+badWord);
        }
        boolean needSendMessage = StringKit.isEmpty(dto.getMessage());
        if (needSendMessage) {
            badWord = SensitiveWordHelper.findFirst(dto.getMessage());
            if (StringKit.isNotEmpty(badWord)) {
                return BizKit.fail("message has bad word:"+badWord);
            }
        }
        Wallet wallet = walletService.lambdaQuery()
                .eq(Wallet::getUid, uid)
                .eq(Wallet::getIsBlock, false)
                .one();
        if(wallet == null){
            log.error("create redPacket,wallet not found,uid:{}", uid);
            return BizKit.fail("wallet not found");
        }
        long totalCost = dto.getNum() * dto.getCost();
        if (wallet.getVcFee() - wallet.getVcLocked() <= totalCost) {
            return BizKit.fail("balance not enough");
        }
        walletService.lambdaUpdate()
                .eq(Wallet::getUid, uid)
                .set(Wallet::getVcFee,wallet.getVcFee() - totalCost)
                .set(Wallet::getVcOutcome,wallet.getVcOutcome() + totalCost);
        RedPacket redPacket = new RedPacket()
                .setNum(dto.getNum())
                .setUid(uid)
                .setStreamId(cache.getStreamId())
                .setTitle(dto.getTitle())
                .setVc(dto.getCost())
                .setVcAll(totalCost);
        save(redPacket);
        int eventId = eventService.begin(new RedPacketEventDTO(uid, cache.getStreamId(), redPacket.getId()).of());
        RedPacketCloseMessage redPacketCloseMessage = new RedPacketCloseMessage(eventId, uid, cache.getStreamId(), redPacket.getId());
        GenericMessage<RedPacketCloseMessage> genericMessage = new GenericMessage<>(redPacketCloseMessage);
        try {
            rocketMQTemplate.syncSend("lary:redpacket-close",genericMessage);
        } catch (Exception e) {
            log.error("sync send red packet close message error,uid:{},streamId:{},redpacketId:{}"
                    , uid ,cache.getStreamId(),redPacket.getId());
        }
        log.info("success send red packet close event,uid:{},streamId:{},redpacketId:{}"
                , uid,cache.getStreamId(),redPacket.getId());
        wkMessageService.send(new CreateRedPacketNotifyDTO(uid, cache.getWkChannelId(),totalCost));
        return BizKit.ok();
    }
}

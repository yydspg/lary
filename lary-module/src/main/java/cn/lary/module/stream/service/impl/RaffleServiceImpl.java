package cn.lary.module.stream.service.impl;

import cn.lary.common.context.RequestContext;
import cn.lary.common.dto.ResponsePair;
import cn.lary.common.kit.BusinessKit;
import cn.lary.common.kit.JSONKit;
import cn.lary.common.kit.StringKit;
import cn.lary.external.wk.api.WKMessageService;
import cn.lary.module.app.service.EventService;
import cn.lary.module.common.cache.KVBuilder;
import cn.lary.module.common.cache.RedisCache;
import cn.lary.module.common.constant.LARY;
import cn.lary.module.event.dto.RaffleEventDTO;
import cn.lary.module.message.dto.stream.CreateRaffleNotifyDTO;
import cn.lary.module.stream.dto.LiveCacheDTO;
import cn.lary.module.stream.dto.RaffleCacheDTO;
import cn.lary.module.stream.listener.raffle.RaffleCloseMessage;
import cn.lary.module.stream.dto.RaffleDTO;
import cn.lary.module.stream.entity.Raffle;
import cn.lary.module.stream.mapper.RaffleMapper;
import cn.lary.module.stream.service.RaffleService;
import cn.lary.module.wallet.entity.Wallet;
import cn.lary.module.wallet.service.WalletService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.houbb.sensitive.word.core.SensitiveWordHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;

import java.util.HashMap;
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
public class RaffleServiceImpl extends ServiceImpl<RaffleMapper, Raffle> implements RaffleService {


    private final KVBuilder kvBuilder;
    private final EventService eventService;
    private final WalletService walletService;

    // external
    private final WKMessageService wkMessageService;
    private final RedisCache redisCache;
    private final RocketMQTemplate rocketMQTemplate;
    
    @Override
    public ResponsePair<Void> raffle(RaffleDTO dto) {
        long uid= RequestContext.getLoginUID();
        Map<Object, Object> map = redisCache.getHash(kvBuilder.goLiveK(uid));
        if(map == null){
            log.error("create raffle fail,no live info, uid:{}", uid);
            return BusinessKit.fail("no live info");
        }
        LiveCacheDTO cache = LiveCacheDTO.of(map);
        Map<Object, Object> raffleData = redisCache.getHash(kvBuilder.raffleK(uid));
        if(raffleData != null){
            log.error("have unfinished raffle,uid:{},streamId:{}", uid,cache.getStreamId());
            return BusinessKit.fail("have unfinished raffle");
        }
        Integer type = dto.getType();
        boolean isInner = type == LARY.Raffle.inner;

        String title = dto.getTitle();
        String badWord = SensitiveWordHelper.findFirst(title);
        if (StringKit.isNotEmpty(badWord)) {
            return BusinessKit.fail("raffle title contains bad word:"+badWord);
        }
        String content = dto.getContent();
        if (!isInner) {
            badWord = SensitiveWordHelper.findFirst(content);
            if (StringKit.isNotEmpty(badWord)) {
                return BusinessKit.fail("raffle content contains bad word:"+badWord);
            }
        }
        boolean needSendMessage = StringKit.isEmpty(dto.getMessage());
        if (needSendMessage) {
            badWord = SensitiveWordHelper.findFirst(dto.getMessage());
            if (StringKit.isNotEmpty(badWord)) {
                return BusinessKit.fail("raffle message contains bad word:"+badWord);
            }
        }
        boolean threshold = dto.getCost() != null && dto.getCost() > 0;
        Wallet wallet = walletService.getOne(new LambdaQueryWrapper<Wallet>()
                .eq(Wallet::getUid, uid));
        if(wallet == null){
            log.error("wallet not found,uid:{}", uid);
            return BusinessKit.fail("wallet not found");
        }
        if (isInner && threshold) {
            long totalCost = dto.getNum() * dto.getCost();
            if (wallet.getVcFee() - wallet.getVcLocked() <= totalCost) {
                return BusinessKit.fail("余额不足，请充值");
            }
        }
        Map param = getThreshold(dto);
        Raffle raffle = new Raffle()
                .setStreamId(cache.getStreamId())
                .setDuration(dto.getDuration())
                .setNum(dto.getNum())
                .setParam(JSONKit.toJSON(param))
                .setContent(dto.getContent())
                .setItemNum(dto.getItemNum())
                .setUid(uid)
                .setTitle(title)
                .setType(dto.getType());
        this.save(raffle);
        long eventId = eventService.begin(new RaffleEventDTO(uid, cache.getStreamId(), raffle.getId()));
        RaffleCacheDTO cacheDTO = new RaffleCacheDTO()
                .setDuration(dto.getDuration())
                .setContent(dto.getContent())
                .setNum(dto.getNum())
                .setMessage(dto.getMessage())
                .setFan(dto.getIsFan())
                .setCost(dto.getCost())
                .setType(type)
                .setFanLevel(dto.getFanLevel());
        redisCache.setHash(kvBuilder.raffleK(uid),kvBuilder.raffleV(cacheDTO),dto.getDuration());
        RaffleCloseMessage closeMessage = new RaffleCloseMessage(eventId, uid, cache.getStreamId(), raffle.getId());

        wkMessageService.send(new CreateRaffleNotifyDTO(uid, cache.getDanmakuId()));
        return BusinessKit.ok();
    }
    /**
     * 获取 门槛参数
     * @param dto {@link RaffleDTO}
     * @return map
     */
    private Map getThreshold(RaffleDTO dto) {
        HashMap<Object, Object> args = new HashMap<>();
        if (dto.getIsFan()) {
            args.put("isFan", true);
        }
        if (dto.getFanLevel() != null ) {
            args.put("fanLevel", dto.getFanLevel());
        }
        if (dto.getCost() != null ){
            args.put("cost", dto.getCost());
        }
        if (dto.getMessage() != null ){
            args.put("message", dto.getMessage());
        }
        return args;
    }
}

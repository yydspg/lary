package cn.lary.module.stream.core;

import cn.lary.core.dto.ResPair;
import cn.lary.kit.BizKit;
import cn.lary.kit.JSONKit;
import cn.lary.kit.StringKit;
import cn.lary.module.app.service.EventService;
import cn.lary.module.common.constant.Lary;
import cn.lary.module.common.cache.KVBuilder;
import cn.lary.module.common.cache.RedisCache;
import cn.lary.module.event.dto.RaffleEventDTO;
import cn.lary.module.event.dto.RedPacketEventDTO;
import cn.lary.module.stream.dto.*;
import cn.lary.module.stream.entity.Raffle;
import cn.lary.module.stream.entity.RedPacket;
import cn.lary.module.stream.entity.StreamRecord;
import cn.lary.module.stream.service.RaffleService;
import cn.lary.module.stream.service.RedPacketService;
import cn.lary.module.stream.service.StreamRecordService;
import cn.lary.module.stream.vo.StreamRecordVO;
import cn.lary.module.wallet.entity.Wallet;
import cn.lary.module.wallet.service.WalletService;
import cn.lary.pkg.wk.api.WKMessageService;
import cn.lary.pkg.wk.dto.message.MessageSendDTO;
import cn.lary.pkg.wk.entity.core.WK;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.houbb.sensitive.word.core.SensitiveWordHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class StreamBizExecute {


    private final RedisCache redisCache;
    private final KVBuilder kvBuilder;
    private final StreamRecordService streamRecordService;
    private final WalletService walletService;
    private final RaffleService raffleService;
    private final EventService eventService;
    private final RedPacketService redPacketService;
    private final RocketMQTemplate rocketMQTemplate;

    // external
    private final WKMessageService wkMessageService;

    /**
     * 直播记录分页查询
     * @param uid u
     * @param page p
     * @param size s
     * @return {@link StreamRecordVO}
     */
    public ResPair<List<StreamRecordVO>> page(int uid, int page, int size) {
        Page<StreamRecord> data = streamRecordService.page(new Page<>(page, size), new LambdaQueryWrapper<StreamRecord>()
                .eq(StreamRecord::getUid, uid)
                .eq(StreamRecord::getStatus, Lary.Stream.Status.down)
                .orderByDesc(StreamRecord::getCreateAt));
        List<StreamRecordVO> vos = new ArrayList<>();
        data.getRecords().forEach(record -> {
            StreamRecordVO vo = new StreamRecordVO().of(record);
            vos.add(vo);
        });
        return BizKit.ok(vos);
    }

    /**
     * 直播抽奖活动
     * @param uid user id
     * @param req {@link RaffleDTO}
     * @return ok
     */
    public ResPair<Void> raffle(int uid, RaffleDTO req){
        Map<Object, Object> map = redisCache.getHash(kvBuilder.goLiveK(uid));
        if(map == null){
            log.error("create raffle fail,no live info, uid:{}", uid);
            return BizKit.fail("no live info");
        }
        LiveCacheDTO cache = LiveCacheDTO.of(map);
        Map<Object, Object> raffleData = redisCache.getHash(kvBuilder.raffleK(uid));
        if(raffleData != null){
            log.error("have unfinished raffle,uid:{},streamId:{}", uid,cache.getStreamId());
            return BizKit.fail("have unfinished raffle");
        }
        Integer type = req.getType();
        boolean isInner = type == Lary.Raffle.inner;

//        Raffle unfinishedData = raffleService.getOne(new LambdaQueryWrapper<Raffle>().eq(Raffle::getUid, uid)
//                .eq(Raffle::getStreamId, cache.getStreamId())
//                .eq(Raffle::getIsSync, false), false);
//        if(unfinishedData != null){
//            log.error("have unfinished raffle,uid:{},streamId:{},raffleId:{}", uid,cache.getStreamId(),unfinishedData.getId());
//            return BizKit.fail("have unfinished raffle");
//        }
        String title = req.getTitle();
        String badWord = SensitiveWordHelper.findFirst(title);
        if (StringKit.isNotEmpty(badWord)) {
            return BizKit.fail("raffle title contains bad word:"+badWord);
        }
        String content = req.getContent();
        if (!isInner) {
            badWord = SensitiveWordHelper.findFirst(content);
            if (StringKit.isNotEmpty(badWord)) {
                return BizKit.fail("raffle content contains bad word:"+badWord);
            }
        }
        boolean needSendMessage = StringKit.isEmpty(req.getMessage());
        if (needSendMessage) {
            badWord = SensitiveWordHelper.findFirst(req.getMessage());
            if (StringKit.isNotEmpty(badWord)) {
                return BizKit.fail("raffle message contains bad word:"+badWord);
            }
        }
        boolean threshold = req.getCost() != null && req.getCost() > 0;
        Wallet wallet = walletService.getOne(new LambdaQueryWrapper<Wallet>()
                .eq(Wallet::getUid, uid));
        if(wallet == null){
            log.error("wallet not found,uid:{}", uid);
            return BizKit.fail("wallet not found");
        }
        if (isInner && threshold) {
            long totalCost = req.getNum() * req.getCost();
            if (wallet.getVcFee() - wallet.getVcLocked() <= totalCost) {
                return BizKit.fail("余额不足，请充值");
            }
        }
        Map param = getThreshold(req);
        Raffle raffle = new Raffle()
                .setStreamId(cache.getStreamId())
                .setDuration(req.getDuration())
                .setNum(req.getNum())
                .setParam(JSONKit.toJSON(param))
                .setContent(req.getContent())
                .setItemNum(req.getItemNum())
                .setUid(uid)
                .setTitle(title)
                .setType(req.getType());
        raffleService.save(raffle);
        int eventId = eventService.begin(new RaffleEventDTO(uid, cache.getStreamId(), raffle.getId()).of());
        RaffleCacheDTO cacheDTO = new RaffleCacheDTO()
                .setDuration(req.getDuration())
                .setContent(req.getContent())
                .setNum(req.getNum())
                .setMessage(req.getMessage())
                .setFan(req.getIsFan())
                .setCost(req.getCost())
                .setType(type)
                .setFanLevel(req.getFanLevel());
        redisCache.setHash(kvBuilder.raffleK(uid),kvBuilder.raffleV(cacheDTO),req.getDuration());
        // TODO  :  使用mq 异步处理抽奖结束任务
        RaffleCloseMessage closeMessage = new RaffleCloseMessage(eventId, uid, cache.getStreamId(), raffle.getId());
        GenericMessage<RaffleCloseMessage> rocketMQMessage = new GenericMessage<>(closeMessage);
        try {
            rocketMQTemplate.syncSend("lary:raffle-close",rocketMQMessage,0,0);
        } catch (Exception e) {
            log.error("sync send raffle close message error,uid:{},msg:{}",uid,e.getMessage());
        }
        String message = "我发布了一个新的抽奖活动快来看看吧";
        wkMessageService.send(new MessageSendDTO()
                .setChannelID(cache.getWkChannelId())
                .setChannelType(WK.ChannelType.stream)
                .setFromUID(uid)
                .setPayload(message.getBytes(StandardCharsets.UTF_8)));
        return BizKit.ok();
    }

    /**
     * 直播红包处理
     * @param uid user id
     * @param uidName user name
     * @return ok
     */
    public ResPair<Void> redPacket(int uid, String uidName, RedPacketDTO req) {
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
        String badWord = SensitiveWordHelper.findFirst(req.getTitle());
        if (StringKit.isNotEmpty(badWord)) {
            return BizKit.fail("title has bad word:"+badWord);
        }
        boolean needSendMessage = StringKit.isEmpty(req.getMessage());
        if (needSendMessage) {
            badWord = SensitiveWordHelper.findFirst(req.getMessage());
            if (StringKit.isNotEmpty(badWord)) {
                return BizKit.fail("message has bad word:"+badWord);
            }
        }
        Wallet wallet = walletService.getOne(new LambdaQueryWrapper<Wallet>()
                .eq(Wallet::getUid, uid)
                .eq(Wallet::getIsBlock, false));
        if(wallet == null){
            log.error("create redPacket,wallet not found,uid:{}", uid);
            return BizKit.fail("wallet not found");
        }
        long totalCost = req.getNum() * req.getCost();
        if (wallet.getVcFee() - wallet.getVcLocked() <= totalCost) {
            return BizKit.fail("balance not enough");
        }
        walletService.lambdaUpdate()
                .eq(Wallet::getUid, uid)
                .set(Wallet::getVcFee,wallet.getVcFee() - totalCost)
                .set(Wallet::getVcOutcome,wallet.getVcOutcome() + totalCost);
        RedPacket redPacket = new RedPacket()
                .setNum(req.getNum())
                .setUid(uid)
                .setStreamId(cache.getStreamId())
                .setTitle(req.getTitle())
                .setVc(req.getCost())
                .setVcAll(totalCost);
        redPacketService.save(redPacket);
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
        String content = "我发布了价值"+totalCost+"的红包,手慢无哦";
        wkMessageService.send(new MessageSendDTO()
                .setChannelID(cache.getWkChannelId())
                .setPayload(content.getBytes(StandardCharsets.UTF_8))
                .setChannelType(WK.ChannelType.stream)
                .setFromUID(uid));
        return BizKit.ok();
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

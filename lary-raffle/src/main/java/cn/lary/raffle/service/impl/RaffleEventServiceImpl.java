package cn.lary.raffle.service.impl;

import cn.lary.common.dto.ResponsePair;
import cn.lary.common.kit.BusinessKit;
import cn.lary.raffle.component.RaffleCacheComponent;
import cn.lary.api.raffle.dto.RaffleEventCache;
import cn.lary.api.raffle.dto.RaffleEventDTO;
import cn.lary.api.raffle.entity.RaffleEvent;
import cn.lary.raffle.mapper.RaffleEventMapper;
import cn.lary.raffle.service.RaffleEventService;
import cn.lary.api.raffle.vo.RaffleEventVO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author paul
 * @since 2024-10-31
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RaffleEventServiceImpl extends ServiceImpl<RaffleEventMapper, RaffleEvent> implements RaffleEventService {

    private final RaffleCacheComponent raffleCacheComponent;
//    private final LiveCacheComponent liveCacheComponent;
//    private final EventService eventService;
//    private final YutakMessageService messageService;
//    private final RoomService roomService;

    @Override
    public ResponsePair<Void> raffle(RaffleEventDTO dto) {
//        long uid= RequestContext.uid();
//        LiveCache cache = liveCacheComponent.getLive(uid);
//        if (cache == null) {
//            log.error("create raffle FAIL,no live info, uid:{}", uid);
//            return BusinessKit.fail("no live info");
//        }
//        RaffleEventCache raffleCache = raffleCacheComponent.getRaffle(uid);
//        if (raffleCache != null) {
//            log.error("have unfinished raffle,uid:{},streamId:{}", uid,cache.getSid());
//            return BusinessKit.fail("have unfinished raffle");
//        }
//
//        String title = dto.getTitle();
//        String badWord = SensitiveWordHelper.findFirst(title);
//        if (StringKit.isNotEmpty(badWord)) {
//            return BusinessKit.fail("raffle title contains bad word:"+badWord);
//        }
//        String content = dto.getContent();
//        badWord = SensitiveWordHelper.findFirst(content);
//        if (StringKit.isNotEmpty(badWord)) {
//            return BusinessKit.fail("raffle content contains bad word:"+badWord);
//        }
//        boolean needSendMessage = StringKit.isEmpty(dto.getMessage());
//        if (needSendMessage) {
//            badWord = SensitiveWordHelper.findFirst(dto.getMessage());
//            if (StringKit.isNotEmpty(badWord)) {
//                return BusinessKit.fail("raffle message contains bad word:"+badWord);
//            }
//        }
//        Map param = getThreshold(dto);
//        RaffleEvent raffle = new RaffleEvent()
//                .setSid(cache.getSid())
//                .setDuration(dto.getDuration())
//                .setNum(dto.getNum())
//                .setParam(JSONKit.toJSON(param))
//                .setContent(dto.getContent())
//                .setItemNum(dto.getItemNum())
//                .setUid(uid)
//                .setTitle(title)
//                .setCategory(dto.getCategory());
//        this.save(raffle);
//        long eventId = eventService.begin(new RafflePublishEventDTO(uid, cache.getSid(), raffle.getId()));
//        RaffleEventCache cacheDTO = new RaffleEventCache()
//                .setDuration(dto.getDuration())
//                .setContent(dto.getContent())
//                .setNum(dto.getNum())
//                .setContent(dto.getMessage())
//                .setFan(dto.getFan())
//                .setCategory(dto.getCategory())
//                .setLevel(dto.getLevel());
//        raffleCacheComponent.setRaffle(uid,cacheDTO);
//        messageService.asyncSendRocketMessage(new RaffleEventAutoCloseMessage()
//                .setEventId(eventId)
//                .setRaffleId(raffle.getId())
//                .setStreamId(raffle.getSid())
//                .setUid(uid)
//                .setCategory(dto.getCategory()));
//        messageService.asyncSendRocketMessage(new RaffleRuleLocalCacheMessage()
//                .setUid(RequestContext.uid())
//                .setShard(getHeat()));
//        messageService.send(new CreateRaffleNotifyDTO(uid, cache.getCid()));
        return BusinessKit.ok();
    }

    @Override
    public ResponsePair<RaffleEventVO> info(long toUid) {
        RaffleEventCache raffle = raffleCacheComponent.getRaffle(toUid);
        if (raffle == null) {
            return BusinessKit.fail("no raffle info");
        }
        return BusinessKit.ok(new RaffleEventVO(raffle));
    }

    private Map getThreshold(RaffleEventDTO dto) {
        HashMap<Object, Object> args = new HashMap<>();
        if (dto.getFan()) {
            args.put("isFan", true);
        }
        if (dto.getLevel() != null ) {
            args.put("fanLevel", dto.getLevel());
        }
        if (dto.getAmount() != null ){
            args.put("cost", dto.getAmount());
        }
        if (dto.getMessage() != null ){
            args.put("message", dto.getMessage());
        }
        return args;
    }

    public int getHeat() {
//        Room room = roomService.lambdaQuery()
//                .select(Room::getBehaviorScore
//                        , Room::getBenefitScore
//                        , Room::getContentScore)
//                .eq(Room::getUid, RequestContext.uid())
//                .one();
//        long flag = room.getBenefitScore() * 3L
//                + room.getBehaviorScore()* 2L
//                + room.getContentScore();
//        if (flag < 100) {
//            return 2;
//        }else if (flag > 300) {
//            return 10;
//        }
//        return 5;
        return -1;
    }
}

package cn.lary.module.stream.core;

import cn.lary.core.dto.ResponsePair;
import cn.lary.kit.*;
import cn.lary.module.app.service.EventService;
import cn.lary.module.app.service.LaryChannelService;
import cn.lary.module.common.constant.Lary;
import cn.lary.module.common.cache.KVBuilder;
import cn.lary.module.common.cache.RedisCache;
import cn.lary.module.event.dto.DownLiveEventDTO;
import cn.lary.module.gift.entity.AnchorTurnover;
import cn.lary.module.gift.service.AnchorTurnoverService;
import cn.lary.module.gift.service.GiftOrderService;
import cn.lary.module.stream.dto.*;
import cn.lary.module.stream.entity.Follow;
import cn.lary.module.stream.entity.Room;
import cn.lary.module.stream.entity.StreamRecord;
import cn.lary.module.stream.service.*;
import cn.lary.module.stream.vo.DownLiveVO;
import cn.lary.module.stream.vo.GoLiveVO;
import cn.lary.module.stream.vo.JoinLiveVO;
import cn.lary.module.user.service.DeviceService;
import cn.lary.module.user.service.UserService;
import cn.lary.pkg.wk.api.WKChannelService;
import cn.lary.pkg.wk.api.WKMessageService;
import cn.lary.pkg.wk.api.WKUserService;
import cn.lary.pkg.wk.dto.channel.SubscribersAddDTO;
import cn.lary.pkg.wk.dto.message.MessageHeader;
import cn.lary.pkg.wk.dto.message.MessageSendDTO;
import cn.lary.pkg.wk.constant.WK;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import retrofit2.Response;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomBizExecute {

    private final RoomService roomService;
    private final RaffleService raffleService;
    private final RedPacketService redPacketService;

    /**
     * 加入直播间
     * @param toUid anchor uid
     * @param ip ip
     * @return {@link JoinLiveVO}
     */
    public ResponsePair<JoinLiveVO> join( int toUid, String ip){
            return roomService.join(toUid,ip);
    }

    /**
     * 加入直播间
     * @param ip ip
     * @param dto {@link GoLiveDTO}
     * @return {@link GoLiveVO}
     */
    public ResponsePair<GoLiveVO> go( String ip, GoLiveDTO dto) {
        return roomService.go(ip,dto);
    }

    /**
     * 结束直播
     * @return {@link DownLiveVO}
     */
    public ResponsePair<DownLiveVO> end() {
        return roomService.end();
    }

    /**
     * 离开直播间
     * @return ok
     */
    public ResponsePair<Void> leave() {
        return roomService.leave();
    }

    /**
     * 直播抽奖活动
     * @param dto {@link RaffleDTO}
     * @return ok
     */
    public ResponsePair<Void> raffle(RaffleDTO dto){
        return raffleService.raffle(dto);
    }

    /**
     * 直播红包活动
     * @param {@link RedPacketDTO}
     * @return ok
     */
    public ResponsePair<Void> redPacket(RedPacketDTO dto) {
        return redPacketService.redPacket(dto);
    }
    /**
     * block接口,这里不打算实现这个逻辑的原因是，如果要手动下线用户
     * 就要维护srs节点，所以现在的情况是在 mq检查连接的时候，判断用户关系
     * todo : 存在直接封掉用户问题，必须要将控制权反转，所以必须要维护这个关系
     * @param toUid 被封禁用户
     * @return ok
     */
    @Deprecated
    public ResponsePair<Void> block(int uid, int toUid) {
        throw new RuntimeException("");
    }
}




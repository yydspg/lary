package cn.lary.module.group.core;

import cn.lary.core.dto.ResPair;
import cn.lary.kit.BizKit;
import cn.lary.module.app.service.EventService;
import cn.lary.module.common.cache.KVBuilder;
import cn.lary.module.common.cache.RedisCache;
import cn.lary.module.danmaku.service.DanmakuService;
import cn.lary.module.group.dto.CreateGroupDTO;
import cn.lary.module.group.vo.CreateGroupVO;
import cn.lary.module.stream.service.FollowService;
import cn.lary.module.stream.service.GiftBuyChannelService;
import cn.lary.module.stream.service.RoomService;
import cn.lary.module.stream.service.StreamRecordService;
import cn.lary.module.user.service.DeviceService;
import cn.lary.module.user.service.UserService;
import cn.lary.pkg.srs.config.SrsConfig;
import cn.lary.pkg.wk.api.WKChannelService;
import cn.lary.pkg.wk.api.WKMessageService;
import cn.lary.pkg.wk.api.WKUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupBizExecute {

    private final DeviceService deviceService;
    private final RoomService roomService;
    private final DanmakuService danmakuService;
    private final SrsConfig srsConfig;
    private final UserService userService;
    private final EventService eventService;
    private final FollowService followService;
    private final GiftBuyChannelService giftBuyChannelService;
    private final StreamRecordService streamRecordService;
    private final KVBuilder kvBuilder;
    private final RedisCache redisCache;
    // external
    private final WKChannelService wkChannelService;
    private final WKMessageService wkMessageService;
    private final WKUserService wkUserService;

    public ResPair<CreateGroupVO> create(String uid, CreateGroupDTO req) {
        return BizKit.ok();
    }
}

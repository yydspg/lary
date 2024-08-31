package cn.lary.module.danmaku.serviceImpl;

import cn.lary.kit.StringKit;
import cn.lary.module.common.config.WkConfig;
import cn.lary.module.danmaku.service.DanmakuService;
import cn.lary.pkg.wk.api.WKChannelService;
import cn.lary.pkg.wk.api.WKMessageService;
import cn.lary.pkg.wk.entity.Request.channel.ChannelCreateReq;
import cn.lary.pkg.wk.entity.core.Channel;
import cn.lary.pkg.wk.entity.core.WK;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DanmakuServiceImpl implements DanmakuService {
    private final WKChannelService wkChannelService;
    private final WKMessageService wkMessageService;
    @Override
    public Channel getDanmakuChannel(String uid) {
        if (StringKit.isEmpty(uid)) {
            return null;
        }
        // build data channel
        ChannelCreateReq channelCreateReq = new ChannelCreateReq();

        wkChannelService.createOrUpdate(channelCreateReq);
        return null;
    }
}

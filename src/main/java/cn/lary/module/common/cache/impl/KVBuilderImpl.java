package cn.lary.module.common.cache.impl;

import cn.hutool.core.lang.hash.Hash;
import cn.lary.kit.JSONKit;
import cn.lary.kit.StringKit;
import cn.lary.kit.UUIDKit;
import cn.lary.module.common.CS.Lary;
import cn.lary.module.common.cache.KVBuilder;
import cn.lary.module.common.server.RedisBizConfig;
import cn.lary.module.stream.dto.*;
import cn.lary.module.user.dto.DeviceAddAckDTO;
import cn.lary.module.user.dto.DeviceLoginDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class KVBuilderImpl implements KVBuilder {

    private final RedisBizConfig redisBizConfig;

    @Override
    public String deviceLoginK(int uid,String deviceId) {
        return redisBizConfig.getTokenCachePrefix() + uid+"@"+deviceId;
    }

    @Override
    public String deviceLoginV(DeviceLoginDTO deviceLoginDTO) {
        return JSONKit.toJSON(deviceLoginDTO);
    }

    @Override
    public String userLoginK(int uid,int deviceFlag) {
        return redisBizConfig.getTokenCachePrefix() + uid+"@"+deviceFlag;
    }

    @Override
    public String userLoginV(String token,int deviceFlag) {
        return token+"@"+deviceFlag;
    }

    @Override
    public String userLoginTokenK(String token) {
        return redisBizConfig.getTokenCachePrefix() + token;
    }

    @Override
    public String userLoginTokenV(int uid, String username, int role) {
        return uid+"@"+username+"@"+role;
    }

    @Override
    public String userRegisterK( String phone) {
        return redisBizConfig.getRegisterPrefix()+phone;
    }

    @Override
    public String userRegisterV(String token) {
        return token;
    }


    @Override
    public String streamRecordK(int uid, int streamId) {
        return redisBizConfig.getStreamRecordPrefix() + uid+":"+streamId;
    }

    @Override
    public Map streamRecordV() {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("watchNum",0);
        map.put("newFansNum",0);
        map.put("starNum",0);
        map.put("watchFanNum",0);
        map.put("giftNum",0);
        return map;
    }

    @Override
    public String addDeviceK(int uid) {
        return redisBizConfig.getSmsAddDevicePrefix()+uid;
    }

    @Override
    public String addDeviceV(DeviceAddAckDTO loginDTO) {
        return JSONKit.toJSON(loginDTO);
    }

    @Override
    public String goLiveK(int uid) {
        return redisBizConfig.getGoLivePrefix() + uid;
    }

    @Override
    public Map goLiveV(LiveCacheDTO dto) {
        Map<Object, Object> args = new HashMap<>();
        args.put("streamId",dto.getStreamId());
        args.put("giftBuyChannelId",dto.getGiftBuyChannelId());
        args.put("wkChannelId",dto.getWkChannelId());
        if (StringKit.isNotEmpty(dto.getIp())) {
            args.put("ip",dto.getIp());
        }
        if (StringKit.isNotEmpty(dto.getStream())) {
            args.put("stream",dto.getStream());
        }
        if (StringKit.isNotEmpty(dto.getSrsClientId())) {
            args.put("srsClientId",dto.getSrsClientId());
        }
        if (StringKit.isNotEmpty(dto.getSrsToken())) {
            args.put("srsToken",dto.getSrsToken());
        }
        if (StringKit.isNotEmpty(dto.getSrsServerId())) {
            args.put("srsServerId",dto.getSrsServerId());
        }
        if (StringKit.isNotEmpty(dto.getSrsClientId())) {
            args.put("srsClientId",dto.getSrsClientId());
        }
        if (StringKit.isNotEmpty(dto.getStream())) {
            args.put("stream",dto.getStream());
        }
        return args;
    }

    @Override
    public String joinLiveK(int uid) {
        return redisBizConfig.getJoinLivePrefix() + uid;
    }

    @Override
    public Map joinLiveV(JoinLiveCacheDTO dto) {
        if (dto == null) {
            return null;
        }
        HashMap<String, String> args = new HashMap<>();
        if (StringKit.isNotEmpty(dto.getIp())) {
            args.put("ip", dto.getIp());
        }
        if (StringKit.isNotEmpty(dto.getStream())) {
            args.put("stream", dto.getStream());
        }
        if (StringKit.isNotEmpty(dto.getSrsToken())) {
            args.put("srsToken", dto.getSrsToken());
        }
        if (StringKit.isNotEmpty(dto.getSrsStreamId())) {
            args.put("srsStreamId", dto.getSrsStreamId());
        }
        if (StringKit.isNotEmpty(dto.getSrsClientId())) {
            args.put("srsClientId", dto.getSrsClientId());
        }
        if (StringKit.isNotEmpty(dto.getSrsServerId())) {
            args.put("srsServerId", dto.getSrsServerId());
        }
        if (StringKit.isNotEmpty(dto.getName())) {
            args.put("name", dto.getName());
        }
        return args;
    }

    @Override
    public String raffleK(int uid) {
        return redisBizConfig.getRafflePrefix()+uid;
    }

    @Override
    public Map raffleV(RaffleCacheDTO dto) {
        if (dto == null) {
            return null;
        }
        HashMap<String, String> args = new HashMap<>();
        if (StringKit.isNotEmpty(dto.getTitle())) {
            args.put("title", dto.getTitle());
        }
        if (StringKit.isNotEmpty(dto.getContent())) {
            args.put("content", dto.getContent());
        }
        if (StringKit.isNotEmpty(dto.getMessage())) {
            args.put("message", dto.getMessage());
        }
        args.put("num",String.valueOf(dto.getNum()));
        args.put("item_num",String.valueOf(dto.getNum()));
        args.put("duration",String.valueOf(dto.getDuration()));
        args.put("type",String.valueOf(dto.getType()));
        return args;
    }

    @Override
    public String redPacketK(int uid) {
        return redisBizConfig.getRedPacketPrefix() + uid;
    }

    @Override
    public Map redPacketV(RedPacketCacheDTO dto) {
        if (dto == null) {
            return null;
        }
        HashMap<String, String> args = new HashMap<>();

        args.put("title", dto.getTitle());
        args.put("num",String.valueOf(dto.getNum()));
        args.put("message",dto.getMessage());
        args.put("cost",String.valueOf(dto.getCost()));
        args.put("startAt",dto.getStartAt());
        return args;
    }

    @Override
    public String raffleListK(int uid) {
        return redisBizConfig.getRaffleListPrefix() +uid;
    }


}

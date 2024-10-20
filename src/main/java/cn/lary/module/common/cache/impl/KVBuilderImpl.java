package cn.lary.module.common.cache.impl;

import cn.lary.kit.StringKit;
import cn.lary.module.common.cache.KVBuilder;
import cn.lary.module.common.server.RedisBizConfig;
import cn.lary.module.stream.dto.*;
import cn.lary.module.user.dto.DeviceAddResponseCacheDTO;
import cn.lary.module.user.dto.DeviceLoginCacheDTO;
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
    public String deviceLoginK(int uid,int flag) {
        return redisBizConfig.getLoginDeviceCachePrefix()+ uid+"@"+flag;
    }

    @Override
    public Map deviceLoginV(DeviceLoginCacheDTO deviceLoginCacheDTO) {
        if (deviceLoginCacheDTO == null) {
            return null;
        }
        Map<String, String> map = new HashMap<>();
        map.put("id", String.valueOf(deviceLoginCacheDTO.getId()));
        map.put("name", deviceLoginCacheDTO.getName());
        map.put("flag", String.valueOf(deviceLoginCacheDTO.getFlag()));
        map.put("level", String.valueOf(deviceLoginCacheDTO.getLevel()));
        return map;
    }

    @Override
    public String userLoginK(int uid,int deviceFlag) {
        return redisBizConfig.getTokenCachePrefix() + uid+"@"+deviceFlag;
    }

    @Override
    public String userLoginV(String token) {
        return token;
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
    public String userDestroyK(int uid) {
        return redisBizConfig.getDestroyPrefix()+uid;
    }

    @Override
    public String userDestroyV(String token) {
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
    public String addDeviceK(int uid,String phone) {
        return redisBizConfig.getSmsAddDevicePrefix()+uid+"@"+phone;
    }

    @Override
    public Map addDeviceV(DeviceAddResponseCacheDTO dto) {
        HashMap<Object, Object> args = new HashMap<>();
        args.put("name",dto.getName());
        args.put("code",dto.getCode());
        args.put("flag",dto.getFlag());
        return args;
    }

    @Override
    public String goLiveK(int uid) {
        return redisBizConfig.getGoLivePrefix() + uid;
    }

    @Override
    public Map goLiveV(LiveCacheDTO dto) {
        Map<Object, Object> args = new HashMap<>();
        args.put("streamId",dto.getStreamId());
        args.put("danmakuId",dto.getDanmakuId());
        args.put("identify",dto.getIdentify());
        args.put("ip",dto.getIp());
        args.put("srsClientId",dto.getSrsClientId());
        args.put("srsToken",dto.getSrsToken());
        args.put("srsServerId",dto.getSrsServerId());
        args.put("srsStreamId", dto.getSrsStreamId());
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

        args.put("identify",dto.getIdentify());
        args.put("name", dto.getName());
        args.put("ip",dto.getIp());
        args.put("srsClientId",dto.getSrsClientId());
        args.put("srsToken",dto.getSrsToken());
        args.put("srsServerId",dto.getSrsServerId());
        args.put("srsStreamId", dto.getSrsStreamId());

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

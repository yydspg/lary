package cn.lary.module.common.cache.impl;

import cn.hutool.core.lang.hash.Hash;
import cn.lary.kit.JSONKit;
import cn.lary.kit.StringKit;
import cn.lary.kit.UUIDKit;
import cn.lary.module.common.CS.Lary;
import cn.lary.module.common.cache.KVBuilder;
import cn.lary.module.common.server.RedisBizConfig;
import cn.lary.module.stream.dto.GoLiveDTO;
import cn.lary.module.stream.dto.JoinLiveCacheDTO;
import cn.lary.module.stream.dto.LiveCacheDTO;
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

    public  String buildRegisterCacheKey(String zone,String phone,int codeType){
        return Lary.RedisPrefix.sms+codeType+"@"+zone+"@"+phone+"@";
    }
    public String buildUserLoginTokenValue(String uid,String username,String role) {
        return uid+"@"+username+"@"+role;
    }
    public  String buildGroupMemberVerCode() {
        return UUIDKit.getUUID()+"@" + Lary.VerifyCode.groupMember;
    }
    public  String buildUserLoginKey(String tokenPrefix,String token) {
        return tokenPrefix+token;
    }
    public  String buildUserLoginKey(String token) {
        return redisBizConfig.getTokenCachePrefix() + token;
    }

    @Override
    public String deviceLoginK(String uid,String deviceId) {
        return redisBizConfig.getTokenCachePrefix() + uid+"@"+deviceId;
    }

    @Override
    public String deviceLoginV(DeviceLoginDTO deviceLoginDTO) {
        return JSONKit.toJSON(deviceLoginDTO);
    }

    @Override
    public String userLoginK(String uid,int deviceFlag) {
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
    public String userLoginTokenV(String uid, String username, int role) {
        return uid+"@"+username+"@"+role;
    }

    @Override
    public String userRegisterK(String uid, String phone) {
        return redisBizConfig.getRegisterPrefix() + uid+"@"+phone;
    }

    @Override
    public String userRegisterV(String token) {
        return token;
    }


    @Override
    public String buildDanmakuChannelTokenKey(String uid) {
        return redisBizConfig.getDanmakuTokenCachePrefix()+uid;
    }

    @Override
    public String buildDanmakuChannelTokenValue(String channelId) {
        return channelId;
    }


    @Override
    public String streamRecordK(String uid, String streamId) {
        return redisBizConfig.getStreamRecordPrefix() + uid+":"+streamId;
    }

    @Override
    public Map streamRecordV() {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("watchNum",0);
        map.put("newFansNum",0);
        map.put("starNum",0);
        map.put("watchFanNum",0);
        return map;
    }

    @Override
    public String addDeviceK(String uid) {
        return redisBizConfig.getSmsAddDevicePrefix()+uid;
    }

    @Override
    public String addDeviceV(DeviceAddAckDTO loginDTO) {
        return JSONKit.toJSON(loginDTO);
    }

    @Override
    public String goLiveK(String uid) {
        return redisBizConfig.getGoLivePrefix() + uid;
    }

    @Override
    public Map goLiveV(LiveCacheDTO dto) {
        Map<String, String> args = new HashMap<>();
        if (StringKit.isNotEmpty(dto.getStreamId())) {
            args.put("streamId",dto.getStreamId());
        }
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
        if (StringKit.isNotEmpty(dto.getStreamId())) {
            args.put("streamId",dto.getStreamId());
        }
        if (StringKit.isNotEmpty(dto.getGiftBuyChannelId())) {
            args.put("giftBuyChannelId",dto.getGiftBuyChannelId());
        }
        if (StringKit.isNotEmpty(dto.getWkChannelId())) {
            args.put("wkChannelId",dto.getWkChannelId());
        }
        return args;
    }

    @Override
    public String joinLiveK(String uid) {
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
        return args;
    }


    public String buildFriendApplyKey(String token,String uid) {
        return redisBizConfig.getFriendApplyTokenCachePrefix() + token + "@" + uid;
    }

    @Override
    public String buildFriendApplyValue(String fromUid, String vercode, String remark) {
        HashMap<String, String> map = new HashMap<>();
        map.put("from_uid",fromUid);
        map.put("vercode", vercode);
        map.put("remark",remark);
        return JSONKit.toJSON(map);
    }
}

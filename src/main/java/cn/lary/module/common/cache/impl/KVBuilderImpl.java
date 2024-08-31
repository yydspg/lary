package cn.lary.module.common.cache.impl;

import cn.lary.kit.JSONKit;
import cn.lary.kit.UUIDKit;
import cn.lary.module.common.CS.Lary;
import cn.lary.module.common.cache.KVBuilder;
import cn.lary.module.common.server.RedisBizConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;

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
    public String buildDeviceLoginTokenKey(String uid) {
        return redisBizConfig.getLoginDeviceCachePrefix() + uid;
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

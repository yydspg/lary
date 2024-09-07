package cn.lary.module.common.cache;

import java.util.Map;

public interface KVBuilder {
    String buildFriendApplyKey(String token,String uid) ;
    String buildFriendApplyValue(String fromUid,String vercode,String remark) ;
    String buildGroupMemberVerCode() ;
    public String buildUserLoginTokenValue(String uid,String username,String role) ;
    public  String buildRegisterCacheKey(String zone,String phone,int codeType);
    public  String buildUserLoginKey(String tokenPrefix,String token) ;
    public  String buildUserLoginKey(String token) ;
    String buildDeviceLoginTokenKey(String uid);
    String buildDanmakuChannelTokenKey(String uid);
    String buildDanmakuChannelTokenValue(String channelId);
    String buildGoLiveKey(String uid);
    String buildStreamRecordKey(String uid,String streamId);
    Map<Object,Object> buildStreamRecordValue();
    Map<String,String> buildGoLiveValue(String streamId,String giftBuyChannelId,String wkChannelId);
}

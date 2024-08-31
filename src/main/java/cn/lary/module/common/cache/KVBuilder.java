package cn.lary.module.common.cache;

public interface KVBuilder {
    String buildFriendApplyKey(String token,String uid) ;
    String buildFriendApplyValue(String fromUid,String vercode,String remark) ;
    String buildGroupMemberVerCode() ;
    public String buildUserLoginTokenValue(String uid,String username,String role) ;
    public  String buildRegisterCacheKey(String zone,String phone,int codeType);
    public  String buildUserLoginKey(String tokenPrefix,String token) ;
    public  String buildUserLoginKey(String token) ;
    String buildDeviceLoginTokenKey(String uid);
}

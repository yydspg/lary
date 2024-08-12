package cn.lary.kit;

import cn.lary.module.common.CS.Lary;
import cn.lary.module.common.server.RedisBizConfig;
import lombok.RequiredArgsConstructor;


public class BizKit {

    public static String buildRegisterCacheKey(String zone,String phone,int codeType){
        return Lary.RedisPrefix.sms+codeType+"@"+zone+"@"+phone+"@";
    }
    public static String buildUserToken(String uid,String username,String role) {
        return uid+"@"+username+"@"+role;
    }
    public static String buildGroupMemberVerCode() {
        return UUIDKit.getUUID()+"@" + Lary.VerifyCode.groupMember;
    }

}

package cn.lary.module.user.component;

import cn.lary.common.dto.ResponsePair;
import cn.lary.common.kit.BusinessKit;
import cn.lary.common.kit.StringKit;
import cn.lary.common.kit.UUIDKit;
import cn.lary.module.cache.component.SMSCacheComponent;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.dromara.sms4j.core.factory.SmsFactory;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserCacheComponent {

    private final  String LARY_USER = "lary:user:";
    private final  String LARY_USER_LOGIN = "lary:user:login:";
    private final  String LARY_USER_REGISTER = "lary:user:register:";
    private final  String LARY_USER_DESTROY = "lary:user:register:";


    private final Cache<Long, UserCache> userCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build();


    private final SMSCacheComponent smsCacheComponent;
    private final RedissonClient redisson;

    public void setData(long uid,int flag, UserCache cache) {
        redisson.<UserCache>getBucket(LARY_USER + uid+":"+flag).set(cache);
    }
    public void dropData(long uid,int flag) {
        redisson.<UserCache>getBucket(LARY_USER + uid+":"+flag).delete();
    }

    public UserCache getData(long uid,int flag) {
        return redisson.<UserCache>getBucket(LARY_USER + uid+":"+flag).get();
    }

    public final void loginSMS(String phone,String name,int flag) {
        buildSMS(LARY_USER_LOGIN,phone,name,flag);
    }
    public final void registerSMS(String phone,String name,int flag) {
       buildSMS(LARY_USER_REGISTER,phone,name,flag);
    }
    public final void destroySMS(String phone,String name,int flag) {
        buildSMS(LARY_USER_DESTROY,phone,name,flag);
    }
    public final  ResponsePair<Void> verifyRegisterSMS(String phone,String code,String name, int flag) {
       return verify(LARY_USER_REGISTER, phone, code, name, flag);
    }

    public final  ResponsePair<Void> verifyLoginSMS(String phone,String code,String name, int flag) {
        return verify(LARY_USER_LOGIN, phone, code, name, flag);
    }
    public final ResponsePair<Void> verifyDestroySMS(String phone,String code,String name, int flag) {
        return verify(LARY_USER_DESTROY,phone,code,name, flag);
    }
    private void buildSMS(String prefix,String phone,String name,int flag){
        String code = UUIDKit.uuidToShort(UUIDKit.getUUID());
        SmsFactory.getSmsBlend("aliyun")
                .sendMessage(phone,code);
        String token = encodeToken(code,name,flag);
        smsCacheComponent.store(prefix,phone,token, Duration.ofMinutes(5L));
    }
    private  ResponsePair<Void> verify(String prefix,String phone,String code,String name, int flag){
        String token = smsCacheComponent.get(prefix, phone);
        if (StringKit.isEmpty(token)) {
            return BusinessKit.fail("code expire");
        }
        String[] data = decodeToken(token);
        if (StringKit.diff(code,data[0])) {
            return BusinessKit.fail("code error");
        }
        if (StringKit.diff(name,data[1]) || flag != Integer.parseInt(data[2])) {
            log.error("code user on other device,phone{},code{},name{},flag{}",phone,code,name,flag);
            return BusinessKit.fail("u attempt to do dangerous ops");
        }
        redisson.<String>getBucket(prefix+phone).delete();
        return BusinessKit.ok();
    }

    private String encodeToken(String code,String name,int flag) {
        return code +"$&"+ name +"$&" +flag;
    }
    private String[] decodeToken(String token) {
        return StringKit.split(token, "$&");
    }
    /**
     * not used
     */
    private CacheLoader<Long,UserCache> loader() {
        return new CacheLoader<Long, UserCache>() {

            @Override
            public @Nullable UserCache load(Long key) throws Exception {
                return redisson.<UserCache>getBucket(LARY_USER + key).get();
            }
        };
    }
}

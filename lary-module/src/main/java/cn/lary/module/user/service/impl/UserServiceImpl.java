package cn.lary.module.user.service.impl;

import cn.lary.common.context.RequestContext;
import cn.lary.common.dto.ResponsePair;
import cn.lary.common.kit.*;
import cn.lary.external.wk.dto.user.UpdateTokenDTO;
import cn.lary.external.wk.vo.route.RouteVO;
import cn.lary.module.app.service.EventService;
import cn.lary.module.common.cache.KVBuilder;
import cn.lary.module.common.cache.RedisCache;
import cn.lary.module.common.constant.LARY;
import cn.lary.module.common.server.RedisBizConfig;
import cn.lary.module.event.dto.UserRegisterEventDTO;
import cn.lary.module.message.service.MessageService;
import cn.lary.module.user.dto.*;
import cn.lary.module.user.entity.Device;
import cn.lary.module.user.entity.User;
import cn.lary.module.user.entity.UserSetting;
import cn.lary.module.user.mapper.UserMapper;
import cn.lary.module.user.service.DeviceService;
import cn.lary.module.user.service.UserService;
import cn.lary.module.user.service.UserSettingService;
import cn.lary.module.user.vo.UserVO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.houbb.sensitive.word.core.SensitiveWordHelper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.sms4j.core.factory.SmsFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author paul
 * @since 2024-07-29
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final RedisCache redisCache;
    private final DeviceService deviceService;
    private final KVBuilder kvBuilder;
    private final RedisBizConfig redisBizConfig;
    private final MessageService messageService;
    private final EventService eventService;
    private final UserSettingService userSettingService;
//    private final WalletService walletService;
    private final TransactionTemplate transactionTemplate;

    @Override
    public ResponsePair<String> login(LoginDTO dto) {
        String password = StringKit.MD5(StringKit.MD5(dto.getPassword()));
        User user = lambdaQuery()
                .eq(User::getUid,dto.getUid())
                .eq(User::getIsDelete,false)
                .one();
        if (user == null) {
            return BusinessKit.fail("user not exist,please register first");
        }
        int uid = user.getUid();

        if(user.getStatus() == LARY.USER.STATUS.BAN) {
            return BusinessKit.fail("user was banned");
        }
        if (StringKit.diff(user.getPassword(), password)) {
            return BusinessKit.fail("password error");
        }
        int deviceLevel = LARY.DEVICE.LEVEL.SLAVE;
        if (LARY.DEVICE.FLAG.APP == dto.getFlag()) {
            deviceLevel = LARY.DEVICE.LEVEL.MASTER;
        }
        boolean needRemovePreviousLogged = false;
        String oldToken = redisCache.get(kvBuilder.userLoginK(user.getUid(), dto.getFlag()));
        if (StringKit.isNotEmpty(oldToken)) {
           // Devices with the same flag will be logged in and
            // the previously logged in devices will be removed
            Map<Object, Object> map = redisCache.getHash(kvBuilder.deviceLoginK(uid, dto.getFlag()));
            if (map == null) {
                log.error("user login failed,uid:{},flag:{}", uid, dto.getFlag());
                return BusinessKit.fail("system error");
            }
            DeviceLoginCacheDTO cache = DeviceLoginCacheDTO.of(map);
           if (cache.getId() == dto.getId()
                || StringKit.same(cache.getName(),dto.getName())
                || cache.getFlag() == dto.getFlag()) {
               return BusinessKit.fail("login repeated");
           }
            needRemovePreviousLogged = true;
        }
        Device device = deviceService.getDevice(dto.getUid(), dto.getId(), dto.getName(), dto.getFlag());
        if (device == null) {
            if (StringKit.isEmpty(dto.getCode()) && deviceLevel == LARY.DEVICE.LEVEL.SLAVE) {
                return BusinessKit.fail("cmd:[login on new device],phone:"+user.getPhone());
            }else {
                if (dto.getCode() == null) {
                    return BusinessKit.fail("cmd:get sms code when login on new device");
                }
                // check code
                Map<Object, Object> map = redisCache.getHash(kvBuilder.addDeviceK(uid, dto.getPhone()));
                if (map == null) {
                    log.error("login on new device failed,no verify code found,uid:{}",uid);
                    return BusinessKit.fail("verify code expire");
                }
                DeviceAddResponseCacheDTO cache = DeviceAddResponseCacheDTO.of(map);
                if(StringKit.diff(dto.getCode(),cache.getCode())
                    || StringKit.diff(dto.getName(),cache.getName())
                    || dto.getFlag() != cache.getFlag()){
                    log.error("verify code not match,uid:{}",uid);
                    return BusinessKit.fail("verify code not match");
                }
                device = new Device()
                        .setUid(uid)
                        .setFlag(dto.getFlag())
                        .setName(dto.getName())
                        .setLevel(deviceLevel);
                deviceService.save(device);
                redisCache.delete(kvBuilder.addDeviceK(uid,dto.getPhone()));
            }
        }
        DeviceLoginCacheDTO deviceLoginCacheDTO = new DeviceLoginCacheDTO()
                .setFlag(dto.getFlag())
                .setId(device.getId())
                .setLevel(device.getLevel())
                .setName(device.getName());
        if (needRemovePreviousLogged) {
            forceLogout(uid, dto.getFlag(),oldToken);
        }
        deviceService.buildDeviceLoginCache(uid,device.getFlag(),deviceLoginCacheDTO);
        String token = buildUserLoginCache(user.getUid(),user.getName(), user.getRole(), device.getFlag());
        messageService.updateToken(new UserLoginUpdateTokenDTO()
                .setUid(uid)
                .setToken(token)
                .setFlag(device.getFlag())
                .setLevel(device.getLevel()));
        deviceService.lambdaUpdate()
                .set(Device::getLastLogin, LocalDateTime.now())
                .eq(Device::getUid, uid)
                .eq(Device::getId,device.getId());
        return BusinessKit.ok(token);
    }

    @Override
    public ResponsePair<String> register(RegisterDTO dto) {
        String verifyCode = redisCache.get(kvBuilder.userRegisterK(dto.getPhone()));
        if(StringKit.diff(verifyCode,dto.getCode())) {
            return BusinessKit.fail("verify code error");
        }
        redisCache.delete(kvBuilder.userRegisterK(dto.getPhone()));
        User user = new User();
        String name = dto.getName();
        if (StringKit.isNotEmpty(name)) {
            String badWord = SensitiveWordHelper.findFirst(name);
            if (StringKit.isNotEmpty(badWord)) {
                return BusinessKit.fail("username contains sensitive word");
            }
            user.setName(name);
        }else {
            user.setName(StringKit.random(6));
        }
        int flag = dto.getFlag();
        int deviceLevel = LARY.DEVICE.LEVEL.SLAVE;
        if (LARY.DEVICE.FLAG.APP == flag) {
            deviceLevel = LARY.DEVICE.LEVEL.MASTER;
        }
        int finalDeviceLevel = deviceLevel;
        User savedUser = transactionTemplate.execute(status -> {
            user.setQrVercode(UUIDKit.getUUID() + "@" + LARY.VerifyCode.QR)
                    .setVercode(UUIDKit.getUUID() + "@" + LARY.VerifyCode.user)
                    .setEmail(dto.getEmail())
                    .setPhone(dto.getPhone())
                    .setZone(dto.getZone())
                    .setIsRobot(false)
                    .setSex(LARY.Sex.man)
                    .setBio(dto.getBio())
                    .setBirthday(dto.getBirthday())
                    .setPassword(StringKit.MD5(StringKit.MD5(dto.getPassword())));
            save(user);
            int uid = user.getUid();
            int eventId = eventService.begin(new UserRegisterEventDTO()
                    .setUid(uid)
                    .setPhone(dto.getPhone())
                    .of());
            userSettingService.save(new UserSetting().setUid(uid));
//            walletService.save(new Wallet().setUid(uid));
//            followService.addSystemHelper(uid);
            eventService.commit(eventId);
            return user;
        });
        Device device = new Device()
                .setUid(user.getUid())
                .setName(dto.getDeviceName())
                .setFlag(flag)
                .setLevel(finalDeviceLevel);
        deviceService.save(device);
        DeviceLoginCacheDTO deviceLoginCacheDTO = new DeviceLoginCacheDTO()
                .setId(device.getId())
                .setName(device.getName())
                .setLevel(finalDeviceLevel)
                .setFlag(flag);
        assert savedUser != null;
        deviceService.buildDeviceLoginCache(savedUser.getUid(),flag, deviceLoginCacheDTO);
        String token = buildUserLoginCache(savedUser.getUid(), name, user.getRole(),flag);
        UpdateTokenDTO tokenDTO = new UpdateTokenDTO()
                .setUid(user.getUid())
                .setToken(token)
                .setFlag(flag)
                .setLevel(deviceLevel);
        messageService.updateToken(tokenDTO);
        return BusinessKit.ok(token);
    }

    @Override
    public ResponsePair<Void> logout(HttpServletRequest request) {
        TokenPair pair = of(request);
        if (pair == null) {
            return BusinessKit.fail("logout fail");
        }
        forceLogout(pair.uid,pair.flag,pair.token);
        return BusinessKit.ok();
    }

    @Override
    public ResponsePair<RouteVO> getRoute() {
       return BusinessKit.ok(messageService.getRoute(RequestContext.getLoginUID()));
    }

    @Override
    public ResponsePair<Void> registerCode(String phone) {
        String token = SmsCodeKit.getToken();
        if (LARY.testMode) {
            SmsFactory.getSmsBlend("aliyun-test").sendMessageAsync(phone,token);
        }
        redisCache.set(kvBuilder.userRegisterK(phone),kvBuilder.userRegisterV(token),redisBizConfig.getRegisterExpire());
        return BusinessKit.ok();
    }

    @Override
    public ResponsePair<Void> destroyCode(String phone) {
        String token = SmsCodeKit.getToken();
        if (LARY.testMode) {
            SmsFactory.getSmsBlend("aliyun-test").sendMessageAsync(phone,token);
        }
        redisCache.set(kvBuilder.userRegisterK(phone),kvBuilder.userRegisterV(token),redisBizConfig.getRegisterExpire());
        return BusinessKit.ok();
    }

    @Override
    public ResponsePair<Void> refresh(HttpServletRequest request) {
        TokenPair pair = of(request);
        if (pair == null) {
            return BusinessKit.fail("refresh fail");
        }
        redisCache.renewal(kvBuilder.userLoginTokenK(pair.token),redisBizConfig.getLoginUserTokenExpire());
        redisCache.renewal(kvBuilder.userLoginK(pair.uid,pair.flag),redisBizConfig.getLoginUserExpire());
        deviceService.renewalDeviceLoginCache(pair.uid,pair.flag);
        return BusinessKit.ok();
    }

    @Override
    public ResponsePair<Void> destroy(UserDestroyDTO dto) {
        int uid = RequestContext.getLoginUID();
        String verifyCode = redisCache.get(kvBuilder.userDestroyK(uid));
        if(StringKit.diff(verifyCode,dto.getCode())){
            return BusinessKit.fail("check verify code error");
        }
        String appToken = redisCache.get(kvBuilder.userLoginK(uid, LARY.DEVICE.FLAG.APP));
        String pcToken = redisCache.get(kvBuilder.userLoginK(uid, LARY.DEVICE.FLAG.PC));
        if (StringKit.isNotEmpty(appToken)){
            forceLogout(uid,LARY.DEVICE.FLAG.APP,appToken);
        }
        if (StringKit.isNotEmpty(pcToken)){
            forceLogout(uid,LARY.DEVICE.FLAG.PC,pcToken);
        }
        transactionTemplate.executeWithoutResult(status -> {
//            walletService.lambdaUpdate()
//                    .set(Wallet::getIsBlock,true)
//                    .set(Wallet::getIsDelete,true)
//                    .eq(Wallet::getUid,uid);
            userSettingService.lambdaUpdate()
                    .set(UserSetting::getIsDelete,true)
                    .eq(UserSetting::getUid,uid);
            lambdaUpdate()
                    .set(User::getIsDelete,true)
                    .eq(User::getUid,uid);
        });
        return BusinessKit.ok();
    }


    @Override
    public ResponsePair<UserVO> update(UserUpdateDTO dto) {
        return null;
    }

    @Override
    public ResponsePair<UserVO> my() {
        User user = lambdaQuery()
                .eq(User::getUid, RequestContext.getLoginUID())
                .one();
        if (user == null) {
            log.error("search user error,uid:{}", RequestContext.getLoginUID());
            return BusinessKit.fail("query fail");
        }
        return BusinessKit.ok(new UserVO(user));
    }

    @Override
    public List<Integer> getValidUsers(List<Integer> members) {
        List<User> data = lambdaQuery()
                .select(User::getUid)
                .eq(User::getStatus, LARY.USER.STATUS.OK)
                .eq(User::getIsDelete, false)
                .in(User::getUid, members)
                .list();
        if (CollectionKit.isEmpty(data)) {
            return null;
        }
        return  data.stream()
                .map(User::getUid)
                .toList();
    }

    private void forceLogout(int uid,int flag,String token) {
        redisCache.delete(kvBuilder.userLoginK(uid,flag));
        redisCache.delete(kvBuilder.userLoginTokenK(token));
        deviceService.removeDeviceLoginCache(uid,flag);
    }
    private String buildUserLoginCache(int uid,String name,int flag,int role) {
        String token = UUIDKit.getUUID() + "@" + flag;
        redisCache.set(kvBuilder.userLoginK(uid,flag),kvBuilder.userLoginV(token),redisBizConfig.getLoginUserExpire());
        redisCache.set(kvBuilder.userLoginTokenK(token),kvBuilder.userLoginTokenV(uid,name,role),redisBizConfig.getLoginUserTokenExpire());
        return token;
    }

    /**
     * 获取token pair
     * @param request {@link HttpServletRequest}
     * @return {@link TokenPair}
     */
    private  TokenPair of(HttpServletRequest request) {
        String token = String.valueOf(request.getHeader("token"));
        if (token == null) {
            return null;
        }
        String[] tmp = token.split("@");
        if (tmp.length != 2) {
            return null;
        }
        String userInfo = redisCache.get(kvBuilder.userLoginTokenK(token));
        if(StringKit.isEmpty(userInfo)) {
            return null;
        }
        String[] args = userInfo.split("@");
        if (args.length <= 2) {
            return null;
        }
        return new TokenPair(Integer.parseInt(args[0]),token,Integer.parseInt(tmp[1]));
    }
}

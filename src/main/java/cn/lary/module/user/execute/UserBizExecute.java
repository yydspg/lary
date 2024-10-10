package cn.lary.module.user.execute;

import cn.lary.core.context.ReqContext;
import cn.lary.core.dto.MultiResponse;
import cn.lary.core.dto.ResPair;
import cn.lary.kit.*;
import cn.lary.module.app.entity.EventData;
import cn.lary.module.app.service.EventService;
import cn.lary.module.common.constant.Lary;
import cn.lary.module.common.cache.KVBuilder;
import cn.lary.module.common.cache.RedisCache;
import cn.lary.module.common.server.RedisBizConfig;
import cn.lary.module.common.server.RegisterConfig;
import cn.lary.module.event.dto.UserRegisterEventDTO;
import cn.lary.module.stream.service.FollowService;
import cn.lary.module.user.dto.*;
import cn.lary.module.user.entity.Device;
import cn.lary.module.user.entity.User;
import cn.lary.module.user.entity.UserRedDot;
import cn.lary.module.user.service.DeviceService;
import cn.lary.module.user.service.UserRedDotService;
import cn.lary.module.user.service.UserService;
import cn.lary.module.user.vo.UserRedDotVO;
import cn.lary.module.wallet.entity.Wallet;
import cn.lary.module.wallet.service.WalletService;
import cn.lary.pkg.wk.api.WKUserService;
import cn.lary.pkg.wk.api.WkRouteService;
import cn.lary.pkg.wk.dto.user.UpdateTokenDTO;
import cn.lary.pkg.wk.entity.core.WK;
import cn.lary.pkg.wk.vo.route.RouteVO;
import cn.lary.pkg.wk.vo.user.UpdateTokenVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.sms4j.core.factory.SmsFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import retrofit2.Response;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserBizExecute {

    private final UserService userService;
    private final DeviceService deviceService;
    private final EventService eventService;
    private final RedisCache redisCache;
    private final RedisBizConfig redisBizConfig;
    private final KVBuilder kvBuilder;
    private final WalletService walletService;
    private final FollowService followService;
    private final UserRedDotService userRedDotService;

    // external
    private final WKUserService wkUserService;
    private final WkRouteService wkRouteService;

    /**
     * 登陆接口<br>
     * 如果从设备登陆,且为新设备,需要请求{@link DeviceBizExecute} <br>
     * 获取code重新请求
     * @param req {@link LoginDTO}
     * @return login token
     */
    @Transactional(rollbackFor = Exception.class)
    public ResPair<String> loginByUid(LoginDTO req) {
        String password = StringKit.MD5(StringKit.MD5(req.getPassword()));
        Integer deviceId = req.getId();
        User user = userService.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getDeleted,false)
                .eq(User::getUid,req.getUid()));
        if (user == null) {
            return BizKit.fail("user not exist,please register first");
        }
        int uid = user.getUid();

        if(user.getStatus() == Lary.UserStatus.ban) {
            return BizKit.fail("user was banned");
        }
        if (StringKit.diff(user.getPassword(), password)) {
            return BizKit.fail("pwd error");
        }
        byte deviceLevel = WK.DeviceLevel.slave;
        if (WK.DeviceFlag.app == req.getFlag()) {
            deviceLevel = WK.DeviceLevel.master;
        }
        String oldToken = redisCache.get(kvBuilder.userLoginK(user.getUid(), req.getFlag()));
        if (StringKit.isNotEmpty(oldToken)) {
            return BizKit.fail("login repeated");
        }

        Device checkedDevice = deviceService.checkWhetherNewDevice(uid, deviceId, req.getName(), req.getModel());
        if (checkedDevice == null) {
            if (StringKit.isEmpty(req.getCode()) && deviceLevel == WK.DeviceLevel.slave) {
                return BizKit.fail("cmd:login on new device,send sms code");
            }else {
                if (req.getCode() == null) {
                    return BizKit.fail("cmd:get sms code when login on new device");
                }
                Map<Object, Object> map = redisCache.getHash(kvBuilder.addDeviceK(uid));
                if (map == null) {
                    log.error("login on new device failed,no verify code,uid:{}",uid);
                    return BizKit.fail("verify code expire");
                }
                DeviceAddAckCacheDTO dto = DeviceAddAckCacheDTO.of(map);
                if(StringKit.diff(dto.getCode(),req.getCode())) {
                    log.error("verify code not match,uid:{}",uid);
                    return BizKit.fail("verify code not match");
                }
                Device device = new Device()
                        .setUid(user.getUid())
                        .setName(req.getName())
                        .setModel(req.getModel());
                deviceService.save(device);
                deviceId = device.getId();
                redisCache.del(kvBuilder.addDeviceK(uid));
            }
        }else {
            deviceId = checkedDevice.getId();
        }
        if (deviceId == null || deviceId <= 0) {
            log.error("device id build error,uid:{}",uid);
            return BizKit.fail("system error");
        }
        Device device = deviceService.queryDevice(uid,deviceId);
        if (device == null) {
            return BizKit.fail("device not exist,please register first");
        }
        DeviceLoginDTO deviceLoginDTO = new DeviceLoginDTO()
                .setFlag(req.getFlag())
                .setId(deviceId)
                .setModel(device.getModel())
                .setName(device.getName());
        redisCache.set(kvBuilder.deviceLoginK(uid,deviceId),kvBuilder.deviceLoginV(deviceLoginDTO),redisBizConfig.getLoginDeviceCacheExpire());
        String token = userLoginRedisSet(user.getName(), user.getRole(), req.getFlag());
        UpdateTokenDTO tokenDTO = new UpdateTokenDTO(user.getUid(), token,req.getFlag(), deviceLevel);
        Response<UpdateTokenVO> vo = wkUserService.updateToken(tokenDTO);
        if (!vo.isSuccessful()) {
            return BizKit.fail(vo.message());
        }
        deviceService.lambdaUpdate()
                .set(Device::getLastLogin, LocalDateTime.now())
                .eq(Device::getUid, uid)
                .eq(Device::getId,device.getId());
        return BizKit.ok(token);
    }

    /**
     * 注册接口<br>
     * 通过验证码校验后,直接执行登陆的逻辑
     * @param req {@link RegisterDTO}
     * @return token
     */
    @Transactional()
    public ResPair<String> register(RegisterDTO req) {
        String verifyCode = redisCache.get(kvBuilder.userRegisterK(req.getPhone()));
        if(StringKit.diff(verifyCode,req.getCode())) {
            return BizKit.fail("verify code error");
        }
        redisCache.del(kvBuilder.userRegisterK(req.getPhone()));
        User user = new User();
        String name = req.getName();
        if (StringKit.isNotEmpty(name)) {
            user.setName(name);
        }else {
            user.setName(StringKit.random(6));
        }
        user.setQrVercode(UUIDKit.getUUID()+"@"+Lary.VerifyCode.QR)
            .setVercode(UUIDKit.getUUID()+"@"+Lary.VerifyCode.user)
            .setEmail(req.getEmail())
            .setPhone(req.getPhone())
            .setZone(req.getZone())
            .setIsRobot(false)
            .setSex(Lary.Sex.man)
            .setBio(req.getBio())
            .setBirthday(req.getBirthday())
            .setPassword(StringKit.MD5(StringKit.MD5(req.getPassword())));
        userService.save(user);
        EventData event = new UserRegisterEventDTO().setUid(user.getUid()).setPhone(req.getPhone()).of();
        int eventId = eventService.begin(event);
        Device device = new Device()
                .setUid(user.getUid())
                .setName(req.getDevice().getName())
                .setModel(req.getDevice().getModel());
        deviceService.save(device);
        DeviceLoginDTO deviceLoginDTO = new DeviceLoginDTO()
                .setId(device.getId())
                .setName(device.getName())
                .setModel(device.getModel())
                .setFlag(req.getDevice().getFlag());
        redisCache.set(kvBuilder.deviceLoginK(user.getUid(),device.getId()),kvBuilder.deviceLoginV(deviceLoginDTO),redisBizConfig.getLoginDeviceCacheExpire());
        //set user login info to redis
        String token = userLoginRedisSet(name, Lary.UserRole.normal, req.getDevice().getFlag());

        UpdateTokenDTO tokenDTO = new UpdateTokenDTO(user.getUid(), token,req.getDevice().getFlag(), WK.DeviceLevel.slave);
        Wallet wallet = new Wallet().setUid(user.getUid());
        walletService.save(wallet);
        followService.addSystemHelper(user.getUid());
        Response<UpdateTokenVO> vo = wkUserService.updateToken(tokenDTO);
        if (!vo.isSuccessful()) {
            return BizKit.fail(vo.message());
        }
        eventService.commit(eventId);
        return BizKit.ok(token);
    }
    /**
     * 对 用户登陆的 token在用户维度和 token维度设置在redis
     * @param name username
     * @param role r
     * @param deviceFlag {@link WK.DeviceFlag}
     */
    private String userLoginRedisSet(String name,int role,int deviceFlag){
        int uid = ReqContext.getLoginUID();
        String token = UUIDKit.getUUID() + "@" + deviceFlag;
        // remove before device login token
        redisCache.del(kvBuilder.userLoginK(uid,deviceFlag));
        redisCache.del(kvBuilder.userLoginTokenK(token));
        // add new token
        redisCache.set(kvBuilder.userLoginK(uid,deviceFlag),kvBuilder.userLoginV(token,deviceFlag),redisBizConfig.getLoginUserExpire());
        redisCache.set(kvBuilder.userLoginTokenK(token),kvBuilder.userLoginTokenV(uid,name,role),redisBizConfig.getLoginUserTokenExpire());
        return token;
    }

    /**
     * 用户注册时的手机验证
     * @param phone 手机号
     * @return ok
     */
    public ResPair<Void> smsCode(String phone) {

        String token = SmsCodeKit.getToken();
        if (Lary.testMode) {
            SmsFactory.getSmsBlend("aliyun-test").sendMessageAsync(phone,token);
        }
        redisCache.set(kvBuilder.userRegisterK(phone),kvBuilder.userRegisterV(token),redisBizConfig.getRegisterExpire());
        return BizKit.ok();
    }

    /**
     * 刷新login的token<br>
     * 客户端的token刷新必须大于过期时间
     * @param uid user id
     * @return token
     */
    public ResPair<Void> refreshToken(String token, RefreshTokenDTO req) {
        int uid = ReqContext.getLoginUID();
        redisCache.renewal(kvBuilder.userLoginK(uid,req.getFlag()),redisBizConfig.getLoginUserExpire());
        redisCache.renewal(kvBuilder.userLoginTokenK(token),redisBizConfig.getLoginUserTokenExpire());
        redisCache.renewal(kvBuilder.deviceLoginK(uid,req.getDeviceId()),redisBizConfig.getLoginDeviceCacheExpire());
        return BizKit.ok();
    }

    /**
     * 退出登陆<br>
     * 清理 redis 缓存
     * @return ok
     */
    public ResPair<Void> logout(int deviceFlag,int deviceId,String token) {
        int uid = ReqContext.getLoginUID();
        redisCache.del(kvBuilder.userLoginK(uid,deviceFlag));
        redisCache.del(kvBuilder.deviceLoginK(uid,deviceId));
        redisCache.del(kvBuilder.userLoginTokenK(token));
        return BizKit.ok();
    }

    /**
     * 获取用户 IM 连接地址<br>
     * 此版本为固定值,后续根据底层IM服务优化
     * @return {@link RouteVO}
     */
    public ResPair<RouteVO> getRoute() {
        Response<RouteVO> route = wkRouteService.getRoute();
        if (route.isSuccessful()) {
            RouteVO routeVO = route.body();
            return BizKit.ok(routeVO);
        }
        return BizKit.fail(route.message());
    }

    /**
     * 获取用户红点<br>
     * 此表用户不同的类型就存在一条记录<br>
     * 所以后续增加策略要全表加
     * @return {@link UserRedDotVO}
     */
    public ResPair<List<UserRedDotVO>> getRedDot() {
        int uid = ReqContext.getLoginUID();
        List<UserRedDot> redDots = userRedDotService.list(new LambdaQueryWrapper<UserRedDot>()
                .eq(UserRedDot::getUid, uid));
        List<UserRedDotVO> vos = new ArrayList<>();
        redDots.forEach(t->{
            vos.add(new UserRedDotVO(t.getCount(),t.getCategory(),t.getIsDot()));
        });
        return BizKit.ok(vos);
    }

}


package cn.lary.module.user.api;

import cn.lary.core.dto.SingleResponse;
import cn.lary.kit.*;
import cn.lary.module.app.entity.AppConfigRes;
import cn.lary.module.app.entity.EventData;
import cn.lary.module.app.service.AppConfigService;
import cn.lary.module.app.service.EventService;
import cn.lary.module.app.service.ShortNoService;
import cn.lary.module.common.CS.Lary;
import cn.lary.module.common.cache.KVBuilder;
import cn.lary.module.common.cache.RedisCache;
import cn.lary.module.common.server.LaryServer;
import cn.lary.module.common.server.RedisBizConfig;
import cn.lary.module.common.server.RegisterConfig;
import cn.lary.module.common.server.ShortNoConfig;
import cn.lary.module.common.service.SmsService;
import cn.lary.module.user.dto.req.DeviceReq;
import cn.lary.module.user.dto.req.LoginReq;
import cn.lary.module.user.dto.req.RegisterReq;
import cn.lary.module.user.dto.res.LoginUserDetailRes;
import cn.lary.module.user.entity.Device;
import cn.lary.module.user.entity.User;
import cn.lary.module.user.service.DeviceService;
import cn.lary.module.user.service.FriendService;
import cn.lary.module.user.service.UserService;
import cn.lary.pkg.wk.api.WKUserService;
import cn.lary.pkg.wk.entity.Request.user.UpdateTokenReq;
import cn.lary.pkg.wk.entity.Response.user.UpdateTokenRes;
import cn.lary.pkg.wk.entity.core.WK;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import retrofit2.Response;

import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final RegisterConfig registerConfig;
    private final AppConfigService appConfigService;
    private final UserService userService;
    private final SmsService smsService;
    private final ShortNoService shortNoService;
    private final ShortNoConfig shortNoConfig;
    private final DeviceService deviceService;
    private final FriendService friendService;
    private final EventService eventService;
    private final RedisCache redisCache;
    private final RedisBizConfig redisBizConfig;
    private final WKUserService wkUserService;
    private final KVBuilder kvBuilder;

    @GetMapping
    public SingleResponse get(@RequestParam(value = "uid", required = true) @NotBlank String uid) {

        return null;
    }
    @PostMapping("/login")
    public SingleResponse login(@Valid @RequestBody LoginReq req) {
        User user = userService.queryByUsername(req.getUsername());
        if (user == null || user.getDeleted()) {
            return ResKit.fail("user not exist");
        }
        if(StringKit.isEmpty(user.getPassword())){
            return ResKit.fail("user can not login");
        }
        String reqPWD = StringKit.MD5(StringKit.MD5(req.getPassword()));
        // ???
        if(StringKit.diff(reqPWD, user.getPassword())){
            return ResKit.fail("pwd error");
        }
        return checkUserLoginInfo(user,req);
    }
    @PostMapping("/register")
    public SingleResponse register(@Validated @RequestBody RegisterReq req) {
        if(registerConfig.isOffe()) {
            return ResKit.fail("register channel is off");
        }
        // query app config

        AppConfigRes appConfig = appConfigService.getAppConfig();
        if (appConfig == null) {
            return ResKit.fail("query app config error");
        }
        // 是否开启业务入口模式
        if (appConfig.isRegisterInviteOn()) {
            if(StringKit.isEmpty(req.getInviteCode())) {
                return ResKit.fail("invite code is null");
            }
            // hashMap
            if (!LaryServer.checkBusinessCode(req.getCode())) {
                return ResKit.fail("invite code not exists");
            }
        }
        // 存在链路追踪
        if (userService.queryByUsername(req.getName()) != null) {
            return ResKit.fail("user already exist");
        }
        // 短信验证
        // test module
        if (false &&!smsService.verify(req.getCode(),req.getZone(), req.getPhone(), Lary.CodeType.Register)) {
            return ResKit.fail("验证码错误");
        }
        // build user id --> uid
        String uid = UUIDKit.getUUID();

        return createUser(uid,req,false);
    }
    private SingleResponse checkUserLoginInfo(User user, LoginReq req) {
        SingleResponse res = doLogin(user, req.getFlag(), req.getDevice());
        if (res.isSuccess()) {
            return res;
        }
        // TODO  :  这里处理的有点简单，实际上应该好好做做
        // build error ,need verify code
        return ResKit.fail("need verify phone");
    }
    private SingleResponse doLogin(User user, byte deviceFlag, DeviceReq deviceReq) {
        if(user.getStatus() == Lary.UserStatus.ban) {
            return ResKit.fail("use baned");
        }
        byte deviceLevel = WK.DeviceLevel.slave;
        if (WK.DeviceFlag.app == deviceFlag) {
            deviceLevel = WK.DeviceLevel.master;
        }
        // app login check
        if (deviceFlag == WK.DeviceFlag.app) {
            if (deviceReq == null) {
                return ResKit.fail("login service info is null");
            }
            Device device = deviceService.queryDevice(deviceReq.getDeviceId(), user.getUid());
            if (device == null) {
                // device not exists
                // try to store device in redis
                String K = kvBuilder.buildDeviceLoginTokenKey(user.getUid());
                String V = JSONKit.toJSON(device);
                // set to redis
                redisCache.set(K,V,redisBizConfig.getLoginDeviceCacheExpire());

            }else {
                // update device
                deviceService.updateDeviceLogin(device);
            }

        }

        String token = null;
        String K = redisBizConfig.getUidTokenCachePrefix()+"@"+deviceFlag+"@"+user.getUid();
        String oldToken = redisCache.get(K);
        if (deviceFlag == WK.DeviceFlag.app) {
            if(StringKit.isNotEmpty(oldToken)) {
                // remove old token
                redisCache.del(K);
            }
        }else {
            // pc or web,do not need del token
            token = oldToken;
        }
        String userTokenInfo = kvBuilder.buildUserLoginTokenValue(user.getUid(),user.getUsername(),user.getRole());
        String userTokenKey = kvBuilder.buildUserLoginKey(redisBizConfig.getTokenCachePrefix(), user.getUid());
        // no need redis transaction
        redisCache.set(userTokenKey,userTokenInfo,redisBizConfig.getTokenExpire());
        redisCache.set(K,token,redisBizConfig.getTokenExpire());
        // sync to wk IM
        UpdateTokenReq tokenReq = new UpdateTokenReq().setUid(user.getUid()).setToken(token).setDeviceLevel(deviceLevel).setDeviceFlag(deviceFlag);
        Response<UpdateTokenRes> res = wkUserService.updateToken(tokenReq);
        if (res.body() != null &&res.body().getStatus() == WK.UpdateTokenStatus.ban) {
            return ResKit.fail("user banned");
        }
        return LoginUserDetailRes.build(user);
    }
    /**
     * this method cost time ,use careful ,I suggest do not use   transactional to execute this,need
     * @param uid user id
     * @param req register req
     * @param isUploadAvatar is avatar upload
     */
    private LoginUserDetailRes createUser(String uid, RegisterReq req, boolean isUploadAvatar) {
        String shortNo = "";
        // 开启数字短编号
        if(shortNoConfig.isNumOn()) {
            shortNo = shortNoService.getShortNo();
        }else {
            shortNo = String.valueOf( SystemKit.now());
        }
        // build insert user
        User user = new User();
        user.setUid(uid);
        // set name
        if(StringKit.isNotEmpty(req.getName())) {
            user.setName(req.getName());
        }else {
            // no name set condition
            AppConfigRes appConfig = appConfigService.getAppConfig();
            if(appConfig.isRegisterUserMustCompleteInfoOn()) {
                user.setName("");
            }else {
                // random name
                user.setName(StringKit.random(6));
            }
        }
        // set sex default man
        user.setSex(Lary.Sex.man);
        // set basic info
        user.setQrVercode(UUIDKit.getUUID()+"@"+Lary.VerifyCode.QR);
        user.setVercode(UUIDKit.getUUID()+"@"+Lary.VerifyCode.user);
        user.setPhone(req.getPhone());
        user.setZone(req.getZone());
        user.setShortNo(shortNo);
        user.setIsRobot(false);
        user.setIsUploadAvatar(isUploadAvatar);
        user.setPassword(StringKit.MD5(StringKit.MD5(req.getPassword())));
        user.setUsername(req.getName());
        // insert user
        userService.save(user);
        // insert user device
        if(req.getDevice() != null) {

            Device device = new Device();
            device.setUid(uid);
            device.setDeviceId(req.getDevice().getDeviceId());
            device.setDeviceName(req.getDevice().getDeviceName());
            device.setDeviceModel(req.getDevice().getDeviceModel());
            deviceService.save(device);
        }
        // add system friend
        friendService.addSystemFriend(uid);
        /// add file helper
        friendService.addFileHelper(uid);
        // start user register event
        HashMap<String, String> map = new HashMap<>();
        map.put("uid", uid);
        if (StringKit.isEmpty(req.getInviteCode())) {
            map.put("inviteCode", req.getInviteCode());
        }
        EventData data = new EventData().setEvent(Lary.Event.register).setType(Lary.EventType.message).setData(JSONKit.toJSON(map));
        int eventId = eventService.begin(data);
        // TODO  :  what ????
        eventService.commit(eventId);
        String token = UUIDKit.getUUID();
        String K = Lary.RedisPrefix.token + token;
        String V = user.getUid()+"@"+user.getName()+"@"+user.getRole();
        // set user register token
        redisCache.set(K,V, redisBizConfig.getTokenExpire());
        // user register to wuKongIm
        UpdateTokenReq tokenReq = new UpdateTokenReq().setUid(uid).setToken(K).setDeviceFlag(req.getFlag()).setDeviceLevel(WK.DeviceLevel.slave);
        wkUserService.updateToken(tokenReq);
        // set shortNo was used
        if (shortNoConfig.isNumOn()) {
            shortNoService.setShortNoUsed(shortNo,"user");
        }
        return LoginUserDetailRes.build(user);
    }

}

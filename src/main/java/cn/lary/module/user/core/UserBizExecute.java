package cn.lary.module.user.core;

import cn.lary.core.dto.ResPair;
import cn.lary.kit.*;
import cn.lary.module.app.entity.EventData;
import cn.lary.module.app.service.AppConfigService;
import cn.lary.module.app.service.EventService;
import cn.lary.module.app.service.ShortNoService;
import cn.lary.module.common.CS.Lary;
import cn.lary.module.common.cache.KVBuilder;
import cn.lary.module.common.cache.RedisCache;
import cn.lary.module.common.server.RedisBizConfig;
import cn.lary.module.common.server.RegisterConfig;
import cn.lary.module.common.server.ShortNoConfig;
import cn.lary.module.event.dto.UserRegisterEventDTO;
import cn.lary.module.user.dto.DeviceAddAckDTO;
import cn.lary.module.user.dto.DeviceLoginDTO;
import cn.lary.module.user.dto.LoginDTO;
import cn.lary.module.user.dto.RegisterDTO;
import cn.lary.module.user.entity.Device;
import cn.lary.module.user.entity.User;
import cn.lary.module.user.service.DeviceService;
import cn.lary.module.user.service.UserService;
import cn.lary.module.user.service.UserUidService;
import cn.lary.pkg.wk.api.WKUserService;
import cn.lary.pkg.wk.dto.user.UpdateTokenDTO;
import cn.lary.pkg.wk.entity.core.WK;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.sms4j.core.factory.SmsFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserBizExecute {

    private final UserService userService;
    private final RegisterConfig registerConfig;
    private final DeviceService deviceService;
    private final EventService eventService;
    private final RedisCache redisCache;
    private final RedisBizConfig redisBizConfig;
    private final KVBuilder kvBuilder;
    // external
    private final WKUserService wkUserService;

    @Transactional(rollbackFor = Exception.class)
    public ResPair<String> login(LoginDTO req) {
        // check user status
        String password = StringKit.MD5(StringKit.MD5(req.getPassword()));
        String deviceId = req.getId();

        User user = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getDeleted,false).eq(User::getUid,req.getUid()));
        if (user == null) {
            return BizKit.fail("user not exist,please register first");
        }
        Integer uid = user.getUid();
        // check login status
        String oldToken = redisCache.get(kvBuilder.userLoginK(user.getUid(), req.getFlag()));
        if (StringKit.isNotEmpty(oldToken)) {
            return BizKit.fail("login repeated");
        }
        // check status
        if(user.getStatus() == Lary.UserStatus.ban) {
            return BizKit.fail("use banned");
        }
        // compare user pwd
        if (StringKit.diff(user.getPassword(), password)) {
            return BizKit.fail("pwd error");
        }
        // check device type
        byte deviceLevel = WK.DeviceLevel.slave;
        if (WK.DeviceFlag.app == req.getFlag()) {
            deviceLevel = WK.DeviceLevel.master;
        }
        // try to find device id
        if (StringKit.isEmpty(deviceId)) {

            // check whether is new device
            List<Device> devices = deviceService.list(new LambdaQueryWrapper<Device>().eq(Device::getUid, uid).eq(Device::getIsDelete, false));
            if (CollectionKit.isNotEmpty(devices)) {
                List<Device> list = devices.stream().filter(device -> StringKit.same(req.getName(), device.getDeviceName()) && StringKit.same(req.getModel(), device.getDeviceModel())).toList();
                if (CollectionKit.isEmpty(list)) {
                    deviceId = req.getId();
                }
            }
        }
        // login
        if (StringKit.isNotEmpty(deviceId)) {
            Device device = deviceService.queryDevice(uid,deviceId);
            if (device == null) {
                return BizKit.fail("device not exist,please register first");
            }
            DeviceLoginDTO deviceLoginDTO = new DeviceLoginDTO().setFlag(req.getFlag()).setId(deviceId).setModel(device.getDeviceModel()).setName(device.getDeviceName());
            // do login biz
            redisCache.set(kvBuilder.deviceLoginK(uid,deviceId),kvBuilder.deviceLoginV(deviceLoginDTO),redisBizConfig.getLoginDeviceCacheExpire());
            String token = userLoginRedisSet(uid, user.getName(), user.getRole(), req.getFlag());
            // update device
            deviceService.update(new LambdaUpdateWrapper<Device>().set(Device::getLastLogin, LocalDateTime.now()).eq(Device::getUid, uid).eq(Device::getDeviceId,device.getDeviceId()));
            return BizKit.ok(token);
        }
        // new device
        if (StringKit.isEmpty(deviceId) && StringKit.isNotEmpty(user.getPhone())) {
            // send sms info
            SecureRandom secureRandom = new SecureRandom();
            String verifyCode = String.valueOf(100000 + secureRandom.nextInt(900000));
            if (Lary.R) {
                SmsFactory.getSmsBlend("aliyun-test").sendMessageAsync("test",verifyCode);
            }
            DeviceAddAckDTO deviceAddAckDTO = new DeviceAddAckDTO().setDeviceModel(req.getModel()).setDeviceName(req.getName()).setVerifyCode(verifyCode);
            redisCache.set(kvBuilder.addDeviceK(uid), kvBuilder.addDeviceV(deviceAddAckDTO),redisBizConfig.getSmsAddDeviceExpire());
            return BizKit.ok();
        }else {
            return BizKit.fail("register not open");
        }
    }

    @Transactional()
    public ResPair<Void> register(RegisterDTO req) {
        if (registerConfig.isOffe()) {
            return BizKit.fail("register channel off");
        }
        // TODO  :  这里实现一个用户名是否重复的判断

        // check verify code
        String verifyCode = redisCache.get(kvBuilder.userRegisterK(req.getPhone()));
        if(StringKit.diff(verifyCode,req.getCode())) {
            //test module
//            return BizKit.fail("verify code error");
        }
        User user = new User();
        String name = req.getName();
        if (StringKit.isNotEmpty(name)) {
            user.setName(name);
        }else {
            user.setName(StringKit.random(6));
        }
        // set basic info
        user.setQrVercode(UUIDKit.getUUID()+"@"+Lary.VerifyCode.QR).setVercode(UUIDKit.getUUID()+"@"+Lary.VerifyCode.user).setEmail(req.getEmail())
        .setPhone(req.getPhone()).setZone(req.getZone()).setIsRobot(false).setSex(Lary.Sex.man).setBio(req.getBio()).setBirthday(req.getBirthday())
        .setPassword(StringKit.MD5(StringKit.MD5(req.getPassword())));
        userService.save(user);
        // start user register event
        EventData event = new UserRegisterEventDTO().setUid(user.getUid()).setPhone(req.getPhone()).of();
        int eventId = eventService.begin(event);
        // build new device
        String deviceId = UUIDKit.getUUID();
        Device device = new Device().setDeviceId(deviceId).setUid(user.getUid()).setDeviceName(req.getDevice().getName())
                .setDeviceModel(req.getDevice().getModel());
        deviceService.save(device);
        // set device login info to redis
        DeviceLoginDTO deviceLoginDTO = new DeviceLoginDTO().setId(deviceId).setName(device.getDeviceName()).setModel(device.getDeviceModel())
                .setFlag(req.getDevice().getFlag());
        redisCache.set(kvBuilder.deviceLoginK(user.getUid(), deviceId),kvBuilder.deviceLoginV(deviceLoginDTO),redisBizConfig.getLoginDeviceCacheExpire());
        //set user login info to redis
        String token = userLoginRedisSet(user.getUid(), name, Lary.UserRole.normal, req.getDevice().getFlag());
        // add system friend
//        friendService.addSystemFriend(uid);
//        /// add file helper
//        friendService.addFileHelper(uid);
        // user register to wuKongIm
        UpdateTokenDTO tokenDTO = new UpdateTokenDTO(user.getUid(), token,req.getDevice().getFlag(), WK.DeviceLevel.slave);
        wkUserService.updateToken(tokenDTO);
//        // set shortNo was used
//        if (shortNoConfig.isNumOn()) {
//            shortNoService.setShortNoUsed(shortNo,"user");
//        }
        eventService.commit(eventId);
        return BizKit.ok();
    }
    /**
     * 对 用户登陆的 token在用户维度和 token维度设置在redis
     * @param uid u
     * @param name username
     * @param role r
     * @param deviceFlag {@link WK.DeviceFlag}
     */
    private String userLoginRedisSet(Integer uid,String name,int role,int deviceFlag){
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
     * 用户注册或者登陆时的 手机验证
     * @param phone 手机号
     * @return ok
     */
    public ResPair<Void> smsCode(String phone) {
        SecureRandom secureRandom = new SecureRandom();
        String token = String.valueOf(100000 + secureRandom.nextInt(900000));
        if (Lary.R) {
            SmsFactory.getSmsBlend("aliyun-test").sendMessageAsync(phone,token);
        }
        // build redis cache
        redisCache.set(kvBuilder.userRegisterK(phone),kvBuilder.userRegisterV(token),redisBizConfig.getRegisterExpire());
        return BizKit.ok();
    }

    /**
     * 刷新 login token
     * @param uid user id
     * @return token
     */
    public ResPair<String> refreshToken(Integer uid) {
        return null;
    }
}


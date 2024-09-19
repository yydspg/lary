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
import cn.lary.module.user.dto.DeviceAddAckDTO;
import cn.lary.module.user.dto.DeviceLoginDTO;
import cn.lary.module.user.dto.LoginDTO;
import cn.lary.module.user.dto.RegisterDTO;
import cn.lary.module.user.entity.Device;
import cn.lary.module.user.entity.User;
import cn.lary.module.user.service.DeviceService;
import cn.lary.module.user.service.UserService;
import cn.lary.pkg.wk.api.WKUserService;
import cn.lary.pkg.wk.dto.user.UpdateTokenReq;
import cn.lary.pkg.wk.entity.core.WK;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.sms4j.core.factory.SmsFactory;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserBizExecute {
    private final UserService userService;
    private final RegisterConfig registerConfig;
    private final AppConfigService appConfigService;
    private final ShortNoService shortNoService;
    private final ShortNoConfig shortNoConfig;
    private final DeviceService deviceService;
    private final EventService eventService;
    private final RedisCache redisCache;
    private final RedisBizConfig redisBizConfig;
    private final WKUserService wkUserService;
    private final KVBuilder kvBuilder;

    public ResPair<String> login(LoginDTO loginDTO) {
        // check user status
        String username = loginDTO.getName();
        String password = StringKit.MD5(StringKit.MD5(loginDTO.getPassword()));
        DeviceLoginDTO deviceDTO = loginDTO.getDeviceLoginDTO();
        String deviceId = deviceDTO.getDeviceId();

        User user = userService.queryByName(username);
        if (user == null) {
            return BizKit.fail("user not exist,please register first");
        }
        String uid = user.getUid();
        // check login status
        String oldToken = redisCache.get(kvBuilder.userLoginK(user.getUid(), loginDTO.getFlag()));
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
        if (WK.DeviceFlag.app == deviceDTO.getDeviceFlag()) {
            deviceLevel = WK.DeviceLevel.master;
        }
        // try to find device id
        if (StringKit.isEmpty(deviceId)) {

            // check whether is new device
            List<Device> devices = deviceService.list(new LambdaQueryWrapper<Device>().eq(Device::getUid, uid).eq(Device::getIsDelete, false));
            if (CollectionKit.isNotEmpty(devices)) {
                List<Device> list = devices.stream().filter(device -> StringKit.same(deviceDTO.getDeviceName(), device.getDeviceName()) && StringKit.same(deviceDTO.getDeviceModel(), device.getDeviceModel())).toList();
                if (CollectionKit.isEmpty(list)) {
                    deviceId = deviceDTO.getDeviceId();
                }
            }
        }
        // login
        if (StringKit.isNotEmpty(deviceId)) {
            Device device = deviceService.queryDevice(uid,deviceId);
            if (device == null) {
                return BizKit.fail("device not exist,please register first");
            }
            DeviceLoginDTO deviceLoginDTO = new DeviceLoginDTO().setDeviceFlag(loginDTO.getFlag()).setDeviceId(deviceId).setDeviceModel(device.getDeviceModel()).setDeviceName(device.getDeviceName());
            // do login biz
            redisCache.set(kvBuilder.deviceLoginK(uid,deviceId),kvBuilder.deviceLoginV(deviceLoginDTO),redisBizConfig.getLoginDeviceCacheExpire());
            String token = userLoginRedisSet(uid, user.getName(), user.getRole(), loginDTO.getFlag());
            // update device
            deviceService.update(new LambdaUpdateWrapper<Device>().set(Device::getLastLogin, LocalDateTime.now()).eq(Device::getUid, uid).eq(Device::getDeviceId,device.getDeviceId()));
            return BizKit.ok(token);
        }
        // new device
        if (StringKit.isEmpty(deviceId) && StringKit.isNotEmpty(user.getPhone())) {
            // send sms info
            SecureRandom secureRandom = new SecureRandom();
            String verifyCode = String.valueOf(100000 + secureRandom.nextInt(900000));
            SmsFactory.getSmsBlend("aliyun-test").sendMessageAsync("test",verifyCode);
            DeviceAddAckDTO deviceAddAckDTO = new DeviceAddAckDTO().setDeviceModel(deviceDTO.getDeviceModel()).setDeviceName(deviceDTO.getDeviceName()).setVerifyCode(verifyCode);
            redisCache.set(kvBuilder.addDeviceK(uid), kvBuilder.addDeviceV(deviceAddAckDTO),redisBizConfig.getSmsAddDeviceExpire());
            return BizKit.ok();
        }else {
            return BizKit.fail("register not open");
        }
    }
    public ResPair<Void> register(RegisterDTO req) {
        if (registerConfig.isOffe()) {
            return BizKit.fail("register channel off");
        }
        // TODO  :  这里实现一个用户名是否重复的判断
        String shortNo = "";
        // 开启数字短编号
        if(shortNoConfig.isNumOn()) {
            shortNo = shortNoService.getShortNo();
        }else {
            shortNo = String.valueOf( SystemKit.now());
        }
        String uid = UUIDKit.getUUID();
        User user = new User().setUid(uid);
        String name = req.getName();
        if (StringKit.isNotEmpty(name)) {
            user.setName(name);
        }else {
            user.setName(StringKit.random(6));
        }
        // start user register event
        HashMap<String, String> map = new HashMap<>();
        map.put("uid", uid);
        if (StringKit.isEmpty(req.getInviteCode())) {
            map.put("inviteCode", req.getInviteCode());
        }
        EventData data = new EventData().setEvent(Lary.Event.register).setType(Lary.EventType.message).setData(JSONKit.toJSON(map));
        int eventId = eventService.begin(data);
        // check verify code
        String verifyCode = redisCache.get(kvBuilder.userRegisterK(uid, req.getPhone()));
        if(StringKit.diff(verifyCode,req.getCode())) {
            return BizKit.fail("verify code error");
        }
        // set basic info
        user.setQrVercode(UUIDKit.getUUID()+"@"+Lary.VerifyCode.QR).setVercode(UUIDKit.getUUID()+"@"+Lary.VerifyCode.user)
        .setPhone(req.getPhone()).setZone(req.getZone()).setShortNo(shortNo).setIsRobot(false).setSex(Lary.Sex.man)
        .setPassword(StringKit.MD5(StringKit.MD5(req.getPassword())));
        userService.save(user);
        // build new device
        String deviceId = UUIDKit.getUUID();
        Device device = new Device().setDeviceId(deviceId).setUid(uid).setDeviceName(req.getDevice().getDeviceName())
                .setDeviceModel(req.getDevice().getDeviceModel());
        deviceService.save(device);
        // set device login info to redis
        DeviceLoginDTO deviceLoginDTO = new DeviceLoginDTO().setDeviceId(deviceId).setDeviceName(device.getDeviceName()).setDeviceModel(device.getDeviceModel()).setDeviceFlag(req.getDevice().getDeviceFlag());
        redisCache.set(kvBuilder.deviceLoginK(uid,deviceId),kvBuilder.deviceLoginV(deviceLoginDTO),redisBizConfig.getLoginDeviceCacheExpire());
        //set user login info to redis
        String token = userLoginRedisSet(uid, name, Lary.UserRole.normal, req.getDevice().getDeviceFlag());
        // add system friend
//        friendService.addSystemFriend(uid);
//        /// add file helper
//        friendService.addFileHelper(uid);
        // user register to wuKongIm
        UpdateTokenReq tokenReq = new UpdateTokenReq().setUid(uid).setToken(token).setDeviceFlag(req.getDevice().getDeviceFlag()).setDeviceLevel(WK.DeviceLevel.slave);
        wkUserService.updateToken(tokenReq);
        // set shortNo was used
        if (shortNoConfig.isNumOn()) {
            shortNoService.setShortNoUsed(shortNo,"user");
        }
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
    String  userLoginRedisSet(String uid,String name,int role,int deviceFlag){
        String token = UUIDKit.getUUID() + "@" + deviceFlag;
        // remove before device login token
        redisCache.del(kvBuilder.userLoginK(uid,deviceFlag));
        redisCache.del(kvBuilder.userLoginTokenK(token));
        // add new token
        redisCache.set(kvBuilder.userLoginK(uid,deviceFlag),kvBuilder.userLoginV(token,deviceFlag),redisBizConfig.getLoginUserExpire());
        redisCache.set(kvBuilder.userLoginTokenK(token),kvBuilder.userLoginTokenV(uid,name,role),redisBizConfig.getLoginUserTokenExpire());
        return token;
    }
    void registerVerifyCode(String uid,String phone) {
        String token = StringKit.random(6);
        SmsFactory.getSmsBlend("aliyun-test").sendMessageAsync(phone,token);
    }
}


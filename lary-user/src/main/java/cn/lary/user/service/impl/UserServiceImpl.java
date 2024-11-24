package cn.lary.user.service.impl;

import cn.lary.api.message.YutakMessageService;
import cn.lary.common.context.RequestContext;
import cn.lary.common.dto.ResponsePair;
import cn.lary.common.id.LaryIDBuilder;
import cn.lary.common.kit.BusinessKit;
import cn.lary.common.kit.CollectionKit;
import cn.lary.common.kit.StringKit;
import cn.lary.common.kit.UUIDKit;
import cn.lary.user.component.UserCache;
import cn.lary.user.component.UserCacheComponent;
import cn.lary.user.constant.DEVICE;
import cn.lary.user.constant.USER;
import cn.lary.user.dto.LoginDTO;
import cn.lary.user.dto.RegisterDTO;
import cn.lary.user.dto.UserDestroyDTO;
import cn.lary.user.dto.UserUpdateDTO;
import cn.lary.user.entity.Device;
import cn.lary.user.entity.User;
import cn.lary.user.entity.UserSetting;
import cn.lary.user.mapper.UserMapper;
import cn.lary.user.service.DeviceService;
import cn.lary.user.service.UserService;
import cn.lary.user.service.UserSettingService;
import cn.lary.user.vo.UserVO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.houbb.sensitive.word.core.SensitiveWordHelper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.List;

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

    private final DeviceService deviceService;
    private final UserSettingService userSettingService;
    private final TransactionTemplate transactionTemplate;
    private final UserCacheComponent userCacheComponent;
    private final LaryIDBuilder builder;

    @DubboReference
    private final YutakMessageService yutakMessageService;
    @Override
    public User virtual(User user) {
        user.setUid(builder.next());
        save(user);
        return user;
    }

    @Override
    public ResponsePair<String> login(LoginDTO dto) {
        String password = StringKit.encrypt(dto.getPassword());
        User user = lambdaQuery()
                .select(User::getUid)
                .eq(User::getUid,dto.getUid())
                .one();
        if (user == null ) {
            return BusinessKit.fail("user not exist,please register first");
        }
        long uid = user.getUid();
        int flag = dto.getFlag();
        if(user.getStatus() == USER.STATUS.BAN) {
            return BusinessKit.fail("u been banned");
        }
        if (StringKit.diff(user.getPassword(), password)) {
            return BusinessKit.fail("password error");
        }
        int deviceLevel = DEVICE.LEVEL.SLAVE;
        if (DEVICE.FLAG.APP == flag) {
            deviceLevel = DEVICE.LEVEL.MASTER;
        }
        UserCache data = userCacheComponent.getData(uid,flag);
        if (data != null) {
           // Devices with the same flag will be logged in and
            // the previously logged in devices will be removed
            long did = data.getDid();
            Device device = deviceService.getDevice(dto.getDid(), dto.getName(), flag);
            if (device != null && device.getDid() == did) {
                log.error("same device when login,uid:{},did:{}", uid, did);
                return BusinessKit.fail("login repeated");
            }
        }
        Device device = deviceService.getDevice(dto.getDid(), dto.getName(), flag);
        if (device == null) {
            if (StringKit.isEmpty(dto.getCode()) && deviceLevel == DEVICE.LEVEL.SLAVE) {
                return BusinessKit.fail("cmd:[login on new device],phone:"+user.getPhone());
            }else {
                // check code
                ResponsePair<Void> pair = userCacheComponent.verifyLoginSMS(dto.getPhone(), dto.getCode(), dto.getName(), flag);
                if (pair.isFail()) {
                    return BusinessKit.fail(pair.getMsg());
                }
                device = new Device()
                        .setUid(uid)
                        .setFlag(flag)
                        .setName(dto.getName())
                        .setLevel(deviceLevel);
                deviceService.build(device);
            }
        }
        UserCache cache = new UserCache()
                .setIp(dto.getIp())
                .setFlag(flag)
                .setDid(device.getDid())
                .setUid(uid);
        userCacheComponent.setData(uid, flag, cache);
        String token = null;
//        yutakMessageService.updateToken(new UserLoginUpdateTokenDTO()
//                .setUid(uid)
////                .setToken(token)
//                .setFlag(device.getFlag())
//                .setLevel(device.getLevel()));
        deviceService.lambdaUpdate()
                .set(Device::getLastLogin, LocalDateTime.now())
                .eq(Device::getUid, uid)
                .eq(Device::getId,device.getId())
                .update();
        return BusinessKit.ok(token);
    }

    @Override
    public ResponsePair<String> register(RegisterDTO dto) {
        ResponsePair<Void> pair = userCacheComponent.verifyRegisterSMS(dto.getPhone(), dto.getCode(), dto.getName(), dto.getFlag());
        if (pair.isFail()) {
            return BusinessKit.fail(pair.getMsg());
        }
        User user = new User();
        String name = dto.getName();
        if (StringKit.isNotEmpty(name) && !SensitiveWordHelper.contains(name)) {
            return BusinessKit.fail("username contains sensitive word");
        }else {
            name = StringKit.random(6);
        }
        user.setUsername(name);
        int flag = dto.getFlag();
        int deviceLevel = DEVICE.LEVEL.SLAVE;
        if (DEVICE.FLAG.APP == flag) {
            deviceLevel = DEVICE.LEVEL.MASTER;
        }
        int finalDeviceLevel = deviceLevel;
        long uid = builder.next();
         transactionTemplate.executeWithoutResult(status -> {
             Device device = new Device()
                     .setUid(uid)
                     .setName(dto.getDeviceName())
                     .setFlag(flag)
                     .setLevel(finalDeviceLevel);
             deviceService.save(device);
             userSettingService.save(new UserSetting().setUid(uid));
            user.setQrVercode(UUIDKit.getUUID() + "@" + USER.VERIFY_CODE.QR)
                    .setVercode(UUIDKit.getUUID() + "@" + USER.VERIFY_CODE.USER)
                    .setEmail(dto.getEmail())
                    .setPhone(dto.getPhone())
                    .setZone(dto.getZone())
                    .setSex(USER.SEX.MAN)
                    .setBio(dto.getBio())
                    .setBirthday(dto.getBirthday())
                    .setPassword(StringKit.encrypt(dto.getPassword()));
            save(user);
        });
         String token = null;
//        UpdateTokenDTO tokenDTO = new UpdateTokenDTO()
//                .setUid(uid)
//                .setToken(token)
//                .setFlag(flag)
//                .setLevel(deviceLevel);
//        yutakMessageService.updateToken(tokenDTO);
        return BusinessKit.ok(token);
    }

    @Override
    public ResponsePair<Void> logout(HttpServletRequest request) {
        return BusinessKit.ok();
    }

//    @Override
//    public ResponsePair<RouteVO> getRoute() {
//       return BusinessKit.ok(yutakMessageService.getRoute(RequestContext.uid()));
//    }

    @Override
    public ResponsePair<Void> registerCode(String phone,String name,int flag) {
        userCacheComponent.registerSMS(phone, name, flag);
        return BusinessKit.ok();
    }

    @Override
    public ResponsePair<Void> destroyCode(String phone,String name,int flag)  {
        userCacheComponent.destroySMS(phone, name, flag);
        return  BusinessKit.ok();
    }

    @Override
    public ResponsePair<Void> refresh(HttpServletRequest request) {
        return BusinessKit.ok();
    }

    @Override
    public ResponsePair<Void> destroy(UserDestroyDTO dto) {
        long uid = RequestContext.uid();
        int flag = RequestContext.flag();
        if (flag != dto.getFlag()) {
            return BusinessKit.fail("not support");
        }
        User user = lambdaQuery()
                .select(User::getPhone,User::getSid,
                        User::getRid,User::getWid)
                .eq(User::getUid, uid)
                .one();
        if (user == null) {
            return BusinessKit.fail("user not exist");
        }
        UserCache appData = userCacheComponent.getData(uid, DEVICE.FLAG.APP);
        UserCache pcData = userCacheComponent.getData(uid, DEVICE.FLAG.PC);
        if (appData != null){
            forceLogout(uid,DEVICE.FLAG.APP);
        }
        if (pcData != null){
            forceLogout(uid,DEVICE.FLAG.PC);
        }
        transactionTemplate.executeWithoutResult(status -> {
//            walletService.lambdaUpdate()
//                    .set(Wallet::getStatus,WALLET.STATUS.DESTROY)
//                    .eq(Wallet::getWid,uid)
//                    .update();
            lambdaUpdate()
                    .set(User::getStatus,USER.STATUS.DESTROY)
                    .eq(User::getUid,uid)
                    .update();
        });
        return BusinessKit.ok();
    }


    @Override
    public ResponsePair<UserVO> update(UserUpdateDTO dto) {
        // TODO  :  impl
        return null;
    }

    @Override
    public ResponsePair<UserVO> my() {
        User user = lambdaQuery()
                .eq(User::getUid, RequestContext.uid())
                .one();
        if (user == null) {
            log.error("search user error,uid:{}", RequestContext.uid());
            return BusinessKit.fail("query FAIL");
        }
        return BusinessKit.ok(new UserVO(user));
    }

    @Override
    public List<Long> getValidUsers(List<Long> members) {
        List<User> data = lambdaQuery()
                .select(User::getUid)
                .in(User::getUid, members)
                .list();
        if (CollectionKit.isEmpty(data)) {
            return null;
        }
        return  data.stream()
                .filter(u->u.getStatus() == USER.STATUS.OK)
                .map(User::getUid)
                .toList();
    }

    private void forceLogout(long uid,int flag) {
        userCacheComponent.dropData(uid,flag);
    }
//    private String buildUserLoginCache(long uid,String name,int flag,int role) {
//        String token = UUIDKit.getUUID() + "@" + flag;
//        cacheComponent.set(kvBuilder.userLoginK(uid,flag),kvBuilder.userLoginV(token), redisBusinessConfig.getLoginUserExpire());
//        cacheComponent.set(kvBuilder.userLoginTokenK(token),kvBuilder.userLoginTokenV(uid,name,role), redisBusinessConfig.getLoginUserTokenExpire());
//        return token;
//    }

//    /**
//     * 获取token pair
//     * @param request {@link HttpServletRequest}
//     * @return {@link TokenPair}
//     */
//    private  TokenPair of(HttpServletRequest request) {
//        String token = String.valueOf(request.getHeader("srsToken"));
//        if (token == null) {
//            return null;
//        }
//        String[] tmp = token.split("@");
//        if (tmp.length != 2) {
//            return null;
//        }
//        String userInfo = cacheComponent.get(kvBuilder.userLoginTokenK(token));
//        if(StringKit.isEmpty(userInfo)) {
//            return null;
//        }
//        String[] args = userInfo.split("@");
//        if (args.length <= 2) {
//            return null;
//        }
//        return new TokenPair(Integer.parseInt(args[0]),token,Integer.parseInt(tmp[1]));
//    }
}

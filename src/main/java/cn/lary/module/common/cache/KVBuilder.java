package cn.lary.module.common.cache;

import cn.lary.module.stream.dto.JoinLiveCacheDTO;
import cn.lary.module.stream.dto.LiveCacheDTO;
import cn.lary.module.stream.dto.RaffleCacheDTO;
import cn.lary.module.stream.dto.RedPacketCacheDTO;
import cn.lary.module.user.dto.DeviceAddAckCacheDTO;
import cn.lary.module.user.dto.DeviceLoginDTO;

import java.util.Map;

public interface KVBuilder {

    /**
     * 生成用户设备登陆 K
     *
     * @param uid user id
     * @return k
     */
    String deviceLoginK(int uid, int deviceId);

    String deviceLoginV(DeviceLoginDTO deviceLoginDTO);

    /**
     * 用户登陆 uid为key,token 为 value
     *
     * @param uid u
     * @return k
     */
    String userLoginK(int uid, int deviceFlag);

    String userLoginV(String token, int deviceFlag);

    /**
     * 用户登陆 token 为 key,uid,username,role为value
     *
     * @param token t
     * @return k
     */
    String userLoginTokenK(String token);

    String userLoginTokenV(int uid, String username, int role);

    /**
     * 构建用户注册时的验证码
     * @param phone
     * @return
     */
    String userRegisterK( String phone);

    String userRegisterV(String token);




    /**
     * 构建直播记录
     *
     * @param uid      u
     * @param streamId s
     * @return s
     */
    String streamRecordK(int uid, int streamId);

    Map streamRecordV();

    /**
     * 新增用户设备
     *
     * @param uid u
     * @return S
     */
    String addDeviceK(int uid);

    Map addDeviceV(DeviceAddAckCacheDTO dto);

    /**
     * 开启直播
     *
     * @param uid u
     * @return s
     */
    String goLiveK(int uid);

    Map goLiveV(LiveCacheDTO dto);

    /**
     * 加入直播  token 验证回调
     *
     * @param uid u
     * @return s
     */
    String joinLiveK(int uid);
    Map joinLiveV(JoinLiveCacheDTO dto);

    /**
     * 直播抽奖
     * @param uid u
     * @return s
     */
    String raffleK(int uid);
    Map raffleV(RaffleCacheDTO dto);

    /**
     * 直播红包
     * @param uid u
     * @return ok
     */
    String redPacketK(int uid);
    Map redPacketV(RedPacketCacheDTO dto);

    /**
     * 直播抽奖集合
     * @param uid u
     * @return s
     */
    String raffleListK(int uid);
}
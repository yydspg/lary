package cn.lary.module.common.cache;

import cn.lary.module.cache.dto.JoinLiveCacheDTO;
import cn.lary.module.cache.dto.LiveCache;
import cn.lary.module.raffle.entity.RaffleEventCache;
import cn.lary.module.cache.dto.RedPacketCacheDTO;
import cn.lary.module.cache.dto.DeviceAddResponseCacheDTO;
import cn.lary.module.cache.dto.DeviceLoginCacheDTO;

import java.util.Map;

public interface KVBuilder {

    /**
     * 生成用户设备登陆 K
     *
     * @param uid user id
     * @return k
     */
    String deviceLoginK(long uid, int flag);

    Map deviceLoginV(DeviceLoginCacheDTO deviceLoginCacheDTO);

    /**
     * 用户登陆 uid为key,token 为 value
     *
     * @param uid u
     * @return k
     */
    String userLoginK(long uid, int deviceFlag);

    String userLoginV(String token);

    /**
     * 用户登陆 token 为 key,uid,username,role为value
     *
     * @param token t
     * @return k
     */
    String userLoginTokenK(String token);

    String userLoginTokenV(long uid, String username, int role);

    /**
     * 构建用户注册时的验证码
     * @param phone p
     * @return s
     */
    String userRegisterK( String phone);

    String userRegisterV(String token);


    /**
     * 构建用户注销时的验证码
     * @return s
     */
    String userDestroyK(long uid);

    String userDestroyV(String token);





    /**
     * 构建直播记录
     *
     * @param uid      u
     * @param streamId s
     * @return s
     */
    String streamRecordK(long uid, long streamId);

    Map streamRecordV();

    /**
     *
     * @param uid u
     * @return S
     */
    String addDeviceK(long uid,String phone);

    Map addDeviceV(DeviceAddResponseCacheDTO dto);

    /**
     * 开启直播
     *
     * @param uid u
     * @return s
     */
    String goLiveK(long uid);

    Map goLiveV(LiveCache dto);

    /**
     * 加入直播  token 验证回调
     *
     * @param uid u
     * @return s
     */
    String joinLiveK(long uid);
    Map joinLiveV(JoinLiveCacheDTO dto);

    /**
     * 直播抽奖
     * @param uid u
     * @return s
     */
    String raffleK(long uid);
    Map raffleV(RaffleEventCache dto);

    /**
     * 直播红包
     * @param uid u
     * @return ok
     */
    String redPacketK(long uid);
    Map redPacketV(RedPacketCacheDTO dto);

    /**
     * 直播抽奖集合
     * @param uid u
     * @return s
     */
    String raffleListK(long uid);

    /**
     * 直播红包的uid防重
     * @param uid u
     * @return ok
     */
    String redPacketUidMapK(long uid);

    /**
     * 直播红包的数据队列
     * @param uid u
     * @return ok
     */
    String redPacketDataListK(long uid);
}
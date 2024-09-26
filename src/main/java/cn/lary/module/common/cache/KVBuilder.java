package cn.lary.module.common.cache;

import cn.lary.module.stream.dto.JoinLiveCacheDTO;
import cn.lary.module.stream.dto.LiveCacheDTO;
import cn.lary.module.user.dto.DeviceAddAckDTO;
import cn.lary.module.user.dto.DeviceLoginDTO;

import java.util.Map;

public interface KVBuilder {
    String buildFriendApplyKey(String token, String uid);

    String buildFriendApplyValue(String fromUid, String vercode, String remark);


    String buildGroupMemberVerCode();

    public String buildUserLoginTokenValue(String uid, String username, String role);

    public String buildRegisterCacheKey(String zone, String phone, int codeType);

    public String buildUserLoginKey(String tokenPrefix, String token);

    public String buildUserLoginKey(String token);

    /**
     * 生成用户设备登陆 K
     *
     * @param uid user id
     * @return k
     */
    String deviceLoginK(String uid, String deviceId);

    String deviceLoginV(DeviceLoginDTO deviceLoginDTO);

    /**
     * 用户登陆 uid为key,token 为 value
     *
     * @param uid u
     * @return k
     */
    String userLoginK(String uid, int deviceFlag);

    String userLoginV(String token, int deviceFlag);

    /**
     * 用户登陆 token 为 key,uid,username,role为value
     *
     * @param token t
     * @return k
     */
    String userLoginTokenK(String token);

    String userLoginTokenV(String uid, String username, int role);

    /**
     * 构建用户注册时的验证码
     *
     * @param uid
     * @param phone
     * @return
     */
    String userRegisterK( String phone);

    String userRegisterV(String token);


    String buildDanmakuChannelTokenKey(String uid);

    String buildDanmakuChannelTokenValue(String channelId);


    /**
     * 构建直播记录
     *
     * @param uid      u
     * @param streamId s
     * @return s
     */
    String streamRecordK(String uid, String streamId);

    Map streamRecordV();

    /**
     * 新增用户设备
     *
     * @param uid u
     * @return S
     */
    String addDeviceK(String uid);

    String addDeviceV(DeviceAddAckDTO loginDTO);

    /**
     * 开启直播
     *
     * @param uid u
     * @return s
     */
    String goLiveK(String uid);

    Map goLiveV(LiveCacheDTO dto);

    /**
     * 加入直播  token 验证回调
     *
     * @param uid u
     * @return s
     */
    String joinLiveK(String uid);
    Map joinLiveV(JoinLiveCacheDTO dto);
}
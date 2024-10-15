package cn.lary.module.stream.core;

import cn.lary.core.dto.PageQuery;
import cn.lary.core.dto.ResPair;
import cn.lary.kit.BizKit;
import cn.lary.module.common.constant.Lary;
import cn.lary.module.common.cache.KVBuilder;
import cn.lary.module.common.cache.RedisCache;
import cn.lary.module.stream.dto.FollowDTO;
import cn.lary.module.stream.dto.LiveCacheDTO;
import cn.lary.module.stream.entity.Follow;
import cn.lary.module.stream.service.FollowService;
import cn.lary.module.user.entity.User;
import cn.lary.module.user.service.UserService;
import cn.lary.pkg.wk.api.WKMessageService;
import cn.lary.pkg.wk.dto.message.MessageHeader;
import cn.lary.pkg.wk.dto.message.MessageSendDTO;
import cn.lary.pkg.wk.constant.WK;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FollowBizExecute {

    private final FollowService followService;
    private final UserService userService;
    private final KVBuilder kvBuilder;
    private final RedisCache redisCache;
    private final WKMessageService wkMessageService;

    /**
     * 关注用户
     * @param uid u
     * @param uidName u
     * @param req {@link FollowDTO}
     * @return ok
     */
    public ResPair<Void> follow(int uid,String uidName,FollowDTO req) {

        Follow relation = followService.getOne(new LambdaQueryWrapper<Follow>().eq(Follow::getUid, uid).eq(Follow::getToUid, req.getToUid()).eq(Follow::getIsDelete, false));
        // once follow
        if (relation != null ) {
            // you had blocked to_uid
            if (relation.getIsBlock()) {
                return BizKit.fail("you had block:"+req.getToUid());
            }
            // just unfollow to_uid
            if (relation.getIsUnfollow()) {
                followService.update(new LambdaUpdateWrapper<Follow>().eq(Follow::getUid, uid).eq(Follow::getToUid, req.getToUid()).set(Follow::getIsUnfollow,true));
                return BizKit.ok();
            }
        }
        // not follow to_uid before
        // check to_uid status
        User toUidInfo = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUid, req.getToUid()).eq(User::getDeleted, false));

        if (toUidInfo == null) {
            return BizKit.fail(req.getToUid()+":not exist");
        }
        int toUid = toUidInfo.getUid();
        if (toUidInfo.getStatus() == Lary.UserStatus.ban) {
            return BizKit.fail(req.getToUid()+":banned");
        }
        // check to_uid -> uid
        Follow reverseRelation = followService.getOne(new LambdaQueryWrapper<Follow>().eq(Follow::getUid, req.getToUid()).eq(Follow::getToUid, uid).eq(Follow::getIsDelete, false));
        boolean isAlone = true;
        if (reverseRelation != null) {
            // to_uid do not follow uid
            if (reverseRelation.getIsBlock()) {
                return BizKit.fail(req.getToUid()+":had block you");
            }
            // build friend relationship
            if (!reverseRelation.getIsUnfollow()) {
                isAlone = false;
            }
        }
        // check whether living
        if (toUidInfo.getIsAnchor() && req.getCode() == Lary.FollowCode.stream) {
            Map<Object, Object> liveInfo = redisCache.getHash(kvBuilder.goLiveK(toUid));
            if (liveInfo != null) {
                LiveCacheDTO cache = LiveCacheDTO.of(liveInfo);
                Map<Object, Object> streamRecord = redisCache.getHash(kvBuilder.streamRecordK(toUid, cache.getStreamId()));
                if (streamRecord == null) {
                    return BizKit.fail("stream record not exist");
                }
                redisCache.incrHash(kvBuilder.streamRecordK(toUid,cache.getStreamId()),"newFansNum");
            }
        }
        //store
        Follow followApply = new Follow()
                .setUid(uid)
                .setToUid(toUid)
                .setUsername(toUidInfo.getName())
                .setBio(toUidInfo.getBio())
                .setAvatar(toUidInfo.getAvatarUrl())
                .setSource(req.getCode())
                .setIsAnchor(toUidInfo.getIsAnchor());
        followService.save(followApply);
        // send message
        if (!isAlone) {
            String payload = uidName + "关注了你";
            wkMessageService.send(new MessageSendDTO()
                    .setHeader(new MessageHeader().setNoPersist(1))
                    .setChannelID(uid)
                    .setChannelType(WK.ChannelType.person)
                    .setFromUID(uid)
                    .setPayload(payload.getBytes(StandardCharsets.UTF_8)));
            payload = "我接受了你的好友申请，快来和我聊天吧";
            wkMessageService.send(new MessageSendDTO()
                    .setHeader(new MessageHeader().setNoPersist(1).setRedDot(1))
                    .setChannelID(uid)
                    .setChannelType(WK.ChannelType.person)
                    .setFromUID(toUid)
                    .setPayload(payload.getBytes(StandardCharsets.UTF_8)));
        }else {
            String payload = uidName + "关注了你，快来回关吧";
            wkMessageService.send(new MessageSendDTO()
                    .setHeader(new MessageHeader().setNoPersist(1).setRedDot(1))
                    .setChannelID(toUid)
                    .setChannelType(WK.ChannelType.person)
                    .setFromUID(uid)
                    .setPayload(payload.getBytes(StandardCharsets.UTF_8)));
        }

        return BizKit.ok();
    }

    /**
     * 取消关注
     * @param uid u
     * @param toUid t
     * @return ok
     */
    public ResPair<Void> unfollow(int uid,int toUid) {

        Follow relation = followService.getOne(new LambdaQueryWrapper<Follow>().eq(Follow::getUid, uid).eq(Follow::getToUid, toUid).eq(Follow::getIsDelete, false));
        if (relation == null) {
            return BizKit.fail("request error");
        }
        // check two-way relation
        Follow reverse = followService.getOne(new LambdaQueryWrapper<Follow>().eq(Follow::getUid, toUid).eq(Follow::getToUid, uid).eq(Follow::getIsDelete, false));
        User toUidInfo = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUid, toUid).eq(User::getDeleted, false));
        if (toUidInfo == null) {
            return BizKit.fail(toUid+":not exist");
        }
        // check whether living
        if (toUidInfo.getIsAnchor() ) {
            Map<Object, Object> liveInfo = redisCache.getHash(kvBuilder.goLiveK(toUid));
            if (liveInfo != null) {
                LiveCacheDTO cache = LiveCacheDTO.of(liveInfo);
                Map<Object, Object> streamRecord = redisCache.getHash(kvBuilder.streamRecordK(toUid, cache.getStreamId()));
                if (streamRecord == null) {
                    return BizKit.fail("stream record not exist");
                }
                redisCache.decrHash(kvBuilder.streamRecordK(toUid,cache.getStreamId()),"newFansNum");
            }
        }
        // not block and unfollow
        if (reverse != null &&!reverse.getIsUnfollow() && !reverse.getIsBlock() && reverse.getIsOneWay()) {
            followService.update(new LambdaUpdateWrapper<Follow>().eq(Follow::getUid,toUid).eq(Follow::getToUid, uid).set(Follow::getIsOneWay,true));
        }
        // update one-way relation
        followService.update(new LambdaUpdateWrapper<Follow>().eq(Follow::getUid, uid).eq(Follow::getToUid,toUid).set(Follow::getIsUnfollow, true));
        return BizKit.ok();
    }

    /**
     * 关注列表
     * @param uid u
     * @param req {@link PageQuery}
     * @return {@link Follow}
     */
    public ResPair<List<Follow>> follows(Integer uid, PageQuery req) {
//        User user = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUid, uid).eq(User::getDeleted, false).eq(User::getStatus, Lary.UserStatus.ok));
//        if (user == null) {
//            return BizKit.fail("user status error");
//        }
        Page<Follow> page = new Page<>(req.getPageIndex(), req.getPageSize());
        Page<Follow> res = followService.page(page, new LambdaQueryWrapper<Follow>()
                .eq(Follow::getUid, uid).eq(Follow::getIsUnfollow,false).eq(Follow::getIsBlock, false).eq(Follow::getIsDelete, false)
                .orderByDesc(Follow::getUpdateAt));
        return BizKit.ok(res.getRecords());
    }

    /**
     * 拉黑
     * @param uid u
     * @param toUid t
     * @return ok
     */
    public ResPair<Void> block(int uid, int toUid) {

        Follow relation = followService.getOne(new LambdaQueryWrapper<Follow>()
                .eq(Follow::getUid, uid)
                .eq(Follow::getToUid, toUid)
                .eq(Follow::getIsDelete, false));
        User toUser = userService.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUid, toUid)
                .eq(User::getDeleted, false)
                .eq(User::getStatus, Lary.UserStatus.ok));
        if ( toUser == null) {
            return BizKit.fail("user status error");
        }
        if (relation == null) {
            followService.save(new Follow()
                    .setUid(uid)
                    .setToUid(toUid)
                    .setIsBlock(true)
                    .setAvatar(toUser.getAvatarUrl())
                    .setBio(toUser.getBio())
                    .setUsername(toUser.getName()));
        }
        followService.update(new LambdaUpdateWrapper<Follow>()
                .eq(Follow::getUid, uid)
                .eq(Follow::getToUid,toUid)
                .set(Follow::getIsBlock, true));
        return BizKit.ok();
    }

    /**
     * 取消拉黑
     * @param uid u
     * @param toUid t
     * @return ok
     */
    public ResPair<Void> unblock(int uid, int toUid) {
        Follow relation = followService.getOne(new LambdaQueryWrapper<Follow>().eq(Follow::getUid, uid).eq(Follow::getToUid, toUid).eq(Follow::getIsDelete, false));
        if (relation == null) {
            log.error("no block record exist,uid:{},toUid:{}", uid, toUid);
            return BizKit.fail("relation status error");
        }
        followService.update(new LambdaUpdateWrapper<Follow>().eq(Follow::getUid, uid).eq(Follow::getToUid,toUid).set(Follow::getIsBlock, false));
        return BizKit.ok();
    }

}

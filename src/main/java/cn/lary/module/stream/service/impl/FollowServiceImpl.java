package cn.lary.module.stream.service.impl;

import cn.lary.core.context.RequestContext;
import cn.lary.core.dto.ResponsePair;
import cn.lary.kit.BusinessKit;
import cn.lary.module.common.cache.KVBuilder;
import cn.lary.module.common.cache.RedisCache;
import cn.lary.module.common.constant.LARY;
import cn.lary.module.message.dto.follow.ActiveFollowResponseDTO;
import cn.lary.module.message.dto.follow.OneWayActiveFollowResponseDTO;
import cn.lary.module.message.dto.follow.PassiveFollowResponseDTO;
import cn.lary.module.message.service.MessageService;
import cn.lary.module.stream.dto.FollowDTO;
import cn.lary.module.stream.dto.FollowPageQueryDTO;
import cn.lary.module.stream.dto.LiveCacheDTO;
import cn.lary.module.stream.entity.Follow;
import cn.lary.module.stream.mapper.FollowMapper;
import cn.lary.module.stream.service.FollowService;
import cn.lary.module.user.entity.User;
import cn.lary.module.user.service.UserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author paul
 * @since 2024-09-04
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow> implements FollowService {
    
    private final KVBuilder kvBuilder;
    private final RedisCache redisCache;
    private final UserService userService;
    private final MessageService messageService;
    private final TransactionTemplate transactionTemplate;

    @Override
    public ResponsePair<List<Integer>> getFollows(int uid) {
        return null;
    }



    @Override
    public void addSystemHelper(int uid) {
        save(new Follow()
                .setUid(uid)
                // TODO  : 这里实现下系统账户
                .setToUid(111222333));
    }

    @Override
    public ResponsePair<Void> follow(FollowDTO dto) {
        int uid = RequestContext.getLoginUID();
        String name = RequestContext.getLoginName();
        int toUid = dto.getToUid();


        User aimUser = userService.lambdaQuery()
                .select(User::getIsAnchor)
                .select(User::getUid)
                .eq(User::getUid, uid)
                .one();
        if (aimUser == null) {
            return BusinessKit.fail("用户不存在");
        }
        if (aimUser.getStatus() == LARY.UserStatus.ban) {
            return BusinessKit.fail("用户已被封禁");
        }
        Follow reverseRelation = lambdaQuery()
                .eq(Follow::getUid, toUid)
                .eq(Follow::getToUid, uid)
                .eq(Follow::getIsDelete, false)
                .one();
        boolean isAlone = true;
        if (reverseRelation != null) {
            if (reverseRelation.getIsBlock()) {
                return BusinessKit.fail("对方拉黑了你");
            }
            if (!reverseRelation.getIsUnfollow()) {
                isAlone = false;
            }
        }
        ResponsePair<Void> pair = transactionTemplate.execute(status -> {
                    Follow relation = lambdaQuery()
                            .eq(Follow::getUid, uid)
                            .eq(Follow::getToUid, toUid)
                            .eq(Follow::getIsDelete, false)
                            .one();
                    if (relation != null) {
                        if (relation.getIsBlock()) {
                            return BusinessKit.fail("你已拉黑对方,请先移出黑名单");
                        }
                        if (relation.getIsUnfollow()) {
                            lambdaUpdate()
                                    .eq(Follow::getUid, uid)
                                    .eq(Follow::getToUid, toUid)
                                    .set(Follow::getIsUnfollow, false);
                            return BusinessKit.ok();
                        }
                    }
                    Follow followApply = new Follow()
                            .setUid(uid)
                            .setToUid(toUid)
                            .setUsername(aimUser.getName())
                            .setBio(aimUser.getBio())
                            .setAvatar(aimUser.getAvatar())
                            .setSource(dto.getCode())
                            .setIsAnchor(aimUser.getIsAnchor());
                    save(followApply);
                    return BusinessKit.ok();
                }
        );
        assert pair != null;
        if (pair.isFail()) {
            return BusinessKit.fail(pair.getMsg());
        }
        if (aimUser.getIsAnchor() && dto.getCode() == LARY.FollowCode.stream) {
            Map<Object, Object> liveInfo = redisCache.getHash(kvBuilder.goLiveK(toUid));
            if (liveInfo != null) {
                LiveCacheDTO cache = LiveCacheDTO.of(liveInfo);
                Map<Object, Object> streamRecord = redisCache.getHash(kvBuilder.streamRecordK(toUid, cache.getStreamId()));
                if (streamRecord == null) {
                    return BusinessKit.fail("stream record not exist");
                }
                redisCache.incrHash(kvBuilder.streamRecordK(toUid,cache.getStreamId()),"newFansNum");
            }
        }
        // send message
        if (!isAlone) {
           messageService.send(new ActiveFollowResponseDTO(uid,toUid,name));
           messageService.send(new PassiveFollowResponseDTO(toUid,uid));
        }else {
            messageService.send(new OneWayActiveFollowResponseDTO(uid,toUid,name));
        }
        return BusinessKit.ok();
    }

    @Override
    public ResponsePair<Void> block(int toUid) {
        int uid = RequestContext.getLoginUID();
        Follow relation = lambdaQuery()
                .eq(Follow::getUid, uid)
                .eq(Follow::getToUid, toUid)
                .eq(Follow::getIsDelete, false)
                .one();
        User toUser = userService.lambdaQuery()
                .select(User::getIsAnchor)
                .select(User::getUid)
                .select(User::getAvatar)
                .select(User::getName)
                .select(User::getBio)
                .eq(User::getUid, toUid)
                .eq(User::getIsDelete, false)
                .eq(User::getStatus, LARY.UserStatus.ok)
                .one();
        if ( toUser == null) {
            return BusinessKit.fail("user status error");
        }
        if (relation == null) {
            save(new Follow()
                    .setUid(uid)
                    .setToUid(toUid)
                    .setIsBlock(true)
                    .setAvatar(toUser.getAvatar())
                    .setBio(toUser.getBio())
                    .setUsername(toUser.getName()));
            return BusinessKit.ok();
        }
        lambdaUpdate()
                .eq(Follow::getUid, uid)
                .eq(Follow::getToUid,toUid)
                .set(Follow::getIsBlock, true);
        return BusinessKit.ok();
    }

    @Override
    public ResponsePair<Void> unblock(int toUid) {
        int uid = RequestContext.getLoginUID();
        Follow relation = lambdaQuery()
                .eq(Follow::getUid, uid)
                .eq(Follow::getToUid, toUid)
                .eq(Follow::getIsDelete, false)
                .one();
        if (relation == null) {
            log.error("no block record exist,uid:{},toUid:{}", uid, toUid);
            return BusinessKit.fail("relation status error");
        }
        lambdaUpdate()
                .eq(Follow::getUid, uid)
                .eq(Follow::getToUid,toUid)
                .set(Follow::getIsBlock, false);
        return BusinessKit.ok();
    }

    @Override
    public ResponsePair<Void> unfollow(int toUid) {
        int uid = RequestContext.getLoginUID();
        Follow relation = lambdaQuery()
                .eq(Follow::getUid, uid)
                .eq(Follow::getToUid, toUid)
                .eq(Follow::getIsDelete, false)
                .one();
        if (relation == null) {
            return BusinessKit.fail("request error");
        }
        Follow reverse = lambdaQuery()
                .eq(Follow::getUid, toUid)
                .eq(Follow::getToUid, uid)
                .eq(Follow::getIsDelete, false)
                .one();
        User aimUser = userService.lambdaQuery()
                .select(User::getIsAnchor)
                .eq(User::getUid, toUid)
                .eq(User::getIsDelete, false)
                .one();
        if (aimUser == null) {
            return BusinessKit.fail("user not exist");
        }
        transactionTemplate.executeWithoutResult(status -> {
            if (reverse != null
                    &&!reverse.getIsUnfollow()
                    && !reverse.getIsBlock()
                    && reverse.getIsOneWay()) {
                lambdaUpdate()
                        .eq(Follow::getUid,toUid)
                        .eq(Follow::getToUid, uid)
                        .set(Follow::getIsOneWay,true);
            }
            lambdaUpdate()
                    .eq(Follow::getUid, uid)
                    .eq(Follow::getToUid,toUid)
                    .set(Follow::getIsUnfollow, true);
        });
        if (aimUser.getIsAnchor() ) {
            Map<Object, Object> liveInfo = redisCache.getHash(kvBuilder.goLiveK(toUid));
            if (liveInfo != null) {
                LiveCacheDTO cache = LiveCacheDTO.of(liveInfo);
                Map<Object, Object> streamRecord = redisCache.getHash(kvBuilder.streamRecordK(toUid, cache.getStreamId()));
                if (streamRecord == null) {
                    return BusinessKit.fail("stream record not exist");
                }
                redisCache.decrHash(kvBuilder.streamRecordK(toUid,cache.getStreamId()),"newFansNum");
            }
        }
        return BusinessKit.ok();
    }

    @Override
    public ResponsePair<List<Follow>> follows( FollowPageQueryDTO dto) {

        return BusinessKit.ok(lambdaQuery()
                .eq(Follow::getUid, RequestContext.getLoginUID())
                .eq(Follow::getIsUnfollow, false)
                .eq(Follow::getIsBlock, false)
                .eq(Follow::getIsDelete, false)
                .orderByDesc(Follow::getUpdateAt)
                .page(new Page<>(dto.getPageIndex(), dto.getPageSize()))
                .getRecords());
    }
}

package cn.lary.module.stream.service.impl;

import cn.lary.common.context.RequestContext;
import cn.lary.common.dto.ResponsePair;
import cn.lary.common.kit.BusinessKit;
import cn.lary.module.common.cache.KVBuilder;
import cn.lary.module.common.cache.CacheComponent;
import cn.lary.module.common.constant.LARY;
import cn.lary.module.message.dto.follow.ActiveFollowResponseDTO;
import cn.lary.module.message.dto.follow.OneWayActiveFollowResponseDTO;
import cn.lary.module.message.dto.follow.PassiveFollowResponseDTO;
import cn.lary.module.message.service.MessageService;
import cn.lary.module.stream.dto.FollowDTO;
import cn.lary.module.stream.dto.FollowPageQueryDTO;
import cn.lary.module.cache.dto.LiveCache;
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
    private final CacheComponent cacheComponent;
    private final UserService userService;
    private final MessageService messageService;
    private final TransactionTemplate transactionTemplate;

    @Override
    public ResponsePair<List<Integer>> getFollows(long uid) {
        return null;
    }



    @Override
    public void addSystemHelper(long uid) {
        save(new Follow()
                .setUid(uid)
                .setToUid(780246889L));
    }

    @Override
    public ResponsePair<Void> follow(FollowDTO dto) {
        long uid = RequestContext.getLoginUID();
        String name = RequestContext.getLoginName();
        long toUid = dto.getToUid();


        User aimUser = userService.lambdaQuery()
                .select(User::getRole)
                .select(User::getUid)
                .eq(User::getUid, uid)
                .one();
        if (aimUser == null) {
            return BusinessKit.fail("用户不存在");
        }
        if (aimUser.getStatus() == LARY.USER.STATUS.BAN) {
            return BusinessKit.fail("用户已被封禁");
        }
        Follow reverseRelation = lambdaQuery()
                .eq(Follow::getUid, toUid)
                .eq(Follow::getToUid, uid)
                .eq(Follow::getIsDelete, false)
                .one();
        boolean isAlone = true;
        if (reverseRelation != null) {
            if (reverseRelation.getStatus() == LARY.FOLLOW.STATUS.BLOCK) {
                return BusinessKit.fail("对方拉黑了你");
            }
            if (reverseRelation.getStatus() == LARY.FOLLOW.STATUS.UNFOLLOW) {
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
                        if (relation.getStatus() == LARY.FOLLOW.STATUS.BLOCK) {
                            return BusinessKit.fail("你已拉黑对方,请先移出黑名单");
                        }
                        if (relation.getStatus() == LARY.FOLLOW.STATUS.UNFOLLOW) {
                            lambdaUpdate()
                                    .eq(Follow::getUid, uid)
                                    .eq(Follow::getToUid, toUid)
                                    .set(Follow::getStatus, LARY.FOLLOW.STATUS.COMMON);
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
                            .setStatus(LARY.FOLLOW.STATUS.COMMON);
                    save(followApply);
                    return BusinessKit.ok();
                }
        );
        assert pair != null;
        if (pair.isFail()) {
            return BusinessKit.fail(pair.getMsg());
        }
        if (aimUser.getRole() == LARY.FOLLOW.ROLE.ANCHOR && dto.getCode() == LARY.FOLLOW.CODE.STREAM) {
            Map<Object, Object> liveInfo = cacheComponent.getHash(kvBuilder.goLiveK(toUid));
            if (liveInfo != null) {
                LiveCache cache = LiveCache.of(liveInfo);
                Map<Object, Object> streamRecord = cacheComponent.getHash(kvBuilder.streamRecordK(toUid, cache.getStreamId()));
                if (streamRecord == null) {
                    return BusinessKit.fail("stream record not exist");
                }
                cacheComponent.incrHash(kvBuilder.streamRecordK(toUid,cache.getStreamId()),"newFansNum");
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
    public ResponsePair<Void> block(long toUid) {
        long uid = RequestContext.getLoginUID();
        Follow relation = lambdaQuery()
                .eq(Follow::getUid, uid)
                .eq(Follow::getToUid, toUid)
                .eq(Follow::getIsDelete, false)
                .one();
        User toUser = userService.lambdaQuery()
                .select(User::getUid,User::getAvatar)
                .select(User::getName,User::getBio)
                .eq(User::getUid, toUid)
                .one();
        if ( toUser == null || toUser.getStatus() == LARY.USER.STATUS.BAN) {
            return BusinessKit.fail("user status error");
        }
        if (relation == null) {
            save(new Follow()
                    .setUid(uid)
                    .setToUid(toUid)
                    .setStatus(LARY.FOLLOW.STATUS.BLOCK)
                    .setAvatar(toUser.getAvatar())
                    .setBio(toUser.getBio())
                    .setUsername(toUser.getName()));
            return BusinessKit.ok();
        }
        lambdaUpdate()
                .eq(Follow::getUid, uid)
                .eq(Follow::getToUid,toUid)
                .set(Follow::getStatus, LARY.FOLLOW.STATUS.BLOCK);
        return BusinessKit.ok();
    }

    @Override
    public ResponsePair<Void> unblock(long toUid) {
        long uid = RequestContext.getLoginUID();
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
                .set(Follow::getStatus, LARY.FOLLOW.STATUS.COMMON);
        return BusinessKit.ok();
    }

    @Override
    public ResponsePair<Void> unfollow(long toUid) {
        long uid = RequestContext.getLoginUID();
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
                .select(User::getRole)
                .eq(User::getUid, toUid)
                .eq(User::getIsDelete, false)
                .one();
        if (aimUser == null) {
            return BusinessKit.fail("user not exist");
        }
        transactionTemplate.executeWithoutResult(status -> {
            lambdaUpdate()
                    .eq(Follow::getUid, uid)
                    .eq(Follow::getToUid,toUid)
                    .set(Follow::getStatus, LARY.FOLLOW.STATUS.UNFOLLOW);
        });
        if (aimUser.getRole() == LARY.FOLLOW.ROLE.ANCHOR) {
            Map<Object, Object> liveInfo = cacheComponent.getHash(kvBuilder.goLiveK(toUid));
            if (liveInfo != null) {
                LiveCache cache = LiveCache.of(liveInfo);
                Map<Object, Object> streamRecord = cacheComponent.getHash(kvBuilder.streamRecordK(toUid, cache.getStreamId()));
                if (streamRecord == null) {
                    return BusinessKit.fail("stream record not exist");
                }
                cacheComponent.decrHash(kvBuilder.streamRecordK(toUid,cache.getStreamId()),"newFansNum");
            }
        }
        return BusinessKit.ok();
    }

    @Override
    public ResponsePair<List<Follow>> follows( FollowPageQueryDTO dto) {

        return BusinessKit.ok(lambdaQuery()
                .eq(Follow::getUid, RequestContext.getLoginUID())
                .orderByDesc(Follow::getUpdateAt)
                .page(new Page<>(dto.getPageIndex(), dto.getPageSize()))
                .getRecords());
    }
}

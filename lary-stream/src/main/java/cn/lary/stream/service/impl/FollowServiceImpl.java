package cn.lary.stream.service.impl;

import cn.lary.common.context.RequestContext;
import cn.lary.common.dto.ResponsePair;
import cn.lary.common.kit.BusinessKit;
import cn.lary.stream.constant.FOLLOW;
import cn.lary.stream.dto.FollowDTO;
import cn.lary.stream.dto.FollowPageQueryDTO;
import cn.lary.stream.entity.Follow;
import cn.lary.stream.mapper.FollowMapper;
import cn.lary.stream.service.FollowService;
import cn.lary.stream.vo.FollowVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

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

//    private final UserService userService;
//    private final MessageService messageService;
    private final TransactionTemplate transactionTemplate;

    @Override
    public void addSystemHelper(long uid) {
        save(new Follow()
                .setUid(uid)
                .setToUid(780246889L));
    }

    @Override
    public ResponsePair<Void> follow(FollowDTO dto) {
//        long uid = RequestContext.uid();
//        String name = RequestContext.name();
//        long toUid = dto.getToUid();
//        User aimUser = userService.lambdaQuery()
//                .select(User::getRole)
//                .select(User::getUid)
//                .eq(User::getUid, uid)
//                .one();
//        if (aimUser == null) {
//            return BusinessKit.fail("h not exists");
//        }
//        if (aimUser.getStatus() == USER.STATUS.BAN) {
//            return BusinessKit.fail("h been blocked");
//        }
//        Follow reverseRelation = lambdaQuery()
//                .eq(Follow::getUid, toUid)
//                .eq(Follow::getToUid, uid)
//                .one();
//        boolean oneWay = true;
//        if (reverseRelation != null) {
//            if (reverseRelation.getStatus() == LARY.FOLLOW.STATUS.BLOCK) {
//                return BusinessKit.fail("h block u");
//            }
//            if (reverseRelation.getStatus() == LARY.FOLLOW.STATUS.UNFOLLOW) {
//                oneWay = false;
//            }
//        }
//        ResponsePair<Void> pair = transactionTemplate.execute(status -> {
//                    Follow relation = lambdaQuery()
//                            .select(Follow::getToUid,Follow::getUid,Follow::getStatus)
//                            .eq(Follow::getUid, uid)
//                            .eq(Follow::getToUid, toUid)
//                            .one();
//                    if (relation != null) {
//                        if (relation.getStatus() == LARY.FOLLOW.STATUS.BLOCK) {
//                            return BusinessKit.fail("u block him,please remove the blacklist first");
//                        }
//                        if (relation.getStatus() == LARY.FOLLOW.STATUS.UNFOLLOW) {
//                            lambdaUpdate()
//                                    .eq(Follow::getUid, uid)
//                                    .eq(Follow::getToUid, toUid)
//                                    .set(Follow::getStatus, LARY.FOLLOW.STATUS.COMMON)
//                                    .update();
//                            return BusinessKit.ok();
//                        }
//                    }
//                    Follow followApply = new Follow()
//                            .setUid(uid)
//                            .setToUid(toUid)
//                            .setUsername(aimUser.getUsername())
//                            .setBio(aimUser.getBio())
//                            .setAvatar(aimUser.getAvatar())
//                            .setSource(dto.getCode())
//                            .setStatus(LARY.FOLLOW.STATUS.COMMON);
//                    save(followApply);
//                    return BusinessKit.ok();
//                }
//        );
//        if (pair == null ){
//            log.error("follow internal server error,uid:{}",uid);
//            return BusinessKit.fail("internal server error");
//        }
//        if (pair.isFail()) {
//            return BusinessKit.fail(pair.getMsg());
//        }
//        if (!oneWay) {
//           messageService.send(new ActiveFollowResponseDTO(uid,toUid,name));
//           messageService.send(new PassiveFollowResponseDTO(toUid,uid));
//        }else {
//            messageService.send(new OneWayActiveFollowResponseDTO(uid,toUid,name));
//        }
        return BusinessKit.ok();
    }

    @Override
    public ResponsePair<Void> block(long toUid) {
        long uid = RequestContext.uid();
        return transactionTemplate.execute(status -> {
            Follow relation = lambdaQuery()
                    .eq(Follow::getUid, uid)
                    .eq(Follow::getToUid, toUid)
                    .one();
//            User toUser = userService.lambdaQuery()
//                    .select(User::getUid,User::getAvatar,
//                            User::getUsername,User::getBio)
//                    .eq(User::getUid, toUid)
//                    .one();
//            if ( toUser == null || toUser.getStatus() == USER.STATUS.BAN) {
//                return BusinessKit.fail("user status error");
//            }
//            if (relation == null) {
//                save(new Follow()
//                        .setUid(uid)
//                        .setToUid(toUid)
//                        .setStatus(LARY.FOLLOW.STATUS.BLOCK)
//                        .setAvatar(toUser.getAvatar())
//                        .setBio(toUser.getBio())
//                        .setUsername(toUser.getUsername()));
//                return BusinessKit.ok();
//            }
            lambdaUpdate()
                    .eq(Follow::getUid, uid)
                    .eq(Follow::getToUid,toUid)
                    .set(Follow::getStatus, FOLLOW.STATUS.BLOCK);
            return BusinessKit.ok();
        });
    }

    @Override
    public ResponsePair<Void> unblock(long toUid) {

        long uid = RequestContext.uid();
        return transactionTemplate.execute(status -> {
            Follow relation = lambdaQuery()
                    .select(Follow::getToUid,Follow::getUid)
                    .eq(Follow::getUid, uid)
                    .eq(Follow::getToUid, toUid)
                    .one();
            if (relation == null) {
                log.error("no block record exist,uid:{},toUid:{}", uid, toUid);
                return BusinessKit.fail("relation status error");
            }
            lambdaUpdate()
                    .eq(Follow::getUid, uid)
                    .eq(Follow::getToUid,toUid)
                    .set(Follow::getStatus, FOLLOW.STATUS.COMMON)
                    .update();
            return BusinessKit.ok();
        });
    }

    @Override
    public ResponsePair<Void> unfollow(long toUid) {
        long uid = RequestContext.uid();
        return transactionTemplate.execute(status -> {
            Follow relation = lambdaQuery()
                    .eq(Follow::getUid, uid)
                    .eq(Follow::getToUid, toUid)
                    .one();
            if (relation == null) {
                return BusinessKit.fail("request error");
            }
            lambdaUpdate()
                    .eq(Follow::getUid, uid)
                    .eq(Follow::getToUid,toUid)
                    .set(Follow::getStatus, FOLLOW.STATUS.UNFOLLOW)
                    .update();
            return BusinessKit.ok();
        });
    }

    @Override
    public ResponsePair<List<FollowVO>> follows(FollowPageQueryDTO dto) {

        return BusinessKit.ok(lambdaQuery()
                .eq(Follow::getUid, RequestContext.uid())
                .orderByDesc(Follow::getCreateAt)
                .page(new Page<>(dto.getPageIndex(), dto.getPageSize()))
                .getRecords()
                .stream()
                .map(FollowVO::new)
                .toList());

    }
}

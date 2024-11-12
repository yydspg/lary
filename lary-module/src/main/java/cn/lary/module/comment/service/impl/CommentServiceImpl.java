package cn.lary.module.comment.service.impl;

import cn.lary.common.context.RequestContext;
import cn.lary.common.dto.ResponsePair;
import cn.lary.common.kit.BusinessKit;
import cn.lary.common.kit.CollectionKit;
import cn.lary.common.kit.StringKit;
import cn.lary.module.comment.component.CommentCacheComponent;
import cn.lary.module.comment.dto.CommentEventCacheDTO;
import cn.lary.module.comment.dto.RootCommentDTO;
import cn.lary.module.comment.dto.RootCommentPageQueryDTO;
import cn.lary.module.comment.entity.Comment;
import cn.lary.module.comment.entity.CommentEvent;
import cn.lary.module.comment.entity.MentionNotifyPayload;
import cn.lary.module.comment.mapper.CommentMapper;
import cn.lary.module.comment.service.CommentEventService;
import cn.lary.module.comment.service.CommentService;
import cn.lary.module.comment.vo.RootCommentVO;
import cn.lary.module.common.constant.LARY;
import cn.lary.module.id.LaryIdGenerator;
import cn.lary.module.id.SystemClock;
import cn.lary.module.message.dto.comment.CommentMentionMessage;
import cn.lary.module.message.service.MessageService;
import cn.lary.module.user.entity.User;
import cn.lary.module.user.service.UserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.houbb.sensitive.word.core.SensitiveWordHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author paul
 * @since 2024-11-10
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    private final CommentCacheComponent commentCacheComponent;
    private final CommentEventService commentEventService;
    private final MessageService messageService;
    private final LaryIdGenerator idGenerator;
    private final UserService userService;
    private final TransactionTemplate transactionTemplate;

    @Override
    public ResponsePair<Void> comment(RootCommentDTO dto) {
        long uid = RequestContext.uid();
        String name = RequestContext.name();
        long eid = dto.getEid();
        CommentEventCacheDTO cache = commentCacheComponent.getEvent(eid);
        if (cache == null) {
            CommentEvent event = commentEventService.lambdaQuery()
                    .select(CommentEvent::getEid,CommentEvent::getUid)
                    .select(CommentEvent::getTid,CommentEvent::getContent)
                    .eq(CommentEvent::getEid, eid)
                    .one();
            if (event == null) {
                return BusinessKit.fail("event not exist");
            }
            cache = new CommentEventCacheDTO()
                    .setEid(eid)
                    .setContent(event.getContent());
        }
        if (cache.getEid() != eid) {
            return BusinessKit.fail("eid invalid");
        }
        if (SensitiveWordHelper.contains(dto.getContent())) {
            return BusinessKit.fail("content invalid");
        }
        long cid = idGenerator.next();
        User user = userService.lambdaQuery()
                .select(User::getAvatar)
                .eq(User::getUid, uid)
                .one();
        if (user == null) {
            return BusinessKit.fail("user not exist");
        }
        Comment comment = new Comment()
                .setUid(uid)
                .setCid(cid)
                .setContent(dto.getContent())
                .setName(name)
                .setAvatar(user.getAvatar());
        save(comment);
        String mentions = dto.getMentions();
        if (StringKit.isNotEmpty(mentions)) {
            String[] users = StringKit.split(mentions, ",");
            if(users == null || users.length == 0){
                return BusinessKit.fail("mentions invalid");
            }
            List<Long> ids = Arrays.stream(users).map(Long::valueOf).distinct().toList();
            if (CollectionKit.isEmpty(ids)) {
                return BusinessKit.fail("mentions invalid");
            }
            List<Long> mentionUsers = userService.lambdaQuery()
                    .select(User::getUid)
                    .select(User::getStatus, User::getIsDelete)
                    .in(User::getUid, ids)
                    .list()
                    .stream()
                    .filter(t -> t.getStatus() == LARY.STATUS.COMMON && !t.getIsDelete())
                    .distinct()
                    .map(User::getUid)
                    .toList();
            MentionNotifyPayload payload = new MentionNotifyPayload()
                    .setEid(eid)
                    .setTimestamp(SystemClock.now());
            messageService.send(new CommentMentionMessage(mentionUsers,uid,payload));
        }
        return BusinessKit.ok();
    }

    @Override
    public ResponsePair<Void> hide(long cid) {

        return transactionTemplate.execute(status -> {
            Comment comment = lambdaQuery()
                    .select(Comment::getEid, Comment::getUid)
                    .eq(Comment::getCid, cid)
                    .one();
            if (comment == null) {
                return BusinessKit.fail("comment not exist");
            }
            if (comment.getUid() != RequestContext.uid()) {
                return BusinessKit.fail("comment uid invalid");
            }
            lambdaUpdate()
                    .set(Comment::getStatus, LARY.COMMENT.STATUS.HIDE)
                    .eq(Comment::getCid, cid)
                    .update();
            return BusinessKit.ok();
        });

    }

    @Override
    public ResponsePair<List<RootCommentVO>> show(RootCommentPageQueryDTO dto) {
        long eid = dto.getEid();
        CommentEventCacheDTO cache = commentCacheComponent.getEvent(dto.getEid());
        if (cache == null) {
            CommentEvent event = commentEventService.lambdaQuery()
                    .select(CommentEvent::getEid,CommentEvent::getUid)
                    .eq(CommentEvent::getEid ,eid )
                    .one();
            if (event == null) {
                return BusinessKit.fail("event not exist");
            }
        }
        List<RootCommentVO> vo = lambdaQuery()
                .select(Comment::getCid, Comment::getUid)
                .select(Comment::getImages, Comment::getMentions)
                .select(Comment::getReplyCount, Comment::getStarCount)
                .select(Comment::getContent, Comment::getStatus)
                .eq(Comment::getEid, eid)
                .page(new Page<>(dto.getPageIndex(), dto.getPageSize()))
                .getRecords()
                .stream()
                .map(RootCommentVO::new)
                .toList();
        return BusinessKit.ok(vo);
    }
}

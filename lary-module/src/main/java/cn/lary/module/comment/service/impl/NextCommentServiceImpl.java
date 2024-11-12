package cn.lary.module.comment.service.impl;

import cn.lary.common.context.RequestContext;
import cn.lary.common.dto.ResponsePair;
import cn.lary.common.kit.BusinessKit;
import cn.lary.common.kit.CollectionKit;
import cn.lary.common.kit.StringKit;
import cn.lary.module.comment.component.CommentCacheComponent;
import cn.lary.module.comment.dto.CommentEventCacheDTO;
import cn.lary.module.comment.dto.NextCommentDTO;
import cn.lary.module.comment.dto.NextCommentPageQueryDTO;
import cn.lary.module.comment.entity.CommentEvent;
import cn.lary.module.comment.entity.MentionNotifyPayload;
import cn.lary.module.comment.entity.NextComment;
import cn.lary.module.comment.mapper.NextCommentMapper;
import cn.lary.module.comment.service.CommentEventService;
import cn.lary.module.comment.service.NextCommentService;
import cn.lary.module.comment.vo.NextCommentVO;
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
public class NextCommentServiceImpl extends ServiceImpl<NextCommentMapper, NextComment> implements NextCommentService {

    private final CommentEventService commentEventService;
    private final CommentCacheComponent commentCacheComponent;
    private final LaryIdGenerator idGenerator;
    private final UserService userService;
    private final GeneralService generalService;
    private final TransactionTemplate transactionTemplate;
    @Override
    public ResponsePair<Void> comment(NextCommentDTO dto) {

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
        NextComment comment = new NextComment()
                .setUid(uid)
                .setCid(cid)
                .setContent(dto.getContent())
                .setName(name)
                .setAvatar(user.getAvatar());
        save(comment);
        ResponsePair<Void> pair = generalService.processUserMention(dto.getMentions(), dto.getContent(), dto.getEid());
        if (pair.isFail()){
            return pair;
        }
        return BusinessKit.ok();
    }

    @Override
    public ResponsePair<Void> hide(long cid) {
        return transactionTemplate.execute(status -> {
            NextComment comment = lambdaQuery()
                    .select(NextComment::getUid,NextComment::getCid)
                    .one();
            if (comment == null) {
                return BusinessKit.fail("comment not exist");
            }
            if (comment.getUid() != RequestContext.uid()) {
                return BusinessKit.fail("comment uid invalid");
            }
            lambdaUpdate()
                    .set(NextComment::getStatus, LARY.COMMENT.STATUS.HIDE)
                    .eq(NextComment::getCid, cid)
                    .update();
            return BusinessKit.ok();
        });
    }



    @Override
    public ResponsePair<List<NextCommentVO>> show(NextCommentPageQueryDTO dto) {
        long eid = dto.getRid();
        List<NextCommentVO> vo = lambdaQuery()
                .select(NextComment::getCid, NextComment::getUid)
                .select(NextComment::getImages, NextComment::getMentions)
                .select(NextComment::getReplyCount, NextComment::getStarCount)
                .select(NextComment::getContent, NextComment::getStatus)
                .eq(NextComment::getRid, eid)
                .page(new Page<>(dto.getPageIndex(), dto.getPageSize()))
                .getRecords()
                .stream()
                .map(NextCommentVO::new)
                .toList();
        return BusinessKit.ok(vo);
    }
}

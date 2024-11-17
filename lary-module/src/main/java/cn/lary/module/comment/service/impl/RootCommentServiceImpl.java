package cn.lary.module.comment.service.impl;

import cn.lary.common.context.RequestContext;
import cn.lary.common.dto.ResponsePair;
import cn.lary.common.kit.BusinessKit;
import cn.lary.common.kit.StringKit;
import cn.lary.module.comment.component.CommentCacheComponent;
import cn.lary.module.comment.dto.CommentEventCacheDTO;
import cn.lary.module.comment.dto.RootCommentDTO;
import cn.lary.module.comment.dto.RootCommentPageQueryDTO;
import cn.lary.module.comment.entity.RootComment;
import cn.lary.module.comment.entity.CommentEvent;
import cn.lary.module.comment.mapper.RootCommentMapper;
import cn.lary.module.comment.service.CommentEventService;
import cn.lary.module.comment.service.RootCommentService;
import cn.lary.module.comment.vo.RootCommentVO;
import cn.lary.module.common.constant.LARY;
import cn.lary.module.id.LaryIDBuilder;
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
public class RootCommentServiceImpl extends ServiceImpl<RootCommentMapper, RootComment> implements RootCommentService {

    private final CommentCacheComponent commentCacheComponent;
    private final CommentEventService commentEventService;
    private final MessageService messageService;
    private final GeneralService generalService;
    private final LaryIDBuilder idGenerator;
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
        RootComment comment = new RootComment()
                .setUid(uid)
                .setCid(cid)
                .setContent(dto.getContent())
                .setName(name)
                .setAvatar(user.getAvatar());
        save(comment);
        String mentions = dto.getMentions();
        if (StringKit.isNotEmpty(mentions)) {
            ResponsePair<Void> pair = generalService.processUserMention(mentions, dto.getContent(), dto.getEid());
            if (pair.isFail()){
                return pair;
            }
        }
        return BusinessKit.ok();
    }

    @Override
    public ResponsePair<Void> hide(long cid) {

        return transactionTemplate.execute(status -> {
            RootComment comment = lambdaQuery()
                    .select(RootComment::getEid, RootComment::getUid)
                    .eq(RootComment::getCid, cid)
                    .one();
            if (comment == null) {
                return BusinessKit.fail("comment not exist");
            }
            if (comment.getUid() != RequestContext.uid()) {
                return BusinessKit.fail("comment uid invalid");
            }
            lambdaUpdate()
                    .set(RootComment::getStatus, LARY.COMMENT.STATUS.HIDE)
                    .eq(RootComment::getCid, cid)
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
                .select(RootComment::getCid, RootComment::getUid)
                .select(RootComment::getImages, RootComment::getMentions)
                .select(RootComment::getReplyCount, RootComment::getStarCount)
                .select(RootComment::getContent, RootComment::getStatus)
                .eq(RootComment::getEid, eid)
                .page(new Page<>(dto.getPageIndex(), dto.getPageSize()))
                .getRecords()
                .stream()
                .map(RootCommentVO::new)
                .toList();
        return BusinessKit.ok(vo);
    }
}

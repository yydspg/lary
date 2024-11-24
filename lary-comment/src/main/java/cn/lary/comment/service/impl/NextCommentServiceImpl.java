package cn.lary.comment.service.impl;

import cn.lary.comment.component.CommentCacheComponent;
import cn.lary.comment.constant.COMMENT;
import cn.lary.comment.dto.CommentEventCacheDTO;
import cn.lary.comment.dto.NextCommentDTO;
import cn.lary.comment.dto.NextCommentPageQueryDTO;
import cn.lary.comment.entity.CommentEvent;
import cn.lary.comment.entity.NextComment;
import cn.lary.comment.mapper.NextCommentMapper;
import cn.lary.comment.service.CommentEventService;
import cn.lary.comment.service.NextCommentService;
import cn.lary.comment.vo.NextCommentVO;
import cn.lary.common.context.RequestContext;
import cn.lary.common.dto.ResponsePair;
import cn.lary.common.id.LaryIDBuilder;
import cn.lary.common.kit.BusinessKit;
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
public class NextCommentServiceImpl extends ServiceImpl<NextCommentMapper, NextComment> implements NextCommentService {

    private final CommentEventService commentEventService;
    private final CommentCacheComponent commentCacheComponent;
    private final LaryIDBuilder idGenerator;
//    private final UserService userService;
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
//        User user = userService.lambdaQuery()
//                .select(User::getAvatar)
//                .eq(User::getUid, uid)
//                .one();
//        if (user == null) {
//            return BusinessKit.fail("user not exist");
//        }
        NextComment comment = new NextComment()
                .setUid(uid)
                .setCid(cid)
                .setContent(dto.getContent())
                .setName(name)
                .setAvatar(null);
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
                    .eq(NextComment::getCid, cid)
                    .one();
            if (comment == null) {
                return BusinessKit.fail("comment not exist");
            }
            if (comment.getUid() != RequestContext.uid()) {
                return BusinessKit.fail("comment uid invalid");
            }
            lambdaUpdate()
                    .set(NextComment::getStatus, COMMENT.STATUS.HIDE)
                    .eq(NextComment::getCid, cid)
                    .update();
            return BusinessKit.ok();
        });
    }



    @Override
    public ResponsePair<List<NextCommentVO>> show(NextCommentPageQueryDTO dto) {
        long rid = dto.getRid();
        return BusinessKit.ok(lambdaQuery()
                .select(NextComment::getCid, NextComment::getUid,
                        NextComment::getContent, NextComment::getStatus,
                        NextComment::getReplyCount, NextComment::getStarCount,
                        NextComment::getImages, NextComment::getMentions)
                .eq(NextComment::getRid, rid)
                .page(new Page<>(dto.getPageIndex(), dto.getPageSize()))
                .getRecords()
                .stream()
                .map(NextCommentVO::new)
                .toList());
    }
}

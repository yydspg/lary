package cn.lary.comment.service.impl;

import cn.lary.api.comment.dto.CommentEventCacheDTO;
import cn.lary.api.comment.dto.CommentEventDTO;
import cn.lary.comment.component.CommentCacheComponent;
import cn.lary.comment.constant.COMMENT;
import cn.lary.api.comment.entity.CommentEvent;
import cn.lary.comment.mapper.CommentEventMapper;
import cn.lary.comment.service.CommentEventService;
import cn.lary.common.context.RequestContext;
import cn.lary.common.dto.ResponsePair;
import cn.lary.common.id.LaryIDBuilder;
import cn.lary.common.kit.BusinessKit;
import cn.lary.common.kit.StringKit;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.houbb.sensitive.word.core.SensitiveWordHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author paul
 * @since 2024-11-10
 */
@Service
@RequiredArgsConstructor
public class CommentEventServiceImpl extends ServiceImpl<CommentEventMapper, CommentEvent> implements CommentEventService {

    private final int UNKNOWN = -1;
    private final LaryIDBuilder builder;
    private final CommentCacheComponent commentCacheComponent;

    @Override
    public ResponsePair<Void> event(CommentEventDTO dto){
        int tid = getEventId(dto.getTid());
        if(tid == UNKNOWN){
            return BusinessKit.fail("event type unknown");
        }
        String badWord = SensitiveWordHelper.findFirst(dto.getContent());
        if(StringKit.isNotEmpty(badWord)) {
            return BusinessKit.fail("contains illegal word");
        }
        long uid = RequestContext.uid();
        long eid = builder.next();
        save(new CommentEvent()
                .setUid(uid)
                .setEid(eid)
                .setContent(dto.getContent())
                .setTid(tid));
        commentCacheComponent.setEvent(new CommentEventCacheDTO()
                .setTid(tid)
                .setContent(dto.getContent())
                .setUid(uid)
                .setEid(eid));
        return BusinessKit.ok();
    }


    public int getEventId(Integer tid) {
        if(tid == null){
            return UNKNOWN;
        }
        if (tid != COMMENT.EVENT.POST) {
            return UNKNOWN;
        }
        return tid;
    }
}

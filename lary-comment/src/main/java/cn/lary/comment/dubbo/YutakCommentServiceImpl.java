package cn.lary.comment.dubbo;

import cn.lary.api.comment.YutakCommentService;
import cn.lary.api.comment.dto.*;
import cn.lary.api.comment.vo.NextCommentVO;
import cn.lary.api.comment.vo.RootCommentVO;
import cn.lary.comment.service.CommentEventService;
import cn.lary.comment.service.NextCommentService;
import cn.lary.comment.service.RootCommentService;
import cn.lary.common.dto.ResponsePair;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@DubboService
@RequiredArgsConstructor
public class YutakCommentServiceImpl implements YutakCommentService {

    private final CommentEventService commentEventService;
    private final RootCommentService rootCommentService;
    private final NextCommentService nextCommentService;


    @Override
    public ResponsePair<Void> comment(RootCommentDTO dto) {
        return rootCommentService.comment(dto);
    }

    @Override
    public ResponsePair<Void> hideRoot(long cid) {
        return rootCommentService.hide(cid);
    }

    @Override
    public ResponsePair<List<RootCommentVO>> show(RootCommentPageQueryDTO dto) {
        return rootCommentService.show(dto);
    }

    @Override
    public ResponsePair<Void> comment(NextCommentDTO dto) {
        return nextCommentService.comment(dto);
    }

    @Override
    public ResponsePair<Void> hideNext(long cid) {
        return nextCommentService.hide(cid);
    }

    @Override
    public ResponsePair<List<NextCommentVO>> show(NextCommentPageQueryDTO dto) {
        return nextCommentService.show(dto);
    }

    @Override
    public ResponsePair<Void> event(CommentEventDTO dto) {
        return commentEventService.event(dto);
    }
}

package cn.lary.comment.component;

import cn.lary.comment.service.NextCommentService;
import cn.lary.comment.service.RootCommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentComment {

    private final RootCommentService commentService;
    private final NextCommentService nextCommentService;



}

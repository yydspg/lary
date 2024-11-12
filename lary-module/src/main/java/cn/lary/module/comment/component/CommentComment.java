package cn.lary.module.comment.component;

import cn.lary.module.comment.service.RootCommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentComment {

    private final RootCommentService commentService;


}

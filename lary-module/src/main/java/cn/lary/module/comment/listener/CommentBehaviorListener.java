package cn.lary.module.comment.listener;

import cn.lary.module.comment.service.NextCommentService;
import cn.lary.module.comment.service.RootCommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;

@Slf4j
@RequiredArgsConstructor
@RocketMQMessageListener(consumerGroup = "lary-comment-behavior",topic = "lary-comment-behavior")
public class CommentBehaviorListener implements RocketMQListener<BehaviorMessage> {

    private final RootCommentService rootCommentService;
    private final NextCommentService nextCommentService;

    @Override
    public void onMessage(BehaviorMessage message) {

    }
}

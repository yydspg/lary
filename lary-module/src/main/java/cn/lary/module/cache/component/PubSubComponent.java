package cn.lary.module.cache.component;


import lombok.RequiredArgsConstructor;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.MessageListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PubSubComponent {

    private final RedissonClient redisson;

    public <T> void subscribeToTopic(String topicName, MessageListener<T> listener ,Class<T> message) {
        RTopic topic = redisson.getTopic(topicName);
        topic.addListener(message,listener);
    }


    public <T> void subscribeToTopics(String[] topicNames, MessageListener<T> listener,Class<T> message) {
        for (String topicName : topicNames) {
            RTopic topic = redisson.getTopic(topicName);
            topic.addListener(message,listener);
        }
    }

    // 发布消息到频道
    public <T> void publishToTopic(String topicName, T message) {
        RTopic topic = redisson.getTopic(topicName);
        topic.publish(message);
    }
}

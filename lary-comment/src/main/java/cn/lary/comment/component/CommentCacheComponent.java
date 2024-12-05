package cn.lary.comment.component;


import cn.lary.api.comment.dto.CommentEventCacheDTO;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class CommentCacheComponent {

    private final RedissonClient redisson;
    private final String LARY_COMMENT_EVENT_CACHE = "lary:comment:event:cache:";
    private final Cache<String,SlidingWindow> cache = Caffeine.newBuilder()
            .maximumSize(10000)
            .softValues()
            .build();

//    private final Caffeine<Long,>
    public void setEvent(CommentEventCacheDTO dto) {
        redisson.<CommentEventCacheDTO>getBucket(LARY_COMMENT_EVENT_CACHE+ dto.getEid()).set(dto);
    }

    public CommentEventCacheDTO getEvent(long eid) {
        return redisson.<CommentEventCacheDTO>getBucket(LARY_COMMENT_EVENT_CACHE+eid).get();
    }

}

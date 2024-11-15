package cn.lary.module.comment.dto;

import cn.lary.module.cache.dto.CacheDTO;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CommentEventCacheDTO extends CacheDTO {
    /**
     * 评论事件id
     */
    private long eid;

    /**
     * 事件类型id
     */
    private int tid;

    /**
     * 用户id
     */
    private long uid;

    /**
     * 事件描述
     */
    private String content;
    
}

package cn.lary.api.comment.dto;


import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CommentEventCacheDTO  {
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

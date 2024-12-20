package cn.lary.api.comment.entity;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MentionNotifyPayload {

    private long eid;

    private long rid;

    private String content;

    private long timestamp;
}

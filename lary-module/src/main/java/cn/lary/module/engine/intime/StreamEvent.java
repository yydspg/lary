package cn.lary.module.engine.intime;


import cn.lary.module.engine.listener.StreamEventMessage;
import lombok.Data;

@Data
public class StreamEvent {

    private long sid;

    private int tag;

    private double score;

    private String url;

    private String title;

    private String username;

    private String avatar;

    private long uid;

    public StreamEvent() {}

    public StreamEvent(StreamEventMessage message) {
        sid = message.getSid();
        tag = message.getTag();
        score = message.getScore();
        url = message.getUrl();
        title = message.getTitle();
        username = message.getUsername();
        avatar = message.getAvatar();
        uid = message.getUid();
    }
}

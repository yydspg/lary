package cn.lary.module.engine.intime;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RankNode {

    private long sid;

    private int tag;

    private String url;

    private String title;

    private String username;

    private String avatar;

    private long uid;

    public RankNode(){}

    public RankNode(StreamEvent event) {
        this.sid = event.getSid();
        this.tag = event.getTag();
        this.url = event.getUrl();
        this.title = event.getTitle();
        this.username = event.getUsername();
        this.avatar = event.getAvatar();
        this.uid = event.getUid();
    }
}

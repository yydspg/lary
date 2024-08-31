package cn.lary.pkg.wk.entity.Response.message;

import cn.lary.pkg.wk.entity.core.Channel;
import lombok.Data;

import java.util.List;

@Data
public class RecentMessage extends Channel {
    private List<Message> messages;
}

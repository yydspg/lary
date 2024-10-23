package cn.lary.external.wk.vo.message;

import cn.lary.external.wk.entity.core.WKChannel;
import lombok.Data;

import java.util.List;

@Data
public class RecentMessage extends WKChannel {
    private List<Message> messages;
}

package cn.lary.pkg.wk.vo.message;

import cn.lary.pkg.wk.entity.core.WKChannel;
import lombok.Data;

import java.util.List;

@Data
public class RecentMessage extends WKChannel {
    private List<Message> messages;
}

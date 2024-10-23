package cn.lary.module.message.dto.stream;

import cn.lary.external.wk.dto.message.MessageHeader;
import cn.lary.external.wk.dto.message.MessageSendDTO;
import lombok.Data;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Data
public class GoLiveNotifyDTO extends MessageSendDTO {

    public GoLiveNotifyDTO(int uid, String name, List<Integer> members) {
        setHeader(new MessageHeader()
                .setNoPersist(1)
                .setRedDot(0));
        setFromUID(uid);
        setSubscribers(members);
        String content = name + "开启了直播,快来围观吧";
        setPayload(content.getBytes(StandardCharsets.UTF_8));
    }
}

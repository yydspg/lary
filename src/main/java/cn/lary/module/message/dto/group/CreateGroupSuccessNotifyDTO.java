package cn.lary.module.message.dto.group;

import cn.lary.pkg.wk.constant.WK;
import cn.lary.pkg.wk.dto.message.MessageHeader;
import cn.lary.pkg.wk.dto.message.MessageSendDTO;
import lombok.Data;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Data
public class CreateGroupSuccessNotifyDTO extends MessageSendDTO {

    public CreateGroupSuccessNotifyDTO build(int creator,int groupId, List<Integer> members) {
        setHeader(new MessageHeader()
                .setNoPersist(1)
                .setRedDot(0));
        setChannelID(groupId);
        setChannelType(WK.ChannelType.group);
        setFromUID(creator);
        String content = "你加入了";
        setPayload(content.getBytes(StandardCharsets.UTF_8));
        setSubscribers(members);
        return this;
    }
}

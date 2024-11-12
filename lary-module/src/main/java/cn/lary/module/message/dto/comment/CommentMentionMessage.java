package cn.lary.module.message.dto.comment;

import cn.lary.common.kit.JSONKit;
import cn.lary.external.wk.dto.message.MessageHeader;
import cn.lary.external.wk.dto.message.MessageSendDTO;
import cn.lary.module.comment.entity.MentionNotifyPayload;
import cn.lary.module.common.constant.LARY;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class CommentMentionMessage extends MessageSendDTO {

    public CommentMentionMessage(List<Long> mentions, long uid, MentionNotifyPayload payload) {
        setHeader(new MessageHeader()
                .setRedDot(1));
        setChannelID(uid);
        setSubscribers(mentions);
        setFromUID(uid);
        setChannelType(LARY.CHANNEL.TYPE.PERSON);
        setPayload(JSONKit.toJSON(payload).getBytes(StandardCharsets.UTF_8));
    }
}

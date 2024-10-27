package cn.lary.module.stream.listener.redpacket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RedPacketCloseMessage {

    private long eventId;
    private long uid;
    private int streamId;
    private long redPacketId;


}

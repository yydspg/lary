package cn.lary.module.stream.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RedPacketCloseMessage {

    private int eventId;
    private int uid;
    private int streamId;
    private long redPacketId;


}

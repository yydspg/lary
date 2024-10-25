package cn.lary.module.stream.listener.raffle;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RaffleCloseMessage {
    private int eventId;
    private int uid;
    private int streamId;
    private int raffleId;
}

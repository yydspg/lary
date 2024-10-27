package cn.lary.module.stream.listener.raffle;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RaffleCloseMessage {
    private long eventId;
    private long uid;
    private long streamId;
    private long raffleId;
}

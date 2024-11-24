package cn.lary.engine.intime;


import cn.lary.engine.listener.StreamEventMessage;

public interface RankOperation {
    /**
     * process external stream up event
     * @param message {@link StreamEventMessage}
     */
    void up(StreamEventMessage message);
    /**
     * process external stream up down
     * @param message {@link StreamEventMessage}
     */
    void down(StreamEventMessage message);
}

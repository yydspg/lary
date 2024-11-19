package cn.lary.module.engine.intime;


import cn.lary.module.engine.listener.StreamEventMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RankOperationImpl implements RankOperation {

    private final RankGroupManager rankGroupManager;

    @Override
    public void up(StreamEventMessage message) {
        rankGroupManager.whenUp(new StreamEvent(message));
    }

    @Override
    public void down(StreamEventMessage message) {
        rankGroupManager.whenDown(new StreamEvent(message));
    }
}

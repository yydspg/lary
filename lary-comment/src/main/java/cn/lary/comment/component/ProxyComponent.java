package cn.lary.comment.component;

import cn.lary.comment.listener.BehaviorMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProxyComponent {


    public final boolean limit(BehaviorMessage message) {
        return false;
    }
}

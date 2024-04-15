package cn.lary.core.ratelimiter.ploy.impl;

import cn.lary.core.constant.RateCS;
import cn.lary.core.ratelimiter.anno.Rate;
import cn.lary.core.ratelimiter.model.impl.FixedWindow;
import cn.lary.core.ratelimiter.ploy.RatePloy;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author paul 2024/4/15
 */
@Component
public class FixTimePloy extends RatePloy<FixedWindow> {
    @Override
    public String getName() {
        return RateCS.Ploy.FIXED_WINDOW;
    }

    @Override
    public Boolean isLimit(FixedWindow limit) {
        return false;
    }


}

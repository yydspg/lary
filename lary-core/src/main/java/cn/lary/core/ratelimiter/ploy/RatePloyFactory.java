package cn.lary.core.ratelimiter.ploy;

import cn.lary.core.ratelimiter.model.Limit;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author paul 2024/4/15
 */

public class RatePloyFactory {
    private RatePloyFactory() {}
    static Map<String, RatePloy<?>> ratePloyMap = new ConcurrentHashMap<>(8);
    // TODO 2024/4/15 : 测试是否能不强转就获得对应的具体实现类
    public static <K extends Limit> RatePloy getRatePloy(String ployName) {
        return  ratePloyMap.get(ployName);
    }
    public static <K extends Limit> void register(String ployName,RatePloy<K> ratePloy) {
        ratePloyMap.put(ployName,ratePloy);
    }
}

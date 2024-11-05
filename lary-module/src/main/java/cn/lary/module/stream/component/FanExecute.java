package cn.lary.module.stream.component;

import cn.lary.common.dto.ResponsePair;
import cn.lary.common.kit.BusinessKit;
import cn.lary.module.common.cache.KVBuilder;
import cn.lary.module.common.cache.CacheComponent;
import cn.lary.module.cache.dto.RedPacketCacheDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FanExecute {

    private final CacheComponent cacheComponent;
    private final KVBuilder kvBuilder;

    /**
     * 查询红包信息
     * @param toUid anchor
     * @return {@link RedPacketCacheDTO}
     */
    public ResponsePair<RedPacketCacheDTO> getRedPacketInfo(long toUid) {
        Map<Object, Object> map = cacheComponent.getHash(kvBuilder.redPacketK(toUid));
        if (map == null) {
            return BusinessKit.fail("no red packet info");
        }
        return BusinessKit.ok(RedPacketCacheDTO.of(map));
    }

    /**
     * 参与红包
     * @param uid u
     * @param toUid anchor
     * @return ok
     */
    public ResponsePair<Void> redWars(long uid, long toUid) {
//        String redpacketData = cacheComponent.execute(uid, toUid);
//        if (StringKit.isNotEmpty(redpacketData)) {
//            return BusinessKit.fail("手速慢了欧");
//        }
        return null;
    }
}

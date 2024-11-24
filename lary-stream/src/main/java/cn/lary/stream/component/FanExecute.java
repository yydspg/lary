package cn.lary.stream.component;

import cn.lary.common.dto.ResponsePair;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FanExecute {

//    private final CacheComponent cacheComponent;
//    private final KVBuilder kvBuilder;

    /**
     * 查询红包信息
     * @param toUid anchor
     * @return {@link RedpacketCacheDTO}
     */
//    public ResponsePair<RedpacketCacheDTO> getRedPacketInfo(long toUid) {
//        Map<Object, Object> map = cacheComponent.getHash(kvBuilder.redPacketK(toUid));
//        if (map == null) {
//            return BusinessKit.fail("no red packet info");
//        }
//        return BusinessKit.ok(RedpacketCacheDTO.of(map));
//    }

    /**
     * 参与红包
     * @param uid u
     * @param toUid anchor
     * @return OK
     */
    public ResponsePair<Void> redWars(long uid, long toUid) {
//        String redpacketData = cacheComponent.execute(uid, toUid);
//        if (StringKit.isNotEmpty(redpacketData)) {
//            return BusinessKit.FAIL("手速慢了欧");
//        }
        return null;
    }
}

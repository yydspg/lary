package cn.lary.stream.component;

import cn.lary.common.dto.ResponsePair;
import cn.lary.api.stream.dto.FollowDTO;
import cn.lary.api.stream.dto.FollowPageQueryDTO;
import cn.lary.stream.service.FollowService;
import cn.lary.api.stream.vo.FollowVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FollowBusinessExecute {

    private final FollowService followService;


    /**
     * 关注用户
     * @param dto {@link FollowDTO}
     * @return OK
     */
    public ResponsePair<Void> follow( FollowDTO dto) {
        return followService.follow(dto);
    }

    /**
     * 取消关注
     * @param toUid t
     * @return OK
     */
    public ResponsePair<Void> unfollow( long toUid) {
        return followService.unfollow(toUid);
    }

    /**
     * 关注列表
     * @param dto {@link FollowPageQueryDTO}
     * @return {@link FollowVO}
     */
    public ResponsePair<List<FollowVO>> follows(FollowPageQueryDTO dto) {
        return followService.follows(dto);
    }

    /**
     * 拉黑
     * @param toUid t
     * @return OK
     */
    public ResponsePair<Void> block( long toUid) {
        return followService.block(toUid);
    }

    /**
     * 取消拉黑
     * @param toUid t
     * @return OK
     */
    public ResponsePair<Void> unblock( long toUid) {
       return followService.unblock(toUid);
    }

}

package cn.lary.module.stream.component;

import cn.lary.common.dto.ResponsePair;
import cn.lary.module.stream.dto.FollowDTO;
import cn.lary.module.stream.dto.FollowPageQueryDTO;
import cn.lary.module.stream.entity.Follow;
import cn.lary.module.stream.service.FollowService;
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
     * @return ok
     */
    public ResponsePair<Void> follow( FollowDTO dto) {
        return followService.follow(dto);
    }

    /**
     * 取消关注
     * @param toUid t
     * @return ok
     */
    public ResponsePair<Void> unfollow( long toUid) {
        return followService.unfollow(toUid);
    }

    /**
     * 关注列表
     * @param dto {@link FollowPageQueryDTO}
     * @return {@link Follow}
     */
    public ResponsePair<List<Follow>> follows( FollowPageQueryDTO dto) {
        return followService.follows(dto);
    }

    /**
     * 拉黑
     * @param toUid t
     * @return ok
     */
    public ResponsePair<Void> block( long toUid) {
        return followService.block(toUid);
    }

    /**
     * 取消拉黑
     * @param toUid t
     * @return ok
     */
    public ResponsePair<Void> unblock( long toUid) {
       return followService.unblock(toUid);
    }

}

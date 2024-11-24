package cn.lary.stream.service;

import cn.lary.common.dto.ResponsePair;
import cn.lary.stream.dto.FollowDTO;
import cn.lary.stream.dto.FollowPageQueryDTO;
import cn.lary.stream.entity.Follow;
import cn.lary.stream.vo.FollowVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author paul
 * @since 2024-09-04
 */
public interface FollowService extends IService<Follow> {

//    Follow build(Follow dto);

//    /**
//     * 获取被关注关系<br>
//     * 请
//     * @param uid uid
//     * @return uid
//     */
//    ResponsePair<List<Long>> getFollows(long uid);

    /**
     * 主动关注系统账户
     * @param uid 关注者
     */
    void addSystemHelper(long uid);

    /**
     * 关注用户
     * @param dto {@link FollowDTO}
     * @return OK
     */
    ResponsePair<Void> follow(FollowDTO dto);

    /**
     * 拉黑
     * @return OK
     */
     ResponsePair<Void> block(long toUid);
    /**
     * 取消拉黑
     * @return OK
     */
     ResponsePair<Void> unblock(long toUid);
    /**
     * 取消关注
     * @return OK
     */
     ResponsePair<Void> unfollow(long toUid);
    /**
     * 关注列表
     * @param dto {@link FollowPageQueryDTO}
     * @return {@link Follow}
     */
     ResponsePair<List<FollowVO>> follows(FollowPageQueryDTO dto);
}

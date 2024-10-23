package cn.lary.module.stream.service;

import cn.lary.common.dto.ResponsePair;
import cn.lary.module.stream.dto.FollowDTO;
import cn.lary.module.stream.dto.FollowPageQueryDTO;
import cn.lary.module.stream.entity.Follow;
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

    /**
     * 获取被关注关系<br>
     * 请
     * @param uid uid
     * @return uid
     */
    ResponsePair<List<Integer>> getFollows(int uid);

    /**
     * 主动关注系统账户
     * @param uid 关注者
     */
    void addSystemHelper(int uid);

    /**
     * 关注用户
     * @param dto {@link FollowDTO}
     * @return ok
     */
    ResponsePair<Void> follow(FollowDTO dto);

    /**
     * 拉黑
     * @return ok
     */
     ResponsePair<Void> block(int toUid);
    /**
     * 取消拉黑
     * @return ok
     */
     ResponsePair<Void> unblock(int toUid);
    /**
     * 取消关注
     * @return ok
     */
     ResponsePair<Void> unfollow(int toUid);
    /**
     * 关注列表
     * @param dto {@link FollowPageQueryDTO}
     * @return {@link Follow}
     */
     ResponsePair<List<Follow>> follows( FollowPageQueryDTO dto);
}

package cn.lary.module.user.service;

import cn.lary.core.dto.ResPair;
import cn.lary.module.user.vo.FriendCodeCheck;
import cn.lary.module.user.vo.UserBaseVO;
import cn.lary.module.user.vo.UserBasicInfo;
import cn.lary.module.user.entity.User;
import cn.lary.module.user.entity.UserShowInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author paul
 * @since 2024-07-29
 */
public interface UserService extends IService<User> {



    UserBaseVO queryBase(int uid);

    /**
     * 对用户筛选有效用户
     * @param members m
     * @return IDs
     */
    List<Integer> getValidUsers(List<Integer> members);
}

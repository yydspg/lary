package cn.lary.module.user.service.impl;

import cn.lary.kit.CollectionKit;
import cn.lary.module.common.constant.Lary;
import cn.lary.module.user.vo.FriendCodeCheck;
import cn.lary.module.user.vo.UserBaseVO;
import cn.lary.module.user.vo.UserBasicInfo;
import cn.lary.module.user.entity.User;
import cn.lary.module.user.entity.UserShowInfo;
import cn.lary.module.user.mapper.UserMapper;
import cn.lary.module.user.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author paul
 * @since 2024-07-29
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserMapper userMapper;


    @Override
    public UserBaseVO queryBase(int uid) {
        return baseMapper.selectBase(uid);
    }

    @Override
    public List<Integer> getValidUsers(List<Integer> members) {
        List<User> data = lambdaQuery()
                .select(User::getUid)
                .eq(User::getStatus, Lary.UserStatus.ok)
                .eq(User::getDeleted, false)
                .in(User::getUid, members)
                .list();
        if (CollectionKit.isEmpty(data)) {
            return null;
        }
        List<Integer> users = new ArrayList<>();
        data.forEach(d->{
            users.add(d.getUid());
        });
        return users;
    }


}

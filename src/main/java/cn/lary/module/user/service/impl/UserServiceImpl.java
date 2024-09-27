package cn.lary.module.user.service.impl;

import cn.lary.module.user.vo.FriendCodeCheck;
import cn.lary.module.user.vo.UserBaseVO;
import cn.lary.module.user.vo.UserBasicInfo;
import cn.lary.module.user.entity.User;
import cn.lary.module.user.entity.UserShowInfo;
import cn.lary.module.user.mapper.UserMapper;
import cn.lary.module.user.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    public User queryByUID(int uid) {
        return baseMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUid,uid).eq(User::getDeleted,false),false);
    }

    @Override
    public UserBaseVO queryBase(int uid) {
        return baseMapper.selectBase(uid);
    }

    @Override
    public UserBasicInfo queryUserBasicInfo(int uid) {
        return baseMapper.selectBasicInfo(uid);
    }

    @Override
    public List<UserShowInfo> queryUserShowInfo(List<String> uids) {
        return userMapper.selectUserShowInfo(uids);
    }


    @Override
    public List<User> queryByUIDs(List<String> uids) {
        LambdaQueryWrapper<User> qw = new LambdaQueryWrapper<User>().eq(User::getDeleted,false).in(User::getUid, uids);
        return baseMapper.selectList(qw);
    }

    @Override
    public List<UserBaseVO> queryUserBaseByUIDs(List<String> uids) {
        return baseMapper.selectBaseByIDs(uids);
    }

}

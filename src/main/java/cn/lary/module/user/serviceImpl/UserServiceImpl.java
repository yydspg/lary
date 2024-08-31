package cn.lary.module.user.serviceImpl;

import cn.lary.module.user.dto.res.FriendCodeCheck;
import cn.lary.module.user.dto.res.UserBaseRes;
import cn.lary.module.user.dto.res.UserBasicInfo;
import cn.lary.module.user.entity.User;
import cn.lary.module.user.mapper.UserMapper;
import cn.lary.module.user.service.UserService;
import com.alipay.api.domain.UserBaseInfo;
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


    @Override
    public User queryByUsername(String username) {
        LambdaQueryWrapper<User> lw = new LambdaQueryWrapper<>();
        lw.eq(User::getUsername, username);
        return baseMapper.selectOne(lw);
    }

    @Override
    public User queryByUID(String uid) {
        LambdaQueryWrapper<User> qw = new LambdaQueryWrapper<>();
        qw.eq(User::getUid, uid);
        return baseMapper.selectOne(qw);
    }

    @Override
    public UserBaseRes queryBase(String uid) {
        return baseMapper.selectBase(uid);
    }

    @Override
    public UserBasicInfo queryUserBasicInfo(String uid) {
        return baseMapper.selectBasicInfo(uid);
    }


    @Override
    public List<User> queryByUIDs(List<String> uids) {
        LambdaQueryWrapper<User> qw = new LambdaQueryWrapper<User>().in(User::getUid, uids);
        return baseMapper.selectList(qw);
    }

    @Override
    public List<UserBaseRes> queryUserBaseByUIDs(List<String> uids) {
        return baseMapper.selectBaseByIDs(uids);
    }

    @Override
    public FriendCodeCheck checkByCode(String code) {
        return baseMapper.checkWithCode(code);
    }

    @Override
    public FriendCodeCheck checkByQRCode(String code) {
        return baseMapper.checkWithQRCode(code);
    }
}

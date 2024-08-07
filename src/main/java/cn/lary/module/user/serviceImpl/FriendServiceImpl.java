package cn.lary.module.user.serviceImpl;

import cn.lary.kit.StringKit;
import cn.lary.module.common.server.AccountConfig;
import cn.lary.module.user.entity.Friend;
import cn.lary.module.user.mapper.FriendMapper;
import cn.lary.module.user.service.FriendService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class FriendServiceImpl extends ServiceImpl<FriendMapper, Friend> implements FriendService {

    private final AccountConfig accountConfig;
    @Override
    public boolean isFriend(String uid, String toUid) {
        LambdaQueryWrapper<Friend> qw = new LambdaQueryWrapper<>();
        qw.eq(Friend::getUid, uid);
        qw.eq(Friend::getToUid, toUid);
        Friend friend = baseMapper.selectOne(qw);
        return friend != null && !friend.getIsDeleted();
    }

    @Override
    @Transactional
    public void  addSystemFriend(String uid) {
        if(StringKit.isEmpty(uid)){
            return ;
        }
        boolean isFriend = isFriend(uid, accountConfig.getSystemUid());
        if(isFriend){
            return ;
        }
        // non friends
        Friend friend = new Friend().setUid(uid).setIsDeleted(false).setToUid(accountConfig.getSystemUid());
        // TODO  :  这里缺少了 version 字段的设置
        baseMapper.insert(friend);
    }

    @Override
    @Transactional
    public void addFileHelper(String uid) {
        if(StringKit.isEmpty(uid)){
            return ;
        }
        boolean isFriend = isFriend(uid, accountConfig.getFileHelperUid());
        if(isFriend){
            return ;
        }
        // non friends
        Friend friend = new Friend().setUid(uid).setIsDeleted(false).setToUid(accountConfig.getFileHelperUid());
        // TODO  :  这里缺少了 version 字段的设置
        baseMapper.insert(friend);
    }
}

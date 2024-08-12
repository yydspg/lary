package cn.lary.module.group.serviceImpl;

import cn.lary.kit.CollectionKit;
import cn.lary.kit.StringKit;
import cn.lary.module.common.CS.Lary;
import cn.lary.module.group.entity.GroupMember;
import cn.lary.module.group.mapper.GroupMemberMapper;
import cn.lary.module.group.service.GroupMemberService;
import cn.lary.module.user.entity.UserBase;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
public class GroupMemberServiceImpl extends ServiceImpl<GroupMemberMapper, GroupMember> implements GroupMemberService {

    @Override
    public boolean isCreatorOrManager(String uid, String groupNo,String creator) {
       if (StringKit.same(uid,creator)) {
           return true;
       }
        LambdaQueryWrapper<GroupMember> qw = new LambdaQueryWrapper<>();
        qw.eq(GroupMember::getGroupNo,groupNo);
        qw.eq(GroupMember::getRole, Lary.Group.Role.manager);
        qw.select(GroupMember::getUid);
        List<String> managerUIDs = baseMapper.selectObjs(qw);
        if (CollectionKit.isEmpty(managerUIDs)) {
            return false;
        }
        return managerUIDs.contains(creator);
    }

    @Override
    public boolean isMember(String uid, String groupNo) {
        LambdaQueryWrapper<GroupMember> qw = new LambdaQueryWrapper<>();
        qw.eq(GroupMember::getGroupNo,groupNo);
        qw.eq(GroupMember::getUid, uid);
        return baseMapper.exists(qw);
    }

    @Override
    public List<String> queryMemberWithStatus(String groupNo, int status) {
        LambdaQueryWrapper<GroupMember> qw = new LambdaQueryWrapper<>();
        qw.select(GroupMember::getUid);
        qw.eq(GroupMember::getGroupNo,groupNo);
        qw.eq(GroupMember::getStatus,status);
        return baseMapper.selectObjs(qw);
    }

    @Override
    public boolean isDeletedMember(String groupNo, String uid) {
        LambdaQueryWrapper<GroupMember> qw = new LambdaQueryWrapper<>();
        qw.eq(GroupMember::getGroupNo,groupNo);
        qw.eq(GroupMember::getUid,uid);
        qw.eq(GroupMember::isDeleted,true);
        return baseMapper.selectCount(qw) > 0;
    }

    @Override
    public void recoveryMember(String groupNo, String uid) {
        LambdaUpdateWrapper<GroupMember> qw = new LambdaUpdateWrapper<>();
        qw.eq(GroupMember::getGroupNo,groupNo);
        qw.eq(GroupMember::getUid,uid);
        qw.set(GroupMember::isDeleted,true);
        baseMapper.update(qw);
    }

    @Override
    public long queryMemberCount(String groupNo) {
        LambdaQueryWrapper<GroupMember> qw = new LambdaQueryWrapper<GroupMember>().eq(GroupMember::getGroupNo, groupNo);
        return baseMapper.selectCount(qw);
    }

    @Override
    public List<String> queryMemberWithLimit(String groupNo, long limit) {
        LambdaQueryWrapper<GroupMember> qw = new LambdaQueryWrapper<GroupMember>().select(GroupMember::getUid).eq(GroupMember::getGroupNo, groupNo).last("limit " + limit);
        return baseMapper.selectObjs(qw);
    }
}

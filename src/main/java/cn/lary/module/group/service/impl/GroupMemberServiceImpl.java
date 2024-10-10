package cn.lary.module.group.service.impl;

import cn.lary.module.group.entity.GroupMember;
import cn.lary.module.group.mapper.GroupMemberMapper;
import cn.lary.module.group.service.GroupMemberService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
    public List<Integer> queryMemberWithStatus(int groupNo, int status) {
        LambdaQueryWrapper<GroupMember> qw = new LambdaQueryWrapper<GroupMember>()
                .select(GroupMember::getUid)
                .eq(GroupMember::getGroupNo,groupNo)
                .eq(GroupMember::getStatus,status);
        return baseMapper.selectObjs(qw);
    }

}

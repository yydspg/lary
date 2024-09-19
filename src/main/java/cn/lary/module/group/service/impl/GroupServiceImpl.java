package cn.lary.module.group.service.impl;

import cn.lary.kit.DateKit;
import cn.lary.module.group.entity.Group;
import cn.lary.module.group.entity.GroupDetail;
import cn.lary.module.group.mapper.GroupMapper;
import cn.lary.module.group.service.GroupService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
public class GroupServiceImpl extends ServiceImpl<GroupMapper, Group> implements GroupService {

    private final GroupMapper groupMapper;

    @Override
    public int querySameDayCreateGroupCount(String uid, LocalDateTime now) {
        LocalDateTime startOfDay = DateKit.getStartOfDay(now);
        LocalDateTime endOfDay = DateKit.getEndOfDay(now);
        LambdaQueryWrapper<Group> qw = new LambdaQueryWrapper<>();
        qw.eq(Group::getCreator,uid);
        qw.ge(Group::getCreateAt,startOfDay);
        qw.le(Group::getCreateAt,endOfDay);
        return Math.toIntExact(groupMapper.selectCount(qw));
    }

    @Override
    public Group queryByNo(String groupNo) {
        LambdaQueryWrapper<Group> qw = new LambdaQueryWrapper<Group>().eq(Group::getGroupNo, groupNo);
        return baseMapper.selectOne(qw,false);
    }

    @Override
    public boolean exists(String groupNo) {
        LambdaQueryWrapper<Group> qw = new LambdaQueryWrapper<Group>().eq(Group::getGroupNo, groupNo);
        return groupMapper.selectCount(qw) > 0;
    }


    @Override
    public List<GroupDetail> querySavedGroups(String uid) {
        return groupMapper.queryMySavedGroups(uid);
    }

    @Override
    public boolean isUploadAvatar(String groupNo) {
        LambdaQueryWrapper<Group> qw = new LambdaQueryWrapper<Group>();
        qw.select(Group::getIsUploadAvatar);
        qw.eq(Group::getGroupNo, groupNo);
        Group group = baseMapper.selectOne(qw);
        if (group == null) {
            return false;
        }
        return group.getIsUploadAvatar();
    }


}

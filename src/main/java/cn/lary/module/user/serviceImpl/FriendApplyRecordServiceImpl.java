package cn.lary.module.user.serviceImpl;

import cn.lary.module.user.entity.FriendApplyRecord;
import cn.lary.module.user.mapper.FriendApplyRecordMapper;
import cn.lary.module.user.service.FriendApplyRecordService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author paul
 * @since 2024-07-29
 */
@Service
public class FriendApplyRecordServiceImpl extends ServiceImpl<FriendApplyRecordMapper, FriendApplyRecord> implements FriendApplyRecordService {

    @Override
    public FriendApplyRecord queryByUidAndToUid(String uid, String toUid) {
        LambdaQueryWrapper<FriendApplyRecord> qw = new LambdaQueryWrapper<FriendApplyRecord>().eq(FriendApplyRecord::getUid, uid).eq(FriendApplyRecord::getToUid, toUid).eq(FriendApplyRecord::getIsDeleted,false);
        return baseMapper.selectOne(qw,false);
    }

    @Override
    public Page<FriendApplyRecord> queryRecords(String toUid, long pageSize, long page) {
        LambdaQueryWrapper<FriendApplyRecord> eq = new LambdaQueryWrapper<FriendApplyRecord>().eq(FriendApplyRecord::getToUid, toUid).eq(FriendApplyRecord::getIsDeleted,false);
        return baseMapper.selectPage(new Page<>(page, pageSize), eq);
    }

    @Override
    public void deleteRecordByUidAndToUid(String uid, String toUid) {
        LambdaUpdateWrapper<FriendApplyRecord> uw = new LambdaUpdateWrapper<FriendApplyRecord>().eq(FriendApplyRecord::getUid, uid).eq(FriendApplyRecord::getToUid, toUid);
        uw.set(FriendApplyRecord::getIsDeleted, true);
        baseMapper.update(uw);
    }

    @Override
    public boolean exists(String uid, String toUid) {
        LambdaQueryWrapper<FriendApplyRecord> qw = new LambdaQueryWrapper<FriendApplyRecord>().eq(FriendApplyRecord::getUid,uid).eq(FriendApplyRecord::getToUid,toUid);
        return baseMapper.selectCount(qw)>0;
    }
}

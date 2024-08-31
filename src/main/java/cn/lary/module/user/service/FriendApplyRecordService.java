package cn.lary.module.user.service;

import cn.lary.module.user.entity.FriendApplyRecord;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
public interface FriendApplyRecordService extends IService<FriendApplyRecord> {

    FriendApplyRecord queryByUidAndToUid(String uid,String toUid);
    Page<FriendApplyRecord> queryRecords(String uid,long pageSize,long page);
    void deleteRecordByUidAndToUid(String uid,String toUid);
    boolean exists(String uid,String toUid);
}

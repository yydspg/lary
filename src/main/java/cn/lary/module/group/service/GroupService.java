package cn.lary.module.group.service;

import cn.lary.module.group.entity.Group;
import cn.lary.module.group.entity.GroupDetail;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author paul
 * @since 2024-07-29
 */
public interface GroupService extends IService<Group> {
    /**
     *
     * @param uid user id
     * @param createTime ct
     * @return count of group created by  this account today
     */
    int querySameDayCreateGroupCount(int uid, LocalDateTime createTime);

    Group queryByNo(String groupNo);
    boolean exists(String groupNo);
    /**
     * 查询 我保存的群聊
     * @param uid user id
     * @return group and group setting combine
     */
    List<GroupDetail> querySavedGroups(Integer uid);

    /**
     * query whether group has upload avatar
     * @param groupNo x
     * @return see
     */
    boolean isUploadAvatar(String groupNo);

}

package cn.lary.module.group.service;

import cn.lary.common.dto.ResponsePair;
import cn.lary.module.group.dto.CreateGroupDTO;
import cn.lary.module.group.entity.Group;
import cn.lary.module.group.vo.CreateGroupVO;
import cn.lary.module.group.vo.GroupDetailVO;
import cn.lary.module.group.vo.GroupVO;
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
     * 检查是否达到最大创建值
     * @param uid u
     * @param now n
     * @return ok
     */
    boolean  isReachCreateLimit(long uid, LocalDateTime now) ;


    /**
     * 创建群聊
     * @param dto {@link CreateGroupDTO}
     * @return {@link CreateGroupVO}
     */
    ResponsePair<CreateGroupVO> create(CreateGroupDTO dto) ;


    /**
     * 解散群聊
     * @param groupId g
     * @return ok
     */
    ResponsePair<Void> disband(long groupId) ;

    /**
     * 全体成员禁言
     * @param groupId g
     * @return ok
     */
    ResponsePair<Void> forbidden(long groupId) ;

    /**
     * 查询群信息
     * @param groupId g
     * @return {@link GroupDetailVO}
     */
    ResponsePair<GroupDetailVO> getGroup(long groupId) ;

    /**
     * 查询我的群聊,通过role
     * @param role 角色
     * @return {@link GroupVO}
     */
    ResponsePair<List<GroupVO>> my(int role) ;

}

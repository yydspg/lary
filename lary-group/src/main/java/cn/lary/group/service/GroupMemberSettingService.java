package cn.lary.group.service;

import cn.lary.common.dto.ResponsePair;
import cn.lary.group.dto.GroupMemberSettingDTO;
import cn.lary.group.entity.GroupMemberSetting;
import cn.lary.group.vo.GroupMemberSettingVO;
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
public interface GroupMemberSettingService extends IService<GroupMemberSetting> {

    /**
     * 修改群成员对群聊的设置<br>
     * 区别于群管理员对群本身的设置<br>
     * @param dto {@link GroupMemberSettingDTO}
     * @return OK
     */
    ResponsePair<Void> saveOrUpdate(GroupMemberSettingDTO dto);

    /**
     * 获取群设置
     * @return {@link GroupMemberSettingVO}
     */
    ResponsePair<List<GroupMemberSettingVO>> my();
}

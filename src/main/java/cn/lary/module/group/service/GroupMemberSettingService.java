package cn.lary.module.group.service;

import cn.lary.core.dto.ResponsePair;
import cn.lary.module.group.dto.GroupMemberSettingDTO;
import cn.lary.module.group.entity.GroupMemberSetting;
import cn.lary.module.group.vo.GroupMemberSettingVO;
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
     * @return ok
     */
    ResponsePair<Void> saveOrUpdate(GroupMemberSettingDTO dto);

    /**
     * 获取群设置
     * @return {@link GroupMemberSettingVO}
     */
    ResponsePair<List<GroupMemberSettingVO>> my();
}

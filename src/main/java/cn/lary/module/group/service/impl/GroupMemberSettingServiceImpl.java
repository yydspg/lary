package cn.lary.module.group.service.impl;

import cn.lary.core.dto.ResponsePair;
import cn.lary.kit.BusinessKit;
import cn.lary.kit.CollectionKit;
import cn.lary.module.group.dto.GroupMemberSettingDTO;
import cn.lary.module.group.entity.GroupMemberSetting;
import cn.lary.module.group.mapper.GroupMemberSettingMapper;
import cn.lary.module.group.service.GroupMemberSettingService;
import cn.lary.module.group.vo.GroupMemberSettingVO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
public class GroupMemberSettingServiceImpl extends ServiceImpl<GroupMemberSettingMapper, GroupMemberSetting> implements GroupMemberSettingService {


    @Override
    public ResponsePair<Void> saveOrUpdate(GroupMemberSettingDTO dto) {
        this.saveOrUpdate(dto.to());
        return BusinessKit.ok();
    }

    @Override
    public ResponsePair<List<GroupMemberSettingVO>> my() {
        List<GroupMemberSetting> data = lambdaQuery()
                .select(GroupMemberSetting::getIsJoinGroupRemind)
                .select(GroupMemberSetting::getIsTop)
                .select(GroupMemberSetting::getIsShowNickname)
                .select(GroupMemberSetting::getIsNoDisturb)
                .select(GroupMemberSetting::getIsHidden)
                .list();
        if (CollectionKit.isEmpty(data)) {
            return BusinessKit.fail("data empty");
        }
        List<GroupMemberSettingVO> vos = new ArrayList<>();
        data.forEach(e -> {
            GroupMemberSettingVO vo = new GroupMemberSettingVO();
            vos.add(vo.of(e));
        });
        return BusinessKit.ok(vos);
    }
}

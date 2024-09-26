package cn.lary.module.group.core;

import cn.lary.core.dto.ResPair;
import cn.lary.kit.BizKit;
import cn.lary.module.app.service.AppConfigService;
import cn.lary.module.app.service.EventService;
import cn.lary.module.app.service.SeqService;
import cn.lary.module.common.cache.KVBuilder;
import cn.lary.module.common.server.AccountConfig;
import cn.lary.module.common.server.GroupConfig;
import cn.lary.module.group.dto.CreateGroupDTO;
import cn.lary.module.group.service.GroupMemberService;
import cn.lary.module.group.service.GroupService;
import cn.lary.module.group.service.GroupSettingService;
import cn.lary.module.group.vo.CreateGroupVO;
import cn.lary.module.user.service.UserService;
import cn.lary.pkg.wk.api.WKChannelService;
import cn.lary.pkg.wk.api.WKMessageService;
import cn.lary.pkg.wk.api.WKUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupBizExecute {

    private final GroupService groupService;
    private final GroupConfig groupConfig;
    private final AppConfigService appConfigService;
    private final AccountConfig accountConfig;
    private final UserService userService;
    private final GroupMemberService groupMemberService;
    private final EventService eventService;
    private final GroupSettingService groupSettingService;
    private final WKChannelService wkChannelService;
    private final SeqService seqService;
    private final KVBuilder kvBuilder;

    // external
    private final WKMessageService wkMessageService;
    private final WKUserService wkUserService;

    public ResPair<CreateGroupVO> create(String creator, String creatorName, CreateGroupDTO req) {
        int count = groupService.querySameDayCreateGroupCount(creator, LocalDateTime.now());
        if (groupConfig.getSameDayCreateMaxCount() <= count) {
            return BizKit.fail("reach max group create count");
        }
        return BizKit.ok();
    }
}

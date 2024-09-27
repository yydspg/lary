package cn.lary.module.group.api;

import cn.lary.core.context.ReqContext;
import cn.lary.core.cs.ResultCode;
import cn.lary.core.dto.MultiResponse;
import cn.lary.core.dto.SingleResponse;
import cn.lary.kit.*;
import cn.lary.module.app.entity.AppConfigRes;
import cn.lary.module.app.entity.EventData;
import cn.lary.module.app.service.AppConfigService;
import cn.lary.module.app.service.EventService;
import cn.lary.module.app.service.SeqService;
import cn.lary.module.common.CS.Lary;
import cn.lary.module.common.cache.KVBuilder;
import cn.lary.module.common.server.AccountConfig;
import cn.lary.module.common.server.GroupConfig;
import cn.lary.module.group.core.GroupBizExecute;
import cn.lary.module.group.dto.*;
import cn.lary.module.group.vo.GroupDetailRes;
import cn.lary.module.group.entity.Group;
import cn.lary.module.group.entity.GroupDetail;
import cn.lary.module.group.entity.GroupMember;
import cn.lary.module.group.service.GroupMemberService;
import cn.lary.module.group.service.GroupService;
import cn.lary.module.group.service.GroupSettingService;
import cn.lary.module.user.vo.UserBaseVO;
import cn.lary.module.user.entity.User;
import cn.lary.module.user.entity.UserBase;
import cn.lary.module.user.service.UserService;
import cn.lary.pkg.wk.api.WKChannelService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/group/")
@RequiredArgsConstructor
public class GroupController {

    private final GroupBizExecute groupBizExecute;

    public SingleResponse<Void> add(){
        return null;
    }
}


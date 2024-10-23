package cn.lary.module.group.api;

import cn.lary.common.dto.ResponsePair;
import cn.lary.common.dto.SingleResponse;
import cn.lary.common.kit.ResponseKit;
import cn.lary.module.group.component.GroupBusinessExecute;
import cn.lary.module.group.dto.CreateGroupDTO;
import cn.lary.module.group.dto.GroupAvatarUploadDTO;
import cn.lary.module.group.file.GroupAvatarUploadProcessor;
import cn.lary.module.group.vo.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/group/")
@RequiredArgsConstructor
public class GroupController {

    private final GroupBusinessExecute groupBusinessExecute;
    private final GroupAvatarUploadProcessor groupAvatarUploadProcessor;

    /**
     * 创建群聊
     * @param dto {@link CreateGroupDTO}
     * @return {@link CreateGroupVO}
     */
    @PostMapping("/create")
    public SingleResponse<CreateGroupVO> create(@RequestBody @Valid CreateGroupDTO dto) {
        ResponsePair<CreateGroupVO> response = groupBusinessExecute.create(dto);
        if (response.isFail()) {
            return ResponseKit.fail(response.getMsg());
        }
        return ResponseKit.ok(response.getData());
    }

    /**
     * 解散群聊
     * @param groupId g
     * @return ok
     */
    @GetMapping("/disband")
    public SingleResponse<Void> disband(int groupId) {
        ResponsePair<Void> response = groupBusinessExecute.disband(groupId);
        if (response.isFail()) {
            return ResponseKit.fail(response.getMsg());
        }
        return ResponseKit.ok();
    }

    /**
     * 全体成员禁言
     * @param groupId g
     * @return ok
     */
    @GetMapping("/forbidden")
    public SingleResponse<Void> forbidden( @RequestParam @NotNull int groupId) {
        ResponsePair<Void> response = groupBusinessExecute.forbidden(groupId);
        if (response.isFail()) {
            return ResponseKit.fail(response.getMsg());
        }
        return ResponseKit.ok();
    }

    /**
     * 查询群信息
     * @param groupId g
     * @return {@link GroupDetailVO}
     */
    @GetMapping("/detail")
    public SingleResponse<GroupDetailVO> getGroup(@RequestParam @NotNull int groupId) {
        ResponsePair<GroupDetailVO> response = groupBusinessExecute.getGroup(groupId);
        if (response.isFail()) {
            return ResponseKit.fail(response.getMsg());
        }
        return ResponseKit.ok(response.getData());
    }

    /**
     * 查询我的群聊,通过role
     * @param role 角色
     * @return {@link GroupVO}
     */
    @GetMapping("/my/groups")
    public SingleResponse<List<GroupVO>> myGroups(@RequestParam @NotNull int role) {
        ResponsePair<List<GroupVO>> response = groupBusinessExecute.myGroups(role);
        if (response.isFail()) {
            return ResponseKit.fail(response.getMsg());
        }
        return ResponseKit.ok(response.getData());
    }

    /**
     * 上传群头像
     * @param dto {@link GroupAvatarUploadDTO}
     * @return url
     */
    public SingleResponse<String> avatar(@RequestBody @Valid GroupAvatarUploadDTO dto) {
        ResponsePair<String> response = groupAvatarUploadProcessor.execute(dto);
        if (response.isFail()) {
            return ResponseKit.fail(response.getMsg());
        }
        return ResponseKit.ok(response.getData());
    }



}

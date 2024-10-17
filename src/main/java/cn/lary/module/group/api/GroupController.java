package cn.lary.module.group.api;

import cn.lary.core.dto.ResponsePair;
import cn.lary.core.dto.SingleResponse;
import cn.lary.kit.ResponseKit;
import cn.lary.module.group.core.GroupBizExecute;
import cn.lary.module.group.dto.CreateGroupDTO;
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

    private final GroupBizExecute groupBizExecute;

    /**
     * 创建群聊
     * @param dto {@link CreateGroupDTO}
     * @return {@link CreateGroupVO}
     */
    @PostMapping("/create")
    public SingleResponse<CreateGroupVO> create(@RequestBody @Valid CreateGroupDTO dto) {
        ResponsePair<CreateGroupVO> res = groupBizExecute.create(dto);
        if (res.isFail()) {
            return ResponseKit.fail(res.getMsg());
        }
        return ResponseKit.ok(res.getData());
    }

    /**
     * 解散群聊
     * @param groupId g
     * @return ok
     */
    @GetMapping("/disband")
    public SingleResponse<Void> disband(int groupId) {
        ResponsePair<Void> res = groupBizExecute.disband(groupId);
        if (res.isFail()) {
            return ResponseKit.fail(res.getMsg());
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
        ResponsePair<Void> res = groupBizExecute.forbidden(groupId);
        if (res.isFail()) {
            return ResponseKit.fail(res.getMsg());
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
        ResponsePair<GroupDetailVO> res = groupBizExecute.getGroup(groupId);
        if (res.isFail()) {
            return ResponseKit.fail(res.getMsg());
        }
        return ResponseKit.ok(res.getData());
    }

    /**
     * 查询我的群聊,通过role
     * @param role 角色
     * @return {@link GroupVO}
     */
    @GetMapping("/my/groups")
    public SingleResponse<List<GroupVO>> myGroups(@RequestParam @NotNull int role) {
        ResponsePair<List<GroupVO>> res = groupBizExecute.myGroups(role);
        if (res.isFail()) {
            return ResponseKit.fail(res.getMsg());
        }
        return ResponseKit.ok(res.getData());
    }





}


package cn.lary.module.group.api;

import cn.lary.core.dto.ResPair;
import cn.lary.core.dto.SingleResponse;
import cn.lary.kit.ResponseKit;
import cn.lary.module.group.core.GroupBizExecute;
import cn.lary.module.group.dto.GroupMemberSettingDTO;
import cn.lary.module.group.vo.GroupMemberSettingVO;
import cn.lary.module.group.vo.GroupMemberVO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/group/member")
@RequiredArgsConstructor
public class GroupMemberController {

    private final GroupBizExecute groupBizExecute;
    /**
     * 查询某种状态的用户
     * @param groupNo g
     * @param status s
     * @return ok
     */
    @GetMapping("/members/status")
    public SingleResponse<List<Integer>> getMembersWithStatus(int groupNo, int status){
        ResPair<List<Integer>> res = groupBizExecute.getMembersWithStatus(groupNo, status);
        if (!res.isOk()) {
            return ResponseKit.fail(res.getMsg());
        }
        return ResponseKit.ok(res.getData());
    }
    /**
     * 退出群聊
     * @param groupId g
     * @return ok
     */
    @GetMapping("/quit")
    public SingleResponse<Void> quit(@RequestParam @NotNull int groupId) {
        ResPair<Void> res = groupBizExecute.quit(groupId);
        if (!res.isOk()) {
            return ResponseKit.fail(res.getMsg());
        }
        return ResponseKit.ok();
    }

    /**
     * 管理员强退
     * @param groupId g
     * @param uid u
     * @return ok
     */
    @GetMapping("/admin/quit")
    public SingleResponse<Void> quitByAdmin(@RequestParam @NotNull int groupId,
                                            @RequestParam @NotNull int uid){
        ResPair<Void> res = groupBizExecute.quitByAdmin(groupId, uid);
        if (!res.isOk()) {
            return ResponseKit.fail(res.getMsg());
        }
        return ResponseKit.ok();
    }

    /**
     * 加入群聊
     * @param groupId g
     * @return ok
     */
    @GetMapping("/join")
    public SingleResponse<Void> join(@RequestParam @NotNull int groupId) {
        ResPair<Void> res = groupBizExecute.join(groupId);
        if (!res.isOk()) {
            return ResponseKit.fail(res.getMsg());
        }
        return ResponseKit.ok();
    }
    /**
     * 管理员邀请加入群聊
     * @param groupId g
     * @param uid  u
     * @return ok
     */
    @GetMapping("/admin/join")
    public SingleResponse<Void> joinByAdmin(@RequestParam @NotNull int groupId,
                                            @RequestParam @NotNull int uid) {
        ResPair<Void> res = groupBizExecute.joinByAdmin(groupId, uid);
        if (!res.isOk()) {
            return ResponseKit.fail(res.getMsg());
        }
        return ResponseKit.ok();
    }


    /**
     * 批量加群
     * @param groupId d
     * @param ids i
     * @return 正确加群的ids
     */
    @GetMapping("/admin/multi/join")
    public SingleResponse<Integer> multiJoinByAdmin(int groupId,List<Integer> ids){
        ResPair<Integer> res = groupBizExecute.multiJoinByAdmin(groupId, ids);
        if (!res.isOk()) {
            return ResponseKit.fail(res.getMsg());
        }
        return ResponseKit.ok(res.getData());
    }

    /**
     * 设置用户为管理员
     * @param groupId g
     * @param uid 被设置的用户
     * @return ok
     */
    @GetMapping("/admin/set")
    public SingleResponse<Void> setAdmin(@RequestParam @NotNull int groupId,
                                         @RequestParam @NotNull int uid){
        ResPair<Void> res = groupBizExecute.setAdmin(groupId, uid);
        if (!res.isOk()) {
            return ResponseKit.fail(res.getMsg());
        }
        return ResponseKit.ok();
    }

    /**
     * 移除管理员
     * @param groupId g
     * @param uid u
     * @return ok
     */
    @GetMapping("/admin/remove")
    public SingleResponse<Void> removeAdmin(@RequestParam @NotNull int groupId,
                                            @RequestParam @NotNull int uid){
        ResPair<Void> res = groupBizExecute.removeAdmin(groupId, uid);
        if (!res.isOk()) {
            return ResponseKit.fail(res.getMsg());
        }
        return ResponseKit.ok();
    }

    /**
     * 查询群成员
     * @param groupId g
     * @return {@link GroupMemberVO}
     */
    @GetMapping("/members")
    public SingleResponse<List<GroupMemberVO>> members(@RequestParam @NotNull int groupId){
        ResPair<List<GroupMemberVO>> res = groupBizExecute.members(groupId);
        if (!res.isOk()) {
            return ResponseKit.fail(res.getMsg());
        }
        return ResponseKit.ok(res.getData());
    }
    /**
     * 转移群主
     * @param groupId g
     * @param uid 被转移用户
     * @return ok
     */
    @GetMapping("/owner")
    public SingleResponse<Void> changeOwner(@RequestParam @NotNull int groupId,
                                            @RequestParam @NotNull int uid) {
        ResPair<Void> res = groupBizExecute.changeOwner(groupId, uid);
        if (!res.isOk()) {
            return ResponseKit.fail(res.getMsg());
        }
        return ResponseKit.ok(res.getData());
    }


    /**
     * 封禁用户
     * @param groupId g
     * @param uid u
     * @return ok
     */
    @GetMapping("/block")
    public SingleResponse<Void> block(@RequestParam @NotNull int groupId,
                                      @RequestParam @NotNull int uid){
        ResPair<Void> res = groupBizExecute.block(groupId, uid);
        if (!res.isOk()) {
            return ResponseKit.fail(res.getMsg());
        }
        return ResponseKit.ok(res.getData());
    }
    /**
     * 修改群成员对群聊的设置<br>
     * 区别于群管理员对群本身的设置<br>
     * @param dto {@link GroupMemberSettingDTO}
     * @return ok
     */
    @PostMapping("/setting")
    public SingleResponse<Void> setMemberGroupSetting(@RequestBody @Valid GroupMemberSettingDTO dto){
        ResPair<Void> res = groupBizExecute.setting(dto);
        if (!res.isOk()) {
            return ResponseKit.fail(res.getMsg());
        }
        return ResponseKit.ok(res.getData());
    }

    /**
     * 获取群设置
     * @return {@link GroupMemberSettingVO}
     */
    @GetMapping("/my/settings")
    public SingleResponse<List<GroupMemberSettingVO>> myGroupSettings(){
        ResPair<List<GroupMemberSettingVO>> res = groupBizExecute.mySettings();
        if (!res.isOk()) {
            return ResponseKit.fail(res.getMsg());
        }
        return ResponseKit.ok(res.getData());
    }
}

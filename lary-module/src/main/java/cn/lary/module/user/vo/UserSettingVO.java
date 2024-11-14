package cn.lary.module.user.vo;

import cn.lary.module.user.entity.UserSetting;
import lombok.Data;

@Data
public class UserSettingVO {

    /**
     * 粉丝列表是否展示
     */
    private int fanList;

    /**
     * 勋章是否展示
     */
    private int medal;

    /**
     * 动态是否展示
     */
    private int  dynamic;

    /**
     * 新消息提醒
     */
    private int  newMessageNotice;

    public UserSettingVO(){}
    public UserSettingVO(UserSetting setting) {
        setFanList(setting.getFanList());
        setMedal(setting.getMedal());
        setDynamic(setting.getDynamic());
        setNewMessageNotice(setting.getNewMessageNotice());
    }
}

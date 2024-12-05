package cn.lary.api.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author paul
 * @since 2024-07-29
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("user_setting")
public class UserSetting implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * setting id
     */
    private Long sid;
    /**
     * uid
     */
    private Long uid;

    /**
     * 粉丝列表是否展示
     */
    private Integer fanList;

    /**
     * 勋章是否展示
     */
    private Integer medal;

    /**
     * 动态是否展示
     */
    private Integer dynamic;

    /**
     * 新消息提醒
     */
    private Integer newMessageNotice;



}

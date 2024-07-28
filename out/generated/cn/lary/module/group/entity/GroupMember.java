package cn.lary.module.group.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

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
@TableName("group_member")
public class GroupMember implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String groupNo;

    private String uid;

    private String remark;

    private Short role;

    private Long version;

    private Short isDeleted;

    private Short status;

    private String vercode;

    private Short robot;

    private String inviteUid;

    /**
     * 群成员禁言时长
     */
    private Integer forbiddenExpireTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createAt;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateAt;
}

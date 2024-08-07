package cn.lary.module.app.entity;

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
 * @since 2024-08-07
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("short_no")
public class ShortNo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 唯一短编号
     */
    private String shortNo;

    /**
     * 是否被用
     */
    private Short used;

    /**
     * 保留，保留的号码将不会再被分配
     */
    private Short hold;

    /**
     * 是否被锁定，锁定了的短编号将不再被分配,直到解锁
     */
    private Short locked;

    /**
     * 被使用的业务，比如 user
     */
    private String business;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createAt;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateAt;
}

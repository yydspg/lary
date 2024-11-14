package cn.lary.module.channel.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author paul
 * @since 2024-11-14
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("lary_channel")
public class LaryChannel implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 频道id
     */
    private Long cid;

    /**
     * 频道类型
     */
    private Integer type;

    /**
     * 用户id
     */
    private Long uid;

    /**
     * 直播间id
     */
    private Long rid;

    /**
     * 状态
     */
    private Integer status;

      @TableField(fill = FieldFill.INSERT)
    private Long createAt;
}

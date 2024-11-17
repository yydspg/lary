package cn.lary.module.invest.entity;

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
 * @since 2024-11-17
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("invest_record")
public class InvestRecord implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 直播id
     */
    private Long sid;

    /**
     * 投放id
     */
    private Long lid;

    /**
     * 直播间id
     */
    private Long rid;

    /**
     * uid
     */
    private Long uid;

    /**
     * 状态
     */
    private Integer sync;

    /**
     * 创建时间
     */
    private Long createAt;
}

package cn.lary.module.report.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author paul
 * @since 2024-10-02
 */
@Getter
@Setter
@Accessors(chain = true)
public class Report implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 举报用户
     */
    private Integer uid;

    /**
     * 类别编号
     */
    private Integer categoryNo;

    /**
     * 频道ID
     */
    private Integer channelId;

    /**
     * 频道类型
     */
    private Short channelType;

    /**
     * 图片路径集合
     */
    private String imageUrl;

    /**
     * 备注
     */
    private String remark;

    /**
     * 状态 0,未处理，1,已处理
     */
    private Short status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

package cn.lary.module.report.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("report_category")
public class ReportCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 类别编号
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 类别名称
     */
    private String name;

    /**
     * 父类别编号
     */
    private Integer parentId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

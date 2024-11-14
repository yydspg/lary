package cn.lary.module.common.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Getter
@Setter
@Accessors(chain = true)
public class Event {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Integer category;

    private Integer type;


    private String data;

    private Integer status;

    private Integer version;


}

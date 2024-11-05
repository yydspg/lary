package cn.lary.common.dto;


import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

/**
 * Page Query Param
 *
 * @author paul
 */
@Getter
@Setter
public  class PageQuery  {

    @JSONField(name = "page_size")
    private Long pageSize ;


    @JSONField(name = "page_index")
    private Long pageIndex ;

}

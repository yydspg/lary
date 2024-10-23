package cn.lary.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("page_size")
    private Long pageSize ;


    @JsonProperty("page_index")
    private Long pageIndex ;

}

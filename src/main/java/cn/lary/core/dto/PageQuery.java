package cn.lary.core.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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

    @Min(value = 0,message = "page size must greater than 0")
    @Max(value = 20,message = "page size must less than 20")
    @JsonProperty("page_size")
    private Long pageSize ;

    @Min(value = 0,message = "page index must greater than 0")
    @JsonProperty("page_index")
    private Long pageIndex ;

}

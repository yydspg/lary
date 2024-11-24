package cn.lary.comment.dto;

import cn.lary.common.dto.PageQuery;
import lombok.Data;

@Data
public class RootCommentPageQueryDTO  extends PageQuery {

    private Integer sort;

    private Long eid;


}

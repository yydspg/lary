package cn.lary.api.comment.dto;

import cn.lary.common.dto.PageQuery;
import lombok.Data;

@Data
public class NextCommentPageQueryDTO extends PageQuery {

    private Integer sort;

    private Long rid;
}

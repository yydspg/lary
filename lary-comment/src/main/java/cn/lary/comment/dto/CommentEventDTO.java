package cn.lary.comment.dto;

import lombok.Data;

@Data
public class CommentEventDTO {
    /**
     * 事件类型id
     */
    private Integer tid;

    /**
     * 事件描述
     */
    private String content;

    /**
     * 事件标题
     */
    private String title;
}

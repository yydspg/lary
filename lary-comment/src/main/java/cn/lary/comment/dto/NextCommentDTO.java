package cn.lary.comment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NextCommentDTO {
    /**
     * 评论root id
     */
    @NotNull
    private Long rid;

    /**
     * 评论父id
     */
    @NotNull
    private Long pid;
    /**
     * 评论事件id
     */
    @NotNull
    private Long eid;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 被提及用户
     */
    private String mentions;

    /**
     * 评论图片
     */
    private String images;


}

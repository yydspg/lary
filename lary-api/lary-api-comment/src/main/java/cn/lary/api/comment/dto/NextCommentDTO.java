package cn.lary.api.comment.dto;


import lombok.Data;

@Data
public class NextCommentDTO {
    /**
     * 评论root id
     */
    private Long rid;

    /**
     * 评论父id
     */
    private Long pid;
    /**
     * 评论事件id
     */
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

package cn.lary.comment.dto;

import lombok.Data;

@Data
public class RootCommentDTO {

    /**
     * 评论事件id
     */
    private Long eid;
    /**
     * 评论发起者uid
     */
    private Long uid;

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

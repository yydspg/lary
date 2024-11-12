package cn.lary.module.comment.vo;


import cn.lary.module.comment.entity.RootComment;
import lombok.Data;

@Data
public class RootCommentVO {
    /**
     * 评论id
     */
    private long cid;

    /**
     * 评论事件id
     */
    private long eid;

    /**
     * 用户uid
     */
    private long uid;

    /**
     * 用户名
     */
    private String name;

    /**
     * 用户头像
     */
    private String avatar;

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

    /**
     * 评论等级
     */
    private int level;

    /**
     * 评论策略
     */
    private int policy;

    /**
     * 评论Ip
     */
    private String ipLabel;

    /**
     * 评论状态
     */
    private int status;

    /**
     * 回复数量
     */
    private int replyCount;

    /**
     * 点赞数量
     */
    private int starCount;

    public RootCommentVO(){
        
    }
    public RootCommentVO(RootComment comment){
        
    }
}

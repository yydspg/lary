package cn.lary.module.comment.service;

import cn.lary.common.dto.ResponsePair;
import cn.lary.module.comment.dto.RootCommentDTO;
import cn.lary.module.comment.dto.RootCommentPageQueryDTO;
import cn.lary.module.comment.entity.Comment;
import cn.lary.module.comment.vo.RootCommentVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author paul
 * @since 2024-11-10
 */
public interface CommentService extends IService<Comment> {
    /**
     * 评论root
     * @param dto {@link RootCommentDTO}
     * @return ok
     */
    ResponsePair<Void> comment(RootCommentDTO dto);

    /**
     * 隐藏评论
     * @param cid c
     * @return ok
     */
    ResponsePair<Void> hide(long cid);

    /**
     * 查询root评论
     * @param dto {@link RootCommentPageQueryDTO}
     * @return {@link RootCommentVO}
     */
    ResponsePair<List<RootCommentVO>> show(RootCommentPageQueryDTO dto);



}

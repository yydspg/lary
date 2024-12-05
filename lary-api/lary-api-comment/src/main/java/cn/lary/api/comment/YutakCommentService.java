package cn.lary.api.comment;

import cn.lary.api.comment.dto.*;
import cn.lary.api.comment.vo.NextCommentVO;
import cn.lary.api.comment.vo.RootCommentVO;
import cn.lary.common.dto.ResponsePair;

import java.util.List;

public interface YutakCommentService {

    /**
     * 评论root
     * @param dto {@link RootCommentDTO}
     * @return OK
     */
    ResponsePair<Void> comment(RootCommentDTO dto);

    /**
     * 隐藏评论
     * @param cid c
     * @return OK
     */
    ResponsePair<Void> hideRoot(long cid);

    /**
     * 查询root评论
     * @param dto {@link RootCommentPageQueryDTO}
     * @return {@link RootCommentVO}
     */
    ResponsePair<List<RootCommentVO>> show(RootCommentPageQueryDTO dto);

    /**
     * 评论next
     * @param dto {@link NextCommentDTO}
     * @return OK
     */
    ResponsePair<Void> comment(NextCommentDTO dto);

    /**
     * 隐藏评论
     * @param cid c
     * @return OK
     */
    ResponsePair<Void> hideNext(long cid);

    /**
     * 查询next评论
     * @param dto {@link RootCommentPageQueryDTO}
     * @return {@link RootCommentVO}
     */
    ResponsePair<List<NextCommentVO>> show(NextCommentPageQueryDTO dto);

    /**
     * 创建评论事件
     * @param dto {@link CommentEventDTO}
     * @return OK
     */
    ResponsePair<Void> event(CommentEventDTO dto);
}

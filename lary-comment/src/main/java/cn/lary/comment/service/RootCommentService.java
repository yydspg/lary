package cn.lary.comment.service;

import cn.lary.comment.dto.RootCommentDTO;
import cn.lary.comment.dto.RootCommentPageQueryDTO;
import cn.lary.comment.entity.RootComment;
import cn.lary.comment.vo.RootCommentVO;
import cn.lary.common.dto.ResponsePair;
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
public interface RootCommentService extends IService<RootComment> {
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
    ResponsePair<Void> hide(long cid);

    /**
     * 查询root评论
     * @param dto {@link RootCommentPageQueryDTO}
     * @return {@link RootCommentVO}
     */
    ResponsePair<List<RootCommentVO>> show(RootCommentPageQueryDTO dto);



}

package cn.lary.comment.service;

import cn.lary.api.comment.dto.NextCommentDTO;
import cn.lary.api.comment.dto.NextCommentPageQueryDTO;
import cn.lary.api.comment.entity.NextComment;
import cn.lary.api.comment.vo.NextCommentVO;
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
public interface NextCommentService extends IService<NextComment> {

    ResponsePair<Void> comment(NextCommentDTO dto);


    ResponsePair<Void> hide(long cid);


    ResponsePair<List<NextCommentVO>> show(NextCommentPageQueryDTO dto);
}

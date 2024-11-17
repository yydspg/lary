package cn.lary.module.comment.service;

import cn.lary.common.dto.ResponsePair;
import cn.lary.module.comment.dto.NextCommentDTO;
import cn.lary.module.comment.dto.NextCommentPageQueryDTO;
import cn.lary.module.comment.entity.NextComment;
import cn.lary.module.comment.vo.NextCommentVO;
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

package cn.lary.comment.service;

import cn.lary.comment.dto.CommentEventDTO;
import cn.lary.comment.entity.CommentEvent;
import cn.lary.common.dto.ResponsePair;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author paul
 * @since 2024-11-10
 */
public interface CommentEventService extends IService<CommentEvent> {

    /**
     * 创建评论事件
     * @param dto {@link CommentEventDTO}
     * @return OK
     */
    ResponsePair<Void> event(CommentEventDTO dto);
}

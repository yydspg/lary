package cn.lary.stream.service;

import cn.lary.api.stream.entity.StreamTag;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author paul
 * @since 2024-08-16
 */
public interface StreamTagService extends IService<StreamTag> {

     boolean verify(int tag);
}

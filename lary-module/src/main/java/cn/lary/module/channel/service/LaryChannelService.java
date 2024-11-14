package cn.lary.module.channel.service;

import cn.lary.module.channel.entity.LaryChannel;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author paul
 * @since 2024-11-14
 */
public interface LaryChannelService extends IService<LaryChannel> {

    LaryChannel build(LaryChannel dto);
}

package cn.lary.module.common.service;


import cn.lary.module.common.entity.Event;
import cn.lary.module.event.dto.AbstractEventData;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author paul
 * @since 2024-08-07
 */
public interface EventService extends IService<Event> {

    long begin(AbstractEventData abstractEventData);

    void commit(long eventId);

    void fail(long eventId,String reason);
}

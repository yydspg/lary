package cn.lary.module.app.service;

import cn.lary.module.app.entity.Event;
import cn.lary.module.app.entity.EventData;
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
    int begin(EventData eventData);
    void commit(long eventID);
}

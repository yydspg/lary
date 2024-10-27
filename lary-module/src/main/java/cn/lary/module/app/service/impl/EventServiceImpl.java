package cn.lary.module.app.service.impl;

import cn.lary.module.app.service.AbstractEventData;
import cn.lary.module.app.entity.Event;
import cn.lary.module.app.mapper.EventMapper;
import cn.lary.module.app.service.EventService;
import cn.lary.module.common.constant.LARY;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author paul
 * @since 2024-08-07
 */
@Slf4j
@Service
public class EventServiceImpl extends ServiceImpl<EventMapper, Event> implements EventService {

    @Override
    public long begin(AbstractEventData abstractEventData) {
        Event e = new Event()
                .setCategory(abstractEventData.getCategory())
                .setData(abstractEventData.getData())
                .setType(abstractEventData.getType());
        save(e);
        return e.getId();
    }

    @Override
    public void commit(long eventId) {
        Event event = new Event()
                .setId(eventId)
                .setStatus(LARY.EVENT.STATUS.COMMIT);
        updateById(event);
    }

    @Override
    public void fail(long eventId, String reason) {
        Event event = new Event()
                .setStatus(LARY.EVENT.STATUS.FAIL)
                .setId(eventId);
        updateById(event);
    }
}

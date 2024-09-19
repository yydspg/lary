package cn.lary.module.app.service.impl;

import cn.lary.module.app.entity.Event;
import cn.lary.module.app.entity.EventData;
import cn.lary.module.app.mapper.EventMapper;
import cn.lary.module.app.service.EventService;
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
    public int begin(EventData eventData) {
        Event e = new Event().setEvent(eventData.getEvent()).setData(eventData.getData()).setType(eventData.getType());
        return baseMapper.insert(e);
    }

    @Override
    public void commit(int eventID) {
        // post handler
        Event e = baseMapper.selectById(eventID);
        if (e == null) {
            log.error("query event error, eventID:{}", eventID);
            return;
        }

    }
}

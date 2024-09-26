package cn.lary.module.app.service.impl;

import cn.lary.module.app.entity.Event;
import cn.lary.module.app.entity.EventData;
import cn.lary.module.app.mapper.EventMapper;
import cn.lary.module.app.service.EventService;
import cn.lary.module.common.CS.Lary;
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
        baseMapper.insert(e);
        return e.getId();
    }

    @Override
    public void commit(int eventID) {
        // post handler
        Event event = new Event().setId(eventID).setStatus(Lary.EventStatus.commit);
        baseMapper.updateById(event);
    }
}

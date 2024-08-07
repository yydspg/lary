package cn.lary.module.app.serviceImpl;

import cn.lary.module.app.entity.Event;
import cn.lary.module.app.mapper.EventMapper;
import cn.lary.module.app.service.EventService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author paul
 * @since 2024-08-07
 */
@Service
public class EventServiceImpl extends ServiceImpl<EventMapper, Event> implements EventService {

}

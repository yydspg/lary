package cn.lary.module.order.serviceImpl;

import cn.lary.module.order.entity.Order;
import cn.lary.module.order.mapper.OrderMapper;
import cn.lary.module.order.service.OrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author paul
 * @since 2024-10-28
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

}

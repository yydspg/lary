package cn.lary.module.group.serviceImpl;

import cn.lary.module.group.entity.Group;
import cn.lary.module.group.mapper.GroupMapper;
import cn.lary.module.group.service.GroupService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author paul
 * @since 2024-07-29
 */
@Service
public class GroupServiceImpl extends ServiceImpl<GroupMapper, Group> implements GroupService {

}

package cn.lary.module.app.service.impl;

import cn.lary.module.app.entity.AppConfig;
import cn.lary.module.app.entity.AppConfigRes;
import cn.lary.module.app.mapper.AppConfigMapper;
import cn.lary.module.app.service.AppConfigService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author paul
 * @since 2024-08-03
 */
@Service
public class AppConfigServiceImpl extends ServiceImpl<AppConfigMapper, AppConfig> implements AppConfigService {

    @Override
    public AppConfigRes getAppConfig() {
        LambdaQueryWrapper<AppConfig> lw = new LambdaQueryWrapper<>();
        lw.orderByDesc(AppConfig::getCreateAt);
        lw.last("limit 1");
        AppConfig app = baseMapper.selectOne(lw);
        return AppConfigRes.build(app);
    }
}

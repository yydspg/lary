package cn.lary.module.app.service;

import cn.lary.module.app.entity.AppConfig;
import cn.lary.module.app.entity.AppConfigRes;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author paul
 * @since 2024-08-03
 */
public interface AppConfigService extends IService<AppConfig> {

    public AppConfigRes getAppConfig();
}

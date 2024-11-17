package cn.lary.module.advertisement.service;

import cn.lary.common.dto.ResponsePair;
import cn.lary.module.advertisement.dto.ProviderBuildDTO;
import cn.lary.module.advertisement.dto.ProviderUpdateDTO;
import cn.lary.module.advertisement.entity.Provider;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author paul
 * @since 2024-11-17
 */
public interface ProviderService extends IService<Provider> {

    ResponsePair<Provider> build(ProviderBuildDTO dto);

    ResponsePair<Void> update(ProviderUpdateDTO dto);
}

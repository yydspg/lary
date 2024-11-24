package cn.lary.advertisement.service;

import cn.lary.advertisement.dto.ProviderBuildDTO;
import cn.lary.advertisement.dto.ProviderUpdateDTO;
import cn.lary.advertisement.entity.Provider;
import cn.lary.common.dto.ResponsePair;
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

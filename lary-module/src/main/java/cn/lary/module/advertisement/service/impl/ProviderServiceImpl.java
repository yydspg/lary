package cn.lary.module.advertisement.service.impl;

import cn.lary.common.dto.ResponsePair;
import cn.lary.common.kit.BusinessKit;

import cn.lary.module.advertisement.dto.ProviderBuildDTO;
import cn.lary.module.advertisement.dto.ProviderUpdateDTO;
import cn.lary.module.advertisement.entity.Provider;
import cn.lary.module.advertisement.mapper.ProviderMapper;
import cn.lary.module.advertisement.service.ProviderService;
import cn.lary.module.common.constant.LARY;
import cn.lary.common.id.LaryIDBuilder;
import cn.lary.common.id.SystemClock;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author paul
 * @since 2024-11-17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProviderServiceImpl extends ServiceImpl<ProviderMapper, Provider> implements ProviderService {

    private final LaryIDBuilder builder;

    @Override
    public ResponsePair<Provider> build(ProviderBuildDTO dto) {
        Provider provider = new Provider()
                .setPid(builder.next())
                .setAmount(dto.getAmount())
                .setName(dto.getName())
                .setStatus(LARY.AD.PROVIDER.COMMON)
                .setCreateAt(SystemClock.now());
        save(provider);
        return BusinessKit.ok(provider);
    }

    @Override
    public ResponsePair<Void> update(ProviderUpdateDTO dto) {
        lambdaUpdate()
                .set(Provider::getName, dto.getName())
                .set(Provider::getStatus, dto.getStatus())
                .eq(Provider::getPid, dto.getPid())
                .update();
        return BusinessKit.ok();
    }
}

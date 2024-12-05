package cn.lary.advertisement.service.impl;

import cn.lary.api.advertisement.constant.AD;
import cn.lary.api.advertisement.dto.ADPageQueryDTO;
import cn.lary.api.advertisement.entity.Advertisement;
import cn.lary.api.advertisement.entity.Provider;
import cn.lary.advertisement.mapper.AdvertisementMapper;
import cn.lary.advertisement.service.AdvertisementService;
import cn.lary.advertisement.service.ProviderService;
import cn.lary.api.advertisement.vo.AdvertisementVO;
import cn.lary.common.dto.ResponsePair;
import cn.lary.common.kit.BusinessKit;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

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
public class AdvertisementServiceImpl extends ServiceImpl<AdvertisementMapper, Advertisement> implements AdvertisementService {

    private final ProviderService providerService;


    @Override
    public ResponsePair<List<AdvertisementVO>> my(ADPageQueryDTO dto) {
        Provider provider = providerService.lambdaQuery()
                .select(Provider::getPid, Provider::getStatus)
                .eq(Provider::getPid, dto.getPid())
                .one();
        if (provider == null) {
            return BusinessKit.fail("no provider found");
        }
        if (provider.getStatus() == AD.PROVIDER.DISBAND
                || provider.getStatus() == AD.PROVIDER.BLOCK) {
            return BusinessKit.fail("provider status error");
        }
        return BusinessKit.ok(lambdaQuery()
                .eq(Advertisement::getPid,provider.getPid())
                .page(new Page<>(dto.getPageIndex(),dto.getPageSize()))
                .getRecords()
                .stream()
                .map(AdvertisementVO::new)
                .toList());
    }

}

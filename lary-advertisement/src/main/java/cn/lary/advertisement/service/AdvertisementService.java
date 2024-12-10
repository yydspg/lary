package cn.lary.advertisement.service;

import cn.lary.api.advertisement.dto.ADPageQueryDTO;
import cn.lary.api.advertisement.entity.Advertisement;
import cn.lary.api.advertisement.vo.AdvertisementVO;
import cn.lary.common.dto.ResponsePair;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author paul
 * @since 2024-11-17
 */
public interface AdvertisementService extends IService<Advertisement> {


    /**
     * 查询广告充值记录
     * @param dto {@link ADPageQueryDTO}
     * @return {@link AdvertisementVO}
     */
    ResponsePair<List<AdvertisementVO>> my(ADPageQueryDTO dto);
}

package cn.lary.module.advertisement.service;

import cn.lary.common.dto.ResponsePair;
import cn.lary.module.advertisement.entity.Advertisement;
import cn.lary.module.advertisement.dto.ADPageQueryDTO;
import cn.lary.module.advertisement.vo.AdvertisementVO;
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



    ResponsePair<List<AdvertisementVO>> my(ADPageQueryDTO dto);
}

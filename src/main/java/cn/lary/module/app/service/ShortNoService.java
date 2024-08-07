package cn.lary.module.app.service;

import cn.lary.module.app.entity.ShortNo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author paul
 * @since 2024-08-07
 */
public interface ShortNoService extends IService<ShortNo> {
    String getShortNo();
    void setShortNoUsed(String shortNo,String business);
}

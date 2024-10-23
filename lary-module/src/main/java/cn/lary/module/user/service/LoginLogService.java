package cn.lary.module.user.service;

import cn.lary.common.dto.ResponsePair;
import cn.lary.module.user.dto.LoginLogPageQueryDTO;
import cn.lary.module.user.entity.LoginLog;
import cn.lary.module.user.vo.LoginVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author paul
 * @since 2024-10-07
 */
public interface LoginLogService extends IService<LoginLog> {
    /**
     * 查询登录记录
     * @param dto {@link LoginLogPageQueryDTO}
     * @return {@link LoginVO}
     */
    ResponsePair<List<LoginVO>> logins(LoginLogPageQueryDTO dto);

    /**
     * 删除历史登录记录
     * @return ok
     */
    ResponsePair<Void> clearHistory();
}

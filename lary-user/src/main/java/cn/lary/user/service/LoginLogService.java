package cn.lary.user.service;

import cn.lary.common.dto.ResponsePair;
import cn.lary.api.user.dto.LoginLogPageQueryDTO;
import cn.lary.api.user.entity.LoginLog;
import cn.lary.api.user.vo.LoginVO;
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
     * @return OK
     */
    ResponsePair<Void> clearHistory();

    /**
     * 创建 LoginLog,无需手动分配id
     * @param log {@link LoginLog}
     * @return OK
     */
    LoginLog build(LoginLog log);
}

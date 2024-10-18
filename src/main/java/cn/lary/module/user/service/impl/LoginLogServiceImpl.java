package cn.lary.module.user.service.impl;

import cn.lary.core.context.RequestContext;
import cn.lary.core.dto.ResponsePair;
import cn.lary.kit.BizKit;
import cn.lary.kit.CollectionKit;
import cn.lary.module.user.dto.LoginLogPageQueryDTO;
import cn.lary.module.user.entity.LoginLog;
import cn.lary.module.user.mapper.LoginLogMapper;
import cn.lary.module.user.service.LoginLogService;
import cn.lary.module.user.vo.LoginVO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author paul
 * @since 2024-10-07
 */
@Slf4j
@Service
public class LoginLogServiceImpl extends ServiceImpl<LoginLogMapper, LoginLog> implements LoginLogService {

    @Override
    public ResponsePair<List<LoginVO>> logins(LoginLogPageQueryDTO dto) {
        List<LoginLog> data = lambdaQuery()
                .eq(LoginLog::getUid, RequestContext.getLoginUID())
                .list();
        if (CollectionKit.isEmpty(data)) {
            log.error("search login log error,uid:{}", RequestContext.getLoginUID());
            return BizKit.fail("no login log data found");
        }
        return BizKit.ok( data.stream()
                .map(LoginVO::new)
                .toList());
    }

    @Override
    public ResponsePair<Void> clearHistory() {
        lambdaUpdate()
                .set(LoginLog::getIsDelete,true)
                .eq(LoginLog::getUid, RequestContext.getLoginUID());
        return BizKit.ok();
    }
}

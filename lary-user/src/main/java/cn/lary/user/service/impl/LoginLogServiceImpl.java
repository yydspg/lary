package cn.lary.user.service.impl;

import cn.lary.common.constant.LARY;
import cn.lary.common.context.RequestContext;
import cn.lary.common.dto.ResponsePair;
import cn.lary.common.id.LaryIDBuilder;
import cn.lary.common.kit.BusinessKit;
import cn.lary.common.kit.CollectionKit;
import cn.lary.user.dto.LoginLogPageQueryDTO;
import cn.lary.user.entity.LoginLog;
import cn.lary.user.mapper.LoginLogMapper;
import cn.lary.user.service.LoginLogService;
import cn.lary.user.vo.LoginVO;
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
 * @since 2024-10-07
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoginLogServiceImpl extends ServiceImpl<LoginLogMapper, LoginLog> implements LoginLogService {

    private final LaryIDBuilder builder;

    @Override
    public ResponsePair<List<LoginVO>> logins(LoginLogPageQueryDTO dto) {
        List<LoginLog> data = lambdaQuery()
                .select(LoginLog::getLevel,LoginLog::getIp,
                        LoginLog::getFlag,LoginLog::getName)
                .eq(LoginLog::getUid, RequestContext.uid())
                .list();
        if (CollectionKit.isEmpty(data)) {
            log.error("search login log error,uid:{}", RequestContext.uid());
            return BusinessKit.fail("no login log data found");
        }
        return BusinessKit.ok(data.stream()
                .map(LoginVO::new)
                .toList());
    }

    @Override
    public ResponsePair<Void> clearHistory() {
        lambdaUpdate()
                .set(LoginLog::getStatus, LARY.REMOVE)
                .eq(LoginLog::getUid, RequestContext.uid())
                .update();
        return BusinessKit.ok();
    }

    @Override
    public final LoginLog build(LoginLog log) {
        log.setLid(builder.next());
        save(log);
        return log;
    }
}

package cn.lary.module.stream.core;

import cn.lary.core.context.RequestContext;
import cn.lary.core.dto.PageQuery;
import cn.lary.core.dto.ResponsePair;
import cn.lary.kit.BizKit;
import cn.lary.module.common.constant.Lary;
import cn.lary.module.common.cache.KVBuilder;
import cn.lary.module.common.cache.RedisCache;
import cn.lary.module.stream.dto.FollowDTO;
import cn.lary.module.stream.dto.FollowPageQueryDTO;
import cn.lary.module.stream.dto.LiveCacheDTO;
import cn.lary.module.stream.entity.Follow;
import cn.lary.module.stream.service.FollowService;
import cn.lary.module.user.entity.User;
import cn.lary.module.user.service.UserService;
import cn.lary.pkg.wk.api.WKMessageService;
import cn.lary.pkg.wk.dto.message.MessageHeader;
import cn.lary.pkg.wk.dto.message.MessageSendDTO;
import cn.lary.pkg.wk.constant.WK;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FollowBizExecute {

    private final FollowService followService;


    /**
     * 关注用户
     * @param req {@link FollowDTO}
     * @return ok
     */
    public ResponsePair<Void> follow( FollowDTO dto) {
        return followService.follow(dto);
    }

    /**
     * 取消关注
     * @param toUid t
     * @return ok
     */
    public ResponsePair<Void> unfollow( int toUid) {
        return followService.unfollow(toUid);
    }

    /**
     * 关注列表
     * @param uid u
     * @param dto {@link FollowPageQueryDTO}
     * @return {@link Follow}
     */
    public ResponsePair<List<Follow>> follows( FollowPageQueryDTO dto) {
        return followService.follows(dto);
    }

    /**
     * 拉黑
     * @param toUid t
     * @return ok
     */
    public ResponsePair<Void> block( int toUid) {
        return followService.block(toUid);
    }

    /**
     * 取消拉黑
     * @param toUid t
     * @return ok
     */
    public ResponsePair<Void> unblock( int toUid) {
       return followService.unblock(toUid);
    }

}

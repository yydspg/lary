package cn.lary.module.stream.service;

import cn.lary.module.stream.entity.Follow;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author paul
 * @since 2024-09-04
 */
public interface FollowService extends IService<Follow> {

    List<String> getFollows(int uid);
    boolean isBlock(int uid,int toUid);
    boolean isFan(int uid,int toUid);
}

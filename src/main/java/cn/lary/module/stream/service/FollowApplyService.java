package cn.lary.module.stream.service;

import cn.lary.module.stream.entity.FollowApply;
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
public interface FollowApplyService extends IService<FollowApply> {

    List<String> getFollows(String uid);
    boolean isBlock(String uid,String toUid);
    boolean isFan(String uid,String toUid);
}

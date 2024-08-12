package cn.lary.module.group.mapper;

import cn.lary.module.group.entity.Group;
import cn.lary.module.group.entity.GroupDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author paul
 * @since 2024-07-29
 */
public interface GroupMapper extends BaseMapper<Group> {

    List<GroupDetail> queryMySavedGroups(@Param("uid") String uid);
}

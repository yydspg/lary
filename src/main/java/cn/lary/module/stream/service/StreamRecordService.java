package cn.lary.module.stream.service;

import cn.lary.core.dto.ResponsePair;
import cn.lary.module.stream.entity.StreamRecord;
import cn.lary.module.stream.vo.StreamRecordVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author paul
 * @since 2024-08-16
 */
public interface StreamRecordService extends IService<StreamRecord> {
    /**
     * 分页查询
     * @param page 页码
     * @param size 大小
     * @return {@link StreamRecordVO}
     */
    ResponsePair<List<StreamRecordVO>> getPages(int page, int size);
}

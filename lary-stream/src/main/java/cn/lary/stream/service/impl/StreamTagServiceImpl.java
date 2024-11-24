package cn.lary.stream.service.impl;

import cn.lary.stream.entity.StreamTag;
import cn.lary.stream.mapper.StreamTagMapper;
import cn.lary.stream.service.StreamTagService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author paul
 * @since 2024-08-16
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StreamTagServiceImpl extends ServiceImpl<StreamTagMapper, StreamTag> implements StreamTagService {

    private final int[] tags = new int[]{1111,2222,3333};


    public boolean verify(int tag) {
        boolean flag = false;
        for (int j : tags) {
            if (j == tag) {
                flag = true;
                break;
            }
        }
        return flag;
    }
}

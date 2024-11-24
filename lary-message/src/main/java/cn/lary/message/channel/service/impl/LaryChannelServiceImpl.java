package cn.lary.message.channel.service.impl;

import cn.lary.common.id.LaryIDBuilder;
import cn.lary.message.channel.entity.LaryChannel;
import cn.lary.message.channel.mapper.LaryChannelMapper;
import cn.lary.message.channel.service.LaryChannelService;
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
 * @since 2024-11-14
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LaryChannelServiceImpl extends ServiceImpl<LaryChannelMapper, LaryChannel> implements LaryChannelService {

    private final LaryIDBuilder builder;

    @Override
    public LaryChannel build(LaryChannel dto) {
        dto.setCid(builder.next());
        save(dto);
        return dto;
    }
}

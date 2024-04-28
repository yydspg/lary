package cn.lary.id.rpc;

import cn.lary.api.id.SegmentIdService;
import cn.lary.api.id.Res;
import cn.lary.id.service.segment.SegService;
import lombok.RequiredArgsConstructor;


/**
 * @author paul 2024/4/27
 */


@RequiredArgsConstructor
public class SegmentIdServiceImpl implements SegmentIdService {

    private final SegService segService;
    @Override
    public Res get(String tag) {
        return segService.get(tag);
    }
}

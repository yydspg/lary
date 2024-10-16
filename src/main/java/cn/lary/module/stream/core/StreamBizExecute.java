package cn.lary.module.stream.core;

import cn.lary.core.dto.ResponsePair;
import cn.lary.module.stream.dto.*;
import cn.lary.module.stream.service.RaffleService;
import cn.lary.module.stream.service.RedPacketService;
import cn.lary.module.stream.service.StreamRecordService;
import cn.lary.module.stream.vo.StreamRecordVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StreamBizExecute {



    private final StreamRecordService streamRecordService;

    /**
     * 直播记录分页查询
     * @param page p
     * @param size s
     * @return {@link StreamRecordVO}
     */
    public ResponsePair<List<StreamRecordVO>> page(int page, int size) {
        return streamRecordService.getPages(page,size);
    }


}

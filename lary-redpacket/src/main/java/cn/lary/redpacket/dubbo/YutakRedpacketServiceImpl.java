package cn.lary.redpacket.dubbo;

import cn.lary.api.redpacket.YutakRedpacketService;
import cn.lary.api.redpacket.dto.RedpacketEventBuildDTO;
import cn.lary.api.redpacket.dto.RedpacketRecordPageQueryDTO;
import cn.lary.api.redpacket.vo.RedpacketEventVO;
import cn.lary.api.redpacket.vo.RedpacketRecordVO;
import cn.lary.api.redpacket.vo.RedpacketTokenVO;
import cn.lary.common.dto.MultiResponse;
import cn.lary.common.dto.ResponsePair;
import cn.lary.common.dto.SingleResponse;
import cn.lary.common.kit.ResponseKit;
import cn.lary.redpacket.component.RedpacketExecute;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Component;

import java.util.List;
@Slf4j
@Component
@DubboService
public class YutakRedpacketServiceImpl implements YutakRedpacketService {

    private RedpacketExecute redpacketExecute;

    @Override
    public SingleResponse<RedpacketTokenVO> join(Long toUid) {
        ResponsePair<RedpacketTokenVO> response = redpacketExecute.join(toUid);
        if (response.isFail()) {
            return ResponseKit.fail(response.getMsg());
        }
        return ResponseKit.ok();
    }

    @Override
    public SingleResponse<RedpacketEventVO>  info(Long toUid) {
        ResponsePair<RedpacketEventVO> response =redpacketExecute.info(toUid);
        if (response.isFail()) {
            return ResponseKit.fail(response.getMsg());
        }
        return ResponseKit.ok(response.getData());
    }

    @Override
    public SingleResponse<Void> build( RedpacketEventBuildDTO dto) {
        ResponsePair<Void> response = redpacketExecute.redpacket(dto);
        if (response.isFail()) {
            return ResponseKit.fail(response.getMsg());
        }
        return ResponseKit.ok();
    }

    @Override
    public MultiResponse<RedpacketRecordVO> my(RedpacketRecordPageQueryDTO dto) {
        ResponsePair<List<RedpacketRecordVO>> response = redpacketExecute.my(dto);
        if (response.isFail()) {
            return ResponseKit.multiFail(response.getMsg());
        }
        return ResponseKit.multiOk(response.getData());
    }
}

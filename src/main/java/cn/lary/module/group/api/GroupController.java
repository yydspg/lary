package cn.lary.module.group.api;

import cn.lary.core.dto.SingleResponse;
import cn.lary.module.group.core.GroupBizExecute;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/v1/group/")
@RequiredArgsConstructor
public class GroupController {

    private final GroupBizExecute groupBizExecute;

    public SingleResponse<Void> add(){
        return null;
    }
}


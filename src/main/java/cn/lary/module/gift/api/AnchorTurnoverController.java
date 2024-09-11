package cn.lary.module.gift.api;

import cn.lary.core.dto.PageResponse;
import cn.lary.module.gift.service.AnchorTurnoverService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 以主播维度，处理流水
 * <p>
 *  前端控制器
 * </p>
 *
 * @author paul
 * @since 2024-08-16
 */
@RestController
@RequestMapping("/v1/gift/anchor")
@RequiredArgsConstructor
public class AnchorTurnoverController {
    private final AnchorTurnoverService anchorTurnoverService;


}

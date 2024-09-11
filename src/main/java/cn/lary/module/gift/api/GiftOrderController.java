package cn.lary.module.gift.api;

import cn.lary.core.dto.SingleResponse;
import cn.lary.module.gift.service.GiftOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author paul
 * @since 2024-08-16
 */
@RestController
@RequestMapping("/v1/gift/buy")
@RequiredArgsConstructor
public class GiftOrderController {

    private final GiftOrderService giftOrderService;

    @PostMapping("/init")
    public SingleResponse create() {
        return null;
    }
}

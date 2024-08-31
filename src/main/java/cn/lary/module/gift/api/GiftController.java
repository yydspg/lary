package cn.lary.module.gift.api;

import cn.lary.core.cs.ResultCode;
import cn.lary.core.dto.MultiResponse;
import cn.lary.core.dto.SingleResponse;
import cn.lary.kit.ResKit;
import cn.lary.module.gift.entity.Gift;
import cn.lary.module.gift.entity.GiftType;
import cn.lary.module.gift.service.GiftService;
import cn.lary.module.gift.service.GiftTypeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/gift")
@RequiredArgsConstructor
public class GiftController {

    private final GiftService giftService;
    private final DataSourceTransactionManager transactionManager;
    private final  GiftTypeService giftTypeService;

    @PostMapping("")
    public SingleResponse createGift(Gift gift) {
        if(gift == null) {
            return ResKit.fail(ResultCode.NO_VALID_JSON);
        }
        LambdaQueryWrapper<GiftType> lw = new LambdaQueryWrapper<>();
        lw.eq(GiftType::getName, gift.getTypeName());
        GiftType giftType = giftTypeService.getOne(lw);
        if(giftType == null) {
            return ResKit.fail("gift type invalid");
        }
        // TODO  :  缺少对 creator 的校验，缺少事务管理
        giftService.save(gift);
        giftType.setCount(giftType.getCount() + 1);
        giftTypeService.saveOrUpdate(giftType);
        return ResKit.ok(gift);
    }
    @GetMapping()
    public MultiResponse list(@RequestParam(value = "typeName") String typeName) {
        LambdaQueryWrapper<Gift> lw = new LambdaQueryWrapper<>();
        lw.eq(Gift::getTypeName, typeName);
        List<Gift> gifts = giftService.list(lw);
        return ResKit.multiOk(gifts);
    }
    @GetMapping("/query")
    public SingleResponse getGift(@RequestParam(value = "id") Integer giftId) {
        Gift gift = giftService.getById(giftId);
        if(gift == null) {
            return ResKit.fail("gift id invalid");
        }
        return ResKit.ok(giftService.getById(giftId));
    }

}

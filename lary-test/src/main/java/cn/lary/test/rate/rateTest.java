package cn.lary.test.rate;


import cn.lary.core.constant.RateCS;
import cn.lary.core.model.vo.ApiRes;
import cn.lary.core.ratelimiter.anno.Rate;
import cn.lary.test.lock.service.TestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author paul 2024/4/25
 */
@Slf4j
@RestController
@RequestMapping("/rate/test")
@Tag(name = "限流接口测试模块")
@RequiredArgsConstructor
public class rateTest {
    private final TestService testService;
    @Operation(summary = "测试1")
    @RequestMapping(value = "/test1",method = RequestMethod.GET)
    @Rate(name = "test",count = 2,size = 1000000000)
    public ApiRes get(){
        testService.test1();
        return ApiRes.success();
    }
}

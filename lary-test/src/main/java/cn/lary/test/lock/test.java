package cn.lary.test.lock;

import cn.lary.core.lock.anno.Lock;
import cn.lary.core.model.vo.ApiRes;
import cn.lary.test.lock.service.TestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author paul 2024/4/23
 *
 */
@Slf4j
@RestController
@RequestMapping("/lock/test")
@Tag(name = "分布式锁测试模块")
@RequiredArgsConstructor
public class test {
    private final TestService testService;
    @Operation(summary = "测试1")
    @RequestMapping(value = "/test1",method = RequestMethod.GET)
    @Lock(name = "wdnmd", expire = 8000000,autoRelease = false)
    public ApiRes get(){
        testService.test1();
        return ApiRes.success();
    }
    // TODO 2024/4/24 : 为何此处无法释放锁
    @RequestMapping(value = "/test2",method = RequestMethod.GET)
    @Lock(name = "caonimacanima")
    public ApiRes get1() {
        return ApiRes.success();
    }
}

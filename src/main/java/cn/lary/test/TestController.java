package cn.lary.test;

import cn.lary.pkg.wk.api.ChannelService;
import cn.lary.pkg.wk.entity.Request.ChannelCreate;
import cn.lary.pkg.wk.entity.Response.Status;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import retrofit2.Response;

@RestController
public class TestController {
    @Resource
    private  ChannelService channelService;

    @GetMapping("/test")
    public void test() {
        ChannelCreate c = new ChannelCreate();
        Response<Status> res = channelService.clearOrUpdate(c);
        System.out.println(res.body());
    }
}

package cn.lary.oss.rest.aliyun;

import cn.lary.core.model.vo.ApiRes;
import cn.lary.oss.channel.aliyun.service.AliyunBucketService;
import cn.lary.oss.standard.args.bucket.CreateBucketArgs;
import cn.lary.oss.standard.args.bucket.DelBucketArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author paul 2024/4/19
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/oss/aliyun/bucket")
public class AliyunBucketCtrl {

    private final AliyunBucketService service;
    @RequestMapping(value = "/{bucketName}",method = RequestMethod.GET)
    public ApiRes exists(@PathVariable(value = "bucketName") String  bucketName) {
        return ApiRes.success(service.exist(bucketName));
    }
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public ApiRes list() {return ApiRes.success(service.list());}

    @RequestMapping(value = "/del",method = RequestMethod.POST)
    public ApiRes del(DelBucketArgs args) {
        service.del(args);
        return ApiRes.success();
    }
    @RequestMapping(value = "/del/{bucketName}",method = RequestMethod.GET)
    public ApiRes del(@PathVariable(value = "bucketName") String bucketName) {
        // pre check bucket exists
        service.del(bucketName);
        return ApiRes.success();
    }
    @RequestMapping(value = "/create",method = RequestMethod.POST)
    public ApiRes create(CreateBucketArgs args) {
        return ApiRes.success(service.create(args));
    }
    @RequestMapping(value = "/create/{bucketName}",method = RequestMethod.GET)
    public ApiRes create(@PathVariable(value = "bucketName") String  buckName) {
        // The requested bucket name is not available. The bucket namespace is shared by all users of the system. Please select a different name and try again
        return ApiRes.success(service.create(buckName));
    }
}

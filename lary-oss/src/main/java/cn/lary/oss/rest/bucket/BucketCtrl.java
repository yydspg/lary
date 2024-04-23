package cn.lary.oss.rest.bucket;

import cn.lary.oss.standard.factory.BucketChannelFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author paul 2024/4/23
 */
@RestController
@RequestMapping("/oss/bucket")
@RequiredArgsConstructor
public class BucketCtrl {

    private final BucketChannelFactory factory;

}

package cn.lary.api.advertisement;

import org.apache.dubbo.remoting.http12.HttpMethods;
import org.apache.dubbo.remoting.http12.rest.Mapping;

public interface YutakAdService {

    @Mapping(path = "/hi", method = HttpMethods.GET)
    String hello();

}

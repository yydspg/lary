package cn.lary.test.lock.service;

import cn.lary.core.lock.anno.Lock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author paul 2024/4/23
 */
@Slf4j
@Service
public class TestService {
    public void test1(){
        log.info("service-->test");
    }
}

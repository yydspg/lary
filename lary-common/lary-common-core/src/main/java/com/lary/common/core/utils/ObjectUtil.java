package com.lary.common.core.utils;

import com.lary.common.core.test.Monkey;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

/**
 * @author paul 2023/12/14
 */
@Slf4j
public class ObjectUtil {
    public static void main(String[] args) {
        Monkey monkey = new Monkey();
        String s = JSONUtil.toString(monkey);
        log.info(JSONUtil.toString(monkey));
        System.out.println(JSONUtil.toObject(s));
        System.out.println(JSONUtil.toObject(s, monkey.getClass()));
        System.out.println(JSONUtil.toCollect(s));
        System.out.println(JSONUtil.collectToString(JSONUtil.toCollect(s)));
        System.out.println(JSONUtil.toBean(s, Monkey.class));


    }
}

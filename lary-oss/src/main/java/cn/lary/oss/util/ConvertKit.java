package cn.lary.oss.util;

import org.springframework.core.convert.converter.Converter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author paul 2024/4/17
 */

public class ConvertKit {
    public static <T, R> List<R> convert(List<T> items, Converter<T, R> converter) {
        if (items != null && !items.isEmpty()) {
            return items.stream().map(converter::convert).toList();
        }
        return new ArrayList<>();
    }

    public static <T, R> R convert(T object, Converter<T, R> converter) {
        return converter.convert(object);
    }
}

package cn.lary.oss.standard.args;

import org.springframework.core.convert.converter.Converter;

/**
 * @author paul 2024/4/17
 */

public interface ArgsConverter<S, T> extends Converter<S, T> {
    void prepare(S source,T target);
    T get(S source);
    @Override
    default T convert(S s) {
        T t = get(s);
        prepare(s,t);
        return t;
    }
}

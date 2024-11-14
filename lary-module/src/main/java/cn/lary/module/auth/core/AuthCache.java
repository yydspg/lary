package cn.lary.module.auth.core;

public interface AuthCache {

    void set(String key, String value);

    void set(String key, String value, long timeout);

    String get(String key);

}

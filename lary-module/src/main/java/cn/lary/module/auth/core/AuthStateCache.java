package cn.lary.module.auth.core;


public interface AuthStateCache {

    void cache(String key, String value);


    void cache(String key, String value, long timeout);


    String get(String key);

    boolean containsKey(String key);
}

package cn.lary.module.auth.core;


public enum AuthDefaultStateCache implements AuthStateCache {


    INSTANCE;

    private AuthCache authCache;

    AuthDefaultStateCache() {
        authCache = null;
    }


    @Override
    public void cache(String key, String value) {
        authCache.set(key, value);
    }


    @Override
    public void cache(String key, String value, long timeout) {
        authCache.set(key, value, timeout);
    }


    @Override
    public String get(String key) {
        return authCache.get(key);
    }


    @Override
    public boolean containsKey(String key) {
        return false;
    }
}

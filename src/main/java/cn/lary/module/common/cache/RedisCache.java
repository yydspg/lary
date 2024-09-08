package cn.lary.module.common.cache;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public interface RedisCache {
      /*
        strings
     */

    // get k
    String get(String k);
    // set k v
    void set(String k,String v);
    //set k v exp
    void set(String k, String v, Long exp);
    // set k v exp timeUnit
    void set(String k, String v, Long exp, TimeUnit timeUnit);
    // mset k v [k v ...]
    void mset(Map<String,String> map);
    // mget k [k ...]
    List<String> mget(Collection<String> keys);
    void del(String k);

    /*
        hash
     */

    //hset k f v
    void setHash(String k,String f,String v);
    void incrHash(String k,String f);
    void decrHash(String k,String f);
    //mhset f v [f v ...]
    void setHash(String k,Map map);
    //hget k f
    String getHash(String k,String f);
    //mhget k f [ f ...]
    Map<Object,Object> getHash(String k);
    //heys k  #return all field names in the hash stored at key
    Collection<String> getHashFieldNames(String k);
    //hvals k  #return all the values in the hash stored at key
    Collection<String> getHashFieldValues(String k);
    //hdel k f [f...]
    void delHashFields(String k,Collection<String> fields);
}

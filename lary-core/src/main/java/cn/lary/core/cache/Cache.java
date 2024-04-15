package cn.lary.core.cache;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author paul 2024/4/9
 */

public interface Cache {
    /*
        strings
     */

    // get k
    String getString(String k);
    // set k v
    void setString(String k,String v);
    //set k v exp
    void setString(String k, String v, Long exp);
    // set k v exp timeUnit
    void setString(String k, String v, Long exp, TimeUnit timeUnit);
    // mset k v [k v ...]
    void setString(Map<String,String> map);
    // mget k [k ...]
    List<String> getString(Collection<String> keys);


    /*
        hash
     */

    //hset k f v
    void setHash(String k,String f,String v);
    //mhset f v [f v ...]
    void setHash(String k,Map<String,Object> map);
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


    /*
    zset
     */
    /*
    #s means score m means member
    zadd s m [s m ...] #adds all the specified members with the specified scorese
    #to the sorted set stored at k.if a m has already exist ,update and reinsert
    # to ensure the corret ordering
    zcard k # return sorted set cardinality (number of elements)

    zrem k m [m ...] #remover the specified m from k

    zincrby k increment m # inbcrement the score of m in k by increment

    zrange k b e # return the specified range of elements in k based on score size

    zrevrange k b e # according to zrange command,return remaining elements

    zscore k m #return m's score in k

    zrangebyscore min max [WITHSCORES] [LIMIT offset count]
    #return count elements in k with the score between min and max (including ==)

    zcount k min max #return number of elements in k with the score between min and max

    zrank k v #return the rank of member in k

    zrevrank k v #obtain reverse index

    zmpop numkeys k [k ...]<MIN | MAX> {COUNT count}
    # the number of k1, k2 ,k3 == numkeys
    #count = the number of every k return element(member + score)
    #MIN means return from head to tail ,MAX else
     */

}

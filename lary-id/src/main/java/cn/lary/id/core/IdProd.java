package cn.lary.id.core;

/**
 * @author paul 2024/4/25
 */

public interface IdProd {
    Res get(String tag);
    void init();
}

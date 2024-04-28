package cn.lary.id.core;

import cn.lary.api.id.Res;

/**
 * id produce interface
 * @author paul 2024/4/25
 */

public interface IdProd {
    Res get(String tag);
    void init();
}

package cn.lary.core.model.vo;

import com.alibaba.fastjson2.JSONObject;

/**
 * @author paul 2024/4/9
 */

public class BaseModel {
    // extension params
    private JSONObject ext;
    public void addExt (String k, Object v){
        if (ext == null) {
            ext = new JSONObject();
        }
        ext.put(k,v);
    }
    // get ext value
    public JSONObject extV() {
        return ext == null ? new JSONObject(): ext ;}
}

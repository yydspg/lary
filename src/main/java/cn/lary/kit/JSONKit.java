package cn.lary.kit;

import cn.lary.core.exception.SystemException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class JSONKit {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    public static String toJSON(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new SystemException(e.getMessage());
        }
    }
    public static Map<String,String> toMap(String json) {
        return null;
    }
}

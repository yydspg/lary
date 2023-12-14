package com.lary.common.core.utils;


import org.springframework.beans.BeanUtils;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 *
 * 定义自己的工具类，尽量不要在业务代码里面直接调用第三方的工具类。这是解耦体现
 * 2个不好的地方：
 * 不同开发会使用不同的第三方工具库
 * 将来不易修改工具类的实现逻辑
 *
 * @author paul 2023/12/14
 */

public class BeanUtil {
    /**
     * @param objectFrom: source
     * @param objectTo: target
     * @author paul
     * @description copy same properties from source to target
     * @date 2023/12/14 18:23
     */
    public static void copyProperties(Object objectFrom,Object objectTo){
        BeanUtils.copyProperties(objectFrom,objectTo);
    }

    /**
     * @param o: 类对象(Object 传入无意义)
     * @return String
     * @author paul
     * @description get current Object properties
     * @date 2023/12/14 18:41
     */
    public static String[] getFieldNames(Object o){
        Field[] fields = o.getClass().getFields();
        Field[] superFields = o.getClass().getSuperclass().getFields();
        String[] fieldName = new String[fields.length + superFields.length];
        int index = 0;
        for (Field field : fields) {
            fieldName[index++] = field.getName();
        }
        //获取继承字段
        for (Field superField : superFields) {
            fieldName[index++] = "id".equals(superField.getName()) ? null : superField.getName();
        }
        return fieldName;
    }

    /**
     * @param o: object
     * @param k: field name
     * @return The value corresponding to the attribute
     * @author paul
     * @description get field by getter method
     * @date 2023/12/14 19:09
     */
    public static Object getFiledValueByName(Object o,String k){
        try {
            StringBuilder sb = new StringBuilder("get");
            sb.append(k);
            //插入
            sb.replace(3,4,String.valueOf(sb.charAt(3)).toUpperCase());
            System.out.println(sb.toString());
            Method method = o.getClass().getMethod(sb.toString());
            Object v = method.invoke(o);
            return v;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param o: Object
     * @return String(格式化)
     * @author paul
     * @description 将对象转换为key value A=a&B=b&C=c 格式
     * @date 2023/12/14 20:05
     */
    public static String formatKetValue(Object o){
        StringBuilder sb = new StringBuilder();
        String[] fieldNames = getFieldNames(o);
        for (int i = 0; i < fieldNames.length; i++) {
            Object v = getFiledValueByName(o,fieldNames[i]);
            assert v != null;
            sb.append("=").append(v).append("&");
        }
        //clear last "&"
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }

    /**
     * @param s: 格式化字符
     * @param t: 构造对象
     * @return T 对象类型
     * @author paul
     * @description key value键值对 转换为 对象 A=a&B=b&C=c 格式 转换为对象
     * @date 2023/12/14 20:47
     */
    public static <T> T formatKeyValuePair(String s,T t){
        String[] entries = s.split("&");
        String[] fieldNames = BeanUtil.getFieldNames(t);
        //optimize
        HashMap<String, Boolean> map = new HashMap<>();
        for (String fieldName : fieldNames) {
            map.put(fieldName,true);
        }
        try {
            for (String entry : entries) {
                String[] kv = entry.split("=");
                //O(1)
                if (map.get(kv[0])) {
                    assert kv.length == 2;
                    Field f = t.getClass().getDeclaredField(kv[0]);
                    f.set(kv[0],kv[1]);
                    map.replace(kv[0],false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }
}

package cn.lary.kit;

import cn.lary.core.dto.ResponsePair;


public class BusinessKit {
        public static <T> ResponsePair<T> ok(){
            return new ResponsePair<T>()
                    .setFail(false);
        }
        public static <T> ResponsePair<T> fail(){
            return new ResponsePair<T>()
                    .setFail(true);
        }

        public static <T> ResponsePair<T> fail(String msg){
            return new ResponsePair<T>()
                    .setFail(true)
                    .setMsg(msg);
        }
        public static <T> ResponsePair<T> ok(T args){
            return new ResponsePair<T>()
                    .setFail(false)
                    .setData(args);
        }
}

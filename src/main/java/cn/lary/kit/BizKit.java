package cn.lary.kit;

import cn.lary.core.dto.ResPair;


public class BizKit {
        public static <T> ResPair<T> ok(){
            return new ResPair<T>().setOk(true);
        }
        public static <T> ResPair<T> fail(){
            return new ResPair<T>().setOk(false);
        }

        public static <T> ResPair<T> fail(String msg){
            return new ResPair<T>().setOk(false).setMsg(msg);
        }
        public static <T> ResPair<T> ok( T args){
            return new ResPair<T>().setOk(true).setData(args);
        }
}

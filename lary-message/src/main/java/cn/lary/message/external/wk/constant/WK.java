package cn.lary.message.external.wk.constant;

public interface WK {
     interface CHANNEL {
         interface TYPE {
             int PERSON = 1; // 个人
             int GROUP = 2; // 群组
             int STREAM = 3; // 客服
             int DATA = 7; //数据
         }
    }
    interface DEVICE {
         interface LEVEL {
             int MASTER = 2;
             int SLAVE = 1;
         }
         interface FLAG {
             int APP = 1;
             int PC = 2;
         }
    }

}

package cn.lary.module.user.dto;


public class TokenPair {
        public int uid;
        public String token;
        public int flag;

        public TokenPair(int uid, String token, int flag) {
            this.uid = uid;
            this.token = token;
            this.flag = flag;
        }
}

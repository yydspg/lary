package cn.lary.module.user.dto;


public class TokenPair {
        public long uid;
        public String token;
        public int flag;

        public TokenPair(long uid, String token, int flag) {
            this.uid = uid;
            this.token = token;
            this.flag = flag;
        }
}

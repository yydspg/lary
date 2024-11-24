package cn.lary.comment.constant;

public interface COMMENT {
    interface EVENT{
        int POST = 1;
    }
    interface STATUS {
        int HIDE = 1;
        int COMMON =2;
    }
    interface BEHAVIOR {
        int COMMENT = 1;
        int STAR = 2;
        int HIDE = 3;
    }
}

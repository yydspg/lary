package cn.lary.module.common.CS;

public interface Lary {
    interface RegisterMode {
        short ON = 1;
        short OFF = 0;
    }
    interface CodeType {
        int Register = 0;
        int PWd = 1;
        int ForgetLoginPWD = 2;
        int CheckMobile = 3;
        int DestroyCount = 4;
    }
}

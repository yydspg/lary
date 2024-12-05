package cn.lary.api.wallet.constant;

public interface WALLET {
    interface STATUS {
        int BLOCK =  1;
        int COMMON = 2;
        int DANGER = 3;
        int DESTROY =  4;
    }
    interface TRANSFER {
        int POCKET = 1;
        int AMOUNT = 2;
    }
}

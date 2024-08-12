package cn.lary.core.cs;

public enum ResultCode {
    NO_VALID_JSON("1001","json build error"),
    GROUP_NO_EXIST("1002","group no exists"),
    FILE_HELP_JOIN_NO_SUPPORT("1003","no support add file helper system account"),
    APP_CONFIG_NO_EXIST("1004","app config no exists"),
    NO_CREATOR_OR_MANAGER("1005","not manager or creator");
    private final String code;
    private final String message;


    ResultCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String code() {
        return this.code;
    }

    public String message() {
        return this.message;
    }
}

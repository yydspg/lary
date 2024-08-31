package cn.lary.core.cs;

public enum ResultCode {
    NO_VALID_JSON("1001","json build error");
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

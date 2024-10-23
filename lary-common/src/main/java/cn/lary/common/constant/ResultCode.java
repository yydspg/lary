package cn.lary.common.constant;

public enum ResultCode {

    NO_VALID_JSON(1001,"json build error");


    private final Integer code;
    private final String message;


    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer code() {
        return this.code;
    }

    public String message() {
        return this.message;
    }
}

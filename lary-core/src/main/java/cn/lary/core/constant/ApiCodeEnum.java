package cn.lary.core.constant;

/**
 * @author paul 2024/4/9
 */

public enum ApiCodeEnum {
    SUCCESS(0, "success"), //请求成功

    CUSTOM_FAIL(9999, "CustomServiceError"),  //自定义业务异常
    /*
    格式字符串，其中可能包含占位符，例如 %s 代表字符串、%d 代表整数等
     */
    SYSTEM_ERROR(10, "SystemError[%s]"),
    PARAMS_ERROR(11, "ArgsError[%s]"),
    DB_ERROR(12, "DBServiceError"),

    SYS_OPERATION_FAIL_CREATE(5000, "createFailure"),
    SYS_OPERATION_FAIL_DELETE(5001, "deleteFailure"),
    SYS_OPERATION_FAIL_UPDATE(5002, "updateFailure"),
    SYS_OPERATION_FAIL_SELECT(5003, "recordNotExists"),
    OSS_UPLOAD_FAIl(200,"ossUploadError"),
    OSS_DELETE_FAIl(200,"ossDeleteError"),
    NO_OSS_VENDER_FAIl(200,"ossSelectError"),
    USER_AUTHORITY_ERROR(200,"userAuthorityError"),
    SYS_PERMISSION_ERROR(5004, "permissionDeny");
    private final int code;

    private final String msg;

    ApiCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode(){
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }
}

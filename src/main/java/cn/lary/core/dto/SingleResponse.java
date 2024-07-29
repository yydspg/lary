package cn.lary.core.dto;

public class SingleResponse extends Response {

    private Object data;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public static <T> SingleResponse buildSuccess(T data) {
        SingleResponse response = new SingleResponse();
        response.setSuccess(true);
        return response;
    }

    public static SingleResponse buildFailure(String errCode, String errMessage) {
        SingleResponse response = new SingleResponse();
        response.setSuccess(false);
        response.setErrCode(errCode);
        response.setErrMessage(errMessage);
        return response;
    }

    public static <T> SingleResponse of(T data) {
        SingleResponse response = new SingleResponse();
        response.setSuccess(true);
        response.setData(data);
        return response;
    }

}
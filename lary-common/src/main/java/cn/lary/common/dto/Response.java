package cn.lary.common.dto;


public  class Response<T> extends DTO{


    private boolean success;

    private int errCode;

    private String errMessage;


    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }

    @Override
    public String toString() {
        return "Response [success=" + success + ", errCode=" + errCode + ", errMessage=" + errMessage + "]";
    }

}

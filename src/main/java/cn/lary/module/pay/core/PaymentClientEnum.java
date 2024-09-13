package cn.lary.module.pay.core;

/**
 * 不同支付方法
 */
public enum  PaymentClientEnum {
    WEB((short) 0,"web"),
    APP((short) 1,"app");
    private final short code;
    private final String desc;
    PaymentClientEnum(short code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    public short getCode() {
        return code;
    }
    public String getDesc() {
        return desc;
    }
    public static PaymentClientEnum getByCode(int code) {
        for (PaymentClientEnum paymentClientEnum : PaymentClientEnum.values()) {
            if (paymentClientEnum.getCode() == code) {
                return paymentClientEnum;
            }
        }
        return null;
    }
}

package com.cqupt.art.enu;

public enum OrderStatusEnum {

    UNPAID(1, "未支付"),
    PAID(0, "已支付");
    private int code;
    private String message;

    OrderStatusEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }
}

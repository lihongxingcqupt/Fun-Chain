package com.cqupt.art.enu;

public enum OrderStatusEnum {

    UNPAID(2, "未支付"),
    PAID(1, "已支付"),

    FAIL(3,"支付失败");
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

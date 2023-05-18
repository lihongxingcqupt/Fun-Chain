package com.cqupt.art.enu;

public enum NftStatusEnum {
    NORMAL(1, "正常"),
    WAIT_CHAIN_CONFIRM(2, "待链上确认"),

    SELLING(3,"寄售中");
    private int code;
    private String message;

    NftStatusEnum(int code, String message) {
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

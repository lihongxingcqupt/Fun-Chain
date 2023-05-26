package com.cqupt.art.enu;

public enum NftBatchStatusEnum {
    UP_CHAINING(1, "上链中"),
    UP_SUCCESS(2, "上链成功"),

    UP_ERROR(3,"上链异常，请联系管理员");
    private int code;
    private String message;

    NftBatchStatusEnum(int code, String message) {
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

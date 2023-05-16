package com.cqupt.art.enu;

public enum UserStatusEnum {
    ACTIVATE_USER(3, "实名且状态正常用户"),
    UN_AUTHORED_USER(1, "状态正常但未实名用户"),
    BANED_USER(0, "禁用账户");
    private int code;
    private String message;

    UserStatusEnum(int code, String message) {
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

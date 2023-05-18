package com.cqupt.art.enu;

/**
 * 获得NFT的方式 1、首发，2、二级购买，3、转增，4、盲盒开出
 */
public enum NftGainTypeEnum {
    FIRST_BUY(1, "首发"),
    TWO_BUY(2, "二级购买"),

    SEND(3,"转增"),

    BLIND_BOX(4,"盲盒开出");
    private int code;
    private String message;

    NftGainTypeEnum(int code, String message) {
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

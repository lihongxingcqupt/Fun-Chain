package com.cqupt.art.exception;

public class AddressIllegalException extends RuntimeException {
    public AddressIllegalException(String address) {
        super("地址 {" + address + "} 不合法！");
    }
}

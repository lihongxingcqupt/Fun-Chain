package com.cqupt.art.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserException extends RuntimeException {
    public UserException(String msg) {
        super(msg);
        log.info(msg);
    }

    public UserException() {
        super("用户信息异常！");
    }
}

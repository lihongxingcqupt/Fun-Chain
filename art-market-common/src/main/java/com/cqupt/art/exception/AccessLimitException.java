package com.cqupt.art.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AccessLimitException extends RuntimeException {
    public AccessLimitException(String msg) {
        super(msg);
        log.info(msg);
    }

    public AccessLimitException() {
        super("访问过于频繁，请重试");
    }
}

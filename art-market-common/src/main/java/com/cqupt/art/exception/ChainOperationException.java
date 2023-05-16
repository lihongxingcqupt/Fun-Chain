package com.cqupt.art.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChainOperationException extends RuntimeException {

    public ChainOperationException(Exception e) {
        super("链上操作异常，异常信息：");
        log.error(e.getMessage());
    }
}

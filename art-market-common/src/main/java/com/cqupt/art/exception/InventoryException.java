package com.cqupt.art.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InventoryException extends RuntimeException {
    public InventoryException(String msg) {
        super(msg);
        log.info(msg);
    }

    public InventoryException() {
        super("库存不足，请重试");
    }
}

package com.cqupt.art.seckill.app;

import com.cqupt.art.utils.R;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
@RestControllerAdvice
public class BaseExceptionHandler {
    @ExceptionHandler(value = InterruptedException.class)
    public R interruptedExceptionHandle(InterruptedException exception){
        return R.ok("请求过于频繁");
    }
}

package com.cqupt.art.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AccessLimit {
    /**
     * 多少秒内
     * @return
     */
    long second() default 5l;

    /**
     * 最大访问次数
     */
    long maxTimes() default 3l;

    /**
     * 禁用时长
     */
    long forbiddenTime() default 120l;
}

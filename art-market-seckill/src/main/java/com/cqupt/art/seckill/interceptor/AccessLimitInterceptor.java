package com.cqupt.art.seckill.interceptor;

import com.cqupt.art.constant.InterceptorConstant;
import com.cqupt.art.exception.AccessLimitException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 接口防刷拦截器
 */
@Slf4j
public class AccessLimitInterceptor implements HandlerInterceptor {
    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    /**
     * 多长时间内限制的次数
     */
    @Value("${interfaceAccess.second}")
    private Long second = 10l;

    /**
     * 这个时段的访问限制次数
     */
    @Value("${interfaceAccess.times}")
    private Long times = 3l;

    /**
     * 禁用时长，当一个时间段的访问超过了限制的次数，就限制当前的请求了，限制多长时间通过这里配置
     */
    @Value("${interfaceAccess.lockTime}")
    private Long lockTime = 60L;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handle){
        /**
         * 限制针对的是同一个IP和同一个请求路径，因此二者都要
         */
        String uri = request.getRequestURI();
        String ip = request.getRemoteAddr();
        String lockKey = InterceptorConstant.LOCK_PREFIX + ip + uri;
        Object isLock = redisTemplate.opsForValue().get(lockKey);

        if(Objects.isNull(isLock)){
            // redis里面没有用户被锁定的信息，说明要么没达到限制，要么是限制已过期

            //获取用户在一段时间内的访问次数

            String countKey = InterceptorConstant.COUNT_PREFIX + ip + uri;
            Object count = redisTemplate.opsForValue().get(countKey);

            if(Objects.isNull(count)){
                //首次访问，则将其次数+1并放行
                log.info("首次访问");
                redisTemplate.opsForValue().set(countKey,1,second, TimeUnit.SECONDS);
            }else{
                // 此前用户访问过（一段时间内访问过）
                if((Integer)count < times){
                    // 访问过，但是次数没有达到限制
                    redisTemplate.opsForValue().increment(countKey);
                }else{
                    log.info("{}禁用访问{}",ip,uri);

                    //将其添加至redis，进行锁定
                    redisTemplate.opsForValue().set(lockKey,1,lockTime,TimeUnit.SECONDS);

                    //删除之前的统计
                    redisTemplate.delete(countKey);
                    throw new AccessLimitException();
                }
            }
        }else{
            // 它在被限制的名单里面，直接抛出不能访问的异常
            throw new AccessLimitException();
        }
        return true;
    }
}

package com.cqupt.art.seckill.app;

import com.cqupt.art.annotation.AccessLimit;
import com.cqupt.art.seckill.entity.vo.SeckillInfoVo;
import com.cqupt.art.seckill.service.SeckillService;
import com.cqupt.art.utils.R;
import com.google.common.util.concurrent.RateLimiter;import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;import java.util.concurrent.TimeUnit;

public class SeckillController {
    @Autowired
    SeckillService seckillService;

    private RateLimiter rateLimiter = RateLimiter.create(100);
    /**
     * 通过自定义的注解灵活的通过反射来实现接口防刷
     * @param info
     * @return
     * @throws InterruptedException
     */
    @AccessLimit(second = 10l,maxTimes = 5l,forbiddenTime = 20l)
    @GetMapping("/nft")
    public R seckill(@RequestBody SeckillInfoVo info) throws InterruptedException {
        if(!rateLimiter.tryAcquire(2,TimeUnit.SECONDS)){
            return R.error("请重试");
        }else{
            // 返回订单号，前端拿到订单号后查询订单进行支付
            String orderSn = seckillService.kill(info);
            return R.ok(orderSn);
        }
    }
}

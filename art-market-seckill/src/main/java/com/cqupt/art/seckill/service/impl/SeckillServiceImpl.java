package com.cqupt.art.seckill.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.cqupt.art.constant.SeckillConstant;
import com.cqupt.art.constant.SeckillOrderMqConstant;
import com.cqupt.art.seckill.config.LoginInterceptor;
import com.cqupt.art.seckill.entity.User;
import com.cqupt.art.seckill.entity.to.NftDetailRedisTo;
import com.cqupt.art.seckill.entity.to.SeckillOrderTo;
import com.cqupt.art.seckill.entity.vo.SeckillInfoVo;
import com.cqupt.art.seckill.service.SeckillService;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

public class SeckillServiceImpl implements SeckillService {
    @Autowired
    RedissonClient redissonClient;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    RabbitTemplate rabbitTemplate;

    @Override
    public void kill(SeckillInfoVo info) throws InterruptedException {
        String token = info.getToken();
        BoundHashOperations<String, String, String> ops = redisTemplate.boundHashOps(SeckillConstant.SECKILL_DETAIL_PREFIX);
        String jsonString = ops.get(info.getName() + "-" + info.getId());
        if (StringUtils.isNotBlank(jsonString)) {
            NftDetailRedisTo to = JSON.parseObject(jsonString, NftDetailRedisTo.class);
            //验证时间
            long curTime = System.currentTimeMillis();
            if (curTime >= to.getStartTime().getTime() && curTime < to.getEndTime().getTime()) {
                if (to.getToken().equals(token)) {
                    //token对，是在秒杀时间进来的
                    //验证是否已购买过
                    User user = LoginInterceptor.threadLocal.get();
                    long ttl = to.getEndTime().getTime() - System.currentTimeMillis();
                    Boolean occupy = redisTemplate.opsForValue().setIfAbsent(SeckillConstant.USER_BOUGHT_FLAG + user.getUserId() + "-" + to.getId(), "1", ttl, TimeUnit.MILLISECONDS);
                    if (occupy) {
                        //当前用户没买过
                        RSemaphore semaphore = redissonClient.getSemaphore(SeckillConstant.SECKILL_SEMAPHORE);
                        boolean acquire = semaphore.tryAcquire(1, 100, TimeUnit.MILLISECONDS);
                        if (acquire) {
                            //发送创建订单的消息
                            SeckillOrderTo orderTo = new SeckillOrderTo();
                            orderTo.setOrderSn(IdWorker.getIdStr());
                            orderTo.setBuyUserId(user.getUserId());
                            orderTo.setGoodsId(to.getId().toString());
                            orderTo.setPrice(new BigDecimal(to.getPrice()));
                            rabbitTemplate.convertAndSend(SeckillOrderMqConstant.EXCHANGE, SeckillOrderMqConstant.ROUTING_KEY, orderTo);
                        }
                    }
                }
            }
        }
    }
}

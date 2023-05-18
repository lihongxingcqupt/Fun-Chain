package com.cqupt.art.seckill.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.cqupt.art.author.dao.NftBatchInfoMapper;
import com.cqupt.art.constant.SeckillConstant;
import com.cqupt.art.constant.SeckillKucunMqConstant;
import com.cqupt.art.constant.SeckillOrderMqConstant;
import com.cqupt.art.exception.InventoryException;
import com.cqupt.art.seckill.config.LoginInterceptor;
import com.cqupt.art.seckill.entity.User;
import com.cqupt.art.seckill.entity.to.NftDetailRedisTo;
import com.cqupt.art.seckill.entity.to.SeckillOrderTo;
import com.cqupt.art.seckill.entity.vo.SeckillInfoVo;
import com.cqupt.art.seckill.service.SeckillService;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class SeckillServiceImpl implements SeckillService {
    @Autowired
    RedissonClient redissonClient;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    NftBatchInfoMapper nftBatchInfoMapper;

    @Override
    public String kill(SeckillInfoVo info){
        BoundHashOperations<String, String, String> ops = redisTemplate.boundHashOps(SeckillConstant.SECKILL_DETAIL_PREFIX);
        String jsonString = ops.get(info.getName() + "-" + info.getId());
        if (StringUtils.isNotBlank(jsonString)) {
            NftDetailRedisTo to = JSON.parseObject(jsonString, NftDetailRedisTo.class);
            //验证时间
            long curTime = System.currentTimeMillis();
            if (curTime >= to.getStartTime().getTime() && curTime < to.getEndTime().getTime()) {

                //验证是否已购买过
                User user = LoginInterceptor.threadLocal.get();
                String isBuy = redisTemplate.opsForValue().get(SeckillConstant.USER_BOUGHT_FLAG + user.getUserId() + "-" + to.getId());

                if (Objects.isNull(isBuy) || isBuy.length() == 0) {
                    /**
                     * 直接在数据库层面操作库存，数据库层面是有锁的，并且加上库存大于0的条件就不会出现超卖的情况
                     */
                    int count = nftBatchInfoMapper.updateKucun();

                    if (count > 0) {
                        //发送创建订单的消息
                        SeckillOrderTo orderTo = new SeckillOrderTo();
                        orderTo.setOrderSn(IdWorker.getIdStr());
                        orderTo.setBuyUserId(user.getUserId());
                        orderTo.setGoodsId(to.getId().toString());
                        orderTo.setPrice(new BigDecimal(to.getPrice()));
                        // 发送消息，后台去创建订单
                        rabbitTemplate.convertAndSend(SeckillOrderMqConstant.EXCHANGE, SeckillOrderMqConstant.ROUTING_KEY, orderTo);

                        // 发送延时消息检查订单是否完成支付，若没有完成则回滚库存
                        long expirStart = TimeUnit.MINUTES.toMillis(5);
                        rabbitTemplate.convertAndSend(SeckillKucunMqConstant.EXCHANGE,SeckillKucunMqConstant.ROUTING_KEY,orderTo.getOrderSn(), message -> {
                            // 这里的失效时间是long类型，普通的TTL方式的类型是String类型
                            message.getMessageProperties().setHeader("x-delay",expirStart);
                            return message;
                        });
                        return orderTo.getOrderSn();
                    } else {
                        throw new InventoryException();
                    }

                }else{
                    throw new RuntimeException("您已经购买过了");
                }
            }else{
                throw new RuntimeException("不在购买时间");
            }
        }
        return null;
    }
}

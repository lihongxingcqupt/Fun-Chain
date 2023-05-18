package com.cqupt.art.order.service.listener;

import com.alibaba.fastjson.JSON;
import com.cqupt.art.order.entity.to.SeckillOrderTo;
import com.cqupt.art.constant.SeckillOrderMqConstant;
import com.cqupt.art.order.service.OrderService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@RabbitListener(queues = SeckillOrderMqConstant.QUEUE)
@Service
@Slf4j
public class SeckillOrderListener {

    @Autowired
    OrderService orderService;

    /**
     * 首发中直接发送一条消息到这里进行订单的持久化
     * @param orderTo
     * @param message
     * @param channel
     */
    @RabbitHandler
    public void createOrder(SeckillOrderTo orderTo, Message message, Channel channel) {
        log.info("收到秒杀消息：{}", JSON.toJSONString(orderTo));
        long tag = message.getMessageProperties().getDeliveryTag();
        try {
            orderService.createSeckillOrder(orderTo);
            channel.basicAck(tag, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

package com.cqupt.art.seckill.service.listener;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cqupt.art.author.dao.NftBatchInfoMapper;
import com.cqupt.art.author.entity.NftBatchInfoEntity;
import com.cqupt.art.author.service.NftBatchInfoService;
import com.cqupt.art.constant.SeckillKucunMqConstant;
import com.cqupt.art.constant.SeckillOrderMqConstant;
import com.cqupt.art.enu.OrderStatusEnum;
import com.cqupt.art.order.entity.Order;
import com.cqupt.art.order.service.OrderService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
@RabbitListener(queues = SeckillKucunMqConstant.QUEUE)
@Service
@Slf4j
public class SeckillKucunListener {

    @Autowired
    OrderService orderService;

    @Autowired
    NftBatchInfoService nftBatchInfoService;

    /**
     * 延时检查首发场景下的订单是否完成支付，若没有则回滚库存。
     * @param orderSn
     * @param message
     * @param channel
     */
    @RabbitHandler
    public void createOrder(String orderSn, Message message, Channel channel) {
        log.info("收到延时消息：{}", JSON.toJSONString(orderSn));

        long tag = message.getMessageProperties().getDeliveryTag();
        try {
            QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("orderSn",orderSn);
            Order one = orderService.getOne(queryWrapper);
            if(one.getStatus() == OrderStatusEnum.UNPAID.getCode()){
                //回滚库存
                NftBatchInfoEntity byId = nftBatchInfoService.getById(one.getGoodsId());

                byId.setInventory(byId.getInventory() + 1);

                nftBatchInfoService.updateDes(byId);
            }
            channel.basicAck(tag, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}

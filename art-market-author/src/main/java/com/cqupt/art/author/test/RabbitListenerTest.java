package com.cqupt.art.author.test;

import com.cqupt.art.author.config.mq.MyNftMqConfig;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RabbitListener(queues = MyNftMqConfig.QUEUE_MINT_PRODUCT)
@Slf4j
public class RabbitListenerTest {

    @SneakyThrows
    @RabbitHandler
    public void listener(Long id, Channel channel, Message message) {
        log.info("消息队列接收到的Id为：{}", id);
        try {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e) {
            log.error(e.getMessage());
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }
}

package com.cqupt.art.author.test;

import com.cqupt.art.author.config.mq.MyNftMqConfig;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.postprocessor.MessagePostProcessorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RabbitProductTest {
    @Autowired
    RabbitTemplate rabbitTemplate;

    @GetMapping("/sendMessage")
    public void sendMessage() {
        rabbitTemplate.convertAndSend(MyNftMqConfig.MINT_EXCHANGE, MyNftMqConfig.MINT_PRODUCT_ROUTING_KEY, 110L);
    }
}

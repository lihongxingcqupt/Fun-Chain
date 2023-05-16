package com.cqupt.art.order.config.mq;

import com.cqupt.art.constant.SeckillOrderMqConstant;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SeckillOrderMqConfig {
    //队列
    @Bean
    public Queue orderSecKillOrrderQueue() {
        Queue queue = new Queue(SeckillOrderMqConstant.QUEUE, true, false, false);
        return queue;
    }

    //交换机
    @Bean
    public Exchange mintExchange() {
        return ExchangeBuilder.directExchange(SeckillOrderMqConstant.EXCHANGE).durable(true).build();
    }

    @Bean
    public Binding mintProductBinding() {
        return new Binding(SeckillOrderMqConstant.QUEUE,
                Binding.DestinationType.QUEUE,
                SeckillOrderMqConstant.EXCHANGE,
                SeckillOrderMqConstant.ROUTING_KEY, null
        );
    }
}

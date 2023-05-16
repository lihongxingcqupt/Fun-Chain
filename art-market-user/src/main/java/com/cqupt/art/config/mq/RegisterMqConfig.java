package com.cqupt.art.config.mq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RegisterMqConfig {
    public static final String REGISTER_EXCHANGE = "register-echange";
    public static final String REGISTER_QUEUE = "register-queue";
    public static final String REGISTER_PRODUCT_ROUTING_KEY = "register.product.route";
    public static final String REGISTER_CONSUME_ROUTING_KEY = "register.consume.route";

    @Bean
    public Exchange registerExchange() {
        return ExchangeBuilder.directExchange(REGISTER_EXCHANGE).durable(true).build();
    }

    @Bean
    public Queue registerQueue() {
        return new Queue(REGISTER_QUEUE, true, false, false);
    }

    @Bean
    public Binding binding() {
        return new Binding(REGISTER_QUEUE, Binding.DestinationType.QUEUE,
                REGISTER_EXCHANGE,
                REGISTER_CONSUME_ROUTING_KEY, null);
    }
}

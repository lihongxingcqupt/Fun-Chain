package com.cqupt.art.author.config.mq;


import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.StringReader;

@Configuration
public class MyNftMqConfig {
    public static final String QUEUE_MINT_PRODUCT = "queue_mint_nft_product";
    public static final String QUEUE_MINT_CONSUME = "queue_mint_nft_consume";
    public static final String MINT_EXCHANGE = "mint_nft_exchange";
    public static final String MINT_PRODUCT_ROUTING_KEY = "chain.mint.product.queue";
    public static final String MINT_CONSUME_ROUTING_KEY = "chain.mint.product.queue";

    @Bean(MINT_EXCHANGE)
    public Exchange mintExchange() {
        return ExchangeBuilder.directExchange(MINT_EXCHANGE).durable(true).build();
    }

    @Bean(QUEUE_MINT_PRODUCT)
    public Queue queueMintProduct() {
        return new Queue(QUEUE_MINT_PRODUCT, true, false, false);
    }

    @Bean(QUEUE_MINT_CONSUME)
    public Queue queueMintConsume() {
        return new Queue(QUEUE_MINT_CONSUME, true, false, false);
    }

    @Bean
    public Binding mintProductBinding() {
        return new Binding(QUEUE_MINT_PRODUCT,
                Binding.DestinationType.QUEUE,
                MINT_EXCHANGE,
                MINT_PRODUCT_ROUTING_KEY, null
        );
    }

    @Bean
    public Binding mintConsumeBinding() {
        return new Binding(QUEUE_MINT_CONSUME,
                Binding.DestinationType.QUEUE,
                MINT_EXCHANGE,
                MINT_CONSUME_ROUTING_KEY, null);
    }
}

package com.cqupt.art.order;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
@EnableRabbit
@EnableRedisHttpSession
public class ArtMarketOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(ArtMarketOrderApplication.class, args);
    }

}

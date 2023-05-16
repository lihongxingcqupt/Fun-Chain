package com.cqupt.art.activity;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients
@EnableRabbit
public class ArtdevActivityApplication {
    public static void main(String[] args) {
        SpringApplication.run(ArtdevActivityApplication.class, args);
    }
}


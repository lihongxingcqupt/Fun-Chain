package com.cqupt.art.sms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ArtMarketSmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ArtMarketSmsApplication.class, args);
    }

}

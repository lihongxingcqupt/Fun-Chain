package com.cqupt.art.chain.config;

import conflux.web3j.Cfx;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CfxConfig {
    @Value("${my.chain.net.url}")
    private String url;

    @Bean
    public Cfx createCfx() {
        return Cfx.create(url);
    }
}

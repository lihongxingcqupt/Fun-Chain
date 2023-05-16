package com.cqupt.art.chain.config;

import conflux.web3j.Account;
import conflux.web3j.Cfx;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
public class AccountConfig {
    @Value("${my.chain.admin.privateKey}")
    private String adminPrivateKey;

    @Resource
    private Cfx cfx;

    @Bean
    public Account createAccount() {
        return Account.create(cfx, adminPrivateKey);
    }

}

package com.cqupt.art.chain.config;

import conflux.web3j.AccountManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccountManagerConfig {

    @Value("${my.chain.chainId}")
    private int chainId;


    @Bean
    public AccountManager accountManager() throws Exception {
        return new AccountManager(chainId);
    }
}

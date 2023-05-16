package com.cqupt.art.chain.config;

import conflux.web3j.Cfx;
import conflux.web3j.contract.ContractCall;
import conflux.web3j.types.Address;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
public class ContractCallConfig {
    @Resource
    Cfx cfx;
    @Value("${my.chain.contract}")
    private String contractAddress;

    @Bean
    public ContractCall getContractCall() {
        return new ContractCall(cfx, new Address(contractAddress));
    }
}

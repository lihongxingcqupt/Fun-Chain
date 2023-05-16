package com.cqupt.art.chain.service;

import com.cqupt.art.chain.entity.AccountInfo;
import org.springframework.stereotype.Service;

@Service
public interface ConfluxAccountService {

    AccountInfo createAccount(String pwd) throws Exception;

}

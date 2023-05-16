package com.cqupt.art.chain.service.impl;

import com.cqupt.art.chain.entity.AccountInfo;
import com.cqupt.art.chain.service.ConfluxAccountService;
import conflux.web3j.AccountManager;
import conflux.web3j.types.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConfluxAccountServiceImpl implements ConfluxAccountService {

    @Autowired
    private AccountManager accountManager;

    @Override
    public AccountInfo createAccount(String pwd) throws Exception {
        AccountInfo ai = new AccountInfo();
        Address address = accountManager.create(pwd);
        String privateKey = accountManager.exportPrivateKey(address, pwd);
        //解锁账户
        accountManager.unlock(address, pwd);
        ai.setAddress(address.getAddress());
        ai.setPassword(pwd);
        ai.setPrivateKey(privateKey);
        return ai;
    }
}

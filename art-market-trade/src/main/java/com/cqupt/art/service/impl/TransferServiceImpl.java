package com.cqupt.art.service.impl;

import com.cqupt.art.feign.UserFeignClient;
import com.cqupt.art.service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


@Service
public class TransferServiceImpl implements TransferService {

    @Autowired
    private UserFeignClient userFeignClient;

    @Override
    public void transfer(String toPhoneNum, String toAddress, Long tokenId) {
        if (!StringUtils.isEmpty(toPhoneNum)) {
            userFeignClient.getUserByPhone(toPhoneNum);
        }
    }
}

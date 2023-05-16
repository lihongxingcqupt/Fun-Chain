package com.cqupt.art.service;

import org.springframework.stereotype.Service;

@Service
public interface TransferService {
    void transfer(String toPhoneNum, String toAddress, Long tokenId);
}

package com.cqupt.art.sms.service;

import org.springframework.stereotype.Service;

@Service
public interface SmsService {
    boolean send(String phone, String ip);

    boolean send(String phone, String userId, String ip);
}

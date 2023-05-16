package com.cqupt.art.sms.controller;

import com.cqupt.art.sms.service.SmsService;
import com.cqupt.art.sms.utils.IpUtil;

import com.cqupt.art.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@Slf4j
@CrossOrigin
public class SmsController {
    @Autowired
    SmsService smsService;

    @GetMapping("registerCode/{phone}")
    public R sendMessage(@PathVariable("phone") String phone, HttpServletRequest request) {
        log.info("注册发送验证码：{}", phone);
        String ip = IpUtil.getIpAddr(request);
        boolean send = false;
        try {
            send = smsService.send(phone, ip);
            if (send) {
                return R.ok("发送短信成功！");
            } else {
                return R.error("发送短信失败");
            }
        } catch (RuntimeException e) {
            log.error(e.getMessage());
            return R.error("触发限制!请不要重复发送验证码！");
        }
    }

    @GetMapping("opretionCode/{phone}/{userId}")
    public R sendMessage(@PathVariable("phone") String phone,
                         @PathVariable("userId") String userId, HttpServletRequest request) {
        String ip = IpUtil.getIpAddr(request);
        try {
            boolean send = smsService.send(phone, userId, ip);
            if (send) {
                return R.ok("发送短信成功！");
            } else {
                return R.error("发送短信失败");
            }
        } catch (RuntimeException e) {
            log.error(e.getMessage());
            return R.error("触发限制!请不要重复发送验证码！");
        }
    }
}

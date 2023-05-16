package com.cqupt.art.feign;

import com.cqupt.art.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;

@FeignClient("art-market-sms")
public interface SmsFeignClient {
    @GetMapping("registerCode/{phone}")
    R sendMessage(@PathVariable("phone") String phone, HttpServletRequest request);
}

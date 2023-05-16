package com.cqupt.art.author.feign;

import com.cqupt.art.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "art-user-service")
public interface UserFeignService {
    @GetMapping("/api/user/getPhoneAndAddById")
    R getPhoneAndAddById(@RequestParam("id") String uid);
}

package com.cqupt.art.feign;

import com.cqupt.art.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("art-user-service")
public interface UserFeignClient {
    @GetMapping("/api/user/getUserByPhone/{phone}")
    R getUserByPhone(@PathVariable("phone") String phone);
}

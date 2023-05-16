package com.cqupt.art.controller;

import com.alibaba.fastjson.JSON;
import com.cqupt.art.config.LoginInterceptor;
import com.cqupt.art.entity.User;
import com.cqupt.art.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class TestController {

    @GetMapping("/user/test")
    public R test() {
        User user = LoginInterceptor.threadLocal.get();
        log.info("controller中的数据：{}", JSON.toJSONString(user));
        return R.ok().put("loginUser", user);
    }
}

package com.cqupt.art.app;

import com.alibaba.fastjson.JSON;
import com.cqupt.art.config.LoginInterceptor;
import com.cqupt.art.entity.User;
import com.cqupt.art.entity.vo.LoginUserVo;
import com.cqupt.art.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@CrossOrigin
@RequestMapping("/app/user")
@Slf4j
public class AppUserController {

    @GetMapping("userInfo")
    public R userInfo(HttpServletResponse response) throws IOException {
        User user = LoginInterceptor.threadLocal.get();
        log.info("获取用户信息：{}", JSON.toJSONString(user));
        if (user == null) {
            response.sendRedirect("http://10.17.156.253:8081/#/login");
            return R.error("请先登录");
        }
        LoginUserVo vo = new LoginUserVo();
        vo.setId(user.getUserId());
        vo.setChainAddress(user.getChainAddress());
        vo.setUserName(user.getUserName());
        String phone = user.getUserPhone();
        phone = phone.replace(phone.substring(3, 7), "****");
        vo.setPhone(phone);
        return R.ok().put("data", vo);
    }
}

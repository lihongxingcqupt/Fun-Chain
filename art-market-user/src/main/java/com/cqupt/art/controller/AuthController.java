package com.cqupt.art.controller;

import com.alibaba.fastjson.JSON;
import com.cqupt.art.entity.User;
import com.cqupt.art.entity.to.UserLoginTo;
import com.cqupt.art.entity.vo.LoginUserVo;
import com.cqupt.art.entity.vo.UserRegisterVo;
import com.cqupt.art.service.UserService;
import com.cqupt.art.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    UserService userService;

    private ThreadLocal<User> threadLocal;

    @PostMapping("/register")
    public R register(@RequestBody @Valid UserRegisterVo registerVo, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(item -> {
                errors.put(item.getField(), item.getDefaultMessage());
            });
            log.error("注册验证出错：{}", JSON.toJSONString(errors));
            return R.error(501, "数据不合法").put("errors", errors);
        }
        return userService.regist(registerVo);
    }

    @PostMapping("login")
    public R login(@RequestBody @Valid UserLoginTo userLoginVo, BindingResult result, HttpSession session, HttpServletResponse response) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(item -> {
                errors.put(item.getField(), item.getDefaultMessage());
            });
            return R.error(501, "数据异常！").put("errors", errors);
        }
        User user = userService.login(userLoginVo);
        if (user != null) {
            session.setAttribute("loginUser", user);
            LoginUserVo vo = new LoginUserVo();
            vo.setId(user.getUserId());
            vo.setChainAddress(user.getChainAddress());
            vo.setUserName(user.getUserName());
            String phone = user.getUserPhone();
            phone = phone.replace(phone.substring(3, 7), "****");
            vo.setPhone(phone);
            return R.ok("登录成功！").put("loginUser", vo);
        }
        return R.error("用户不存在或账户密码错误！");
    }

}

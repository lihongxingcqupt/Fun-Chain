package com.cqupt.art.chain.controller;


import com.cqupt.art.chain.entity.AccountInfo;
import com.cqupt.art.exception.ChainOperationException;
import com.cqupt.art.utils.R;
import com.cqupt.art.chain.service.ConfluxAccountService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Date;

@RestController
@RequestMapping("/chain/account")
@Slf4j
public class AccountController {
    @Autowired
    private ConfluxAccountService accountService;

    //    @ApiOperation("创建账户，返回账户相关信息，包括密码，私钥，地址，由调用方确定是否放到数据库")
    @GetMapping("/createAccount/{pwd}")
    public R createAccount(@PathVariable("pwd") String pwd) {
        log.info("createAccount---pwd  {}", pwd);
        long start = new Date().getTime();
        try {
            AccountInfo ai = accountService.createAccount(pwd);
            log.info("账户信息 {}", ai.toString());
            long end = new Date().getTime();
            log.info("创建链上账户所用时间：{}毫秒", end - start);
            return R.ok().put("data", ai);
        } catch (Exception e) {
            throw new ChainOperationException(e);
        }
    }
}

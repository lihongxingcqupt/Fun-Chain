package com.cqupt.art.entity.vo;

import lombok.Data;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class UserRegisterVo {
    //TODO 做验证
    //电话号的正则写不来，先不写了
    private String phoneNumber;
    @Size(min = 6, max = 6, message = "验证码必须是6位数字！")
    private String smsCode;
    @Pattern(regexp = "/^(?![0-9]+$)[a-z0-9]{1,50}$/", message = "密码必须同时包含字母和数字！")
    private String password;
}

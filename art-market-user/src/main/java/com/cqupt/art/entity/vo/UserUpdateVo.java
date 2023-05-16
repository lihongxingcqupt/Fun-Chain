package com.cqupt.art.entity.vo;

import lombok.Data;

@Data
public class UserUpdateVo {
    private String userName;
    //todo 号码验证
    private String phoneNumber;
    private Integer userStatus;
}

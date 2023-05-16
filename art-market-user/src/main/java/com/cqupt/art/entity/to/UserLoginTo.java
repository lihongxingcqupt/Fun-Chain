package com.cqupt.art.entity.to;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UserLoginTo {
    @NotNull
    private String phoneNumber;
    @NotNull
    private String password;
}

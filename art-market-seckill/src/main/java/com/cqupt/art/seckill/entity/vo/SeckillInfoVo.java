package com.cqupt.art.seckill.entity.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SeckillInfoVo {
    @NotNull
    private Long id;
    @NotNull
    private String name;
    @NotNull
    private String token;
}

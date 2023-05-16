package com.cqupt.art.activity.entity.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
@Data
public class DengjiVo {
    @NotNull
    private Long nftId;
    @NotNull
    private String userId;

}

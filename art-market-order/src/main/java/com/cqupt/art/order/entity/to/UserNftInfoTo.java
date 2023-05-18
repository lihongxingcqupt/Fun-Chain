package com.cqupt.art.order.entity.to;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserNftInfoTo {
    private String txHash;
    private BigDecimal price;
    private Integer status;  //1 正常 2 待链上确认 3 寄售中
    private String artId;
    private Integer localId;
}

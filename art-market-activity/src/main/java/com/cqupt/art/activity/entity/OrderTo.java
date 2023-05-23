package com.cqupt.art.activity.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author lihongxing
 * @Date 2023/5/23 20:45
 */
@Data
public class OrderTo {
    private String orderSn;
    private String buyUserId;
    private String goodsId;
    private BigDecimal price;
}

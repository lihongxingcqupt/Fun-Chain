package com.cqupt.art.order.entity.to;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SeckillOrderTo {

    private String orderSn;
    private String buyUserId;
    private String goodsId;
    private BigDecimal price;
}

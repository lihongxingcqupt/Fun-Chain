package com.cqupt.art.order.entity.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class TokenItemInfo {
    private Integer localId;
    private BigDecimal price;
    private boolean sail;
    private List<TokenItemInfo> details;
}

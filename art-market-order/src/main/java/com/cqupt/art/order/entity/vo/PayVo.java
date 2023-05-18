package com.cqupt.art.order.entity.vo;

import lombok.Data;

@Data
public class PayVo {
    private String out_trade_no; // 订单号 必填
    private String subject; // 藏品名称
    private String total_amount;  // 付款金额 必填
    private String body; // 几级订单（首发订单或者是二级市场订单） 可空
}

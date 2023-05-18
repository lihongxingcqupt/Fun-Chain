package com.cqupt.art.order.entity.vo;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 支付宝异步回调参数，含义见：
 * https://opendocs.alipay.com/open/203/105286#%E5%BC%82%E6%AD%A5%E9%80%9A%E7%9F%A5%E5%8F%82%E6%95%B0
 */
@Data
public class AlipayAsyncVo {
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date notify_time;
    private String notify_type;
    private String notify_id;
    private String app_id;
    private String charset;
    private String version;
    private String sign_type;
    private String sign;
    private String trade_no;
    private String out_trade_no;
    private String out_biz_no;
    private String buyer_id;
    private String buyer_logon_id;
    private String seller_id;
    private String seller_email;
    private String trade_status;
    private String total_amount;
    private String receipt_amount;
    private String invoice_amount;
    private String buyer_pay_amount;
    private String point_amount;
    private String refund_fee;
    private String subject;
    private String body;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date gmt_create;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date gmt_payment;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date gmt_refund;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date gmt_close;
    private String fund_bill_list;
    private String passback_params;
    private String voucher_detail_list;
}

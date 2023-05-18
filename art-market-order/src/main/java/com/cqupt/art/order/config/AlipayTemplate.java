package com.cqupt.art.order.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.cqupt.art.order.entity.vo.PayVo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "alipay")
@Component
@Data
@Slf4j
public class AlipayTemplate {

    //在支付宝创建的应用的id
    @Value("${alipay.app_id}")
    private String app_id;
    // 商户私钥，您的PKCS8格式RSA2私钥
    @Value("${alipay.merchant_private_key}")
    private String merchant_private_key;

    @Value("${alipay.alipay_public_key}")
    private String alipay_public_key;
    // 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
    @Value("${alipay.notify_url}")
    private String notify_url;
    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //同步通知，支付成功，一般跳转到成功页
    @Value("${alipay.return_url}")
    private String return_url;
    // 签名方式
    private String sign_type = "RSA2";
    // 字符编码格式
    private String charset = "utf-8";
    // 支付宝网关； https://openapi.alipaydev.com/gateway.do
    private String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    public String pay(PayVo vo) throws AlipayApiException {
        AlipayClient aliPayClient = new DefaultAlipayClient(gatewayUrl, app_id, merchant_private_key, "json", charset, alipay_public_key, sign_type);

        AlipayTradePagePayRequest payRequest = new AlipayTradePagePayRequest();
        payRequest.setReturnUrl(return_url);
        payRequest.setNotifyUrl(notify_url);
        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = vo.getOut_trade_no();
        //付款金额，必填
        String total_amount = vo.getTotal_amount();
        //订单名称，必填
        String subject = vo.getSubject();
        //商品描述，可空
        String body = vo.getBody();
        payRequest.setBizContent("{\"out_trade_no\":\"" + out_trade_no + "\","
                + "\"total_amount\":\"" + total_amount + "\","
                + "\"subject\":\"" + subject + "\","
                + "\"body\":\"" + body + "\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
        String result = aliPayClient.pageExecute(payRequest).getBody();

        log.info("支付宝的响应：{}", result);
        return result;
    }
}

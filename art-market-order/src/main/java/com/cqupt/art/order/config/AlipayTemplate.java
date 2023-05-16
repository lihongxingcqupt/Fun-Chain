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

//	// 商户appid
//	public static String APPID = "2021000119678392";
//	// 私钥 pkcs8格式的
//	public static String RSA_PRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCBcmRkbaEEvYw+8MhHTH7+Hzn618Myk8nyF3Il14nh0KQs5Rnl19YvwQpR9y2h8S71DOiNXeM5jt9ttz3JoFQTkd5nKvdwSgGjvlwXZZBTdCG9+Dfyp055oJKz6YS7DqKgq4vAPT5cfDjVOUdyKkiiv4uNBfONh7wA+ToeAM2BZG6EeESvys8dCfSbQwHfxR5FO0T2GfPJXuiUyoXf90HU/6KsK6tBN2bBBV1N3v6rZcipSHP/5EFA3golvjoJu968GWv0+I6H49simkyb4llDtAgItz1rW+/qyJVIvEUNN4loKIA/eNnA23TmYXkrJt2XuN/pJgzSM4scuxTtm0tNAgMBAAECggEAVv2S7dKmpNLkzt9tGE0WApzlj+czrGeZDPIoqbmhVXmq+uizcLyNmeX2k4KyzeORFOkp1ubDbsjO6aJ9CWSPW99pQJelVXbGh03EAY0lYo/mpQ2MO6unlyELtZA2vKZnL+ZYskX2BJ0ObGxj0RsNWSG0HH9lXzVaaKUUI1mSwWLR0I41U209AMj8RauNJ514KRJfx+1cBPxS00+UIf3EDlQjkXCk4Mc8n810m1Iqi+zLZhBMA6A0Aiv9pKwJHJNbt9xK6iWewFiwQixlvrOE1FOFuTMwGpWbwZtHAt8zDbIsJveI04qKfgdNAPFyjthCfIcG8pon46yTajiCU9J3gQKBgQC/XIa1A3Ipryr+R5POc7Qe/5DamqTtvtJobHhAt3B751GSHtk3x3UfsrpFfjlvCV+SEcakHMW5MTknruh+r+GTtM+nJH7VuyWPDM6rtxVjToT9fozMnWZTysxTV1IO5pCXuDGNinnumrpL9UWk2pVK954N01H4CNEMTg6dcRsdvQKBgQCtK/c/JaoOMinkfWuMdAmmux/QZu+ungBwxI0M/M3uX0fp/lR+ylU8bJ7K5NLOsBVCoW/TyzF1pP5vZLl+zZRakHpVrJvBZBuKOpLuuutIPgaPy8MJtoZIybhK7bGOZKd31hjrFhIHwWLcJNapc160WIHwl5VQQzQF139qZ+5U0QKBgAkRt4ioqyZymNUIS2cDcMvvTMwvfBWQb6RnT6OxjfExW1pTf/P2zgQ8kjts6gfrJ85ibUml23EVdiH9ViN67nPzo8vGHQTAI92Nu9XDjY3xULg64S1pYEokWF6yyr7OCphGMIMGxxSZ3K5w52YSvfSZm7IIC+B3eoDcyjCIBcL5AoGBAKRTPRpZ9kXkSiZ88UplbiceGDFdDIHpgsd/a81wM0XIemhLpAhwnXNqK0Ci5HunUXVZ1dDnVvao65vuu6rdD7LkuIGF+oMIKK4BqR4Kl2uFGBrpoxkbb8+wQ0YuybcyQkwQCLNDzTY4x0PZXS2Mwchm2F7iqJhyPPJyLIFfQCSRAoGAGV+DsMAGq5HuL2z203rZJSrNdV5L+ZtBfurDqX+WtkTdmo8DQgWzm82EC+wWM6ZUPlGYjPTOQtzq55Lg0tJH0GCQJDCO+n15WJxVbKpvhNN7uJIs5TorctFgfYAGNatYR9jN7wo79d8ccL/wOaBKcppk8vzEbqBhCtu1iMbf054=";
//
//	// 服务器异步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
//	public static String notify_url = "http://localhost:8080/alipay.trade.wap.pay-JAVA-UTF-8/notify_url.jsp";
//
//	// 页面跳转同步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问 商户可以自定义同步跳转地址
//	public static String return_url = "http://localhost:8080/alipay.trade.wap.pay-JAVA-UTF-8/return_url.jsp";
//	// 请求网关地址
//	public static String URL = "https://openapi.alipaydev.com/gateway.do";
//	// 编码
//	public static String CHARSET = "UTF-8";
//	// 返回格式
//	public static String FORMAT = "json";
//	// 支付宝公钥
//	public static String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjaFVHzdYgeVZ28FUpSjmnqquACcQEnToGv0XIxrGjzppQIg9dHcKxXil6/ZIM7PYfhlcLfOkr+RxoEzuKjJnI/Pang/T+aUgAMkkfPSE3Z6x0Iz0ks/HhzsRI9kXrKFv/3o8RUIpgLzHSZ3OD9Y4wry6nmhOu5DBATdvRIG9DzTs/DqVEd5dc3bAnpb6ykUccbiONhnf20wivz4Avw4Z2zMdpxYhjOfukFXivDJuAz6MFZizsgQuBdYT7wu/4brWuC5B5KTS0lcV8PEMlywTBoCEvdhkRPJ5B2dhmC1QYQK/K0SoDGgjtXdteNxu+UBMZMT+bUA4cShLSGTPV+3/yQIDAQAB";
//	// 日志记录目录
//	public static String log_path = "/log";
//	// RSA2
//	public static String SIGNTYPE = "RSA2";
}

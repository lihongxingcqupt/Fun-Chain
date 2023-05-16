package com.cqupt.art.sms.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MsmConstantUtil implements InitializingBean {
    @Value("${tencent.sms.secretID}")
    private String secretID;
    @Value("${tencent.sms.secretKey}")
    private String secretKey;
    @Value("${tencent.sms.endPoint}")
    private String endPoint;
    @Value("${tencent.sms.appId}")
    private String appId;
    @Value("${tencent.sms.signName}")
    private String signName;
    @Value("${tencent.sms.templateId}")
    private String templateId;
    //六个相关的参数
    public static String SECRET_ID;
    public static String SECRET_KEY;
    public static String END_POINT;
    public static String APP_ID;
    public static String SIGN_NAME;
    public static String TEMPLATE_ID;

    @Override
    public void afterPropertiesSet() throws Exception {
        SECRET_ID = secretID;
        SECRET_KEY = secretKey;
        END_POINT = endPoint;
        APP_ID = appId;
        SIGN_NAME = signName;
        TEMPLATE_ID = templateId;
    }
}

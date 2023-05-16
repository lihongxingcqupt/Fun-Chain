package com.cqupt.art.sms.service.impl;

import com.cqupt.art.sms.entity.LogSms;
import com.cqupt.art.sms.service.LogSmsService;
import com.cqupt.art.sms.service.SmsService;
import com.cqupt.art.sms.utils.MsmConstantUtil;
import com.cqupt.art.sms.utils.RandomCodeUtil;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;
import com.tencentcloudapi.sms.v20210111.models.SendStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class SmssServiceImpl implements SmsService {
    //TODO 太耦合了，考虑把记录日志需要的信息丢给消息队列去处理
    @Autowired
    private LogSmsService logSmsService;
    @Autowired
    StringRedisTemplate redisTemplate;

    public static final String SMS_COUNT_PREFIX = "nft:sms:count:";
    public static final String SMS_CODE_PREFIX = "nft:sms:content:code:";

    @Override
    public boolean send(String phone, String ip) {
        log.info("手机号：{},ip地址：{}", phone, ip);
        LogSms smsLog = new LogSms();
        smsLog.setCreateTime(new Date());
        smsLog.setSmsFunc("loginSendMeaage");
        smsLog.setSmsPhoneNumber(phone);
        smsLog.setSmsIp(ip);
        return sendCode(ip, phone, smsLog);
    }

    @Override
    public boolean send(String phone, String userId, String ip) {
        log.info("手机号：{},ip地址：{},userId:{}", phone, ip, userId);
        LogSms smsLog = new LogSms();
        smsLog.setCreateTime(new Date());
        smsLog.setSmsFunc("loginSendMeaage");
        smsLog.setSmsPhoneNumber(phone);
        smsLog.setSmsIp(ip);
        smsLog.setSmsUserId(userId);
        return sendCode(ip, phone, smsLog);
    }

    /**
     * 同一个手机号或者同一个ip一个小时只能发3条消息
     * 使用redis实现
     * TODO 对发送频率进行限制
     *
     * @param ip
     * @param phone
     * @return
     */
    private boolean isInterceptSend(String ip, String phone) {
        Long count = redisTemplate.opsForValue().increment(SMS_COUNT_PREFIX + phone, 1);
        if (count != null && count > 2) {
            return false;
        } else {
            // 验证码次数凌晨清除
            Duration duration = Duration.between(LocalDateTime.now(), LocalDate.now().plusDays(1).atTime(0, 0, 0));
            redisTemplate.expire(SMS_COUNT_PREFIX + phone, duration.toMinutes(), TimeUnit.MINUTES);
            return true;
        }
//        BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(SMS_COUNT_PREFIX);
//        Boolean hasKey = ops.hasKey(phone);
//        if(hasKey){
//            Long count = ops.increment(phone, 1);
//            log.info("当前用户发送次数：{}",count);
//            return count == null || count.intValue() <= 2;
//        }else{
//
//            ops.put(phone,1);
//            //设置过期时间，第二天0点自动恢复次数
//            Duration duration = Duration.between(LocalDateTime.now(), LocalDate.now().plusDays(1).atTime(0, 0, 0));
//            redisTemplate.expire(SMS_COUNT_PREFIX+phone,duration.toMinutes(), TimeUnit.MINUTES);
//            return true;
//        }
    }


    public boolean sendCode(String ip, String phone, LogSms smsLog) {
        if (!isInterceptSend(ip, phone)) {
            throw new RuntimeException("当前手机号发送短信过多！");
        }
        try {
            log.info("SECRET_ID：{}", MsmConstantUtil.SECRET_ID);
            Credential credential = new Credential(MsmConstantUtil.SECRET_ID, MsmConstantUtil.SECRET_KEY);
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint(MsmConstantUtil.END_POINT);
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            SmsClient smsClient = new SmsClient(credential, "ap-beijing", clientProfile);
            SendSmsRequest request = new SendSmsRequest();
            String[] phoneNumber = {phone};
            request.setPhoneNumberSet(phoneNumber);
            request.setSmsSdkAppId(MsmConstantUtil.APP_ID);
            request.setSignName(MsmConstantUtil.SIGN_NAME);
            request.setTemplateId(MsmConstantUtil.TEMPLATE_ID);
            String verificationCode = RandomCodeUtil.getSixBitRandom();
            smsLog.setSmsCode(verificationCode);
            //设置10分钟有效
            //key hashKey value
            redisTemplate.opsForValue().set(SMS_CODE_PREFIX + phone, verificationCode, 10, TimeUnit.MINUTES);
//            redisTemplate.expire(SMS_CODE_PREFIX+":-"+phone,10,TimeUnit.MINUTES);
            String[] templateParamSet = {verificationCode};
            String content = "【踩坑指北公众号】您的动态验证码为：" + verificationCode + "，您正在进行密码重置操作，如非本人操作，请忽略本短信！";
            smsLog.setSmsContent(content);
            request.setTemplateParamSet(templateParamSet);
            SendSmsResponse response = smsClient.SendSms(request);//异常是在这里抛出来的
            SendStatus sendStatus = response.getSendStatusSet()[0];
            smsLog.setFee(sendStatus.getFee());
            smsLog.setRequestId(response.getRequestId());
            smsLog.setResponseCode(sendStatus.getCode());
            smsLog.setResponseMessage(sendStatus.getMessage());
            log.info(SendSmsResponse.toJsonString(response));
            logSmsService.save(smsLog);
            return true;
        } catch (TencentCloudSDKException e) {
            log.error(e.getMessage());
            smsLog.setFee(0l);
            smsLog.setResponseMessage("发送短信失败");
            smsLog.setRequestId("发送短信失败");
            smsLog.setResponseCode("发送短信失败");
            logSmsService.save(smsLog);
            return false;
        }
    }
}

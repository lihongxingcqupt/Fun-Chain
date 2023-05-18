package com.cqupt.art.sms.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 短信发送记录表
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("pm_log_sms")
public class LogSms implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 使用发送短信的requestId
     */
    @TableId(type = IdType.ID_WORKER_STR)
    private String smsId;

    /**
     * 发送者ID(注册时发送验证码不需要记录)
     */
    private String smsUserId;

    /**
     * 短信内容
     */
    private String smsContent;

    /**
     * 短信号码
     */
    private String smsPhoneNumber;

    /**
     * 短信返回值
     */
    private String smsReturn;

    /**
     * 短信中的验证码
     */
    private String smsCode;

    /**
     * 调用短信的接口（只集成了腾讯云的短信）
     */
    private String smsFunc;

    /**
     * IP地址
     */
    private String smsIp;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 本次花费
     */
    private Long fee;

    /**
     * 云服务返回的code
     */
    private String responseCode;

    /**
     * 云服务返回的message
     */
    private String responseMessage;

    /**
     * 云服务发送的请求id
     */
    private String requestId;


}

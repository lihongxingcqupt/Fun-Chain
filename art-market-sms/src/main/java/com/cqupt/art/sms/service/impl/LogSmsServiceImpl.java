package com.cqupt.art.sms.service.impl;

import com.cqupt.art.sms.entity.LogSms;
import com.cqupt.art.sms.mapper.LogSmsMapper;
import com.cqupt.art.sms.service.LogSmsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 短信发送记录表 服务实现类
 * </p>
 *
 * @author lihongxing
 * @since 2022-11-15
 */
@Service
public class LogSmsServiceImpl extends ServiceImpl<LogSmsMapper, LogSms> implements LogSmsService {

}

package com.cqupt.art.sms.mapper;

import com.cqupt.art.sms.entity.LogSms;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 短信发送记录表 Mapper 接口
 * </p>
 *
 * @author huangxudong
 * @since 2022-11-15
 */
@Mapper
public interface LogSmsMapper extends BaseMapper<LogSms> {

}

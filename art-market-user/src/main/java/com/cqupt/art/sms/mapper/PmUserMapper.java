package com.cqupt.art.sms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cqupt.art.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author huangxudong
 * @since 2022-11-03
 */
@Mapper
public interface PmUserMapper extends BaseMapper<User> {

}

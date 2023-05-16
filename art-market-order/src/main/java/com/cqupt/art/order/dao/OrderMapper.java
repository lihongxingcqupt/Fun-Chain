package com.cqupt.art.order.dao;

import com.cqupt.art.order.entity.Order;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 订单表 Mapper 接口
 * </p>
 *
 * @author huangxudong
 * @since 2022-11-22
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

}

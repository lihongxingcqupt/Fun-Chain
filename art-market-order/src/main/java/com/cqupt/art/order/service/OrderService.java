package com.cqupt.art.order.service;

import com.cqupt.art.order.entity.Order;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cqupt.art.order.entity.to.SeckillOrderTo;
import com.cqupt.art.order.entity.vo.AlipayAsyncVo;
import com.cqupt.art.order.entity.vo.PayVo;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单表 服务类
 * </p>
 *
 * @author huangxudong
 * @since 2022-11-22
 */
@Service
public interface OrderService extends IService<Order> {

    void createSeckillOrder(SeckillOrderTo orderTo);

    PayVo getOrderPay(String orderSn);

    boolean handlerPayResult(AlipayAsyncVo alipayAsyncVo);
}

package com.cqupt.art.order.service.impl;

import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cqupt.art.order.dao.OrderMapper;
import com.cqupt.art.order.entity.Order;
import com.cqupt.art.order.entity.to.SeckillOrderTo;
import com.cqupt.art.order.entity.vo.PayVo;
import com.cqupt.art.order.feign.NftWorksClient;
import com.cqupt.art.order.service.OrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqupt.art.utils.R;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author huangxudong
 * @since 2022-11-22
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private NftWorksClient worksClient;

    @Override
    public void createSeckillOrder(SeckillOrderTo orderTo) {
        Order order = new Order();
        BeanUtils.copyProperties(orderTo, order);

        //卖方id为0为首发订单
        order.setSellUserId("0");
        order.setNum(1);
        //每次只能买一个
        order.setSumPrice(orderTo.getPrice());
        //不发优惠卷
        order.setPayMoney(orderTo.getPrice());
        order.setStatus(1);
        this.save(order);
        //秒杀限制了总量，所以库存是不会出问题的，所以不用先锁库存，支付成功了锁库存就行了
    }

    @Override
    public PayVo getOrderPay(String orderSn) {
        Order order = this.getOne(new QueryWrapper<Order>().eq("order_sn", orderSn));
        PayVo payVo = new PayVo();
        payVo.setOut_trade_no(orderSn);
        BigDecimal amount = order.getSumPrice();
        amount.setScale(2, BigDecimal.ROUND_UP);
        payVo.setTotal_amount(amount.toString());

        R r = worksClient.getNftName(order.getGoodsId().toString());
        if (r.getCode() == 200) {
            String name = r.getData("data", new TypeReference<String>() {
            });
            payVo.setSubject(name);
            if ("0".equals(order.getSellUserId())) {
                payVo.setBody("首发订单-" + name);
            } else {
                payVo.setBody("二级订单-" + order.getSellUserId() + "-" + name);
            }
        }
        return payVo;
    }
}

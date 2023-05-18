package com.cqupt.art.order.service.impl;

import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cqupt.art.constant.SeckillConstant;
import com.cqupt.art.enu.NftGainTypeEnum;
import com.cqupt.art.enu.NftStatusEnum;
import com.cqupt.art.enu.OrderStatusEnum;
import com.cqupt.art.order.dao.OrderMapper;
import com.cqupt.art.order.entity.Order;
import com.cqupt.art.order.entity.UserToken;
import com.cqupt.art.order.entity.UserTokenItem;
import com.cqupt.art.order.entity.to.ChainTransferTo;
import com.cqupt.art.order.entity.to.NftBatchInfoTo;
import com.cqupt.art.order.entity.to.SeckillOrderTo;
import com.cqupt.art.order.entity.to.TransferLog;
import com.cqupt.art.order.entity.vo.AlipayAsyncVo;
import com.cqupt.art.order.entity.vo.PayVo;
import com.cqupt.art.order.feign.NftWorksClient;
import com.cqupt.art.order.feign.TradeClient;
import com.cqupt.art.order.service.OrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqupt.art.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.concurrent.*;


@Service
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private NftWorksClient worksClient;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    TradeClient tradeClient;

    @Autowired
    RabbitTemplate rabbitTemplate;

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

    }

    /**
     * 封装订单支付的VO，以调用支付宝进行支付
     * @param orderSn
     * @return
     */
    @Override
    public PayVo getOrderPay(String orderSn) {
        Order order = this.getOne(new QueryWrapper<Order>().eq("order_sn", orderSn));
        PayVo payVo = new PayVo();
        payVo.setOut_trade_no(orderSn);
        BigDecimal amount = order.getSumPrice();
        amount.setScale(2, BigDecimal.ROUND_UP);
        payVo.setTotal_amount(amount.toString());

        R r = worksClient.getNftInfo(order.getGoodsId().toString());
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

    /**
     * 根据支付结果进行处理
     * @param alipayAsyncVo
     * @return
     */
    @Override
    public boolean handlerPayResult(AlipayAsyncVo alipayAsyncVo) {
        Order order = this.getOne(new QueryWrapper<Order>().eq("order_sn", alipayAsyncVo.getOut_trade_no()));
        if ("TRADE_SUCCESS".equals(alipayAsyncVo.getTrade_status())) {
            //支付成功
            order.setStatus(OrderStatusEnum.PAID.getCode());
            order.setPayTime(alipayAsyncVo.getGmt_payment());
            order.setEndTime(alipayAsyncVo.getGmt_close());
            this.updateById(order);
            //支付成功，应当给用户转入藏品
            transferToUser(order);

            //将用户添加到redis并设置过期时间，使用户本场不能进行购买
            long ttl = order.getEndTime().getTime() - System.currentTimeMillis();
            redisTemplate.opsForValue().setIfAbsent(SeckillConstant.USER_BOUGHT_FLAG + order.getId(), "1", ttl, TimeUnit.MILLISECONDS);

            return true;
        } else if ("TRADE_CLOSED".equals(alipayAsyncVo.getTrade_status())) {
            //超时关闭
            order.setStatus(OrderStatusEnum.FAIL.getCode());
            order.setEndTime(alipayAsyncVo.getGmt_close());
            this.updateById(order);
            return true;
        }
        return false;
    }

    /**
     * 支付成功，进行财产转移，包括本地转移和链上转移
     * @param order
     */
    @Transactional
    public void transferToUser(Order order) {
        /**
         * 封装链上财产转移的TO，远程调用区块链服务进行链上的财产转移
         */
        ChainTransferTo chainTransferTo = new ChainTransferTo();
        chainTransferTo.setFromUserId(order.getSellUserId());
        chainTransferTo.setToUserId(order.getBuyUserId());
        chainTransferTo.setArtId(order.getGoodsId());
        Integer localId = order.getLocalId();
        if (localId == null) {
            log.info("订单支付完成，生成本地id");
            R r = worksClient.getLocalId(order.getGoodsId(), order.getBuyUserId());
            localId = r.getData("data", new TypeReference<Integer>() {
            });
            log.info("生成的本地id为：{}", localId);
        }
        chainTransferTo.setLocalId(localId);

        // 链上转帐，远程调用的话会阻塞在这里，影响效率
        rabbitTemplate.convertAndSend("nft-order-event", "nft.order.chain.transfer", chainTransferTo);

        /**
         * 本地转移：
         * UserTokenItem：用户和藏品的一一对应
         * UserToken：用户拥有的藏品
         * TransferLog：交易日志
         */

        /**
         * 封装用户和藏品的对应信息，用于展示用户的全部藏品的详细信息
         */
        UserTokenItem item = new UserTokenItem();
        item.setPrice(order.getPrice());
        item.setLocalId(localId);
        // 藏品的状态,待链上确认
        item.setStatus(NftStatusEnum.WAIT_CHAIN_CONFIRM.getCode());
        //获取藏品的方式，有卖家ID就是二级市场，没有的话就是首发
        int gainType = order.getSellUserId().equals("0") ? NftGainTypeEnum.FIRST_BUY.getCode() : NftGainTypeEnum.TWO_BUY.getCode();
        item.setGainType(gainType);
        R r = worksClient.getNftInfo(order.getGoodsId());
        NftBatchInfoTo nftBatchInfoTo = null;
        if (r.getCode() == 200) {
            nftBatchInfoTo = r.getData("data", new TypeReference<NftBatchInfoTo>() {
            });
            item.setTokenType(nftBatchInfoTo.getType());
        }


        /**
         * 封装 userToken，用户和藏品的概览信息，用于展示，某某用户拥有 xx 等几件藏品
         */
        R result = tradeClient.getUserToken(order.getBuyUserId(), order.getGoodsId());
        UserToken userToken = null;
        if (result.getCode() == 200) {
            userToken = result.getData("data", new TypeReference<UserToken>() {});
        }
        if (userToken == null) {
            userToken = new UserToken();
            userToken.setUserId(order.getBuyUserId());
            userToken.setArtId(order.getGoodsId());
            userToken.setCount(order.getNum());
            userToken.setSail(0);

        } else {
            userToken.setCount(userToken.getCount() + order.getNum());
        }

        /**
         * 封装交易日志
         */
        TransferLog logTo = new TransferLog();
        logTo.setFromUid(order.getSellUserId());
        logTo.setToUid(order.getBuyUserId());
        logTo.setNftId(order.getGoodsId());
        logTo.setLocalId(order.getLocalId());
        logTo.setPrice(order.getPrice());

        logTo.setLocalId(localId);
        //保存交易日志
        if (order.getSellUserId().equals("0")) {
            logTo.setType(1);
        } else {
            logTo.setType(4);
        }


        /**
         * 自定义线程池
         */
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                5,
                5,
                60,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100000),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
        /**
         * 使用 CompletableFuture 开启异步保存三张表，因为三张表都是跨服务保存，若不异步，性能会很差
         */
        // 保存 UserTokenItem
        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() -> {
            tradeClient.saveUserTokenItem(item);
        }, executor);

        // 保存 UserToken
        UserToken finalUserToken = userToken;
        CompletableFuture<Void> voidCompletableFuture1 = CompletableFuture.runAsync(() -> {
            tradeClient.saveUserToken(finalUserToken);
        }, executor);

        // 保存 交易日志
        CompletableFuture<Void> voidCompletableFuture2 = CompletableFuture.runAsync(() -> {
            tradeClient.saveTransferLog(logTo);
        }, executor);
    }
}

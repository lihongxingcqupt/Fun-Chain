package com.cqupt.art.service.listener;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cqupt.art.config.mq.RegisterMqConfig;
import com.cqupt.art.entity.User;
import com.cqupt.art.entity.to.AccountInfoTo;
import com.cqupt.art.entity.vo.UserRegisterVo;
import com.cqupt.art.feign.ChainClient;
import com.cqupt.art.service.UserService;
import com.cqupt.art.utils.R;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RabbitListener(queues = RegisterMqConfig.REGISTER_QUEUE)
@Slf4j
public class RegisterListener {
    @Autowired
    private ChainClient chainClient;
    @Autowired
    private UserService userService;

    @SneakyThrows
    @RabbitHandler
    public void createChainAccount(UserRegisterVo vo, Channel channel, Message message) {
        log.info("创建链上账户，密码为：{}", vo.getPassword());
        R r = chainClient.createAccount(vo.getPassword());
        log.info("链上返回的R：{}", JSON.toJSONString(r));
        if (r.getCode() == 200) {
            User user = userService.getOne(new QueryWrapper<User>().eq("user_phone", vo.getPhoneNumber()));
            AccountInfoTo ai = r.getData("data", new TypeReference<AccountInfoTo>() {
            });
            user.setChainAddress(ai.getAddress());
            user.setPrivateKey(ai.getPrivateKey());
            user.setAccountPassword(ai.getPassword());
            userService.updateUser(user);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } else {
            log.info("fuck,创建链上账户又出问题了....，重试吧...");
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        }
    }
}

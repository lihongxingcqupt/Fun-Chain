package com.cqupt.art.activity.service.listener;

import com.alibaba.fastjson2.TypeReference;
import com.cqupt.art.activity.dao.WinningUserDao;
import com.cqupt.art.activity.entity.ActivityEntity;
import com.cqupt.art.activity.entity.NftBatchInfoEntity;
import com.cqupt.art.activity.entity.WinningUserEntity;
import com.cqupt.art.activity.entity.vo.ActivityVo;
import com.cqupt.art.activity.feign.AuthorFeignClient;
import com.cqupt.art.utils.R;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service

@Slf4j
public class ChouqianListener {
    @Autowired
    AuthorFeignClient authorFeignClient;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    WinningUserDao winningUserDao;

    @RabbitListener(queues = "chouqianDuilie")
    @SneakyThrows
    @RabbitHandler
    public void chouqian(ActivityEntity activityEntity, Channel channel, Message message){
        int num = 0;//发行数量，也就是中奖人数
        R r = authorFeignClient.getById(activityEntity.getNftId());
        if(r.getCode() != 200){
            log.error("远程调用查询作品失败");
        }else{
            NftBatchInfoEntity nftBatchInfoEntity = r.getData("data", new TypeReference<NftBatchInfoEntity>() {
            });
            num = nftBatchInfoEntity.getTotalSupply();
        }
        LocalDateTime now = LocalDateTime.now();
        log.info("收到消息id是{}时间是{}",activityEntity,now);
        BoundSetOperations userIds = redisTemplate.boundSetOps("userIds");
        Set ids = userIds.members();
        for (Object id : ids) {
            System.out.println((String) id);
        }
        List<Object> list = new ArrayList<>(ids);
        //将顺序随机打乱后，选取前num个人即时中签的人
        Collections.shuffle(list);
        for(int i = 0;i < num && i < list.size();i++){
            log.info("中奖用户是{}",list.get(i).toString());
            WinningUserEntity winningUserEntity = new WinningUserEntity();
            winningUserEntity.setUserId(list.get(i).toString());
            winningUserEntity.setActivityId(activityEntity.getId());
            winningUserDao.insert(winningUserEntity);
        }

        log.info("抽签成功，手动ack消息");
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }

}

package com.cqupt.art.author.service.listener;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cqupt.art.author.config.mq.MyNftMqConfig;
import com.cqupt.art.author.entity.AuthorEntity;
import com.cqupt.art.author.entity.NftBatchInfoEntity;
import com.cqupt.art.author.entity.NftInfoEntity;
import com.cqupt.art.author.entity.NftMetadata;
import com.cqupt.art.author.entity.to.CreateNftBatchInfoTo;
import com.cqupt.art.author.entity.to.CreateNftBatchResultTo;
import com.cqupt.art.author.feign.ChainFeignService;
import com.cqupt.art.author.service.AuthorService;
import com.cqupt.art.author.service.NftBatchInfoService;
import com.cqupt.art.author.service.NftInfoService;
import com.cqupt.art.utils.R;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RabbitListener(queues = MyNftMqConfig.QUEUE_MINT_PRODUCT)
@Slf4j
public class ChainListener {
    @Autowired
    NftBatchInfoService batchInfoService;
    @Autowired
    AuthorService authorService;
    @Autowired
    ChainFeignService chainFeignService;

    @Autowired
    NftInfoService nftInfoService;

    @SneakyThrows
    @RabbitHandler
    public void upToChain(NftBatchInfoEntity batchInfoEntity, Channel channel, Message message) {
        log.info("消费端收到消息：{}", JSON.toJSONString(batchInfoEntity));
        CreateNftBatchInfoTo infoTo = new CreateNftBatchInfoTo();
        AuthorEntity author = authorService.getById(batchInfoEntity.getAuthorId());
        log.info("查到的作者信息：{}", JSON.toJSONString(author));
        batchInfoEntity = batchInfoService.getOne(new QueryWrapper<NftBatchInfoEntity>().eq("author_id", author.getAuthorId()).eq("name", batchInfoEntity.getName()));
        infoTo.setAuthorName(author.getAuthorName());
        infoTo.setNum(batchInfoEntity.getTotalSupply());
        NftMetadata metadata = new NftMetadata();
        metadata.setName(batchInfoEntity.getName());
        metadata.setDescription(batchInfoEntity.getDescription());
        metadata.setImage(batchInfoEntity.getImageUrl());
        infoTo.setMetadata(metadata);
        R r = chainFeignService.createNftBatchOnce(infoTo);
        if (r.getCode() == 200) {
            CreateNftBatchResultTo resultTo = r.getData("data", new TypeReference<CreateNftBatchResultTo>() {
            });
            batchInfoEntity.setTxHash(resultTo.getTxHash());
            batchInfoEntity.setTokenUri(resultTo.getTokenUri());
            //更新batch的信息
            batchInfoService.update(batchInfoEntity, new QueryWrapper<NftBatchInfoEntity>().eq("author_id", author.getAuthorId()).eq("name", batchInfoEntity.getName()));
            //把tokenids放到表中
            NftInfoEntity infoEntity = new NftInfoEntity();
            BeanUtils.copyProperties(batchInfoEntity, infoEntity);
            infoEntity.setArtId(batchInfoEntity.getId().toString());
            infoEntity.setTokenName(batchInfoEntity.getName());
            Integer totalSupply = batchInfoEntity.getTotalSupply();
            List<BigInteger> tokenIds = resultTo.getTokenIds();
            List<NftInfoEntity> entiityList = new ArrayList<>();
            for (int i = 0; i < tokenIds.size(); i++) {
                NftInfoEntity entity = new NftInfoEntity();
                BeanUtils.copyProperties(infoEntity, entity);
                infoEntity.setTokenId(tokenIds.get(i).longValue());
                int localId = i + 1;
                log.info("localId---->{}", String.valueOf(localId));
                entity.setLocalId(localId);
                entiityList.add(entity);
            }
            log.info("插入数据库的数据： {}", JSON.toJSONString(entiityList));
            nftInfoService.saveBatch(entiityList);
            log.info("插入成功");
            //上链成功，给消息队列确认
            log.info("上链成功，手动ack消息");
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } else {
            log.info("上链异常，拒绝消费消息......重试");
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        }
    }
}

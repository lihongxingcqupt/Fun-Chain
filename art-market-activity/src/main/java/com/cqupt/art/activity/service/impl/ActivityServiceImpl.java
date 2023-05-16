package com.cqupt.art.activity.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqupt.art.activity.dao.ActivityDao;
import com.cqupt.art.activity.entity.ActivityEntity;
import com.cqupt.art.activity.entity.NftBatchInfoEntity;
import com.cqupt.art.activity.entity.vo.ActivityVo;
import com.cqupt.art.activity.entity.vo.DengjiVo;
import com.cqupt.art.activity.feign.AuthorFeignClient;
import com.cqupt.art.activity.service.ActivityService;
import com.cqupt.art.utils.PageUtils;
import com.cqupt.art.utils.Query;
import com.cqupt.art.utils.R;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service("activityService")
public class ActivityServiceImpl extends ServiceImpl<ActivityDao, ActivityEntity> implements ActivityService {
    @Autowired
    AuthorFeignClient authorFeignClient;
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    RedisTemplate<String,String> redisTemplate;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<ActivityEntity> queryWrapper = new QueryWrapper<>();

        IPage<ActivityEntity> page = this.page(
                new Query<ActivityEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    /**
     * 往mq中发送消息使之到期后后台能自动的往登记了的人当中抽出中签的人
     * 返回当前的活动信息给前台展示活动页面
     * @param nftId
     * @return
     */
    @Override
    public ActivityVo shangjia(Long nftId) {
        //先将活动上架前端所需的信息返回给前端
        R r = authorFeignClient.getById(nftId);
        ActivityVo activityVo = new ActivityVo();
        if(r.getCode() != 200){
            log.error("远程调用查询作品失败");
        }else{
            NftBatchInfoEntity nftBatchInfoEntity = r.getData("data", new TypeReference<NftBatchInfoEntity>() {
            });
            activityVo.setNftBatchInfoEntity(nftBatchInfoEntity);
        }
        Map<String, Object> param = new HashMap<>();
        param.put("nft_id",nftId);
        List<ActivityEntity> activityEntities = this.getBaseMapper().selectByMap(param);
        activityVo.setActivityEntity(activityEntities.get(0));



        //计算延时时间，也就是抽签结果公布距离现在的时间的毫秒值
        ActivityEntity activityEntity = activityEntities.get(0);
        Date drawRelease = activityEntity.getDrawRelease();
        Instant instant = drawRelease.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        //todo 这里的时间数据库默认的不是北京时间因此会出现问题，要永久修改只能修改配置文件，现在修改了的只是暂时的
        LocalDateTime raleaseTime = instant.atZone(zoneId).toLocalDateTime();
        LocalDateTime now = LocalDateTime.now();
        long expirStart = Duration.between(now, raleaseTime).toMillis();

        System.out.println(expirStart);
//        long expirStart = 30000;

        //往mq中发送延时消息，一到抽签结果出现的时间就会收到消息，并给出结果
        rabbitTemplate.convertAndSend("chouqianExchange","chouqianRouting",activityEntity, message -> {
            // 这里的失效时间是long类型，普通的TTL方式的类型是String类型
            message.getMessageProperties().setHeader("x-delay",expirStart);
            return message;
        });
        /**
         * 每一次上架都将上一次redis中存放用户ID的set移除；
         */
        redisTemplate.delete("userIds");
        return activityVo;
    }

    @Override
    public boolean dengji(DengjiVo dengjiVo) {
        /**
         * 先检验时间有无问题,也就是现在的请求时间是不是在活动范围中
         */
        Map<String, Object> param = new HashMap<>();
        param.put("nft_id",dengjiVo.getNftId());
        ActivityEntity activityEntity = this.getBaseMapper().selectByMap(param).get(0);
        Date regisBegin = activityEntity.getRegisBegin();
        Date regisEnd = activityEntity.getRegisEnd();
        Date now = new Date(System.currentTimeMillis());
        if(!now.after(regisBegin) || !now.before(regisEnd)){
            return false;
        }
//        BoundSetOperations memberId = redisTemplate.boundSetOps("memberId");
//        redisTemplate.expire("memberId",5, TimeUnit.MINUTES);
//        memberId.add("1");
//        memberId.add("2");
//        memberId.add("2");
//        memberId.add("3");
//        memberId.add("3");
        BoundSetOperations userIds = redisTemplate.boundSetOps("userIds");
        userIds.add(dengjiVo.getUserId());
        return true;
    }
}

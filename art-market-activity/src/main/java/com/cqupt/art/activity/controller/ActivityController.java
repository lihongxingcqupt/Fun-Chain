package com.cqupt.art.activity.controller;

import com.alibaba.fastjson.JSONObject;
import com.cqupt.art.activity.entity.ActivityEntity;
import com.cqupt.art.activity.entity.vo.ActivityVo;
import com.cqupt.art.activity.entity.vo.DengjiVo;
import com.cqupt.art.activity.service.ActivityService;
import com.cqupt.art.utils.PageUtils;
import com.cqupt.art.utils.R;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RequestMapping("/author/activity")
@RestController
public class ActivityController {

    @Autowired
    ActivityService activityService;
    /**
     * 新建一个活动，但还没开始，先放到那里，点击开始后再开始
     */
    @RequestMapping("/save")
    public R save(@RequestBody ActivityEntity activityEntity){
        activityService.save(activityEntity);
        return R.ok();
    }


    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = activityService.queryPage(params);
        return R.ok().put("page", page);
    }

    /**
     * 将这个活动正式上架，往mq中发送一条消息，利用延时队列实现在登记完了以后自动抽签
     */
    @GetMapping("shangjia")
    public R shangjia(@RequestParam("nftId") Long nftId){
        ActivityVo activityVo = activityService.shangjia(nftId);
        return R.ok().put("data",activityVo);
    }

    /**
     * 登记时间到了，开始登记，将用户的ID存入Redis中做后面的抽签算法
     */
    @Valid
    @PostMapping("/dengji")
    public R dengji(@RequestBody DengjiVo dengjiVo){
        boolean result = activityService.dengji(dengjiVo);
        if(!result){
           return R.error("登记时间已过");
        }
        return R.ok();
    }

    @GetMapping("/zhongqianGoumai")
    public R zhongqianGoumai(@RequestParam("userId") Long userId,@RequestParam("activityId") String activityId){
        return null;
    }
}

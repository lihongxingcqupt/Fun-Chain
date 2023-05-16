package com.cqupt.art.activity.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cqupt.art.activity.entity.ActivityEntity;
import com.cqupt.art.activity.entity.vo.ActivityVo;
import com.cqupt.art.activity.entity.vo.DengjiVo;
import com.cqupt.art.utils.PageUtils;

import java.util.Map;

public interface ActivityService extends IService<ActivityEntity> {
    PageUtils queryPage(Map<String, Object> params);

    ActivityVo shangjia(Long nftId);

    boolean dengji(DengjiVo dengjiVo);
}

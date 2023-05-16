package com.cqupt.art.activity.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cqupt.art.activity.entity.ActivityEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ActivityDao extends BaseMapper<ActivityEntity> {
}

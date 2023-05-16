package com.cqupt.art.author.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cqupt.art.author.entity.AuthorEntity;
import com.cqupt.art.utils.PageUtils;

import java.util.List;
import java.util.Map;

public interface AuthorService extends IService<AuthorEntity> {
    List<AuthorEntity> getlist();

    PageUtils queryPage(Map<String, Object> params);

}

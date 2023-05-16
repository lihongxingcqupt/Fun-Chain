package com.cqupt.art.author.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqupt.art.author.dao.AuthorDao;
import com.cqupt.art.author.entity.AuthorEntity;
import com.cqupt.art.author.service.AuthorService;
import com.cqupt.art.utils.PageUtils;
import com.cqupt.art.utils.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("authorService")
public class AuthorServiceImpl extends ServiceImpl<AuthorDao, AuthorEntity> implements AuthorService {

    @Override
    public List<AuthorEntity> getlist() {
        return this.baseMapper.selectList(null);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<AuthorEntity> queryWrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (key != null && key.length() != 0) {
            queryWrapper.eq("author_id", key).or().like("author_name", key);
        }
        IPage<AuthorEntity> page = this.page(
                new Query<AuthorEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }
}

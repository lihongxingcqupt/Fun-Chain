package com.cqupt.art.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cqupt.art.sms.entity.PmArticles;
import com.cqupt.art.service.PmArticlesService;
import com.cqupt.art.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 文章记录表 前端控制器
 * </p>
 */
@RestController
@RequestMapping("/api/pm-articles")
@CrossOrigin
@Slf4j
public class PmArticlesController {

    @Autowired
    private PmArticlesService articlesService;

    // 只是保存到了数据库
    @PostMapping("/publishNotice")
    public R publishNotice(@RequestBody PmArticles article) {
        log.info("PmArticles--->{}", article.toString());
        if (article.getPublishTime() == null) {
            return R.error("发布时间不能为空！");
        }
        articlesService.save(article);
        log.info("保存公告成功！");
        return R.ok("发布公告成功！");
    }

    @GetMapping("/list/{curPage}/{limit}")
    public R list(@PathVariable("curPage") Integer curPage, @PathVariable("limit") Integer limit) {
        log.info("list curPage->{},limit->{}", curPage, limit);
        Page<PmArticles> queryPage = new Page<>(curPage, limit);
        IPage<PmArticles> page = articlesService.page(queryPage);
        List<PmArticles> records = page.getRecords();
        long total = page.getTotal();
        return R.ok().put("items", records).put("total", total);
    }

    @GetMapping("/articleDetail/{id}")
    public R getArticle(@PathVariable("id") String articleId) {
        PmArticles article = articlesService.getById(articleId);
        return R.ok().put("data", article);
    }


}

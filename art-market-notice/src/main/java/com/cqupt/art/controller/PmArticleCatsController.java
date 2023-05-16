package com.cqupt.art.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cqupt.art.sms.entity.PmArticleCats;
import com.cqupt.art.sms.entity.vo.ArticleCatsVo;
import com.cqupt.art.service.PmArticleCatsService;
import com.cqupt.art.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 文章分类表 前端控制器
 * </p>
 *
 * @author huangxudong
 * @since 2022-11-07
 */
@CrossOrigin
@RestController
@RequestMapping("/api/article-cats")
public class PmArticleCatsController {
    @Autowired
    private PmArticleCatsService articleCatsService;

    @GetMapping("/list/{curPage}/{limit}")
    public R list(@PathVariable("curPage") Integer curPage, @PathVariable("limit") Integer limit) {
        ArticleCatsVo vo = articleCatsService.listVo(curPage, limit);
        return R.ok().put("data", vo).put("total", vo.getItems().size());
    }

    @PostMapping("/addArticleCat")
    public R addCat(@RequestBody PmArticleCats cats) {
        if (cats.getParentId() != null) {
            articleCatsService.save(cats);
            return R.ok("新增公告分类成功！");
        } else {
            return R.error("请输入完整数据！");
        }
    }

    @PostMapping("/updateNoticeCat")
    public R updateNoticeCat(@RequestBody PmArticleCats cats) {
        boolean updated = articleCatsService.updateById(cats);
        if (updated) {
            return R.ok("更新分类成功！");
        } else {
            return R.error("更新分类失败");
        }
    }

    //只用一级分类去区分公告类型
    @GetMapping("/getCats")
    public R getCats(@RequestParam(value = "limit", required = false) Integer limit) {
        QueryWrapper<PmArticleCats> wrapper = new QueryWrapper<PmArticleCats>();
        if (limit != null) {
            wrapper.gt("cat_id", limit).eq("is_show", 1);
        } else {
            wrapper.eq("parent_id", 0);
        }
        List<PmArticleCats> rootCats = articleCatsService.list(wrapper);
        Map<Integer, String> idNameMap = rootCats.stream().collect(Collectors.toMap(PmArticleCats::getCatId, PmArticleCats::getCatName, (k1, k2) -> k1));
        if (limit == null) {
            idNameMap.put(0, "根分类");
        }
        return R.ok().put("parentMap", idNameMap);
    }

    @GetMapping("/deleteCat")
    public R deleteCat(@RequestParam("catId") Integer catId) {
        articleCatsService.removeById(catId);
        return R.ok("删除分类成功！");
    }

}

package com.cqupt.art.author.controller;

import com.cqupt.art.author.entity.AuthorEntity;
import com.cqupt.art.author.service.AuthorService;
import com.cqupt.art.utils.PageUtils;
import com.cqupt.art.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/author/authorInfo")
public class AuthorController {
    @Autowired
    AuthorService authorService;


    @RequestMapping("/list")

    public R list(@RequestParam Map<String, Object> params) {

        PageUtils page = authorService.queryPage(params);
        return R.ok().put("page", page);
    }

    /**
     * 通过id获取信息
     */


    public R info(@PathVariable("authorId") Long authorId) {

        AuthorEntity author = authorService.getById(authorId);

        return R.ok().put("author", author);
    }

    /**
     * 保存
     */


    public R save(@RequestBody AuthorEntity authorEntity) {

        authorService.save(authorEntity);
        return R.ok();
    }

    /**
     * 修改
     */


    public R update(@RequestBody AuthorEntity author) {

        authorService.updateById(author);
        return R.ok();
    }

    /**
     * 删除
     */

    public R delete(@RequestBody Long[] brandIds) {

        authorService.removeByIds(Arrays.asList(brandIds));
        return R.ok();
    }
}

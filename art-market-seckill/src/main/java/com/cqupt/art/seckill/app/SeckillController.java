package com.cqupt.art.seckill.app;

import com.cqupt.art.seckill.entity.vo.SeckillInfoVo;
import com.cqupt.art.seckill.service.SeckillService;
import com.cqupt.art.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

public class SeckillController {
    @Autowired
    SeckillService seckillService;


    @GetMapping("/nft")
    public R seckill(@RequestBody SeckillInfoVo info) throws InterruptedException {
        seckillService.kill(info);
        return R.ok().put("status", true);
    }
}

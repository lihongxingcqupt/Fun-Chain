package com.cqupt.art.seckill.service;


import com.cqupt.art.seckill.entity.vo.SeckillInfoVo;

public interface SeckillService {
    String kill(SeckillInfoVo info) throws InterruptedException;
}

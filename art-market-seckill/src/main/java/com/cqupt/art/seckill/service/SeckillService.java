package com.cqupt.art.seckill.service;


import com.cqupt.art.seckill.entity.vo.SeckillInfoVo;

public interface SeckillService {
    void kill(SeckillInfoVo info) throws InterruptedException;
}

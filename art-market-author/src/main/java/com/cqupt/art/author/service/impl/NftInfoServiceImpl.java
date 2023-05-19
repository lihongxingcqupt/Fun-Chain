package com.cqupt.art.author.service.impl;


import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqupt.art.author.dao.NftInfoDao;
import com.cqupt.art.author.entity.NftInfoEntity;
import com.cqupt.art.author.entity.to.TransferLogTo;
import com.cqupt.art.author.entity.to.UserTo;
import com.cqupt.art.author.entity.vo.NftAndUserVo;
import com.cqupt.art.author.feign.TradeFeignService;
import com.cqupt.art.author.feign.UserFeignService;
import com.cqupt.art.author.service.NftInfoService;
import com.cqupt.art.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service("nftInfoService")
@CacheConfig(cacheNames = "nftInfo")
public class NftInfoServiceImpl extends ServiceImpl<NftInfoDao, NftInfoEntity> implements NftInfoService {

    @Autowired
    UserFeignService userFeignService;

    @Autowired
    TradeFeignService tradeFeignService;

    @Override
    @Cacheable(key = "#id")
    public List<NftAndUserVo> queryPage(Map<String, Object> params) {
        QueryWrapper<NftInfoEntity> queryWrapper = new QueryWrapper<>();
        String artName = (String) params.get("artName");
        if (artName != null && artName.length() != 0) {
            queryWrapper.like("token_name", artName);
        }
        String state = (String) params.get("state");
        if (state != null && state.length() != 0) {
            int state_int = Integer.parseInt(state);
            queryWrapper.and(q -> {
                q.eq("state", state_int);
            });
        }
        String getWay = (String) params.get("getWay");
        if (getWay != null && getWay.length() != 0) {
            int getWay_int = Integer.parseInt(getWay);
            queryWrapper.and(q -> {
                q.eq("getWay", getWay_int);
            });
        }

        List<NftInfoEntity> nftInfoEntityList = this.list(queryWrapper);
        List<NftAndUserVo> nftAndUserVoList = new ArrayList<>();
        for (NftInfoEntity nftInfoEntity : nftInfoEntityList) {
            R r = userFeignService.getPhoneAndAddById(nftInfoEntity.getUserId());
            String phone = (String) params.get("phone");
            if (r.getCode() != 0) {
                log.error("远程查询用户信息失败");
            } else {
                UserTo userTo = r.getData("data", new TypeReference<UserTo>() {
                });
                if (phone != null && phone.length() > 0) {
                    if (userTo.getUserPhone().equals(phone)) {
                        NftAndUserVo nftAndUserVo = new NftAndUserVo();
                        nftAndUserVo.setUserInfo(userTo);
                        nftAndUserVo.setNftInfoEntity(nftInfoEntity);
                        nftAndUserVoList.add(nftAndUserVo);
                    }
                } else {
                    NftAndUserVo nftAndUserVo = new NftAndUserVo();
                    nftAndUserVo.setUserInfo(userTo);
                    nftAndUserVo.setNftInfoEntity(nftInfoEntity);
                    nftAndUserVoList.add(nftAndUserVo);
                }
            }
        }
        int curPage = (int) params.get("curPage");
        int capacity = (int) params.get("capacity");

        return nftAndUserVoList.subList((curPage - 1) * capacity, (curPage - 1) * capacity + capacity);
    }

    @Override
    public List<TransferLogTo> getTransforLog(Long id) {
        R r = tradeFeignService.getTransforLog(id);
        if (r.getCode() != 0) {
            log.error("远程查询流转记录失败");
            return null;
        }
        List<TransferLogTo> data = r.getData("data", new TypeReference<List<TransferLogTo>>() {
        });
        return data;
    }

    @Override
    @CachePut("nftList")
    public List<NftInfoEntity> getAllNft() {
        List<NftInfoEntity> list = this.list();
        return list;
    }
}

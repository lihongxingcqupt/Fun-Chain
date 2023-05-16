package com.cqupt.art.author.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cqupt.art.author.entity.NftInfoEntity;
import com.cqupt.art.author.entity.to.TransferLogTo;
import com.cqupt.art.author.entity.vo.NftAndUserVo;
import com.cqupt.art.utils.PageUtils;

import java.util.List;
import java.util.Map;


public interface NftInfoService extends IService<NftInfoEntity> {
    List<NftAndUserVo> queryPage(Map<String, Object> params);

    List<TransferLogTo> getTransforLog(Long id);
}

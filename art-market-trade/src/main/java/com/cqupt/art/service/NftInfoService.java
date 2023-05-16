package com.cqupt.art.service;

import com.cqupt.art.entity.PmNftInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author huangxudong
 * @since 2022-11-07
 */
public interface NftInfoService extends IService<PmNftInfo> {

    String airdrop(String uid, String toAddress, String artId, int num);
}

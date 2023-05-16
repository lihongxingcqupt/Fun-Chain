package com.cqupt.art.author.entity.vo;

import com.cqupt.art.author.entity.NftInfoEntity;
import com.cqupt.art.author.entity.to.UserTo;
import lombok.Data;

@Data
public class NftAndUserVo {
    private UserTo userInfo;
    private NftInfoEntity nftInfoEntity;
}

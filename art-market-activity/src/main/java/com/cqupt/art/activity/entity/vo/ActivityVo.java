package com.cqupt.art.activity.entity.vo;

import com.cqupt.art.activity.entity.ActivityEntity;
import com.cqupt.art.activity.entity.NftBatchInfoEntity;
import lombok.Data;

@Data
public class ActivityVo {
    private ActivityEntity activityEntity;
    private NftBatchInfoEntity nftBatchInfoEntity;
}

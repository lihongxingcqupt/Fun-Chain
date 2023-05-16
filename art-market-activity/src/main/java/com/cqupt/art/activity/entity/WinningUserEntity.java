package com.cqupt.art.activity.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("pm_winning_user")
public class WinningUserEntity {
    private Long activityId;
    private String userId;
}

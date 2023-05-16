package com.cqupt.art.activity.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
@Data
@ToString
@TableName("pm_activity")
public class ActivityEntity implements Serializable {
    private Long id;
    private Long nftId;
    private Date regisBegin;
    private Date regisEnd;
    private Date drawRelease;
    private Date canBuy;
}

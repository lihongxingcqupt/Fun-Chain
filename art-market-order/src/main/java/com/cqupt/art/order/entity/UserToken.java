package com.cqupt.art.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
/**
 * 用户所拥有的nft映射表
 */

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("pm_user_token")
public class UserToken implements Serializable {

    private static final long serialVersionUID = 1L;


    @TableId(value = "id", type = IdType.ID_WORKER_STR)
    private String id;

    private String userId;

    private String artId;

    private Integer count;

    private Integer sail;


}

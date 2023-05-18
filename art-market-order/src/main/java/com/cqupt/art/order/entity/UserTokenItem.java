package com.cqupt.art.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 用户和藏品对应的详细信息
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("pm_user_token_item")
public class UserTokenItem implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.ID_WORKER_STR)
    private String id;

    /**
     * 藏品类型：0、藏品，1、盲盒
     */
    private Integer tokenType;

    /**
     * 获取类型：1、首发，2、二级购买，3、转增，4、盲盒开出
     */
    private Integer gainType;

    /**
     * 链上交易hash
     */
    private String txHash;

    /**
     * 获取的价格
     */
    private BigDecimal price;

    /**
     * 藏品的本地id
     */
    private Integer localId;

    /**
     * 藏品状态：1、正常，2、待链上确认，3、寄售中（链上转出确认中）
     */
    private Integer status;

    private String mapId;


}

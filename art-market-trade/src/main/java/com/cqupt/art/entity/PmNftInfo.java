package com.cqupt.art.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author lihongxing
 * @since 2022-11-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class PmNftInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 表的id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 发行数量
     */
    private Integer totalSupply;

    /**
     * 作者id
     */
    private Long authorId;

    /**
     * 作品类型1藏品2盲盒
     */
    private Integer type;

    /**
     * 合约地址，写死的，就是交易那个地址
     */
    private String contractAddress;

    /**
     * 本地id，标识是这一批的第几个
     */
    private Integer localId;

    /**
     * metadata name
     */
    private String tokenName;

    /**
     * metadata img_url
     */
    private String imageUrl;

    /**
     * metadata img_url
     */
    private String description;

    /**
     * 创建nft的交易hash，一批中是一个，标识此次交易，链服务返回
     */
    private String txHash;

    /**
     * 链上的id
     */
    private Long tokenId;

    /**
     * 属于哪个用户
     */
    private String userId;

    /**
     * 作品id
     */
    private String artId;


}

package com.cqupt.art.author.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

@ToString
@Data
@TableName("pm_nft_info")
public class NftInfoEntity implements Cloneable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Integer totalSupply;

    private Long authorId;

    private Integer type;

    private String contractAddress;

    private Integer localId; //本地id标识该藏品是此批的第几个

    private String tokenName;

    private String imageUrl;

    private String description;

    private String txHash;

    private Long tokenId; // 链上的唯一ID，真正的表示这个藏品唯一性的东西

    private String userId; // 该藏品属于哪个用户

    private String artId; // 与batch_info的id对应，方便前端选择空投

    private Integer state; // 藏品的状态：1持有中2已转赠3转售中4已卖出5开盲盒销毁6合成销毁

    private Integer getWay; //获取到该藏品的方式：1购买2赠送3二级市场4盲盒5合成6空投7兑换码
}

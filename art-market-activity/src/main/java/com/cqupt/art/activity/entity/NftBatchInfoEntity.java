package com.cqupt.art.activity.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.Date;

@ToString
@Data
@TableName("pm_nft_batch_info")
public class NftBatchInfoEntity{
    private static final long serialVersionUID = 1L;
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;
    @NotNull
    private Integer totalSupply;
    @NotNull
    private Long authorId;
    @NotNull
    private Integer type;

    private String contractAddress;

    @NotNull
    private String name;

    @NotNull
    private String imageUrl;

    private String description;

    private String txHash;
    @NotNull
//    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
//    @JsonFormat(pattern = "yyyy-MM-dd 'T' HH:mm:ss", timezone = "GMT+8")
    private Date issueTime;

    private Date createTime;

    private Long foundryId;

    @NotNull
    private Float price;

    private Integer isOpen;

    private Integer inventory;

    private String tokenUri;
}

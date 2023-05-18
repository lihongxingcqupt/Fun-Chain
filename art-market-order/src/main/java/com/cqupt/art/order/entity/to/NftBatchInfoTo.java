package com.cqupt.art.order.entity.to;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class NftBatchInfoTo {
    private Long id;

    private Integer totalSupply;

    private Long authorId;

    private Integer type;

    private String contractAddress;


    private String name;

    private String imageUrl;

    private String description;

    private String txHash;

    private Date issueTime;

    private Date createTime;

    private Long foundryId;

    @NotNull
    private Float price;

    private Integer isOpen;

    private Integer inventory;

    private String tokenUri;
    private Integer lanuchStatus;
}

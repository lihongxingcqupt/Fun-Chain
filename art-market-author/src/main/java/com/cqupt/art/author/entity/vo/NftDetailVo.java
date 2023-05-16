package com.cqupt.art.author.entity.vo;

import lombok.Data;

import java.util.Date;

@Data
public class NftDetailVo {
    private Long id;
    private String authorName;
    private String avatarUrl; //作者头像
    private String contractAddress;
    private Integer totalSupply;
    private String name;
    private String imageUrl;
    private String description;
    private String txHash;
    private Float price;
    private String authorDesc;
    private Date startTime;
    private Date endTime;
}

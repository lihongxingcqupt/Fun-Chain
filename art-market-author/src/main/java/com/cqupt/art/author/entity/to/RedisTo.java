package com.cqupt.art.author.entity.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class RedisTo {
    private Long id;
    private String authorName;
    private String contractAddress;
    private String name;
    private String imageUrl;
    private String description;
    private String txHash;
    private Float price;
    private String authorDesc;
    private Date startTime;
    private Date endTime;
    private String token;
}

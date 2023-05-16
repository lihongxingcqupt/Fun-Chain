package com.cqupt.art.author.entity.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class SnapUpNftInfoVo {
    private String id;
    private String name;
    private Integer totalSupply;
    private String imageUrl;
    private Double price;
    private String author;
    private String authorAvatar;
    private Integer inventory;
    private Integer lanuchStatus;
}

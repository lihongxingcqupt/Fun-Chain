package com.cqupt.art.author.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("pm_author")
public class AuthorEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(value = "author_id", type = IdType.AUTO)
    private Long authorId;

    private String authorName;

    private String authorDesc;

    private String avatarUrl;

    private Date createTime;

    private Date modifyTime;

}

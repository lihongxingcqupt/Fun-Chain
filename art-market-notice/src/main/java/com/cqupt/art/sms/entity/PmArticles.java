package com.cqupt.art.sms.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 文章记录表
 * </p>
 *
 * @author lihongxing
 * @since 2022-11-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class PmArticles implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增ID
     */
    @TableId(type = IdType.AUTO)
    @TableField("article_id")
    private Integer articleId;

    /**
     * 父类ID
     */
    @TableField("cat_id")
    private Integer catId;

    /**
     * 文章logo
     */
    private String logo;

    /**
     * 文章标题
     */
    @TableField("article_title")
    private String articleTitle;

    /**
     * 文章内容
     */
    @TableField("article_content")
    private String articleContent;

    /**
     * 关键字
     */
    @TableField("article_key")
    private String articleKey;

    /**
     * 创建者
     */
    @TableField("staff_id")
    private Integer staffId;

    /**
     * 觉得文章有帮助的次数
     */
    private Integer solve;

    /**
     * 觉得文章没帮助的次数
     */
    private Integer unsolve;

    /**
     * 浏览量 0未读 >0浏览量
     */
    private Integer num;

    /**
     * 是否显示
     */
    @TableField("is_show")
    private Integer isShow;

    /**
     * 有效状态
     */
    @TableField("data_flag")
    private Integer dataFlag;

    /**
     * 创建时间
     */

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableField("publish_time")
    private Date publishTime;

}

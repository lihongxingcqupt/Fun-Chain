package com.cqupt.art.sms.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;
import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 文章分类表
 * </p>
 *
 * @author lihongxing
 * @since 2022-11-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class PmArticleCats implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增ID
     */
    @TableId(type = IdType.AUTO)
    @TableField("cat_id")
    private Integer catId;

    /**
     * 父ID
     */
    @TableField("parent_id")
    private Integer parentId;

    /**
     * 是否显示 0：隐藏 1：显示
     */
    @TableField("is_show")
    private Integer isShow;

    /**
     * 分类名称
     */
    @TableField("cat_name")
    private String catName;

    /**
     * 排序号
     */
    @TableField("cat_sort")
    private Integer catSort;

    /**
     * 删除标志
     */
    @TableField("data_flag")
    @TableLogic(value = "1", delval = "0")
    private Integer dataFlag;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;


    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

}

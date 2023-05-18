package com.cqupt.art.seckill.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author lihongxing
 * @since 2022-11-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
//@ApiModel(value="PmUser对象", description="用户表")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableField("user_id")
    @TableId(type = IdType.ID_WORKER_STR)
    private String userId;

    //    @ApiModelProperty(value = "姓名")
    @TableField("user_name")
    private String userName;

    @TableField("real_name")
    private String realName;

    @TableField("pay_password")
    private Integer payPassword;
    //    @ApiModelProperty(value = "身份证号")
    @TableField("card_id")
    @Pattern(regexp = " /^[1-9]\\d{5}(19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$/", message = "身份证不合法！")
    private String cardID;

    //    @ApiModelProperty(value = "手机号")
    @TableField("user_phone")
    @Pattern(regexp = "/^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$/", message = "手机号不合法")
    private String userPhone;

    //    @ApiModelProperty(value = "邀请用户ID")
    @TableField("invite_id")
    private Integer inviteId;

    //    @ApiModelProperty(value = "邀请码")
    @TableField("invite_code")
    private String inviteCode;

    //    @ApiModelProperty(value = "邀请好友奖励")
    @TableField("friend_money")
    private BigDecimal friendMoney;

    //    @ApiModelProperty(value = "订单分享奖励")
    @TableField("order_money")
    private BigDecimal orderMoney;

    //    @ApiModelProperty(value = "累计佣金")
    @TableField("total_money")
    private BigDecimal totalMoney;

    //    @ApiModelProperty(value = "可提现金额")
    @TableField("draw_money")
    private BigDecimal drawMoney;

    //    @ApiModelProperty(value = "会员星级")
    private Integer star;

    //    @ApiModelProperty(value = "邀请总人数")
    @TableField("invited_count")
    private Integer invitedCount;

    //    @ApiModelProperty(value = "账号状态 0:停用 1:启用")
    @TableField("user_status")
    private Integer userStatus;

    //    @ApiModelProperty(value = "注册时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    //    @ApiModelProperty(value = "密码")
    @Pattern(regexp = "/^(?![0-9]+$)[a-z0-9]{1,50}$/", message = "密码必须同时包含字母和数字！")
    private String password;

    //    @ApiModelProperty(value = "链上地址")
    private String chainAddress;

    //    @ApiModelProperty(value = "钱包私钥")
    private String privateKey;

    //    @ApiModelProperty(value = "链上钱包密码")
    private String accountPassword;


}

package com.zykj.btlv.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @TableName user
 */
@TableName(value ="user")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 地址
     */
    @TableField(value = "address")
    private String address;

    /**
     * 上级地址
     */
    @TableField(value = "parentAddress")
    private String parentAddress;

    /**
     * lp数量
     */
    @TableField(value = "lp")
    private BigDecimal lp;

    /**
     * 余额
     */
    @TableField(value = "balance")
    private BigDecimal balance;

    /**
     * 总额度
     */
    @TableField(value = "totalQuota")
    private BigDecimal totalQuota;

    /**
     * 剩余额度
     */
    @TableField(value = "surplusQuota")
    private BigDecimal surplusQuota;

    /**
     * 已领取
     */
    @TableField(value = "received")
    private BigDecimal received;

    /**
     * 管理奖等级
     */
    @TableField(value = "grade")
    private Integer grade;

    /**
     * 入金价格（USDT）
     */
    @TableField(value = "usdtPrice")
    private BigDecimal usdtPrice;

    /**
     * 有效分享人数
     */
    @TableField(value = "people")
    private Integer people;

    /**
     * performance
     */
    @TableField(value = "performance")
    private BigDecimal performance;

    /**
     * isGrade
     */
    @TableField(value = "isGrade")
    private Integer isGrade;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
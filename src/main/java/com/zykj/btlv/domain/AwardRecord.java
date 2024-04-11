package com.zykj.btlv.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @TableName award_record
 */
@TableName(value ="award_record")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AwardRecord implements Serializable {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 哈希
     */
    @TableField(value = "hash")
    private String hash;

    /**
     * 地址
     */
    @TableField(value = "address")
    private String address;

    /**
     * 数量
     */
    @TableField(value = "amount")
    private BigDecimal amount;

    /**
     * 
     */
    @TableField(value = "time")
    private Date time;

    /**
     *
     */
    @TableField(value = "type")
    private Integer type;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
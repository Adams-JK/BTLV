package com.zykj.btlv.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserVo {
    private String userAddr;//用户地址
    private String parentAddress;//上级地址
    private BigDecimal usdtPrice;//入金价格（USDT）
    private BigDecimal payUsdt;//入金USDT
    private BigDecimal payBtl;//入金BTL
    private BigDecimal dailyRate;//日收益率
    private Integer people;//分享人数
    private Integer grade;//管理奖等级
    private BigDecimal gradeRatio;//管理奖奖励比例
    private BigDecimal accelerate1;//1-3层加速
    private BigDecimal accelerate2;//4-7层加速
    private BigDecimal accelerate3;//8-10层加速
    private BigDecimal performance1;//团队业绩
}
